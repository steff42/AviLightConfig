package de.prim.avilight.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import de.prim.avilight.model.lightmodes.LightMode;
import de.prim.avilight.utils.Constants;

/**
 * The Class ProgramDefinition.
 */
public class ProgramDefinition
{

  /** The channel. */
  @XmlAttribute
  private byte       channel;

  /** The segment. */
  @XmlAttribute
  private byte       segment;

  /** The light mode. */
  @XmlTransient
  private LightMode  lightMode;

  /** The periodDuration. */
  @XmlTransient
  private BigDecimal periodDuration;

  /** The flash. */
  @XmlTransient
  private BigDecimal flashDuration;

  /** The flash count. */
  @XmlTransient
  private int        flashCount;

  /** The flash count bitmask. */
  @XmlTransient
  private static int FLASH_COUNT_BITMASK    = 0b00000011;

  /** The flash duration bitmask. */
  @XmlTransient
  private static int FLASH_DURATION_BITMASK = 0b11111100;

  /**
   * Instantiates a new program definition. Constructor for Jaxb.
   */
  public ProgramDefinition()
  {
    super();
  }

  /**
   * Instantiates a new program definition.
   *
   * @param channel
   *          the channel
   * @param segment
   *          the segment
   * @param algorithmId
   *          the algorithm id
   * @param period
   *          the period
   * @param flash
   *          the flash
   */
  public ProgramDefinition( byte channel, byte segment, byte algorithmId, int period, byte flash )
  {
    super();
    this.channel = channel;
    this.segment = segment;
    this.lightMode = LightModeAlgorithms.getLightModeFromAlgorithmId( algorithmId );
    this.periodDuration = convertFromRawPeriod( period );
    this.flashDuration = convertRawFlashToFlashDuration( flash );
    this.flashCount = convertRawFlashToFlashCount( flash );
  }

  /**
   * Instantiates a new program definition.
   *
   * @param channel
   *          the channel
   * @param segment
   *          the segment
   * @param lightMode
   *          the light mode
   * @param periodDuration
   *          the period duration
   * @param flashDuration
   *          the flash duration
   * @param flashCount
   *          the flash count
   */
  public ProgramDefinition( byte channel, byte segment, LightMode lightMode,
      BigDecimal periodDuration, BigDecimal flashDuration, int flashCount )
  {
    super();
    this.channel = channel;
    this.segment = segment;
    this.lightMode = lightMode;
    this.periodDuration = periodDuration;
    this.flashDuration = flashDuration;
    this.flashCount = flashCount;

  }

  /**
   * Gets the channel.
   *
   * @return the channel
   */
  public byte getChannel()
  {
    return channel;
  }

  /**
   * Gets the segment.
   *
   * @return the segment
   */
  public byte getSegment()
  {
    return segment;
  }

  /**
   * Gets the light mode.
   *
   * @return the light mode
   */
  public LightMode getLightMode()
  {
    return lightMode;
  }

  /**
   * Gets the flash count.
   *
   * @return the flash count
   */
  public int getFlashCount()
  {
    return flashCount;
  }

  /**
   * Gets the flash duration.
   *
   * @return the flash duration
   */
  public BigDecimal getFlashDuration()
  {
    return flashDuration;
  }

  /**
   * Gets the period.
   *
   * @return the period
   */
  public BigDecimal getPeriodDuration()
  {
    return periodDuration;
  }

  /**
   * Gets the algorithm.
   *
   * @return the algorithm
   */
  @XmlAttribute
  public byte getAlgorithm()
  {
    return LightModeAlgorithms.getAlgorithmIdFromLightMode( lightMode );
  }

  /**
   * Sets the algorithm.
   *
   * @param algorithm
   *          the new algorithm
   */
  protected void setAlgorithm( byte algorithm )
  {
    lightMode = LightModeAlgorithms.getLightModeFromAlgorithmId( algorithm );
  }

  /**
   * Gets the period as raw value.
   *
   * @return the period
   */
  @XmlAttribute
  public int getPeriod()
  {
    return convertToRawPeriod();
  }

  /**
   * Sets the period.
   *
   * @param period
   *          the new period
   */
  protected void setPeriod( int period )
  {
    this.periodDuration = convertFromRawPeriod( period );
  }

  /**
   * Gets the flash.
   *
   * @return the flash
   */
  @XmlAttribute
  public byte getFlash()
  {
    return convertToRawFlash();
  }

  /**
   * Sets the flash.
   *
   * @param flash
   *          the new flash
   */
  protected void setFlash( byte flash )
  {
    this.flashDuration = convertRawFlashToFlashDuration( flash );
    this.flashCount = convertRawFlashToFlashCount( flash );
  }

  /**
   * Gets the number of the flashes.
   *
   * @param flash
   *          the flash
   * @return the flashes
   */
  private byte convertRawFlashToFlashCount( byte flash )
  {
    // Add one because counting starts at 0 but humans want to read out 1.
    return (byte) ( ( flash & FLASH_COUNT_BITMASK ) + 1 );
  }

  /**
   * Convert raw flash to flash duration.
   *
   * @param flash
   *          the flash
   * @return the big decimal
   */
  private BigDecimal convertRawFlashToFlashDuration( byte flash )
  {
    BigDecimal toConvert = new BigDecimal( Byte.toUnsignedInt( flash ) >> 2 );
    return Constants.TICK_TIME.multiply( toConvert, Constants.ROUND_HALF_UP_PRE_2 );
  }

  /**
   * Sets the number of the flashes.
   *
   * @return the byte
   */
  private byte convertToRawFlash()
  {
    // This is done because we always start counting with 0 to save space
    int shiftedNumberOfFlashes = flashCount - 1;
    int durationInTicks = 0;
    if ( flashDuration != null )
    {
      durationInTicks = flashDuration.divide( Constants.TICK_TIME, Constants.ROUND_HALF_UP_PRE_0 )
          .intValue();
    }

    return (byte) ( ( FLASH_DURATION_BITMASK & ( durationInTicks << 2 ) )
        | ( shiftedNumberOfFlashes & FLASH_COUNT_BITMASK ) );
  }

  /**
   * Convert to raw period.
   *
   * @return the int
   */
  private int convertToRawPeriod()
  {
    int period = 0;
    if ( periodDuration != null )
    {
      BigDecimal converted = periodDuration.divide( Constants.TICK_TIME,
          Constants.ROUND_HALF_UP_PRE_0 );
      period = 0xFFFF & converted.intValue();
    }

    return period;
  }

  /**
   * Convert from raw period.
   *
   * @param rawPeriod
   *          the raw period
   * @return the big decimal
   */
  private BigDecimal convertFromRawPeriod( int rawPeriod )
  {
    return Constants.TICK_TIME.multiply( new BigDecimal( rawPeriod ),
        Constants.ROUND_HALF_UP_PRE_2 );
  }

  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    StringBuffer sb = new StringBuffer();

    if ( lightMode != null )
    {
      sb.append( lightMode );
      if ( lightMode.hasNumberOfFlashes() )
      {
        sb.append( ' ' );
        sb.append( flashCount );
        sb.append( 'x' );
      }

      if ( lightMode.hasDuration() )
      {
        sb.append( ' ' );
        if ( lightMode.hasOffDuration() )
        {
          sb.append( Constants.NUMBER_FORMAT.format( flashDuration ) );
        }
        else
        {
          sb.append( Constants.NUMBER_FORMAT.format( periodDuration ) );
        }
        sb.append( "s On" );
      }

      if ( lightMode.hasOffDuration() )
      {
        sb.append( ' ' );
        sb.append( Constants.NUMBER_FORMAT.format( periodDuration ) );
        sb.append( "s Off" );
      }
    }
    else
    {
      sb.append( "No Light Mode specified!" );
    }

    return sb.toString();
  }

}
