package de.prim.comm.protocol;

import de.prim.avilight.utils.HexUtils;

/**
 * The Class TelegramSeparator separates telegrams. Call
 * {@link #processInput(byte[], int)} on received data.
 */
public abstract class TelegramSeparator
{
  /** The Constant STX. */
  public static final byte STX = 2;

  /** The Constant ETX. */
  public static final byte ETX = 3;

  /** The Constant ESCAPE. */
  public static final byte ESCAPE = 27;

  /** The last received byte was Escape, check if double. */
  protected boolean lastEscape;

  /** The stx has already been received. */
  protected boolean stxReceived;

  /** The received data. */
  protected byte[] receivedData;

  /** The received data ptr. */
  protected int receivedDataPtr;

  /**
   * Called on Buffer overflow.
   */
  protected abstract void bufferOverflow();

  /**
   * Process the telegram.
   * 
   * @param buffer
   *          the buffer
   * @param length
   *          the length
   */
  protected abstract void processTelegram(byte[] buffer, int length);

  /**
   * Instantiates a new telegram separator.
   * 
   * @param bufferSize
   *          the buffer size
   */
  public TelegramSeparator(int bufferSize)
  {
    receivedData = new byte[bufferSize];
  }

  /**
   * Process received input. Separates the input into telegrams.
   * 
   * @param buffer
   *          the buffer
   * @param count
   *          the count
   */
  public void processInput(byte[] buffer, int count)
  {
    int pos = 0;
    while (pos < count)
    {
      if (!lastEscape && !stxReceived)
      {
        int startPos = pos;
        while (pos < count && buffer[pos] != STX)
        {
          pos++;
        }

        if (pos > startPos)
        {
          System.out.println( "Bytes skipped: "
              + HexUtils.toHex( buffer, startPos, pos - startPos ) );
        }
        if (pos < count && buffer[pos] == STX)
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

      while (pos < count && buffer[pos] != ETX)
      {
        if (receivedDataPtr >= receivedData.length)
        {
          // Overflow
          bufferOverflow();

          receivedDataPtr = 0;
          lastEscape = false;
          stxReceived = false;
          return;
        }

        if (buffer[pos] == ESCAPE)
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

      if (!lastEscape && pos < count && buffer[pos] == ETX)
      {
        processTelegram( receivedData, receivedDataPtr );

        receivedDataPtr = 0;
        lastEscape = false;
        stxReceived = false;
        pos++;
      }
    }
  }
}
