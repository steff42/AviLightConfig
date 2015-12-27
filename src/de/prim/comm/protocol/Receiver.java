package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventError;
import de.prim.comm.event.CommEventReceiver;

public class Receiver implements ProtocolHandler
{

  @Override
  public CommEvent processData( byte[] data, int size )
  {
    if (size == 2 + 2 * data[1])
    {
      return new CommEventReceiver( data );
    }
    else
    {
      return new CommEventError( "Telegram size error", data, size );
    }
  }

}
