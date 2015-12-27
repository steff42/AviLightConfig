package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventError;
import de.prim.comm.event.CommEventReceiverChannelMode;

public class ReceiverChannelMode implements ProtocolHandler
{

  @Override
  public CommEvent processData( byte[] data, int size )
  {
    if (size == 2 + data[1])
    {
      return new CommEventReceiverChannelMode( data );
    }
    else
    {
      return new CommEventError( "Telegram size error", data, size );
    }
  }

}
