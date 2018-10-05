/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.loader.csv;

import static java.util.stream.Collectors.toList;

import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.CharSource;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.StandardId;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.date.Tenor;
import com.opengamma.strata.basics.index.FloatingRateName;
import com.opengamma.strata.basics.index.IborIndex;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.collect.io.CsvIterator;
import com.opengamma.strata.collect.io.CsvRow;
import com.opengamma.strata.collect.io.ResourceLocator;
import com.opengamma.strata.collect.io.UnicodeBom;
import com.opengamma.strata.collect.result.FailureItem;
import com.opengamma.strata.collect.result.FailureReason;
import com.opengamma.strata.collect.result.ValueWithFailures;
import com.opengamma.strata.loader.LoaderUtils;
import com.opengamma.strata.market.curve.CurveName;
import com.opengamma.strata.market.sensitivity.CurveSensitivities;
import com.opengamma.strata.market.sensitivity.CurveSensitivitiesType;
import com.opengamma.strata.product.PortfolioItemInfo;

/**
 * Loads sensitivities from CSV files.
 * <p>
 * The sensitivities are expected to be in a CSV format known to Strata.
 * The parser currently supports two different CSV formats.
 * Columns may occur in any order.
 * 
 * <h4>List format</h4>
 * <p>
 * The following columns are supported:
 * <ul>
 * <li>'Id Scheme' (optional) - the name of the scheme that the identifier is unique within, defaulted to 'OG-Sensitivity'.
 * <li>'Id' (optional) - the identifier of the sensitivity, such as 'SENS12345'.
 * <li>'Reference - a currency, floating rate name, index name or curve name.
 *   The standard reference name for a discount curve is the currency, such as 'GBP'.
 *   The standard reference name for a forward curve is the index name, such as 'GBP-LIBOR-3M'.
 *   Any curve name may be used however, which will be specific to the market data setup.
 * <li>'Sensitivity Tenor' - the tenor of the bucketed sensitivity, such as '1Y'.
 * <li>one or more sensitivity value columns, the type of the sensitivity is specified by the header name,
 *   such as 'ZeroRateDelta'.
 * </ul>
 * <p>
 * The identifier columns are not normally present as the identifier is completely optional.
 * If present, the values must be repeated for each row that forms part of one sensitivity.
 * If the parser finds a different identifier, it will create a second sensitivity instance.
 * <p>
 * When parsing the value columns, if the cell is empty, the combination of type/reference/tenor/value
 * will not be added to the result, so use an explicit zero to include a zero value.
 * 
 * <h4>Grid format</h4>
 * <p>
 * The following columns are supported:<br />
 * <ul>
 * <li>'Id Scheme' (optional) - the name of the scheme that the identifier is unique within, defaulted to 'OG-Sensitivity'.
 * <li>'Id' (optional) - the identifier of the sensitivity, such as 'SENS12345'.
 * <li>'Sensitivity Type' - defines the type of the sensitivity value, such as 'ZeroRateDelta' or 'ZeroRateGamma'.
 * <li>'Sensitivity Tenor' - the tenor of the bucketed sensitivity, such as '1Y'.
 * <li>one or more sensitivity value columns, the reference of the sensitivity is specified by the header name.
 *   The reference can be a currency, floating rate name, index name or curve name.
 *   The standard reference name for a discount curve is the currency, such as 'GBP'.
 *   The standard reference name for a forward curve is the index name, such as 'GBP-LIBOR-3M'.
 *   Any curve name may be used however, which will be specific to the market data setup.
 * </ul>
 * <p>
 * The identifier columns are not normally present as the identifier is completely optional.
 * If present, the values must be repeated for each row that forms part of one sensitivity.
 * If the parser finds a different identifier, it will create a second sensitivity instance.
 * <p>
 * When parsing the value columns, if the cell is empty, the combination of type/reference/tenor/value
 * will not be added to the result, so use an explicit zero to include a zero value.
 */
public final class SensitivityCsvLoader {

  // default schemes
  static final String DEFAULT_SCHEME = "OG-Sensitivity";

  // CSV column headers
  private static final String ID_SCHEME_HEADER = "Id Scheme";
  private static final String ID_HEADER = "Id";
  private static final String REFERENCE_HEADER = "Reference";
  private static final String TYPE_HEADER = "Sensitivity Type";
  private static final String TENOR_HEADER = "Sensitivity Tenor";
  private static final ImmutableSet<String> GRID_HEADERS =
      ImmutableSet.of(ID_SCHEME_HEADER, ID_HEADER, TYPE_HEADER, TENOR_HEADER);

  /**
   * The resolver, providing additional information.
   */
  private final SensitivityCsvInfoResolver resolver;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance that uses the standard set of reference data.
   * 
   * @return the loader
   */
  public static SensitivityCsvLoader standard() {
    return new SensitivityCsvLoader(SensitivityCsvInfoResolver.standard());
  }

  /**
   * Obtains an instance that uses the specified set of reference data.
   * 
   * @param refData  the reference data
   * @return the loader
   */
  public static SensitivityCsvLoader of(ReferenceData refData) {
    return new SensitivityCsvLoader(SensitivityCsvInfoResolver.of(refData));
  }

  /**
   * Obtains an instance that uses the specified resolver for additional information.
   * 
   * @param resolver  the resolver used to parse additional information
   * @return the loader
   */
  public static SensitivityCsvLoader of(SensitivityCsvInfoResolver resolver) {
    return new SensitivityCsvLoader(resolver);
  }

  // restricted constructor
  private SensitivityCsvLoader(SensitivityCsvInfoResolver resolver) {
    this.resolver = ArgChecker.notNull(resolver, "resolver");
  }

  //-------------------------------------------------------------------------
  /**
   * Checks whether the source is a CSV format sensitivities file.
   * <p>
   * This parses the headers as CSV and checks that mandatory headers are present.
   * 
   * @param charSource  the CSV character source to check
   * @return true if the source is a CSV file with known headers, false otherwise
   */
  public boolean isKnownFormat(CharSource charSource) {
    try (CsvIterator csv = CsvIterator.of(charSource, true)) {
      if (!csv.containsHeader(TENOR_HEADER)) {
        return false;
      }
      if (csv.containsHeader(REFERENCE_HEADER) || csv.containsHeader(TYPE_HEADER)) {
        return true;
      } else {
        return csv.headers().stream()
            .filter(header -> knownReference(header))
            .findAny()
            .isPresent();
      }
    } catch (RuntimeException ex) {
      return false;
    }
  }

  // for historical compatibility, we determine known format by looking for these specific things
  // the new approach is to require either the 'Reference' or the 'Sensitivity Type' column
  private static boolean knownReference(String refStr) {
    try {
      Optional<IborIndex> ibor = IborIndex.extendedEnum().find(refStr);
      if (ibor.isPresent()) {
        return true;
      } else {
        Optional<FloatingRateName> frName = FloatingRateName.extendedEnum().find(refStr);
        if (frName.isPresent()) {
          return true;
        } else if (refStr.length() == 3) {
          Currency.of(refStr);  // this may throw an exception validating the string
          return true;
        } else {
          return false;
        }
      }
    } catch (RuntimeException ex) {
      return false;
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Loads one or more CSV format sensitivities files.
   * <p>
   * In most cases each file contains one sensitivity instance, however the file format is capable
   * of representing any number.
   * <p>
   * Within a single file and identifier, the same combination of type, reference and tenor must not be repeated.
   * No checks are performed between different input files.
   * It may be useful to merge the sensitivities in the resulting list in a separate step after parsing.
   * <p>
   * CSV files sometimes contain a Unicode Byte Order Mark.
   * This method uses {@link UnicodeBom} to interpret it.
   * 
   * @param resources  the CSV resources
   * @return the loaded sensitivities, parsing errors are captured in the result
   */
  public ValueWithFailures<List<CurveSensitivities>> load(Collection<ResourceLocator> resources) {
    Collection<CharSource> charSources = resources.stream()
        .map(r -> UnicodeBom.toCharSource(r.getByteSource()))
        .collect(toList());
    return parse(charSources);
  }

  //-------------------------------------------------------------------------
  /**
   * Parses one or more CSV format position files, returning sensitivities.
   * <p>
   * In most cases each file contains one sensitivity instance, however the file format is capable
   * of representing any number.
   * <p>
   * Within a single file and identifier, the same combination of type, reference and tenor must not be repeated.
   * No checks are performed between different input files.
   * It may be useful to merge the sensitivities in the resulting list in a separate step after parsing.
   * <p>
   * CSV files sometimes contain a Unicode Byte Order Mark.
   * Callers are responsible for handling this, such as by using {@link UnicodeBom}.
   * 
   * @param charSources  the CSV character sources
   * @return the loaded sensitivities, parsing errors are captured in the result
   */
  public ValueWithFailures<List<CurveSensitivities>> parse(Collection<CharSource> charSources) {
    List<CurveSensitivities> parsed = new ArrayList<>();
    List<FailureItem> failures = new ArrayList<>();
    for (CharSource charSource : charSources) {
      parse(charSource, parsed, failures);
    }
    return ValueWithFailures.of(ImmutableList.copyOf(parsed), failures);
  }

  // parse a single file
  private void parse(CharSource charSource, List<CurveSensitivities> parsed, List<FailureItem> failures) {
    try (CsvIterator csv = CsvIterator.of(charSource, true)) {
      if (!csv.headers().contains(TENOR_HEADER)) {
        failures.add(FailureItem.of(
            FailureReason.PARSING, "CSV file could not be parsed: Missing column: {}", TENOR_HEADER));
      } else if (csv.headers().contains(REFERENCE_HEADER)) {
        parseListFile(csv, parsed, failures);
      } else {
        parseGridFile(csv, parsed, failures);
      }
    } catch (RuntimeException ex) {
      failures.add(FailureItem.of(FailureReason.PARSING, ex, "CSV file could not be parsed: {}", ex.getMessage()));
    }
  }

  //-------------------------------------------------------------------------
  // parses the file in list format
  private ValueWithFailures<CurveSensitivities> parseListFile(
      CsvIterator csv,
      List<CurveSensitivities> parsed,
      List<FailureItem> failures) {

    CurveSensitivitiesBuilder builder = CurveSensitivities.builder();
    while (csv.hasNext()) {
      CsvRow row = csv.next();
      try {
        // parse tenor and delta value
        CurveInputSensitivityPoint curveInputSensitivityPoint = parseCurveInputSensitivityPoint(row.getValue(TENOR_HEADER));
        String refStr = row.getValue(REFERENCE_HEADER);
        double zeroDelta = Double.parseDouble(row.findValue(ZERO_RATE_DELTA_FIELD).orElse("0"));
        CurveInputSensitivityTarget target = parseReference(refStr);
        builder.add(CurveInputSensitivityType.ZERO_RATE_DELTA, target, curveInputSensitivityPoint, zeroDelta);
        
        // parse other values if available
        
        if (row.headers().contains(ZERO_RATE_GAMMA_FIELD)) {
          double zeroGamma = Double.parseDouble(row.findValue(ZERO_RATE_GAMMA_FIELD).orElse("0"));
          builder.add(CurveInputSensitivityType.ZERO_RATE_GAMMA, target, curveInputSensitivityPoint, zeroGamma);
        }
        
        if (row.headers().contains(ZERO_RATE_DELTA_BASIS_FIELD)) {
          double zeroGamma = Double.parseDouble(row.findValue(ZERO_RATE_DELTA_BASIS_FIELD).orElse("0"));
          builder.add(CurveInputSensitivityType.ZERO_RATE_DELTA_BASIS, target, curveInputSensitivityPoint, zeroGamma);
        }
        
        if (row.headers().contains(ZERO_RATE_GAMMA_BASIS_FIELD)) {
          double zeroGamma = Double.parseDouble(row.findValue(ZERO_RATE_GAMMA_BASIS_FIELD).orElse("0"));
          builder.add(CurveInputSensitivityType.ZERO_RATE_GAMMA_BASIS, target, curveInputSensitivityPoint, zeroGamma);
        }
      } catch (IllegalArgumentException ex) {
        return ValueWithFailures.of(
            CurveSensitivities.empty(),
            FailureItem.of(
                FailureReason.PARSING, "CSV file could not be parsed at line {}: {}", row.lineNumber(), ex.getMessage()));
      }
    }
    return ValueWithFailures.of(builder.build());
  }

  // parses the file in grid format
  private void parseGridFile(
      CsvIterator csv,
      List<CurveSensitivities> parsed,
      List<FailureItem> failures) {

    Map<String, CurveName> references = new LinkedHashMap<>();
    for (String header : csv.headers()) {
      String headerLowerCase = header.toLowerCase(Locale.ENGLISH);
      if (!GRID_HEADERS.contains(headerLowerCase) && !resolver.isInfoColumn(headerLowerCase)) {
        references.put(header, CurveName.of(header));
      }
    }

    while (csv.hasNext()) {
      CsvRow row = csv.peek();
      try {
        PortfolioItemInfo info = parseInfo(row);
        String id = info.getId().map(StandardId::toString).orElse("");
        
        Map<CurveSensitivitiesType, Map<Tenor, Double>> parsedMap = new LinkedHashMap<>();
        List<CsvRow> batchRows = csv.nextBatch(r -> matchId(r, id));
        for (CsvRow batchRow : batchRows) {
          Tenor tenor = LoaderUtils.parseTenor(row.getValue(TENOR_HEADER));  // TODO: date?
          CurveSensitivitiesType type = row.findValue(TYPE_HEADER)
              .map(str -> CurveSensitivitiesType.of(str))
              .orElse(CurveSensitivitiesType.ZERO_RATE_DELTA);
          for (Entry<String, CurveName> entry : references.entrySet()) {
            String valueStr = row.getField(entry.getKey());
            if (!valueStr.isEmpty()) {
              parsedMap.computeIfAbsent(type, t -> new HashMap<>()).put(tenor, LoaderUtils.parseDouble(valueStr));
            }
          }
        }

      } catch (IllegalArgumentException ex) {
        return ValueWithFailures.of(
            CurveSensitivities.empty(),
            FailureItem.of(
                FailureReason.PARSING, "CSV file could not be parsed at line {}: {}", row.lineNumber(), ex.getMessage()));
      }
    }
    return ValueWithFailures.of(builder.build());
  }

  //-------------------------------------------------------------------------
  //  /**
  //   * Parses sensitivity from a collection of files.
  //   * <p>
  //   * The same sensitivity (defined by type, target and tenor) may occur in two different files.
  //   * If this occurs, the sensitivity values will be totalled.
  //   * 
  //   * @param charSources  the files to parse
  //   * @return the sensitivities, together with any failures
  //   */
  //  private static ValueWithFailures<CurveSensitivities> merge(Collection<CharSource> charSources) {
  //    List<ValueWithFailures<CurveSensitivities>> parsed = charSources.stream()
  //        .map(CurveSensitivitiesCsvLoader::parse)
  //        .collect(toImmutableList());
  //    List<FailureItem> failures = parsed.stream()
  //        .flatMap(vwf -> vwf.getFailures().stream())
  //        .collect(toImmutableList());
  //    List<CurveInputSensitivity> parsedSens = parsed.stream()
  //        .flatMap(vwf -> vwf.getValue().getSensitivities().stream())
  //        .collect(toImmutableList());
  //    return ValueWithFailures.of(CurveSensitivities.of(parsedSens), failures);
  //  }

  //-------------------------------------------------------------------------
  // parse the sensitivity info
  private PortfolioItemInfo parseInfo(CsvRow row) {
    PortfolioItemInfo info = PortfolioItemInfo.empty();
    String scheme = row.findField(ID_SCHEME_HEADER).orElse(DEFAULT_SCHEME);
    row.findValue(ID_HEADER).ifPresent(id -> info.withId(StandardId.of(scheme, id)));
    return resolver.parseSensitivityInfo(row, info);
  }

  // checks if the identifier in the row matches the previous one
  private boolean matchId(CsvRow row, String id) {
    String scheme = row.findField(ID_SCHEME_HEADER).orElse(DEFAULT_SCHEME);
    String rowId = row.findValue(ID_HEADER).map(str -> StandardId.of(scheme, str).toString()).orElse("");
    return id.equals(rowId);
  }

  //-------------------------------------------------------------------------
  // parse a tenor/date and perform sanity checks if the input is a tenor
  private static CurveInputSensitivityPoint parseCurveInputSensitivityPoint(String input) {
    
    CurveInputSensitivityPoint curveInputSensitivityPoint = CurveInputSensitivityPoint.parse(input);
    
    //Sanity checks for tenor
    if (curveInputSensitivityPoint.getTenor().isPresent()) {
      Period period = curveInputSensitivityPoint.getTenor().get().getPeriod();
      if (period.getDays() > 0 && period.toTotalMonths() > 0) {
        throw new IllegalArgumentException("Tenor cannot be a mixture of days and months/years: " + input);
      }
      if (period.getDays() > 365) {
        throw new IllegalArgumentException("Tenor cannot be day-based when over 1 year (365 days): " + input);
      }
      if (period.getYears() > 5 && period.getMonths() != 0) {
        throw new IllegalArgumentException("Tenor cannot have months when over 5 years: " + input);
      }
    }
    return curveInputSensitivityPoint;
  }

}
