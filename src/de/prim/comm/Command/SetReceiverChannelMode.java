package de.prim.comm.Command;

import de.prim.comm.protocol.AviLightProtocol;

public class SetReceiverChannelMode extends MultiByteCommand
{

  public SetReceiverChannelMode(byte channel, byte mode)
  {
    super( AviLightProtocol.CMD_SET_RECEIVER_CHANNEL_MODE, new byte[] {
        channel, mode } );
  }

}
