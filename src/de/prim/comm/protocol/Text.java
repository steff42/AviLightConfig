package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventUtil;

public class Text implements ProtocolHandler
{

  @Override
  public CommEvent processData(byte[] data, int size)
  {
    return CommEventUtil.createTextEvent( data, size );
  }

}
