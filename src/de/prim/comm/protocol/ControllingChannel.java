package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventControllingChannel;
import de.prim.comm.event.CommEventError;

public class ControllingChannel implements ProtocolHandler
{

  @Override
  public CommEvent processData(byte[] data, int size)
  {
    if (size == 2 + data[1])
    {
      return new CommEventControllingChannel( data );
    }
    else
    {
      return new CommEventError( "Telegram size error", data, size );
    }
  }

}
