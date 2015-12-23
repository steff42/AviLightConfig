package de.prim.comm.processor;

import java.io.IOException;

/**
 * The Interface ByteProcessor processes single bytes, typically for
 * sending.
 */
public interface ByteProcessor
{

  /**
   * Process a single byte.
   * 
   * @param b
   *          the b
   * @throws IOException 
   */
  void processByte(byte b) throws IOException;
}
