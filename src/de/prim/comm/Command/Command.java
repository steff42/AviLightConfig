package de.prim.comm.Command;

import java.io.IOException;

import de.prim.avilight.utils.HexUtils;
import de.prim.comm.processor.TelegramEscapeByteProcessor;
import de.prim.comm.protocol.AviLightProtocol;
import de.prim.comm.protocol.TelegramSeparator;

/**
 * The Class Command encapsulates a command to the hardware
 */
public class Command
{
  /** The command byte. */
  private byte cmd;

  /** see {@link AviLightProtocol#CMD_PING}. */
  public static Command PING = new Command( AviLightProtocol.CMD_PING );

  /** see {@link AviLightProtocol#CMD_INFO} */
  public static Command GET_INFO = new Command( AviLightProtocol.CMD_INFO );
  /** see {@link AviLightProtocol#CMD_RECEIVER} */
  public static Command GET_RECEIVER = new Command(
      AviLightProtocol.CMD_RECEIVER );
  public static Command RECEIVER_CHANNEL_MODE = new Command(
      AviLightProtocol.CMD_GET_RECEIVER_CHANNEL_MODE );
  /** see {@link AviLightProtocol#CMD_CHANNEL_INFO} */
  public static Command CHANNEL_INFO = new Command(
      AviLightProtocol.CMD_CHANNEL_INFO );
  /** see {@link AviLightProtocol#CMD_GET_CONTROLLING_CHANNEL} */
  public static Command GET_CONTROLLING_CHANNEL = new Command(
      AviLightProtocol.CMD_GET_CONTROLLING_CHANNEL );
  public static Command VOLTAGE = new Command( AviLightProtocol.CMD_GET_VOLTAGE );
  public static Command DUMP_PROGRAM_ARRAY = new Command(
      AviLightProtocol.CMD_DUMP_PROGRAM_ARRAY );
  public static Command DUMP_EEPROM = new Command(
      AviLightProtocol.CMD_DUMP_EEPROM );
  public static Command DUMP_RECEIVER = new Command(
      AviLightProtocol.CMD_DUMP_RECEIVER );
  public static Command DUMP_CHANNELS = new Command(
      AviLightProtocol.CMD_DUMP_CHANNELS );
  public static Command DUMP_GLOBALS = new Command(
      AviLightProtocol.CMD_DUMP_GLOBALS );
  public static Command ENTER_PROGRAMMING_MODE = new Command(
      AviLightProtocol.CMD_ENTER_PROGRAMMING_MODE );
  public static Command RESET = new Command( AviLightProtocol.CMD_RESET );

  public static Command WRITE_TO_EEPROM = new Command(
      AviLightProtocol.CMD_WRITE_TO_EEPROM );
  public static Command READ_FROM_EEPROM = new Command(
      AviLightProtocol.CMD_READ_FROM_EEPROM );
  public static Command CMD_GET_LEARN_STICKMODE = new Command(
      AviLightProtocol.CMD_GET_LEARN_STICKMODE );

  /**
   * Instantiates a new command.
   * 
   * @param cmd
   *          the cmd
   */
  protected Command(byte cmd)
  {
    this.cmd = cmd;
  }

  /**
   * Gets the cmd.
   * 
   * @return the cmd
   */
  public byte getCmd()
  {
    return cmd;
  }

  /**
   * Send.
   * 
   * @param byteProcessor
   *          the byte processor
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void send(TelegramEscapeByteProcessor byteProcessor)
      throws IOException
  {
    byteProcessor.processByte( TelegramSeparator.STX );
    byteProcessor.processByte( getCmd() );
    byteProcessor.sendETX();
  }

  @Override
  public String toString()
  {
    return "Command " + HexUtils.toHex( cmd );
  }

}
