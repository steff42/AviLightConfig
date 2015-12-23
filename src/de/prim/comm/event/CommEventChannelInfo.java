package de.prim.comm.event;

/**
 * The Class CommEventChannelInfo.
 */
public class CommEventChannelInfo extends CommEvent
{

  /** The output channels. */
  private byte outputChannels;

  /** The switch channels. */
  private byte switchChannels;

  /** The receiver channels. */
  private byte receiverChannels;

  /** The max # of segments */
  private byte maxSegments;

  /**
   * Instantiates a new comm event channel info.
   * 
   * @param buffer
   *          the buffer
   * @param size
   */
  public CommEventChannelInfo(byte buffer[], int size)
  {
    super( buffer[0] );

    outputChannels = buffer[1];
    switchChannels = buffer[2];
    receiverChannels = buffer[3];
    if (size > 4)
    {
      maxSegments = buffer[4];
    }
    else
    {
      maxSegments = 6;
    }
  }

  public byte getOutputChannels()
  {
    return outputChannels;
  }

  public byte getSwitchChannels()
  {
    return switchChannels;
  }

  public byte getReceiverChannels()
  {
    return receiverChannels;
  }

  @Override
  public String toString()
  {
    return "CommEventChannelInfo [outputChannels=" + outputChannels
        + ", switchChannels=" + switchChannels + ", receiverChannels="
        + receiverChannels + "]";
  }

}
