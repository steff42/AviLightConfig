package de.prim.comm.event;

import de.prim.avilight.utils.HexUtils;

public class CommEventReceiverChannelMode extends CommEvent
{
  private byte receiverMode[];

  public CommEventReceiverChannelMode(byte buffer[])
  {
    super( buffer[0] );

    receiverMode = new byte[buffer[1]];
    for (int i = 0; i < buffer[1]; i++)
    {
      receiverMode[i] = buffer[2 + i];
    }
  }

  public byte[] getReceiverMode()
  {
    return receiverMode;
  }

  @Override
  public String toString()
  {
    return "CommEventReceiverChannelMode [receiverMode="
        + HexUtils.toHex( receiverMode ) + "]";
  }

}
