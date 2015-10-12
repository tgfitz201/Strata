/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.strata.basics.value;

import java.io.Serializable;
import java.util.Set;

import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.MetaBean;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.impl.light.LightMetaBean;

/**
 * A value and its derivatives.
 * <p>
 * This defines a standard way to return a value and its derivatives to certain inputs. It is in particular used 
 * as a return object for Algorithmic Differentiation versions of some functions.
 */
@BeanDefinition(style = "light")
public final class ValueDerivatives
    implements ImmutableBean, Serializable {
   
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  /**
   * The value of the variable.
   */
  @PropertyDefinition
  private final double value;
  /**
   * The derivatives of the variable with respect to some inputs.
   */
  @PropertyDefinition
  private final double[] derivatives;  


  //-------------------------------------------------------------------------
  /**
   * Obtains an instance.
   * 
   * @param value  the value
   * @param derivatives  the derivatives of the value
   * @return the object
   */
  public static ValueDerivatives of(double value, double[] derivatives) {
    return new ValueDerivatives(value, derivatives);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ValueDerivatives}.
   */
  private static MetaBean META_BEAN = LightMetaBean.of(ValueDerivatives.class);

  /**
   * The meta-bean for {@code ValueDerivatives}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return META_BEAN;
  }

  private ValueDerivatives(
      double value,
      double[] derivatives) {
    this.value = value;
    this.derivatives = (derivatives != null ? derivatives.clone() : null);
  }

  @Override
  public MetaBean metaBean() {
    return META_BEAN;
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
   * Gets the value of the variable.
   * @return the value of the property
   */
  public double getValue() {
    return value;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the derivatives of the variable with respect to some inputs.
   * @return the value of the property
   */
  public double[] getDerivatives() {
    return (derivatives != null ? derivatives.clone() : null);
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ValueDerivatives other = (ValueDerivatives) obj;
      return JodaBeanUtils.equal(getValue(), other.getValue()) &&
          JodaBeanUtils.equal(getDerivatives(), other.getDerivatives());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getValue());
    hash = hash * 31 + JodaBeanUtils.hashCode(getDerivatives());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("ValueDerivatives{");
    buf.append("value").append('=').append(getValue()).append(',').append(' ');
    buf.append("derivatives").append('=').append(JodaBeanUtils.toString(getDerivatives()));
    buf.append('}');
    return buf.toString();
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
