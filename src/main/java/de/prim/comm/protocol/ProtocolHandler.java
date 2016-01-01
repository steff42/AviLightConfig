package de.prim.comm.protocol;

import de.prim.comm.event.CommEvent;

public interface ProtocolHandler
{
  CommEvent processData( byte[] data, int size );
}
