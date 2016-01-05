package de.prim.avilight.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public interface Constants
{
  /** The Constant APPLICATION_NAME. */
  public static final String        APPLICATION_NAME      = "AviLightConfig";

  /** The Constant MAX_RECEIVER_CHANNELS. */
  public static final int           MAX_RECEIVER_CHANNELS = 4;

  /** The Constant SEGMENT_COUNT. */
  public static final int           SEGMENT_COUNT         = 7;

  /** The Constant TICK_TIME is the time in seconds of one tick. */
  public static final BigDecimal    TICK_TIME             = new BigDecimal( "0.008192" );

  /** The Constant NUMBER_FORMAT is the decimal formatter for times. */
  public static final DecimalFormat NUMBER_FORMAT         = new DecimalFormat( "##0.00" );

  /** The Constant ROUND_HALF_UP_PRE_2 providing the standard roundig method. */
  public static final MathContext   ROUND_HALF_UP_PRE_2   = new MathContext( 2,
                                                              RoundingMode.HALF_UP );

  /**
   * The Constant ROUND_HALF_UP_PRE_0 providing a rounding method without
   * fractional digits.
   */
  public static final MathContext   ROUND_HALF_UP_PRE_0   = new MathContext( 0,
                                                              RoundingMode.HALF_UP );

  /** The Constant BUFFER_SIZE. */
  public static final int           BUFFER_SIZE           = 512;
}
