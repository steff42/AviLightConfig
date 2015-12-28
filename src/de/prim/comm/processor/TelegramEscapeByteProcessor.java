package de.prim.comm.processor;

import java.io.IOException;

import de.prim.comm.utils.CommUtils;

/**
 * The Class TelegramEscapeByteProcessor escapes byte sequences.
 */
public class TelegramEscapeByteProcessor implements ByteProcessor
{

  /** The next. */
  private ByteProcessor next;

  // private byte debug[] = new byte[Constants.BUFFER_SIZE];
  // private int index = 0;

  /**
   * Instantiates a new telegram escape byte processor.
   *
   * @param next
   *          the next
   */
  public TelegramEscapeByteProcessor( ByteProcessor next )
  {
    super();
    this.next = next;
  }

  /**
   * Send etx without escape.
   *
   * @throws IOException
   */
  public void sendETX() throws IOException
  {
    next.processByte( CommUtils.ETX );
    // insertByte( CommUtils.ETX );

    // if (debug[1] == 0xa)
    // {
    // System.out.println( "OUT: " + HexUtils.toHex( debug, index ) );
    // }
    // index = 0;
  }

  // private void insertByte(byte b)
  // {
  // if (index < debug.length)
  // {
  // debug[index++] = b;
  // }
  // }

  /* (non-Javadoc) */
  @Override
  public void processByte( byte b ) throws IOException
  {
    if (b == CommUtils.ESCAPE || b == CommUtils.ETX)
    {
      next.processByte( CommUtils.ESCAPE );
      // insertByte( CommUtils.ESCAPE );
    }

    next.processByte( b );
    // insertByte( b );

  }

}
