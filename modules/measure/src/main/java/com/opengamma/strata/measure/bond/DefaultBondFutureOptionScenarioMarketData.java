/**
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.measure.bond;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableConstructor;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.light.LightMetaBean;

import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.data.scenario.ScenarioMarketData;

/**
 * The default market data for bond future options, used for calculation across multiple scenarios.
 * <p>
 * This uses a {@link BondFutureOptionMarketDataLookup} to provide a view on {@link ScenarioMarketData}.
 */
@BeanDefinition(style = "light")
final class DefaultBondFutureOptionScenarioMarketData
    implements BondFutureOptionScenarioMarketData, ImmutableBean, Serializable {

  /**
   * The lookup.
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final BondFutureOptionMarketDataLookup lookup;
  /**
   * The market data.
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final ScenarioMarketData marketData;
  /**
   * The cache of single scenario instances.
   */
  private final AtomicReferenceArray<BondFutureOptionMarketData> cache;  // derived

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance based on a lookup and market data.
   * <p>
   * The lookup knows how to obtain the volatilities from the market data.
   * This might involve accessing a surface or a cube.
   *
   * @param lookup  the lookup
   * @param marketData  the market data
   * @return the rates market view
   */
  public static DefaultBondFutureOptionScenarioMarketData of(
      BondFutureOptionMarketDataLookup lookup,
      ScenarioMarketData marketData) {

    return new DefaultBondFutureOptionScenarioMarketData(lookup, marketData);
  }

  @ImmutableConstructor
  private DefaultBondFutureOptionScenarioMarketData(
      BondFutureOptionMarketDataLookup lookup,
      ScenarioMarketData marketData) {

    this.lookup = ArgChecker.notNull(lookup, "lookup");
    this.marketData = ArgChecker.notNull(marketData, "marketData");
    this.cache = new AtomicReferenceArray<>(marketData.getScenarioCount());
  }

  //-------------------------------------------------------------------------
  @Override
  public BondFutureOptionScenarioMarketData withMarketData(ScenarioMarketData marketData) {
    return DefaultBondFutureOptionScenarioMarketData.of(lookup, marketData);
  }

  //-------------------------------------------------------------------------
  @Override
  public int getScenarioCount() {
    return marketData.getScenarioCount();
  }

  @Override
  public BondFutureOptionMarketData scenario(int scenarioIndex) {
    BondFutureOptionMarketData current = cache.get(scenarioIndex);
    if (current != null) {
      return current;
    }
    return cache.updateAndGet(
        scenarioIndex,
        v -> v != null ? v : lookup.marketDataView(marketData.scenario(scenarioIndex)));
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code DefaultBondFutureOptionScenarioMarketData}.
   */
  private static MetaBean META_BEAN = LightMetaBean.of(DefaultBondFutureOptionScenarioMarketData.class);

  /**
   * The meta-bean for {@code DefaultBondFutureOptionScenarioMarketData}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return META_BEAN;
  }

  static {
    JodaBeanUtils.registerMetaBean(META_BEAN);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

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
   * Gets the lookup.
   * @return the value of the property, not null
   */
  @Override
  public BondFutureOptionMarketDataLookup getLookup() {
    return lookup;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the market data.
   * @return the value of the property, not null
   */
  @Override
  public ScenarioMarketData getMarketData() {
    return marketData;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      DefaultBondFutureOptionScenarioMarketData other = (DefaultBondFutureOptionScenarioMarketData) obj;
      return JodaBeanUtils.equal(lookup, other.lookup) &&
          JodaBeanUtils.equal(marketData, other.marketData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(lookup);
    hash = hash * 31 + JodaBeanUtils.hashCode(marketData);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("DefaultBondFutureOptionScenarioMarketData{");
    buf.append("lookup").append('=').append(lookup).append(',').append(' ');
    buf.append("marketData").append('=').append(JodaBeanUtils.toString(marketData));
    buf.append('}');
    return buf.toString();
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
