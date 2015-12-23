package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventError;
import de.prim.comm.event.CommEventMemory;

public class ReadPage implements ProtocolHandler
{

  @Override
  public CommEvent processData(byte[] data, int size)
  {
    if (size == 128 + 3)
    {
      return new CommEventMemory( data );
    }
    else
    {
      return new CommEventError( "Telegram size error", data, size );
    }
  }

}
