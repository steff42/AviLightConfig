package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventError;
import de.prim.comm.event.CommEventVoltage;

public class Voltage implements ProtocolHandler
{

  @Override
  public CommEvent processData(byte[] data, int size)
  {
    if (size == 3 || size == 5)
    {
      return new CommEventVoltage( data, size );
    }
    else
    {
      return new CommEventError( "Telegram size error", data, size );
    }
  }

}
