package de.prim.comm.event;

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
  public CommEventVoltage(byte[] buffer, int size)
  {
    super( buffer[0] );

    voltage = (0xff & buffer[1]) | ((0xff & buffer[2]) << 8);
    if (size > 3)
    {
      limit = (0xff & buffer[3]) | ((0xff & buffer[4]) << 8);
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
  public double getVoltage()
  {
    return voltage / 36.0;
  }

  public double getLimit()
  {
    return limit / 36.0;
  }

}
