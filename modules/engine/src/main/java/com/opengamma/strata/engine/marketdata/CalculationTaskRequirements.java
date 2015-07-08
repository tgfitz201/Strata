/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.engine.marketdata;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableConstructor;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.market.MarketDataId;
import com.opengamma.strata.basics.market.ObservableId;
import com.opengamma.strata.engine.marketdata.scenarios.LocalScenarioDefinition;

// TODO This needs to know the cell of the task. But what about functions that can calculate multiple results?
/**
 * A collection of market data IDs specifying the market data required for performing a set of calculations.
 */
@BeanDefinition(builderScope = "private")
public final class CalculationTaskRequirements implements ImmutableBean {

  /** A set of requirements which specifies that no market data is required. */
  //private static final CalculationTaskRequirements EMPTY = CalculationTaskRequirements.builder().build();

  /** The row index of the value in the results grid. */
  @PropertyDefinition
  private final int rowIndex;

  /** The column index of the value in the results grid. */
  @PropertyDefinition
  private final int columnIndex;

  /** Keys identifying the market data values required for the calculations. */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableSet<ObservableId> observables;

  /** Keys identifying the market data values required for the calculations. */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableSet<MarketDataId<?>> nonObservables;

  /** Keys identifying the time series of market data values required for the calculations. */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableSet<ObservableId> timeSeries;

  /**
   * The currencies in the calculation results. The market data must include FX rates in the
   * to allow conversion into the reporting currency. The FX rates must have the output currency as the base
   * currency and the reporting currency as the counter currency.
   */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableSet<Currency> outputCurrencies;

  /** . */
  @PropertyDefinition(validate = "notNull")
  private final List<LocalScenarioDefinition> localScenarios;

  /**
   * Returns an empty mutable builder for building up a set of requirements.
   *
   * @return an empty mutable builder for building up a set of requirements
   */
  public static CalculationTaskRequirementsBuilder builder(int rowIndex, int columnIndex) {
    return new CalculationTaskRequirementsBuilder(rowIndex, columnIndex);
  }

  /**
   * Returns a set of requirements specifying that no market data is required.
   *
   * @return a set of requirements specifying that no market data is required
   */
  //public static CalculationTaskRequirements empty() {
  //  return EMPTY;
  //}

  /**
   * Returns a set of calculation requirements built from a set of market data requirements.
   *
   * @param marketDataRequirements  a set of requirements for market data
   * @return a set of calculation requirements built from a set of market data requirements
   */
  //public static CalculationTaskRequirements of(MarketDataRequirements marketDataRequirements) {
  //  // TODO How is this going to work?
  //  return CalculationTaskRequirements.builder()
  //      .addValues(marketDataRequirements.getObservables())
  //      .addValues(marketDataRequirements.getNonObservables())
  //      .addTimeSeries(marketDataRequirements.getTimeSeries())
  //      .build();
  //}

  // package-private constructor, used by MarketDataRequirementsBuilder
  @ImmutableConstructor
  CalculationTaskRequirements(
      int rowIndex,
      int columnIndex,
      Set<? extends ObservableId> observables,
      Set<? extends MarketDataId<?>> nonObservables,
      Set<ObservableId> timeSeries,
      Set<Currency> outputCurrencies,
      List<LocalScenarioDefinition> localScenarios) {

    this.rowIndex = rowIndex;
    this.columnIndex = columnIndex;
    this.observables = ImmutableSet.copyOf(observables);
    this.nonObservables = ImmutableSet.copyOf(nonObservables);
    this.timeSeries = ImmutableSet.copyOf(timeSeries);
    this.outputCurrencies = ImmutableSet.copyOf(outputCurrencies);
    this.localScenarios = ImmutableList.copyOf(localScenarios);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CalculationTaskRequirements}.
   * @return the meta-bean, not null
   */
  public static CalculationTaskRequirements.Meta meta() {
    return CalculationTaskRequirements.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(CalculationTaskRequirements.Meta.INSTANCE);
  }

  @Override
  public CalculationTaskRequirements.Meta metaBean() {
    return CalculationTaskRequirements.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the row index of the value in the results grid.
   * @return the value of the property
   */
  public int getRowIndex() {
    return rowIndex;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the column index of the value in the results grid.
   * @return the value of the property
   */
  public int getColumnIndex() {
    return columnIndex;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets keys identifying the market data values required for the calculations.
   * @return the value of the property, not null
   */
  public ImmutableSet<ObservableId> getObservables() {
    return observables;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets keys identifying the market data values required for the calculations.
   * @return the value of the property, not null
   */
  public ImmutableSet<MarketDataId<?>> getNonObservables() {
    return nonObservables;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets keys identifying the time series of market data values required for the calculations.
   * @return the value of the property, not null
   */
  public ImmutableSet<ObservableId> getTimeSeries() {
    return timeSeries;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the currencies in the calculation results. The market data must include FX rates in the
   * to allow conversion into the reporting currency. The FX rates must have the output currency as the base
   * currency and the reporting currency as the counter currency.
   * @return the value of the property, not null
   */
  public ImmutableSet<Currency> getOutputCurrencies() {
    return outputCurrencies;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets .
   * @return the value of the property, not null
   */
  public List<LocalScenarioDefinition> getLocalScenarios() {
    return localScenarios;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      CalculationTaskRequirements other = (CalculationTaskRequirements) obj;
      return (getRowIndex() == other.getRowIndex()) &&
          (getColumnIndex() == other.getColumnIndex()) &&
          JodaBeanUtils.equal(getObservables(), other.getObservables()) &&
          JodaBeanUtils.equal(getNonObservables(), other.getNonObservables()) &&
          JodaBeanUtils.equal(getTimeSeries(), other.getTimeSeries()) &&
          JodaBeanUtils.equal(getOutputCurrencies(), other.getOutputCurrencies()) &&
          JodaBeanUtils.equal(getLocalScenarios(), other.getLocalScenarios());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getRowIndex());
    hash = hash * 31 + JodaBeanUtils.hashCode(getColumnIndex());
    hash = hash * 31 + JodaBeanUtils.hashCode(getObservables());
    hash = hash * 31 + JodaBeanUtils.hashCode(getNonObservables());
    hash = hash * 31 + JodaBeanUtils.hashCode(getTimeSeries());
    hash = hash * 31 + JodaBeanUtils.hashCode(getOutputCurrencies());
    hash = hash * 31 + JodaBeanUtils.hashCode(getLocalScenarios());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(256);
    buf.append("CalculationTaskRequirements{");
    buf.append("rowIndex").append('=').append(getRowIndex()).append(',').append(' ');
    buf.append("columnIndex").append('=').append(getColumnIndex()).append(',').append(' ');
    buf.append("observables").append('=').append(getObservables()).append(',').append(' ');
    buf.append("nonObservables").append('=').append(getNonObservables()).append(',').append(' ');
    buf.append("timeSeries").append('=').append(getTimeSeries()).append(',').append(' ');
    buf.append("outputCurrencies").append('=').append(getOutputCurrencies()).append(',').append(' ');
    buf.append("localScenarios").append('=').append(JodaBeanUtils.toString(getLocalScenarios()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CalculationTaskRequirements}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code rowIndex} property.
     */
    private final MetaProperty<Integer> rowIndex = DirectMetaProperty.ofImmutable(
        this, "rowIndex", CalculationTaskRequirements.class, Integer.TYPE);
    /**
     * The meta-property for the {@code columnIndex} property.
     */
    private final MetaProperty<Integer> columnIndex = DirectMetaProperty.ofImmutable(
        this, "columnIndex", CalculationTaskRequirements.class, Integer.TYPE);
    /**
     * The meta-property for the {@code observables} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableSet<ObservableId>> observables = DirectMetaProperty.ofImmutable(
        this, "observables", CalculationTaskRequirements.class, (Class) ImmutableSet.class);
    /**
     * The meta-property for the {@code nonObservables} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableSet<MarketDataId<?>>> nonObservables = DirectMetaProperty.ofImmutable(
        this, "nonObservables", CalculationTaskRequirements.class, (Class) ImmutableSet.class);
    /**
     * The meta-property for the {@code timeSeries} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableSet<ObservableId>> timeSeries = DirectMetaProperty.ofImmutable(
        this, "timeSeries", CalculationTaskRequirements.class, (Class) ImmutableSet.class);
    /**
     * The meta-property for the {@code outputCurrencies} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableSet<Currency>> outputCurrencies = DirectMetaProperty.ofImmutable(
        this, "outputCurrencies", CalculationTaskRequirements.class, (Class) ImmutableSet.class);
    /**
     * The meta-property for the {@code localScenarios} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<LocalScenarioDefinition>> localScenarios = DirectMetaProperty.ofImmutable(
        this, "localScenarios", CalculationTaskRequirements.class, (Class) List.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "rowIndex",
        "columnIndex",
        "observables",
        "nonObservables",
        "timeSeries",
        "outputCurrencies",
        "localScenarios");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 23238424:  // rowIndex
          return rowIndex;
        case -855241956:  // columnIndex
          return columnIndex;
        case 121811856:  // observables
          return observables;
        case 824041091:  // nonObservables
          return nonObservables;
        case 779431844:  // timeSeries
          return timeSeries;
        case -1022597040:  // outputCurrencies
          return outputCurrencies;
        case 101869496:  // localScenarios
          return localScenarios;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends CalculationTaskRequirements> builder() {
      return new CalculationTaskRequirements.Builder();
    }

    @Override
    public Class<? extends CalculationTaskRequirements> beanType() {
      return CalculationTaskRequirements.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code rowIndex} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Integer> rowIndex() {
      return rowIndex;
    }

    /**
     * The meta-property for the {@code columnIndex} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Integer> columnIndex() {
      return columnIndex;
    }

    /**
     * The meta-property for the {@code observables} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableSet<ObservableId>> observables() {
      return observables;
    }

    /**
     * The meta-property for the {@code nonObservables} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableSet<MarketDataId<?>>> nonObservables() {
      return nonObservables;
    }

    /**
     * The meta-property for the {@code timeSeries} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableSet<ObservableId>> timeSeries() {
      return timeSeries;
    }

    /**
     * The meta-property for the {@code outputCurrencies} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableSet<Currency>> outputCurrencies() {
      return outputCurrencies;
    }

    /**
     * The meta-property for the {@code localScenarios} property.
     * @return the meta-property, not null
     */
    public MetaProperty<List<LocalScenarioDefinition>> localScenarios() {
      return localScenarios;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 23238424:  // rowIndex
          return ((CalculationTaskRequirements) bean).getRowIndex();
        case -855241956:  // columnIndex
          return ((CalculationTaskRequirements) bean).getColumnIndex();
        case 121811856:  // observables
          return ((CalculationTaskRequirements) bean).getObservables();
        case 824041091:  // nonObservables
          return ((CalculationTaskRequirements) bean).getNonObservables();
        case 779431844:  // timeSeries
          return ((CalculationTaskRequirements) bean).getTimeSeries();
        case -1022597040:  // outputCurrencies
          return ((CalculationTaskRequirements) bean).getOutputCurrencies();
        case 101869496:  // localScenarios
          return ((CalculationTaskRequirements) bean).getLocalScenarios();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code CalculationTaskRequirements}.
   */
  private static final class Builder extends DirectFieldsBeanBuilder<CalculationTaskRequirements> {

    private int rowIndex;
    private int columnIndex;
    private Set<ObservableId> observables = ImmutableSet.of();
    private Set<MarketDataId<?>> nonObservables = ImmutableSet.of();
    private Set<ObservableId> timeSeries = ImmutableSet.of();
    private Set<Currency> outputCurrencies = ImmutableSet.of();
    private List<LocalScenarioDefinition> localScenarios = ImmutableList.of();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 23238424:  // rowIndex
          return rowIndex;
        case -855241956:  // columnIndex
          return columnIndex;
        case 121811856:  // observables
          return observables;
        case 824041091:  // nonObservables
          return nonObservables;
        case 779431844:  // timeSeries
          return timeSeries;
        case -1022597040:  // outputCurrencies
          return outputCurrencies;
        case 101869496:  // localScenarios
          return localScenarios;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 23238424:  // rowIndex
          this.rowIndex = (Integer) newValue;
          break;
        case -855241956:  // columnIndex
          this.columnIndex = (Integer) newValue;
          break;
        case 121811856:  // observables
          this.observables = (Set<ObservableId>) newValue;
          break;
        case 824041091:  // nonObservables
          this.nonObservables = (Set<MarketDataId<?>>) newValue;
          break;
        case 779431844:  // timeSeries
          this.timeSeries = (Set<ObservableId>) newValue;
          break;
        case -1022597040:  // outputCurrencies
          this.outputCurrencies = (Set<Currency>) newValue;
          break;
        case 101869496:  // localScenarios
          this.localScenarios = (List<LocalScenarioDefinition>) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public CalculationTaskRequirements build() {
      return new CalculationTaskRequirements(
          rowIndex,
          columnIndex,
          observables,
          nonObservables,
          timeSeries,
          outputCurrencies,
          localScenarios);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(256);
      buf.append("CalculationTaskRequirements.Builder{");
      buf.append("rowIndex").append('=').append(JodaBeanUtils.toString(rowIndex)).append(',').append(' ');
      buf.append("columnIndex").append('=').append(JodaBeanUtils.toString(columnIndex)).append(',').append(' ');
      buf.append("observables").append('=').append(JodaBeanUtils.toString(observables)).append(',').append(' ');
      buf.append("nonObservables").append('=').append(JodaBeanUtils.toString(nonObservables)).append(',').append(' ');
      buf.append("timeSeries").append('=').append(JodaBeanUtils.toString(timeSeries)).append(',').append(' ');
      buf.append("outputCurrencies").append('=').append(JodaBeanUtils.toString(outputCurrencies)).append(',').append(' ');
      buf.append("localScenarios").append('=').append(JodaBeanUtils.toString(localScenarios));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}