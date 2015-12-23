package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventError;
import de.prim.comm.event.CommEventPing;

public class Ping implements ProtocolHandler
{

  /*
   * (non-Javadoc)
   * 
   * @see de.prim.comm.protocol.ProtocolHandler#processData(byte[])
   */
  @Override
  public CommEvent processData(byte[] data, int size)
  {
    if (size != 2)
    {
      return new CommEventError( "To much data for ping: ", data, size );
    }
    else
    {
      return new CommEventPing( data );
    }
  }

}
