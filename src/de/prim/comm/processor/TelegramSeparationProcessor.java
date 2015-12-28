package de.prim.comm.processor;

import de.prim.avilight.utils.HexUtils;
import de.prim.comm.utils.CommUtils;

/**
 * The Class TelegramSeparationProcessor separates received data in telegrams
 */
public class TelegramSeparationProcessor implements DataProcessor
{
  /** The last received byte was Escape, check if double. */
  protected boolean     lastEscape;

  /** The stx has already been received. */
  protected boolean     stxReceived;

  /** The received data. */
  protected byte[]      receivedData;

  /** The received data ptr. */
  protected int         receivedDataPtr;

  /** The next processor. */
  private DataProcessor next;

  /**
   * Instantiates a new telegram separation processor.
   *
   * @param next
   *          the next
   * @param bufferSize
   *          the buffer size
   */
  public TelegramSeparationProcessor( DataProcessor next, int bufferSize )
  {
    super();
    this.next = next;
    receivedData = new byte[bufferSize];
  }

  /* (non-Javadoc) */
  @Override
  public void processData( byte[] buffer, int count )
  {
    // System.out.println( "received: " + HexUtils.toHex( buffer, count ));

    int pos = 0;
    while ( pos < count )
    {
      if ( !lastEscape && !stxReceived )
      {
        int startPos = pos;
        while (pos < count && buffer[pos] != CommUtils.STX)
        {
          pos++;
        }

        if ( pos > startPos )
        {
          System.out
              .println( "Bytes skipped: " + HexUtils.toHex( buffer, startPos, pos - startPos ) );
        }
        if (pos < count && buffer[pos] == CommUtils.STX)
        {
          pos++;
          stxReceived = true;
        }
      }

      if (pos < count && lastEscape)
      {
        receivedData[receivedDataPtr++] = buffer[pos++];
        lastEscape = false;
      }

      while (pos < count && buffer[pos] != CommUtils.ETX)
      {
        if ( receivedDataPtr >= receivedData.length )
        {
          // Overflow
          // bufferOverflow();

          System.out.println( "Overflow!!" );
          receivedDataPtr = 0;
          lastEscape = false;
          stxReceived = false;
          return;
        }

        if ( buffer[pos] == CommUtils.ESCAPE )
        {
          if (pos + 1 < count)
          {
            pos++;
            receivedData[receivedDataPtr++] = buffer[pos++];
          }
          else
          {
            lastEscape = true;
            pos++;
            break;
          }
        }
        else
        {
          receivedData[receivedDataPtr++] = buffer[pos++];
        }
      }

      if (!lastEscape && pos < count && buffer[pos] == CommUtils.ETX)
      {
        next.processData( receivedData, receivedDataPtr );

        receivedDataPtr = 0;
        lastEscape = false;
        stxReceived = false;
        pos++;
      }
    }
  }

}
