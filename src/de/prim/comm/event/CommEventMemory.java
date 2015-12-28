package de.prim.comm.event;

import de.prim.avilight.utils.HexUtils;

/**
 * The Class CommEventMemory.
 */
public class CommEventMemory extends CommEvent
{
  private int  address;

  private byte memory[];

  /**
   * Instantiates a new comm event memory.
   *
   * @param data
   *          the data
   */
  public CommEventMemory( byte[] data )
  {
    super( data[0] );

    address = ( 0xff & data[1] ) + ( ( 0xff & data[2] ) << 8 );

    memory = new byte[128];
    System.arraycopy( data, 3, memory, 0, memory.length );
  }

  /**
   * Gets the address.
   *
   * @return the address
   */
  public int getAddress()
  {
    return address;
  }

  /**
   * Gets the memory.
   *
   * @return the memory
   */
  public byte[] getMemory()
  {
    return memory;
  }

  @Override
  public String toString()
  {
    return "Memory " + HexUtils.toHex16( address ) + ": " + HexUtils.toHex( memory );
  }

}
