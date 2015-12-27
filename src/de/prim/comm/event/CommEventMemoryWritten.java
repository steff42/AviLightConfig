package de.prim.comm.event;

public class CommEventMemoryWritten extends CommEvent
{
  private int  address;
  private byte status;

  public CommEventMemoryWritten( byte[] data )
  {
    super( data[0] );

    address = ( 0xff & data[1] ) + ( ( 0xff & data[2] ) << 8 );
    status = data[3];

    // System.out.println( "ROM: " + HexUtils.toHex( data[4] ) + " PROG: "
    // + HexUtils.toHex( data[5] ) + " ADDR: " + HexUtils.toHex( data[7] )
    // + HexUtils.toHex( data[6] ) );
  }

  public int getAddress()
  {
    return address;
  }

  public byte getStatus()
  {
    return status;
  }

  @Override
  public String toString()
  {
    return "CommEventMemoryWritten [address=" + address + ", status=" + status + "]";
  }

}
