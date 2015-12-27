package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventConfigChanged;
import de.prim.comm.event.CommEventError;

public class ConfigChanged implements ProtocolHandler
{

  @Override
  public CommEvent processData( byte[] data, int size )
  {
    if ( size != 1 )
    {
      return new CommEventError( "To much data for Config changed", data, size );
    }
    else
    {
      return new CommEventConfigChanged( data[0] );
    }
  }

}
