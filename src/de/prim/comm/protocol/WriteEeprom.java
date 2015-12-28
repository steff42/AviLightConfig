package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventWriteEeprom;

public class WriteEeprom implements ProtocolHandler
{

  @Override
  public CommEvent processData( byte[] data, int size )
  {
    return new CommEventWriteEeprom( data[0] );
  }

}
