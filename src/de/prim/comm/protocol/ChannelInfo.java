package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventChannelInfo;
import de.prim.comm.event.CommEventError;

public class ChannelInfo implements ProtocolHandler
{

  /*
   * (non-Javadoc)
   * 
   * @see de.prim.comm.protocol.ProtocolHandler#processData(byte[], int)
   */
  @Override
  public CommEvent processData(byte[] data, int size)
  {
    if (size >= 4)
    {
      return new CommEventChannelInfo( data, size );
    }
    else
    {
      return new CommEventError( "to less data for ChannelInfo: " + size, data,
          size );
    }
  }

}
