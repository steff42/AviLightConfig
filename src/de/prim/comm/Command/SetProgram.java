package de.prim.comm.Command;

import de.prim.comm.protocol.AviLightProtocol;

public class SetProgram extends MultiByteCommand
{

  public SetProgram(byte outputChannel, byte segment, byte algorithm,
      int period, byte flash)
  {
    super( AviLightProtocol.CMD_SET_PROGRAMM, new byte[] { outputChannel,
        segment, algorithm, (byte) (period & 0xff),
        (byte) (0xff & (period >> 8)), flash } );
  }

}
