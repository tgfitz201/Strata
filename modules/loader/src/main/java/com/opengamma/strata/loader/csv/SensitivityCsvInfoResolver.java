/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.loader.csv;

import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.collect.io.CsvRow;
import com.opengamma.strata.product.PortfolioItemInfo;

/**
 * Resolves additional information when parsing sensitivity CSV files.
 * <p>
 * Data loaded from a CSV may contain additional information that needs to be captured.
 * This plugin point allows the additional CSV columns to be parsed and captured.
 */
public interface SensitivityCsvInfoResolver {

  /**
   * Obtains an instance that uses the standard set of reference data.
   * 
   * @return the loader
   */
  public static SensitivityCsvInfoResolver standard() {
    return StandardCsvInfoResolver.of(ReferenceData.standard());
  }

  /**
   * Obtains an instance that uses the specified set of reference data.
   * 
   * @param refData  the reference data
   * @return the loader
   */
  public static SensitivityCsvInfoResolver of(ReferenceData refData) {
    return StandardCsvInfoResolver.of(refData);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the reference data being used.
   * 
   * @return the reference data
   */
  public abstract ReferenceData getReferenceData();

  /**
   * Checks if the column header is an info column that this resolver will parse.
   * 
   * @param headerLowerCase  the header case, in lower case (ENGLISH) form
   * @return true if the header is for an info column
   */
  public default boolean isInfoColumn(String headerLowerCase) {
    return false;
  }

  /**
   * Parses attributes to update {@code PortfolioItemInfo}.
   * <p>
   * If it is available, the sensitivity ID will have been set before this method is called.
   * It may be altered if necessary, although this is not recommended.
   * 
   * @param row  the CSV row to parse
   * @param info  the info to update and return
   * @return the updated info
   */
  public default PortfolioItemInfo parseSensitivityInfo(CsvRow row, PortfolioItemInfo info) {
    // do nothing
    return info;
  }

}
