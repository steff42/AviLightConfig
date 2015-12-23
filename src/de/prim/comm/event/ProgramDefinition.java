package de.prim.comm.event;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import de.prim.avilight.Constants;
import de.prim.avilight.gui.dlg.FlashingModeComboBoxModel;
import de.prim.comm.Command.Command;
import de.prim.comm.Command.SetProgram;
import de.prim.comm.data.Modifiable;

/**
 * The Class ProgramDefinition.
 */
public class ProgramDefinition implements Modifiable
{

  /** The channel. */
  @XmlAttribute
  private byte channel;

  /** The segment. */
  @XmlAttribute
  private byte segment;

  /** The algorithm. */
  @XmlAttribute
  private byte algorithm;

  /** The period. */
  @XmlAttribute
  private int period;

  /** The flash. */
  @XmlAttribute
  private byte flash;

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
  public ProgramDefinition(byte channel, byte segment, byte algorithm,
      int period, byte flash)
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
  public void setAlgorithm(int algorithm)
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
  public void setPeriod(int period)
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
  public void setFlash(byte flash)
  {
    this.flash = flash;
  }

  /**
   * Gets the flashes.
   * 
   * @return the flashes
   */
  @XmlTransient
  public byte getFlashes()
  {
    return (byte) (flash & 3);
  }

  /**
   * Sets the flashes.
   * 
   * @param flashes
   *          the new flashes
   */
  public void setFlashes(int flashes)
  {
    flash = (byte) ((flash & 0xfc) | (flashes & 3));
  }

  /**
   * Gets the flash duration.
   * 
   * @return the flash duration
   */
  @XmlTransient
  public double getFlashDuration()
  {
    return (flash >> 2) * Constants.TICK_TIME;
  }

  /**
   * Sets the flash duration.
   * 
   * @param duration
   *          the new flash duration
   */
  public void setFlashDuration(double duration)
  {
    flash = (byte) ((0xfc & (((int) (duration / Constants.TICK_TIME)) << 2)) | (flash & 3));
  }

  /**
   * Gets the duration.
   * 
   * @return the duration
   */
  @XmlTransient
  public double getDuration()
  {
    return period * Constants.TICK_TIME;
  }

  /**
   * Sets the duration.
   * 
   * @param d
   *          the new duration
   */
  public void setDuration(double d)
  {
    period = 0xffff & (int) (d / Constants.TICK_TIME);
  }

  /**
   * Gets the command.
   * 
   * @return the command
   */
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

    if (algorithm >= 0
        && algorithm < FlashingModeComboBoxModel.CHANNEL_MODES.length)
    {
      sb.append( FlashingModeComboBoxModel.CHANNEL_MODES[algorithm] );
      if (FlashingModeComboBoxModel.HAS_NUMBER_OF[algorithm])
      {
        sb.append( ' ' );
        sb.append( 1 + getFlashes() );
        sb.append( 'x' );
      }

      if (FlashingModeComboBoxModel.HAS_DURATION[algorithm])
      {
        sb.append( ' ' );
        sb.append( Constants.NUMBER_FORMAT.format( getFlashDuration() ) );
        sb.append( "s On" );
      }

      if (FlashingModeComboBoxModel.HAS_OFF_DURATION[algorithm])
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
