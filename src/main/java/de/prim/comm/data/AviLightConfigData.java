package de.prim.comm.data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.prim.avilight.model.ProgramDefinition;
import de.prim.avilight.utils.Constants;
import de.prim.avilight.view.events.AviLightActionEvent;
import de.prim.avilight.view.events.AviLightDataEvent;
import de.prim.avilight.view.events.AviLightTerminalEvent;
import de.prim.comm.command.Command;
import de.prim.comm.command.CommandReadPage;
import de.prim.comm.command.CommandWritePage;
import de.prim.comm.command.GetProgramm;
import de.prim.comm.command.SetControllingChannel;
import de.prim.comm.command.SetLearnStickMode;
import de.prim.comm.command.SetProgram;
import de.prim.comm.command.SetReceiverChannelMode;
import de.prim.comm.command.SetVoltageLimit;
import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventBatteryLimit;
import de.prim.comm.event.CommEventChannelInfo;
import de.prim.comm.event.CommEventControllingChannel;
import de.prim.comm.event.CommEventLearnStickMode;
import de.prim.comm.event.CommEventListener;
import de.prim.comm.event.CommEventMemoryWritten;
import de.prim.comm.event.CommEventPing;
import de.prim.comm.event.CommEventProgram;
import de.prim.comm.event.CommEventReceiver;
import de.prim.comm.event.CommEventReceiver.ReceiverChannel;
import de.prim.comm.event.CommEventReceiverChannelMode;
import de.prim.comm.event.CommEventText;
import de.prim.comm.event.CommEventVoltage;
import de.prim.comm.processor.TelegramEscapeByteProcessor;
import de.prim.comm.protocol.AviLightProtocol;
import de.prim.intelhex.HexReader;
import de.prim.intelhex.Memory;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventTarget;

/**
 * The Class AviLightConfigData.
 */
@XmlRootElement ( name = "avilight")
@XmlAccessorType ( XmlAccessType.PROPERTY)
public class AviLightConfigData implements CommEventListener
{

  /** The Constant FILE_VERSION. */
  public static final String          FILE_VERSION = "1.0";

  /** The sender. */
  @XmlTransient
  private TelegramEscapeByteProcessor sender;

  /** The file version. */
  @XmlAttribute
  private String                      fileVersion  = FILE_VERSION;

  /** The firmware version. */
  @XmlElement
  private String                      firmwareVersion;

  /** The output channels. */
  @XmlAttribute
  private int                         outputChannels;

  /** The switch channels. */
  @XmlAttribute
  private int                         switchChannels;

  /** The receiver channels. */
  @XmlAttribute
  private int                         receiverChannels;

  /** The receiver channel modes. */
  @XmlTransient
  private byte[]                      receiverChannelModes;

  /** The controlling channel. */
  @XmlTransient
  private byte[]                      controllingChannel;

  /** The output channels definition. */
  @XmlElement
  private ProgramDefinition[][]       outputChannelsDefinition;

  /** The switch channel definition. */
  @XmlElement
  private ProgramDefinition[][]       switchChannelDefinition;

  /** The voltage. */
  @XmlTransient
  private BigDecimal                  voltage;

  /** The voltage limit. */
  private BigDecimal                  limit;

  /** The modified list. */
  @XmlTransient
  private Queue<Modifiable>           modifiedList = new ConcurrentLinkedQueue<Modifiable>();

  /** The ready to send. */
  @XmlTransient
  private AtomicBoolean               readyToSend  = new AtomicBoolean( false );

  /** The channel data. */
  @XmlTransient
  private ReceiverChannel[]           receiverChannelData;

  /** Firmware upgrade memory. */
  @XmlTransient
  private Memory                      memory;

  /** The last send time stamp. */
  @XmlTransient
  private long                        lastSend;

  /** The last received time stamp. */
  @XmlTransient
  private long                        lastReceived;

  /** The data mode. */
  @XmlTransient
  private DataMode                    dataMode;

  /** The event target. */
  @XmlTransient
  private EventTarget                 eventTarget;

  /** The learn stick mode. */
  private byte                        learnStickMode;

  private AtomicBoolean               loadAllData  = new AtomicBoolean( false );

  /**
   * No Arg Constructor for jaxb.
   */
  public AviLightConfigData()
  {
    super();
  }

  /**
   * Instantiates a new avi light config data.
   *
   * @param eventTarget
   *          the event target
   */
  public AviLightConfigData( EventTarget eventTarget )
  {
    super();
    this.eventTarget = eventTarget;
  }

  /**
   * Send next.
   */
  private void sendNext()
  {
    readyToSend.set( true );
    if ( !modifiedList.isEmpty() )
    {
      modification( modifiedList.poll() );
    }
  }

  /**
   * Modification.
   *
   * @param modifiable
   *          the modifiable
   */
  public void modification( Modifiable modifiable )
  {
    if ( readyToSend.compareAndSet( true, false ) )
    {
      saveModification( modifiable );
    }
    else
    {
      modifiedList.add( modifiable );
    }
  }

  /**
   * Reset modifications.
   */
  public void resetModifications()
  {
    readyToSend.set( false );
    modifiedList.clear();
  }

  /**
   * Save modification.
   *
   * @param modifiable
   *          the modifiable
   */
  private void saveModification( Modifiable modifiable )
  {
    sendCommand( modifiable.getCommand() );
  }

  /**
   * Sets the sender.
   *
   * @param sender
   *          the new sender
   */
  public void setSender( TelegramEscapeByteProcessor sender )
  {
    this.sender = sender;
  }

  /**
   * Send an AviLight data event.
   *
   * @param dataEvent
   *          the data event
   */
  private void sendAviLightDataEvent( final AviLightDataEvent aviLightDataEvent )
  {
    if ( eventTarget != null )
    {
      Platform.runLater( () -> Event.fireEvent( eventTarget, aviLightDataEvent ) );
    }
  }

  /**
   * Send AviLight terminal event.
   *
   * @param aviLightTerminalEvent
   *          the avi light terminal event
   */
  private void sendAviLightTerminalEvent( final AviLightTerminalEvent aviLightTerminalEvent )
  {
    if ( eventTarget != null )
    {
      Platform.runLater( () -> Event.fireEvent( eventTarget, aviLightTerminalEvent ) );
    }
  }

  /**
   * Send avi light action event.
   *
   * @param aviLightActionEvent
   *          the avi light action event
   */
  private void sendAviLightActionEvent( final AviLightActionEvent aviLightActionEvent )
  {
    if ( eventTarget != null )
    {
      Platform.runLater( () -> Event.fireEvent( eventTarget, aviLightActionEvent ) );
    }
  }

  /**
   * Close.
   */
  public void close()
  {
    sender = null;
    initData();
  }

  /**
   * Initializes the communication.
   */
  public void init()
  {
    initData();

    dataMode = DataMode.StartInfo;
    loadAllData.set( true );
    readyToSend.set( false );
    sendCommand( Command.PING );
  }

  /**
   * Inits the data.
   */
  private void initData()
  {
    resetModifications();

    outputChannels = 0;
    switchChannels = 0;
    receiverChannels = 0;

    receiverChannelModes = null;
    controllingChannel = null;
    outputChannelsDefinition = null;
    switchChannelDefinition = null;
    voltage = BigDecimal.ZERO;
  }

  /**
   * Send command.
   *
   * @param command
   *          the command
   */
  private synchronized void sendCommand( Command command )
  {
    if ( sender != null )

    {
      lastSend = System.currentTimeMillis();

      if ( AviLightProtocol.enableDump
          || AviLightProtocol.LOG_COMMAND.contains( command.getCmd() ) )
      {
        System.out.println( "S: " + command );
      }
      try
      {
        command.send( sender );
      }
      catch ( IOException e )
      {
        System.out.println( "Exception sending " + command );

        e.printStackTrace();
      }
    }

  }

  /**
   * Creates the program definition array.
   *
   * @param channels
   *          the channels
   * @return the program definition[][]
   */
  private ProgramDefinition[][] createProgramDefinitionArray( int channels )
  {
    ProgramDefinition[][] programDefinitions = new ProgramDefinition[channels][];

    for ( int i = 0; i < channels; i++ )
    {
      programDefinitions[i] = new ProgramDefinition[Constants.SEGMENT_COUNT];
    }

    return programDefinitions;
  }

  /** {@inheritDoc} */
  /* (non-Javadoc) */
  @Override
  public void eventOccured( CommEvent commEvent )
  {
    lastReceived = System.currentTimeMillis();

    // if (commEvent.getSource() != 4)
    // {
    // System.out.println( "R: " + commEvent.toString() );
    // }
    switch ( commEvent.getSource() )
    {
      /* First the stuff that is done on a regular basis (polled) */
      case AviLightProtocol.CMD_RECEIVER:
        receiverChannelData = ( (CommEventReceiver) commEvent ).getChannelData();
        sendCommand( Command.VOLTAGE );

        sendAviLightDataEvent( new AviLightDataEvent( AviLightDataEvent.RECEIVER_DATA_RECEIVED ) );
        break;

      case AviLightProtocol.CMD_GET_VOLTAGE:
        voltage = ( (CommEventVoltage) commEvent ).getVoltage();
        readyToSend.set( true );

        sendAviLightDataEvent( new AviLightDataEvent( AviLightDataEvent.VOLTAGE_RECEIVED ) );
        break;

      case AviLightProtocol.CMD_PING:
        processCmdPing( commEvent );
        break;

      /* Next the stuff that can be changed on the GUI */
      case AviLightProtocol.CMD_GET_CONTROLLING_CHANNEL:
        controllingChannel = ( (CommEventControllingChannel) commEvent ).getControllingChannel();

        sendAviLightDataEvent(
            new AviLightDataEvent( AviLightDataEvent.CONTROLLING_CHANNEL_RECEIVED ) );

        if ( loadAllData.get() )
        {
          sendCommand( new GetProgramm( (byte) 0, (byte) 0 ) );
        }
        break;

      case AviLightProtocol.CMD_SET_CONTROLLING_CHANNEL:
        controllingChannel = ( (CommEventControllingChannel) commEvent ).getControllingChannel();
        readyToSend.set( true );

        sendAviLightDataEvent(
            new AviLightDataEvent( AviLightDataEvent.CONTROLLING_CHANNEL_RECEIVED ) );
        break;

      case AviLightProtocol.CMD_GET_BATTERY_LIMIT:
        limit = ( (CommEventBatteryLimit) commEvent ).getLimit();
        sendAviLightDataEvent( new AviLightDataEvent( AviLightDataEvent.BATTERY_LIMIT_RECEIVED ) );

        if ( loadAllData.get() )
        {
          sendCommand( Command.CMD_GET_LEARN_STICKMODE );
        }
        break;

      case AviLightProtocol.CMD_SET_BATTERY_LIMIT:
        limit = ( (CommEventBatteryLimit) commEvent ).getLimit();
        readyToSend.set( true );

        sendAviLightDataEvent( new AviLightDataEvent( AviLightDataEvent.BATTERY_LIMIT_RECEIVED ) );

        break;

      case AviLightProtocol.CMD_SET_RECEIVER_CHANNEL_MODE:
        readyToSend.set( true );
        sendAviLightDataEvent(
            new AviLightDataEvent( AviLightDataEvent.RECEIVER_CHANNEL_MODE_RECEIVED ) );
        break;

      case AviLightProtocol.CMD_GET_RECEIVER_CHANNEL_MODE:
        receiverChannelModes = ( (CommEventReceiverChannelMode) commEvent ).getReceiverMode();

        sendAviLightDataEvent(
            new AviLightDataEvent( AviLightDataEvent.RECEIVER_CHANNEL_MODE_RECEIVED ) );

        if ( loadAllData.get() )
        {
          sendCommand( Command.GET_CONTROLLING_CHANNEL );
        }

        break;

      case AviLightProtocol.CMD_GET_LEARN_STICKMODE:
        learnStickMode = ( (CommEventLearnStickMode) commEvent ).getLearnStickMode();
        sendAviLightDataEvent(
            new AviLightDataEvent( AviLightDataEvent.LEARN_STICK_MODE_RECEIVED ) );

        if ( loadAllData.get() )
        {
          sendCommand( Command.RECEIVER_CHANNEL_MODE );
        }
        break;

      case AviLightProtocol.CMD_SET_LEARN_STICKMODE:
        learnStickMode = ( (CommEventLearnStickMode) commEvent ).getLearnStickMode();
        sendAviLightDataEvent(
            new AviLightDataEvent( AviLightDataEvent.LEARN_STICK_MODE_RECEIVED ) );
        readyToSend.set( true );
        break;

      /* Now everything else */
      case AviLightProtocol.CMD_CONFIG_CHANGED:
        // No break, fall trough to CMD_READ_FROM_EEPROM
      case AviLightProtocol.CMD_READ_FROM_EEPROM:
        init();
        break;

      case AviLightProtocol.CMD_INFO:
        firmwareVersion = ( (CommEventText) commEvent ).getText();
        sendCommand( Command.CHANNEL_INFO );
        break;

      case AviLightProtocol.CMD_CHANNEL_INFO:

        outputChannels = ( (CommEventChannelInfo) commEvent ).getOutputChannels();
        outputChannelsDefinition = createProgramDefinitionArray( outputChannels );

        switchChannels = ( (CommEventChannelInfo) commEvent ).getSwitchChannels();
        switchChannelDefinition = createProgramDefinitionArray( switchChannels );

        receiverChannels = ( (CommEventChannelInfo) commEvent ).getReceiverChannels();

        sendAviLightActionEvent( new AviLightActionEvent( AviLightActionEvent.START_PROGRESS,
            ( outputChannels + switchChannels ) * Constants.SEGMENT_COUNT ) );

        sendAviLightDataEvent( new AviLightDataEvent( AviLightDataEvent.INFO_DATA_RECEIVED ) );

        sendCommand( Command.BATTERY_LIMIT );

        break;

      case AviLightProtocol.CMD_SET_PROGRAMM:
        sendNext();
        break;

      case AviLightProtocol.CMD_GET_PROGRAMM:

        processCmdGetProgramm( commEvent );
        break;

      case AviLightProtocol.CMD_TERMINAL:
        sendAviLightTerminalEvent( new AviLightTerminalEvent( AviLightTerminalEvent.TERMINAL_EVENT,
            ( (CommEventText) commEvent ).getText() ) );
        readyToSend.set( true );
        break;

      case AviLightProtocol.CMD_ENTER_PROGRAMMING_MODE:
        // readyToSend.set( false );
        // startFirmwareUpgrade();
        sendCommand( Command.PING );
        break;

      case AviLightProtocol.CMD_READ_PAGE:
        // System.out.println( "Rec: " + commEvent );
        break;

      case AviLightProtocol.CMD_WRITE_PAGE:
        // System.out.println( "Rec: " + commEvent );
        if ( ( (CommEventMemoryWritten) commEvent ).getStatus() == (byte) 0xff )
        {
          nextPage( ( ( (CommEventMemoryWritten) commEvent ).getAddress() / Memory.PAGESIZE ) + 1 );
        }
        else
        {
          sendCommand( new CommandReadPage( ( (CommEventMemoryWritten) commEvent ).getAddress() ) );
        }
        break;
    }
  }

  /**
   * Process cmd get programm.
   *
   * @param commEvent
   *          the comm event
   */
  private void processCmdGetProgramm( CommEvent commEvent )
  {
    byte channel = ( (CommEventProgram) commEvent ).getChannel();
    byte segment = ( (CommEventProgram) commEvent ).getSegment();

    sendAviLightActionEvent( new AviLightActionEvent( AviLightActionEvent.PROGRESS,
        ( channel * Constants.SEGMENT_COUNT ) + segment ) );

    ProgramDefinition programDefinition = ( (CommEventProgram) commEvent ).getProgramDefinition();
    if ( channel < outputChannels )
    {
      outputChannelsDefinition[channel][segment] = programDefinition;
    }
    else
    {
      switchChannelDefinition[channel - outputChannels][segment] = programDefinition;
    }

    System.out.println( "C:" + channel + " S:" + segment );
    segment++;
    if ( segment >= Constants.SEGMENT_COUNT )
    {
      segment = 0;
      channel++;
    }

    if ( channel < ( outputChannels + switchChannels ) )
    {
      sendCommand( new GetProgramm( channel, segment ) );
    }
    else
    {
      loadAllData.set( false );
      readyToSend.set( true );
      sendAviLightActionEvent( new AviLightActionEvent( AviLightActionEvent.STOP_PROGRESS, 0 ) );

      sendAviLightDataEvent( new AviLightDataEvent( AviLightDataEvent.PROGRAM_DATA_RECEIVED ) );
    }
  }

  /**
   * Process cmd ping.
   *
   * @param commEvent
   *          the comm event
   */
  private void processCmdPing( CommEvent commEvent )
  {
    if ( DataMode.StartInfo.equals( dataMode ) )
    {
      if ( ( (CommEventPing) commEvent ).getFirmwareMode() == CommEventPing.BOOTLOADER )
      {
        sendCommand( Command.PING );
      }
      else if ( ( (CommEventPing) commEvent ).getFirmwareMode() == CommEventPing.BOOTLOADER_NO_APP )
      {
        sendAviLightActionEvent(
            new AviLightActionEvent( AviLightActionEvent.ASK_FOR_UPGRADE, 0 ) );
      }
      else
      {
        sendCommand( Command.GET_INFO );
      }
    }
    else if ( DataMode.StartFirmwareUpgrade.equals( dataMode ) )
    {
      resetModifications();
      readyToSend.set( false );
      if ( ( (CommEventPing) commEvent ).getFirmwareMode() == CommEventPing.APPLICATION )
      {
        sendCommand( Command.ENTER_PROGRAMMING_MODE );
      }
      else
      {
        startFirmwareUpgrade();
      }
    }
    else
    {
      readyToSend.set( true );
    }
  }

  /**
   * Gets the firmware version.
   *
   * @return the firmware version
   */
  public String getFirmwareVersion()
  {
    return firmwareVersion;
  }

  /**
   * Gets the output channels.
   *
   * @return the output channels
   */
  public int getOutputChannels()
  {
    return outputChannels;
  }

  /**
   * Gets the switch channels.
   *
   * @return the switch channels
   */
  public int getSwitchChannels()
  {
    return switchChannels;
  }

  /**
   * Gets the receiver channels.
   *
   * @return the receiver channels
   */
  public int getReceiverChannels()
  {
    return receiverChannels;
  }

  /**
   * Sets the controlling channel.
   *
   * @param index
   *          the output channel index
   * @param value
   *          the receiver channel
   */
  public void setControllingChannel( byte index, byte value )
  {
    if ( controllingChannel != null && index < controllingChannel.length )
    {
      if ( controllingChannel[index] != value )
      {
        modification( new ModifiableImpl( new SetControllingChannel( index, value ) ) );
      }
    }
  }

  /**
   * Sets the program.
   *
   * @param channel
   *          the channel
   * @param segment
   *          the segment
   * @param programDefinition
   *          the program definition
   */
  public void setProgram( ProgramDefinition programDefinition )
  {
    modification( new ModifiableImpl( new SetProgram( programDefinition.getChannel(),
        programDefinition.getSegment(), programDefinition.getAlgorithm(),
        programDefinition.getPeriod(), programDefinition.getFlash() ) ) );
  }

  /**
   * Gets the controlling channel.
   *
   * @return the controlling channel
   */
  public byte[] getControllingChannel()
  {
    return controllingChannel;
  }

  /**
   * Gets the output channels definition.
   *
   * @return the output channels definition
   */
  public ProgramDefinition[][] getOutputChannelsDefinition()
  {
    return outputChannelsDefinition;
  }

  /**
   * Gets the switch channel definition.
   *
   * @return the switch channel definition
   */
  public ProgramDefinition[][] getSwitchChannelDefinition()
  {
    return switchChannelDefinition;
  }

  /**
   * Gets the receiver channel data.
   *
   * @return the receiver channel data
   */
  public ReceiverChannel[] getReceiverChannelData()
  {
    return receiverChannelData;
  }

  /**
   * Gets the receiver channel modes.
   *
   * @return the receiver channel modes
   */
  public byte[] getReceiverChannelModes()
  {
    return receiverChannelModes;
  }

  /**
   * Sets the receiver channel mode.
   *
   * @param index
   *          the index
   * @param selection
   *          the selection
   */
  public void setReceiverChannelMode( int index, int selection )
  {
    if ( receiverChannelModes != null && index < receiverChannelModes.length )
    {
      if ( receiverChannelModes[index] != (byte) selection )
      {
        receiverChannelModes[index] = (byte) selection;

        modification( new ModifiableImpl(
            new SetReceiverChannelMode( (byte) index, receiverChannelModes[index] ) ) );
      }
    }
  }

  /**
   * Sets the learn stick mode.
   *
   * @param learnStickMode
   *          the new stick learn mode
   */
  public void setStickLearnMode( int index, boolean value )
  {
    if ( index < receiverChannels )
    {
      boolean current = ( ( learnStickMode >> index ) & 1 ) == 1;
      if ( current != value )
      {
        modification( new ModifiableImpl(
            new SetLearnStickMode( (byte) ( learnStickMode ^ ( 1 << index ) ) ) ) );
      }
    }
  }

  /**
   * Sets the voltage limit.
   *
   * @param voltage
   *          the new voltage limit
   */
  public void setVoltageLimit( BigDecimal voltage )
  {
    if ( this.voltage.compareTo( voltage ) != 0 )
    {

      int vi = 0;
      if ( ( BigDecimal.ZERO.compareTo( voltage ) <= 0 )
          && ( new BigDecimal( "28.4" ).compareTo( voltage ) > 0 ) )
      {
        vi = new BigDecimal( "36" ).multiply( voltage, Constants.ROUND_HALF_UP_PRE_0 ).intValue();
      }

      modification( new ModifiableImpl( new SetVoltageLimit( vi ) ) );
    }
  }

  /**
   * To list from Array.
   *
   * @param array
   *          the array
   * @return the list
   */
  private List<Byte> toList( byte[] array )
  {
    List<Byte> list = new ArrayList<Byte>( array.length );

    for ( byte b : array )
    {
      list.add( b );
    }

    return list;
  }

  // /**
  // * To array.
  // *
  // * @param list
  // * the list
  // * @return the byte[]
  // */
  // private byte[] toArray( List<Byte> list )
  // {
  // byte[] array = new byte[list.size()];
  //
  // for ( int i = 0; i < list.size(); i++ )
  // {
  // array[i] = list.get( i );
  // }
  //
  // return array;
  // }

  /**
   * Gets the receiver modes.
   *
   * @return the receiver modes
   */
  @XmlElementWrapper ( name = "receiverChannelModes")
  @XmlElement ( name = "mode")
  public List<Byte> getReceiverModes()
  {
    return toList( receiverChannelModes );
  }

  // /**
  // * Sets the receiver modes.
  // *
  // * @param list
  // * the new receiver modes
  // */
  // public void setReceiverModes( List<Byte> list )
  // {
  // receiverChannelModes = toArray( list );
  // }

  /**
   * Gets the controlling channel list.
   *
   * @return the controlling channel list
   */
  @XmlElementWrapper ( name = "controllingChannels")
  @XmlElement ( name = "channel")
  public List<Byte> getControllingChannelList()
  {
    return toList( controllingChannel );
  }

  // /**
  // * Sets the controlling channel list.
  // *
  // * @param list
  // * the new controlling channel list
  // */
  // public void setControllingChannelList( List<Byte> list )
  // {
  // controllingChannel = toArray( list );
  // }

  /**
   * Gets the voltage.
   *
   * @return the voltage
   */
  public BigDecimal getVoltage()
  {
    return voltage;
  }

  /**
   * Gets the limit.
   *
   * @return the limit
   */
  public BigDecimal getLimit()
  {
    return limit;
  }

  /**
   * Gets the learn stick mode.
   *
   * @return the learn stick mode
   */
  public byte getLearnStickMode()
  {
    return learnStickMode;
  }

  /**
   * called cyclic by swing timer for communication tasks.
   *
   * @return true, if successful
   */
  public boolean cyclicCommunication()
  {
    if ( readyToSend.get() && !loadAllData.get() )
    {
      if ( !modifiedList.isEmpty() )
      {
        sendNext();
      }
      else
      {
        if ( readyToSend.compareAndSet( true, false ) )
        {
          sendCommand( Command.GET_RECEIVER );
        }
      }
    }

    long diff = System.currentTimeMillis() - lastSend;
    if ( modifiedList.isEmpty() && diff > 5000 )
    {
      modification( new ModifiableImpl( Command.PING ) );
    }

    diff = System.currentTimeMillis() - lastReceived;
    return diff < 5000;
  }

  /**
   * Firmware upgrade.
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void firmwareUpgrade() throws IOException
  {

    memory = new Memory();
    HexReader hexReader = new HexReader( memory );
    hexReader.readHex( new FileReader( new File(
        // "D:\\HardwareProjekte\\RC\\AviLight3\\Firmware\\AviLight3\\AviLight3\\Debug\\AviLight3.hex"
        // ) ) );
        "AviLight3.hex" ) ) );

    // AviLightProtocol.enableDump = true;

    resetModifications();
    readyToSend.set( false );
    dataMode = DataMode.StartFirmwareUpgrade;
    sendCommand( Command.PING );
    // modification( new ModifiableImpl( Command.PING ) );
    // modification( new ModifiableImpl( Command.ENTER_PROGRAMMING_MODE ) );
  }

  /**
   * Start firmware upgrade.
   */
  void startFirmwareUpgrade()
  {
    dataMode = DataMode.FirmwareUpgradeInProgress;
    sendAviLightActionEvent(
        new AviLightActionEvent( AviLightActionEvent.START_PROGRESS, memory.getMaxPage() ) );
    nextPage( 0 );
  }

  /**
   * Save config.
   */
  public void saveConfig()
  {
    sendCommand( Command.WRITE_TO_EEPROM );
  }

  /**
   * Reload config.
   */
  public void reloadConfig()
  {
    loadAllData.set( true );
    readyToSend.set( false );
    sendCommand( Command.READ_FROM_EEPROM );
  }

  /**
   * Next page.
   *
   * @param page
   *          the page
   */
  private void nextPage( int page )
  {
    int pageNo = page;
    byte[] pageMem = null;
    while ( pageNo < Memory.PAGES )
    {
      pageMem = memory.getPage( pageNo );
      if ( pageMem != null )
      {
        break;
      }
      pageNo++;
    }

    if ( pageMem != null )
    {
      sendAviLightActionEvent( new AviLightActionEvent( AviLightActionEvent.PROGRESS, pageNo ) );

      System.out.println( "Programming page " + pageNo );
      sendCommand( new CommandWritePage( AviLightProtocol.CMD_WRITE_PAGE, pageNo * Memory.PAGESIZE,
          pageMem ) );
    }
    else
    {
      sendAviLightActionEvent( new AviLightActionEvent( AviLightActionEvent.STOP_PROGRESS, 0 ) );

      sendCommand( Command.RESET );
      System.out.println( "Programming done" );

      init();
      // readyToSend.set( false );
      // sendCommand( Command.GET_INFO );

    }

  }
}
