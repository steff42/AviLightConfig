package de.prim.comm.event;

import java.util.Arrays;

/**
 * The Class ReceiverEvent.
 */
public class CommEventReceiver extends CommEvent
{

  /**
   * The Class ReceiverChannel.
   */
  public static class ReceiverChannel
  {

    /** The valid. */
    private boolean valid;

    /** The channel value: 0 .. 255. */
    private int     value;

    /** The segment value: 0 .. 4. */
    private int     segment;

    /**
     * Instantiates a new receiver channel.
     *
     * @param valid
     *          the valid
     * @param value
     *          the value
     * @param segment
     *          the segment
     */
    public ReceiverChannel( boolean valid, byte value, byte segment )
    {
      super();
      this.valid = valid;
      this.value = value & 0xff;
      this.segment = segment;
    }

    /**
     * Checks if is valid.
     *
     * @return true, if is valid
     */
    public boolean isValid()
    {
      return valid;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public int getValue()
    {
      return value;
    }

    /**
     * Gets the segment.
     *
     * @return the segment
     */
    public int getSegment()
    {
      return segment;
    }

    @Override
    public String toString()
    {
      return "ReceiverChannel [valid=" + valid + ", value=" + value + ", segment=" + segment + "]";
    }

  };

  /** The channels. */
  private ReceiverChannel channelData[];

  /**
   * Instantiates a new receiver event.
   *
   * @param data
   *          the data
   */
  public CommEventReceiver( byte data[] )
  {
    super( data[0] );

    int channels = data[1];
    if (channels > 0 && channels <= 4)
    {
      channelData = new ReceiverChannel[channels];
      for ( int i = 0; i < channels; i++ )
      {
        byte segment = data[2 + ( 2 * i )];
        byte value = data[3 + ( 2 * i )];

        if (segment < 0 || segment > 4)
        {
          channelData[i] = new ReceiverChannel( false, (byte) 0, (byte) 0 );
        }
        else
        {
          channelData[i] = new ReceiverChannel( true, value, segment );
        }
      }
    }
  }

  public ReceiverChannel[] getChannelData()
  {
    return channelData;
  }

  @Override
  public String toString()
  {
    return "ReceiverEvent [channelData=" + Arrays.toString( channelData ) + "]";
  }

}
