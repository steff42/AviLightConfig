package de.prim.comm.Command;

import java.io.IOException;

import de.prim.avilight.utils.HexUtils;
import de.prim.comm.processor.TelegramEscapeByteProcessor;
import de.prim.comm.protocol.TelegramSeparator;

public class MultiByteCommand extends Command
{

  /** The data. */
  protected byte[] data;

  /**
   * Instantiates a new multi byte command.
   * 
   * @param cmd
   *          the cmd
   * @param data
   *          the data
   */
  public MultiByteCommand(byte cmd, byte[] data)
  {
    this( cmd );
    this.data = data;
  }

  protected MultiByteCommand(byte cmd)
  {
    super( cmd );
  }

  /**
   * Gets the data.
   * 
   * @return the data
   */
  public byte[] getData()
  {
    return data;
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
    for (byte b : data)
    {
      byteProcessor.processByte( b );
    }
    byteProcessor.sendETX();
  }

  @Override
  public String toString()
  {
    return super.toString() + " [" + data.length + "] " + HexUtils.toHex( data );
  }
}
