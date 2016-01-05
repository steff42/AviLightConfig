package de.prim.avilight.model.lightmodes;

import java.math.BigDecimal;

/**
 * The Class PulseLightModeImpl.
 *
 * @author Steff Lukas
 */
public class PulseLightModeImpl implements LightMode
{
  private final String     modeName    = "Puls";

  private final BigDecimal minDuration = new BigDecimal( "0.01" );
  private final BigDecimal maxDuration = new BigDecimal( "500.00" );

  /** {@inheritDoc} */
  @Override
  public String getModeName()
  {
    return modeName;
  }

  /** {@inheritDoc} */
  @Override
  public BigDecimal getMinDuration()
  {
    return minDuration;
  }

  /** {@inheritDoc} */
  @Override
  public BigDecimal getMaxDuration()
  {
    return maxDuration;
  }

  /** {@inheritDoc} */
  @Override
  public String validateDuration( BigDecimal duration )
  {
    String error = null;
    if ( duration == null || minDuration.compareTo( duration ) > 0
        || maxDuration.compareTo( duration ) < 0 )
    {
      error = "Die Dauer muss zwischen 0,01 s und 500 s liegen!";
    }

    return error;
  }

  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return modeName;
  }

}
