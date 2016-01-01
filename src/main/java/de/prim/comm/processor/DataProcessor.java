package de.prim.comm.processor;

/**
 * The Interface DataProcessor defines a data processor
 */
public interface DataProcessor
{

  /**
   * process the data.
   *
   * @param buffer
   *          the buffer
   * @param length
   *          the length
   */
  void processData( byte[] buffer, int length );
}
