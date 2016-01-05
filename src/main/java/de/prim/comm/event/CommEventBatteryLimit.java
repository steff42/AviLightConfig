package de.prim.comm.event;

import java.math.BigDecimal;

import de.prim.avilight.utils.Constants;

public class CommEventBatteryLimit extends CommEvent
{
  /** the voltage limit */
  private int limit;

  /**
   * Instantiates a new comm event voltage.
   *
   * @param buffer
   *          the buffer
   * @param size
   */
  public CommEventBatteryLimit( byte[] buffer, int size )
  {
    super( buffer[0] );
    limit = ( 0xff & buffer[1] ) | ( ( 0xff & buffer[2] ) << 8 );
  }

  /**
   * Gets the limit.
   *
   * @return the limit
   */
  public BigDecimal getLimit()
  {
    return new BigDecimal( limit ).divide( new BigDecimal( "36" ), Constants.ROUND_HALF_UP_PRE_2 );
  }
}
