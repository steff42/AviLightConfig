package de.prim.comm.protocol;

import java.io.IOException;
import java.io.OutputStream;

public class TelegramOutputStream implements TelegramSender
{
  private OutputStream outputStream;

  public TelegramOutputStream(OutputStream outputStream)
  {
    super();
    this.outputStream = outputStream;
  }

  @Override
  public void sendByte(byte b) throws IOException
  {
    //System.out.print( HexUtils.toHex( b ) + " " );
    
    outputStream.write( b & 0xff );
    outputStream.flush();
    try
    {
      Thread.sleep( 25 );
    }
    catch (InterruptedException e)
    {
    }
  }

}
