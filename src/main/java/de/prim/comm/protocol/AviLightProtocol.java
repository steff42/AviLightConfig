package de.prim.comm.protocol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.prim.avilight.utils.HexUtils;
import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventError;
import de.prim.comm.event.CommEventListener;
import de.prim.comm.processor.DataProcessor;

/**
 * The Class AviLightProtocol.
 */
public class AviLightProtocol implements DataProcessor
{

  // Ping the device, the answer is a ping too
  /** The Constant CMD_PING. */
  public static final byte                  CMD_PING                      = (byte) 0;

  // Answers the firmware version as a string
  /** The Constant CMD_INFO. */
  public static final byte                  CMD_INFO                      = (byte) 1;

  // Print string to terminal
  /** The Constant CMD_TERMINAL. */
  public static final byte                  CMD_TERMINAL                  = (byte) 2;

  // Send Channel Info
  public static final byte                  CMD_CHANNEL_INFO              = (byte) 3;

  // Get Receiver Channels
  public static final byte                  CMD_RECEIVER                  = 4;

  // Get the receiver channel modes
  public static final byte                  CMD_GET_RECEIVER_CHANNEL_MODE = 5;

  // Sets the receiver channel mode
  public static final byte                  CMD_SET_RECEIVER_CHANNEL_MODE = 6;

  public static final byte                  CMD_GET_CONTROLLING_CHANNEL   = 7;
  public static final byte                  CMD_SET_CONTROLLING_CHANNEL   = 8;

  public static final byte                  CMD_GET_PROGRAMM              = 9;
  public static final byte                  CMD_SET_PROGRAMM              = 10;

  public static final byte                  CMD_GET_VOLTAGE               = 11;

  public static final byte                  CMD_ENTER_PROGRAMMING_MODE    = 12;

  // config changed on device, reload settings
  public static final byte                  CMD_CONFIG_CHANGED            = 13;

  // save permanently
  public static final byte                  CMD_WRITE_TO_EEPROM           = 14;

  // load program from eeprom
  public static final byte                  CMD_READ_FROM_EEPROM          = 15;

  public static final byte                  CMD_GET_LEARN_STICKMODE       = 17;
  public static final byte                  CMD_SET_LEARN_STICKMODE       = 18;
  public static final byte                  CMD_SET_BATTERY_LIMIT         = 19;

  public static final byte                  CMD_DUMP_PROGRAM_ARRAY        = 0x7b;
  public static final byte                  CMD_DUMP_EEPROM               = 0x7c;
  public static final byte                  CMD_DUMP_RECEIVER             = 0x7d;
  public static final byte                  CMD_DUMP_GLOBALS              = 0x7e;
  public static final byte                  CMD_DUMP_CHANNELS             = 0x7f;

  // Bootloader commands
  public static final byte                  CMD_READ_PAGE                 = (byte) 0x80;
  public static final byte                  CMD_WRITE_PAGE                = (byte) 0x81;
  public static final byte                  CMD_RESET                     = (byte) 0x82;

  /** The HANDLER. */
  private static Map<Byte, ProtocolHandler> HANDLER                       = new HashMap<Byte, ProtocolHandler>();
  static
  {
    HANDLER.put( CMD_PING, new Ping() );
    HANDLER.put( CMD_INFO, new Text() );
    HANDLER.put( CMD_TERMINAL, HANDLER.get( CMD_INFO ) );
    HANDLER.put( CMD_CHANNEL_INFO, new ChannelInfo() );
    HANDLER.put( CMD_RECEIVER, new Receiver() );
    HANDLER.put( CMD_GET_RECEIVER_CHANNEL_MODE, new ReceiverChannelMode() );
    HANDLER.put( CMD_GET_CONTROLLING_CHANNEL, new ControllingChannel() );
    HANDLER.put( CMD_SET_CONTROLLING_CHANNEL, HANDLER.get( CMD_GET_CONTROLLING_CHANNEL ) );
    HANDLER.put( CMD_GET_PROGRAMM, new Program() );
    HANDLER.put( CMD_SET_PROGRAMM, HANDLER.get( CMD_GET_PROGRAMM ) );
    HANDLER.put( CMD_GET_VOLTAGE, new Voltage() );
    HANDLER.put( CMD_CONFIG_CHANGED, new ConfigChanged() );
    HANDLER.put( CMD_READ_PAGE, new ReadPage() );
    HANDLER.put( CMD_WRITE_PAGE, new WritePage() );
    HANDLER.put( CMD_ENTER_PROGRAMMING_MODE, new EnterProgrammingMode() );
    HANDLER.put( CMD_WRITE_TO_EEPROM, new WriteEeprom() );
    HANDLER.put( CMD_READ_FROM_EEPROM, new ReadEeprom() );
    HANDLER.put( CMD_GET_LEARN_STICKMODE, new LearnStickMode() );
    HANDLER.put( CMD_SET_LEARN_STICKMODE, HANDLER.get( CMD_GET_LEARN_STICKMODE ) );
    HANDLER.put( CMD_SET_BATTERY_LIMIT, HANDLER.get( CMD_GET_VOLTAGE ) );
  }

  /** Enable logging of commands. */
  public static Set<Byte>                   LOG_COMMAND;
  static
  {
    LOG_COMMAND = new HashSet<Byte>();
    // LOG_COMMAND.add( CMD_SET_PROGRAMM );
    // LOG_COMMAND.add( CMD_SET_CONTROLLING_CHANNEL );
    // LOG_COMMAND.add( CMD_GET_PROGRAMM );
    // LOG_COMMAND.add( CMD_GET_CONTROLLING_CHANNEL );
    // LOG_COMMAND.add( CMD_GET_PROGRAMM );
    // LOG_COMMAND.add( CMD_GET_VOLTAGE );
    // LOG_COMMAND.add( CMD_RECEIVER );
    LOG_COMMAND.add( CMD_ENTER_PROGRAMMING_MODE );
    // LOG_COMMAND.add( CMD_WRITE_PAGE );
    // LOG_COMMAND.add( CMD_RESET );
    // LOG_COMMAND.add( CMD_GET_RECEIVER_CHANNEL_MODE );
    // LOG_COMMAND.add( CMD_SET_RECEIVER_CHANNEL_MODE );
    // LOG_COMMAND.add( CMD_DUMP_CHANNELS );
    LOG_COMMAND.add( CMD_CONFIG_CHANGED );
    LOG_COMMAND.add( CMD_WRITE_TO_EEPROM );
    LOG_COMMAND.add( CMD_READ_FROM_EEPROM );
    // LOG_COMMAND.add( CMD_GET_LEARN_STICKMODE );
    LOG_COMMAND.add( CMD_SET_LEARN_STICKMODE );
  }

  /** The listener. */
  // private List<CommEventListener> listener;

  /** The last event, only for testing. */
  private CommEvent                         lastEvent;

  private CommEventListener                 eventListener;

  public static boolean                     enableDump                    = false;

  /**
   * Instantiates a new avi light protocol.
   */
  public AviLightProtocol( CommEventListener eventListener )
  {
    super();
    this.eventListener = eventListener;
  }

  /**
   * Adds the listener.
   *
   * @param commEventListener
   *          the comm event listener
   */
  public void addListener( CommEventListener commEventListener )
  {
    throw new IllegalArgumentException();
  }

  @Override
  public void processData( byte[] receiveBuffer, int length )
  {
    if ( enableDump )
    {
      System.out.println( "REC: [" + length + "] " + HexUtils.toHex( receiveBuffer, length ) );
    }

    CommEvent commEvent;
    ProtocolHandler protocolHandler = HANDLER.get( receiveBuffer[0] );
    if ( protocolHandler == null )
    {
      System.out.println( "No Protocol handler for " + HexUtils.toHex( receiveBuffer, length ) );

      commEvent = new CommEventError( "No Protocol handler for ", receiveBuffer, length );
    }
    else
    {
      commEvent = protocolHandler.processData( receiveBuffer, length );

      if ( commEvent != null )
      {
        if ( enableDump || LOG_COMMAND.contains( commEvent.getSource() ) )
        {
          System.out.println( "Received: " + commEvent );
          // + HexUtils.toHex( receiveBuffer, length ) );
        }
      }
    }

    if ( commEvent != null )
    {
      if ( commEvent instanceof CommEventError )
      {
        System.out.println( commEvent );
      }

      if ( eventListener != null )
      {
        eventListener.eventOccured( commEvent );
      }
    }

    lastEvent = commEvent;
  }

  public CommEvent getLastEvent()
  {
    return lastEvent;
  }
}
