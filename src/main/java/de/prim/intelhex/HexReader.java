package de.prim.intelhex;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

/**
 * The Class HexReader.
 */
public class HexReader
{

  /** The address offset. */
  private int              offset;

  /** The line number reader. */
  private LineNumberReader lineNumberReader;

  /** The current hex line. */
  private HexLineIterator  hexLineIterator;

  /** The memory. */
  private Memory           memory;

  /**
   * Instantiates a new hex reader.
   *
   * @param memory
   *          the memory
   */
  public HexReader( Memory memory )
  {
    super();

    this.memory = memory;
  }

  /**
   * Read hex.
   *
   * @param in
   *          the in
   * @param memory
   *          the memory
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void readHex( Reader in ) throws IOException
  {
    offset = 0;

    lineNumberReader = new LineNumberReader( in );

    String line;
    while ( ( line = lineNumberReader.readLine() ) != null )
    {
      if (line.length() > 0 && line.charAt( 0 ) == ':')
      {
        if ( ( ( line.length() - 1 ) & 1 ) == 0 )
        {
          if ( readLine( line ) )
          {
            break;
          }
        }
        else
        {
          throw new IOException( "Line [" + lineNumberReader.getLineNumber() + "] length mismatch" );
        }
      }
      else
      {
        throw new IOException( "Line [" + lineNumberReader.getLineNumber()
            + "] does not start with ':'" );
      }
    }
  }

  /**
   * checks, if next byte is available.
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void nextByteAvailable() throws IOException
  {
    if ( !hexLineIterator.hasNext() )
    {
      throw new IOException( "Line to short: " + lineNumberReader.getLineNumber() );
    }
  }

  /**
   * Reads a single line.
   *
   * @param line
   *          the line
   * @return true, if end of line record processed
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private boolean readLine( String line ) throws IOException
  {
    hexLineIterator = new HexLineIterator( line, 1 );
    nextByteAvailable();
    byte reclen = hexLineIterator.next();

    nextByteAvailable();
    int addr = 0xff00 & ( hexLineIterator.next() << 8 );
    nextByteAvailable();
    addr |= 0x00ff & hexLineIterator.next();

    nextByteAvailable();
    byte rectype = hexLineIterator.next();

    switch ( rectype )
    {
      case 0:
        dataRecord( reclen, addr );
        return false;
      case 1:
        endOfLineRecord();
        return true;
      case 2:
        extendedSegment();
        return false;

      default:
        throw new IOException( "unsupported rec type: " + rectype + " in line "
            + lineNumberReader.getLineNumber() );
    }
  }

  private void extendedSegment() throws IOException
  {
    int segment = 0xff00 & ( hexLineIterator.next() << 8 );
    nextByteAvailable();
    segment |= 0x00ff & hexLineIterator.next();

    offset = segment << 4;

    testChecksum();
  }

  /**
   * End of line record.
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void endOfLineRecord() throws IOException
  {
    testChecksum();
  }

  /**
   * Processes the data record.
   *
   * @param reclen
   *          the reclen
   * @param addr
   *          the addr
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void dataRecord( byte reclen, int addr ) throws IOException
  {
    byte data[] = new byte[reclen];
    for ( int i = 0; i < reclen; i++ )
    {
      nextByteAvailable();

      data[i] = hexLineIterator.next();
    }

    for ( int i = 0; i < reclen; i++ )
    {
      memory.set( addr + offset + i, data[i] );
    }

    testChecksum();
  }

  /**
   * Test checksum.
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void testChecksum() throws IOException
  {
    byte checksum = (byte) ( 0xff & -hexLineIterator.getSum() );

    nextByteAvailable();
    if ( checksum != hexLineIterator.next() )
    {
      throw new IOException( "Checksum error: " + lineNumberReader.getLineNumber() );
    }

    if ( hexLineIterator.hasNext() )
    {
      throw new IOException( "Line to long: " + lineNumberReader.getLineNumber() );
    }
  }

  /**
   * Gets the actual offset.
   *
   * @return the actual offset
   */
  public int getActualOffset()
  {
    return offset;
  }

}
