package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventError;
import de.prim.comm.event.CommEventProgram;

public class Program implements ProtocolHandler
{

  @Override
  public CommEvent processData( byte[] data, int size )
  {
    if ( size == 7 )
    {
      return new CommEventProgram( data );
    }
    else
    {
      return new CommEventError( "Telegram size error", data, size );
    }
  }

}
