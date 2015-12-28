package de.prim.comm.command;

import de.prim.comm.protocol.AviLightProtocol;

public class GetProgramm extends MultiByteCommand
{

  public GetProgramm( byte ouputChannel, byte segment )
  {
    super( AviLightProtocol.CMD_GET_PROGRAMM, new byte[]
    { ouputChannel, segment } );
  }

}
