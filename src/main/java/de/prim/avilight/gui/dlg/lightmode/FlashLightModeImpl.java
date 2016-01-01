package de.prim.avilight.gui.dlg.lightmode;

import java.math.BigDecimal;

/**
 * The Class FlashLightModeImpl.
 *
 * @author Steff Lukas
 */
public class FlashLightModeImpl implements LightMode
{

  private final String     modeName       = "Blitzen";

  private final int        minNumber      = 1;
  private final int        maxNumber      = 4;

  private final BigDecimal minDuration    = new BigDecimal( "0.01" );
  private final BigDecimal maxDuration    = new BigDecimal( "0.50" );

  private final BigDecimal minOffDuration = new BigDecimal( "0.01" );
  private final BigDecimal maxOffDuration = new BigDecimal( "500.00" );

  /** {@inheritDoc} */
  @Override
  public String getModeName()
  {
    return modeName;
  }

  /** {@inheritDoc} */
  @Override
  public int getMinNumber()
  {
    return minNumber;
  }

  /** {@inheritDoc} */
  @Override
  public int getMaxNumber()
  {
    return maxNumber;
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
    if ( minDuration.compareTo( duration ) > 0 || maxDuration.compareTo( duration ) < 0 )
    {
      error = "Die Dauer muss zwischen 0,01 s und 0,5 s liegen!";
    }

    return error;
  }

  /** {@inheritDoc} */
  @Override
  public BigDecimal getMinOffDuration()
  {
    return minOffDuration;
  }

  /** {@inheritDoc} */
  @Override
  public BigDecimal getMaxOffDuration()
  {
    return maxOffDuration;
  }

  /** {@inheritDoc} */
  @Override
  public String validateOffDuration( BigDecimal offDuration )
  {
    String error = null;
    if ( minOffDuration.compareTo( offDuration ) > 0 || maxOffDuration.compareTo( offDuration ) < 0 )
    {
      error = "Die Pause muss zwischen 0,01 s und 500 s liegen!";
    }

    return error;
  }

  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return getModeName();
  }
}
