package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventBatteryLimit;
import de.prim.comm.event.CommEventError;

public class BatteryLimit implements ProtocolHandler
{

  @Override
  public CommEvent processData( byte[] data, int size )
  {
    if ( size == 3 )
    {
      return new CommEventBatteryLimit( data, size );
    }
    else
    {
      return new CommEventError( "Telegram size error", data, size );
    }
  }

}
