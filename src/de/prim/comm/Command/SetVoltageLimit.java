package de.prim.comm.Command;

import de.prim.comm.protocol.AviLightProtocol;

public class SetVoltageLimit extends MultiByteCommand
{

  public SetVoltageLimit( int voltage)
  {
    super( AviLightProtocol.CMD_SET_BATTERY_LIMIT, new byte[] {
        (byte) (voltage & 0xff), (byte) ((voltage >> 8) & 0xff) } );
  }

}
