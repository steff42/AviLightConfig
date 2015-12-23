package de.prim.comm.protocol;

import java.io.IOException;

public interface TelegramSender
{
  
  /**
   * Send a byte.
   *
   * @param b the b
   * @throws IOException 
   */
  void sendByte( byte b ) throws IOException;
}
