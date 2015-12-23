package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventError;
import de.prim.comm.event.CommEventMemoryWritten;

public class WritePage implements ProtocolHandler
{

  @Override
  public CommEvent processData(byte[] data, int size)
  {
    if (size >= 4)
    {
      return new CommEventMemoryWritten( data );
    }
    else
    {
      return new CommEventError( "Telegram size error", data, size );
    }
  }

}
