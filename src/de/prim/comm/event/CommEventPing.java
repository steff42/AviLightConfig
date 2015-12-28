package de.prim.comm.event;

public class CommEventPing extends CommEvent
{
  public static final byte APPLICATION       = 0;
  public static final byte BOOTLOADER        = 1;
  public static final byte BOOTLOADER_NO_APP = 2;

  private byte             firmwareMode;

  public CommEventPing( byte[] data )
  {
    super( data[0] );
    this.firmwareMode = data[1];
  }

  public byte getFirmwareMode()
  {
    return firmwareMode;
  }

  @Override
  public String toString()
  {
    return "CommEventPing [firmwareMode=" + firmwareMode + "]";
  }

}
