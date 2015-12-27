package de.prim.comm.event;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import de.prim.avilight.Constants;
import de.prim.avilight.gui.dlg.FlashingModeComboBoxModel;
import de.prim.avilight.gui.dlg.lightmode.LightMode;
import de.prim.comm.command.Command;
import de.prim.comm.command.SetProgram;
import de.prim.comm.data.Modifiable;

/**
 * The Class ProgramDefinition.
 */
public class ProgramDefinition implements Modifiable
{

  /** The channel. */
  @XmlAttribute
  private byte       channel;

  /** The segment. */
  @XmlAttribute
  private byte       segment;

  /** The algorithm. */
  @XmlAttribute
  private byte       algorithm;

  /** The period. */
  @XmlAttribute
  private int        period;

  /** The flash. */
  @XmlAttribute
  private byte       flash;

  @XmlTransient
  private static int FLASH_COUNT_BITMASK    = 0b00000011;

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
   * @param algorithm
   *          the algorithm
   * @param period
   *          the period
   * @param flash
   *          the flash
   */
  public ProgramDefinition( byte channel, byte segment, byte algorithm, int period, byte flash )
  {
    super();
    this.channel = channel;
    this.segment = segment;
    this.algorithm = algorithm;
    this.period = period;
    this.flash = flash;
  }

  /**
   * Gets the algorithm.
   *
   * @return the algorithm
   */
  @XmlTransient
  public byte getAlgorithm()
  {
    return algorithm;
  }

  /**
   * Sets the algorithm.
   *
   * @param algorithm
   *          the new algorithm
   */
  public void setAlgorithm( int algorithm )
  {
    this.algorithm = (byte) algorithm;
  }

  /**
   * Gets the period.
   *
   * @return the period
   */
  @XmlTransient
  public int getPeriod()
  {
    return period;
  }

  /**
   * Sets the period.
   *
   * @param period
   *          the new period
   */
  public void setPeriod( int period )
  {
    this.period = period;
  }

  /**
   * Gets the flash.
   *
   * @return the flash
   */
  @XmlTransient
  public byte getFlash()
  {
    return flash;
  }

  /**
   * Sets the flash.
   *
   * @param flash
   *          the new flash
   */
  public void setFlash( byte flash )
  {
    this.flash = flash;
  }

  /**
   * Gets the number of the flashes.
   *
   * @return the flashes
   */
  @XmlTransient
  public byte getNumberOfFlashes()
  {
    // Add one because counting starts at 0 but humans want to read out 1.
    return (byte) ( ( flash & FLASH_COUNT_BITMASK ) + 1 );
  }

  /**
   * Sets the number of the flashes.
   *
   * @param numberOfFlashes
   *          the new flashes
   */
  public void setNumberOfFlashes( int numberOfFlashes )
  {
    // This is done because we always start counting with 0 to save space
    int shiftedFlashes = numberOfFlashes - 1;
    flash = (byte) ( ( flash & FLASH_DURATION_BITMASK ) | ( shiftedFlashes & FLASH_COUNT_BITMASK ) );

  }

  /**
   * Gets the flash duration.
   *
   * @return the flash duration
   */
  @XmlTransient
  public BigDecimal getFlashDuration()
  {
    BigDecimal toConvert = new BigDecimal( Byte.toUnsignedInt( flash ) >> 2 );
    return Constants.TICK_TIME.multiply( toConvert, Constants.ROUND_HALF_UP_PRE_2 );
  }

  /**
   * Sets the flash duration.
   *
   * @param duration
   *          the new flash duration
   */
  public void setFlashDuration( BigDecimal duration )
  {
    if ( duration != null )
    {
      int durationInTicks = duration.divide( Constants.TICK_TIME, Constants.ROUND_HALF_UP_PRE_0 )
          .intValue();
      flash = (byte) ( ( FLASH_DURATION_BITMASK & ( durationInTicks << 2 ) ) | ( flash & FLASH_COUNT_BITMASK ) );
    }
    else
    {
      flash = (byte) ( flash & ~FLASH_COUNT_BITMASK );
    }
  }

  /**
   * Gets the duration.
   *
   * @return the duration
   */
  @XmlTransient
  public BigDecimal getDuration()
  {
    return Constants.TICK_TIME.multiply( new BigDecimal( period ) );
  }

  /**
   * Sets the duration.
   *
   * @param duration
   *          the new duration
   */
  public void setDuration( BigDecimal duration )
  {
    if ( duration != null )
    {
      BigDecimal converted = duration.divide( Constants.TICK_TIME, Constants.ROUND_HALF_UP_PRE_0 );
      period = 0xFFFF & converted.intValue();
    }
    else
    {
      period = 0;
    }
  }

  /**
   * Gets the command.
   *
   * @return the command
   */
  @Override
  @XmlTransient
  public Command getCommand()
  {
    return new SetProgram( channel, segment, algorithm, period, flash );
  }

  /* (non-Javadoc) */
  @Override
  public String toString()
  {
    StringBuffer sb = new StringBuffer();

    if ( ( algorithm >= 0 ) && ( algorithm < FlashingModeComboBoxModel.CHANNEL_MODES.length ) )
    {
      LightMode lightMode = FlashingModeComboBoxModel.CHANNEL_MODES[algorithm];

      sb.append( lightMode );
      if ( lightMode.hasNumberOfFlashes() )
      {
        sb.append( ' ' );
        sb.append( getNumberOfFlashes() );
        sb.append( 'x' );
      }

      if ( lightMode.hasDuration() )
      {
        sb.append( ' ' );
        if ( lightMode.hasOffDuration() )
        {
          sb.append( Constants.NUMBER_FORMAT.format( getFlashDuration() ) );
        }
        else
        {
          sb.append( Constants.NUMBER_FORMAT.format( getDuration() ) );
        }
        sb.append( "s On" );
      }

      if ( lightMode.hasOffDuration() )
      {
        sb.append( ' ' );
        sb.append( Constants.NUMBER_FORMAT.format( getDuration() ) );
        sb.append( "s Off" );
      }
    }
    else
    {
      sb.append( "Undef Mode: " );
      sb.append( algorithm );
    }

    return sb.toString();
  }

}
