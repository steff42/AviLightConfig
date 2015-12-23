package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventEnterProgrammingMode;
import de.prim.comm.event.CommEventError;

public class EnterProgrammingMode implements ProtocolHandler
{

  @Override
  public CommEvent processData(byte[] data, int size)
  {
    if (size != 1)
    {
      return new CommEventError( "To much data for ping", data, size );
    }
    else
    {
      return new CommEventEnterProgrammingMode( data[0] );
    }
  }

}
