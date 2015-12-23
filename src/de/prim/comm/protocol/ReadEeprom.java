package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventReadEeprom;

public class ReadEeprom implements ProtocolHandler
{

  @Override
  public CommEvent processData(byte[] data, int size)
  {
    return new CommEventReadEeprom( data[0] );
  }

}
