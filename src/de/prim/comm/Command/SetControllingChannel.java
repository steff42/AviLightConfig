package de.prim.comm.Command;

import de.prim.comm.protocol.AviLightProtocol;

/**
 * see {@link AviLightProtocol#CMD_SET_CONTROLLING_CHANNEL}
 */
public class SetControllingChannel extends MultiByteCommand
{

  /**
   * Instantiates a new sets the receiver channel mode.
   * 
   * @param ouputChannel
   *          the ouput channel
   * @param inputChannel
   *          the input channel
   */
  public SetControllingChannel(byte ouputChannel, byte inputChannel)
  {
    super( AviLightProtocol.CMD_SET_CONTROLLING_CHANNEL, new byte[] {
        ouputChannel, inputChannel } );
  }
}
