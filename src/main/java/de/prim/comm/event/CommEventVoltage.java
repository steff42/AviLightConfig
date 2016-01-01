package de.prim.comm.event;

import java.math.BigDecimal;

import de.prim.avilight.Constants;

public class CommEventVoltage extends CommEvent
{

  /** The voltage. */
  private int voltage;

  /** the voltage limit */
  private int limit;

  /**
   * Instantiates a new comm event voltage.
   *
   * @param buffer
   *          the buffer
   * @param size
   */
  public CommEventVoltage( byte[] buffer, int size )
  {
    super( buffer[0] );

    voltage = ( 0xff & buffer[1] ) | ( ( 0xff & buffer[2] ) << 8 );
    if ( size > 3 )
    {
      limit = ( 0xff & buffer[3] ) | ( ( 0xff & buffer[4] ) << 8 );
    }
  }

  /**
   * Gets the adc value.
   *
   * @return the adc value
   */
  public int getAdcValue()
  {
    return voltage;
  }

  /**
   * Gets the voltage.
   *
   * @return the voltage
   */
  public BigDecimal getVoltage()
  {
    return new BigDecimal( voltage ).divide( new BigDecimal( "36" ),
        Constants.ROUND_HALF_UP_PRE_2 );
  }

  public BigDecimal getLimit()
  {
    return new BigDecimal( limit ).divide( new BigDecimal( "36" ), Constants.ROUND_HALF_UP_PRE_2 );
  }
}
