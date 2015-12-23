package de.prim.comm.event;

import de.prim.avilight.utils.HexUtils;

public class CommEventControllingChannel extends CommEvent
{
  private byte controllingChannel[];

  public CommEventControllingChannel(byte buffer[])
  {
    super( buffer[0] );

    controllingChannel = new byte[buffer[1]];
    for (int i = 0; i < buffer[1]; i++)
    {
      controllingChannel[i] = buffer[2 + i];
    }

  }

  public byte[] getControllingChannel()
  {
    return controllingChannel;
  }

  @Override
  public String toString()
  {
    return "CommEventControllingChannel [controllingChannel="
        + HexUtils.toHex( controllingChannel ) + "]";
  }

}
