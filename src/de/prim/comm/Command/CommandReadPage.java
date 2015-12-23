package de.prim.comm.Command;

import de.prim.comm.protocol.AviLightProtocol;

public class CommandReadPage extends MultiByteCommand
{

  public CommandReadPage(int address)
  {
    super( AviLightProtocol.CMD_READ_PAGE, new byte[] {
        (byte) (address & 0xff), (byte) (0xff & (address >> 8)) } );
  }

}
