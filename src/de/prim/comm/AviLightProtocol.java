package de.prim.comm;

public class AviLightProtocol
{
  public static final int BUFFER_SIZE = 256;

  private static final byte STX = 2;
  private static final byte ETX = 3;

  /** The received data. */
  private byte[] receivedData;

  /** The received data ptr. */
  private int receivedDataPtr;

  /**
   * Instantiates a new avi light protocol.
   */
  public AviLightProtocol()
  {
    this.receivedData = new byte[BUFFER_SIZE];
    this.receivedDataPtr = 0;
  }

  /**
   * Process input received from serial port, search the start byte and process
   * data until end byte in a loop.
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
      if (receivedDataPtr == 0)
      {
        while (pos < count && buffer[pos] != STX)
        {
          pos++;
        }

        if (pos < count && buffer[pos] == STX)
        {
          pos++;
        }
      }

      while (pos < count && buffer[pos] != ETX)
      {
        if (receivedDataPtr >= receivedData.length)
        {
          // Overflow
          receivedDataPtr = 0;
          return;
        }
        receivedData[receivedDataPtr++] = buffer[pos++];
      }

      if (pos < count && buffer[pos] == ETX)
      {
        processInputData();
      }
    }
  }

  private void processInputData()
  {
    // TODO Auto-generated method stub

  }
}
