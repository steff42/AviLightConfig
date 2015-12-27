package de.prim.intelhex;

import java.util.Iterator;

import de.prim.avilight.utils.HexUtils;

public class HexLineIterator implements Iterator<Byte>, Iterable<Byte>
{

  /** The line. */
  private String line;

  /** The pos. */
  private int    pos;

  /** The sum over all bytes. */
  private int    sum;

  /**
   * Instantiates a new hex line iterator.
   *
   * @param line
   *          the line
   * @param startPos
   *          the start pos
   */
  public HexLineIterator( String line, int startPos )
  {
    super();
    sum = 0;
    this.line = line;
    this.pos = startPos;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#hasNext()
   */
  @Override
  public boolean hasNext()
  {
    return pos < line.length() - 1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#next()
   */
  @Override
  public Byte next()
  {
    byte value;
    value = (byte) ( HexUtils.parseHexChar( line.charAt( pos++ ) ) << 4 );
    value |= HexUtils.parseHexChar( line.charAt( pos++ ) );

    sum += 0xff & value;

    return value;
  }

  @Override
  public void remove()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<Byte> iterator()
  {
    return this;
  }

  /**
   * Gets the sum.
   *
   * @return the sum
   */
  public int getSum()
  {
    return sum;
  }

}
