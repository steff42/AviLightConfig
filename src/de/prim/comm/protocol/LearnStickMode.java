package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventError;
import de.prim.comm.event.CommEventLearnStickMode;

public class LearnStickMode implements ProtocolHandler
{

  @Override
  public CommEvent processData( byte[] data, int size )
  {
    if ( size == 2 )
    {
      return new CommEventLearnStickMode( data );
    }
    else
    {
      return new CommEventError( "Telegram size error", data, size );
    }
  }

}
