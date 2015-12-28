package de.prim.comm.data;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.prim.avilight.Constants;
import de.prim.comm.command.Command;
import de.prim.comm.command.CommandReadPage;
import de.prim.comm.command.CommandWritePage;
import de.prim.comm.command.GetProgramm;
import de.prim.comm.command.SetControllingChannel;
import de.prim.comm.command.SetLearnStickMode;
import de.prim.comm.command.SetReceiverChannelMode;
import de.prim.comm.command.SetVoltageLimit;
import de.prim.comm.data.DataEvent.Type;
import de.prim.comm.event.CommEvent;
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
import de.prim.comm.event.ProgramDefinition;
import de.prim.comm.processor.TelegramEscapeByteProcessor;
import de.prim.comm.protocol.AviLightProtocol;
import de.prim.intelhex.HexReader;
import de.prim.intelhex.Memory;

// TODO: Auto-generated Javadoc
/**
 * The Class AviLightConfigData.
 */
@XmlRootElement ( name = "avilight")
@XmlAccessorType ( XmlAccessType.PROPERTY)
public class AviLightConfigData implements CommEventListener
{

  /** The Constant FILE_VERSION. */
  public static final String          FILE_VERSION          = "1.0";

  /** No App found, only bootloader. */
  public static final String          ACTION_ASK_UPGRADE    = "ASK_FOR_UPGRADE";

  /** The Constant ACTION_START_PROGRESS. */
  public static final String          ACTION_START_PROGRESS = "START_PROGRESS";

  /** The Constant ACTION_PROGRESS. */
  public static final String          ACTION_PROGRESS       = "PROGRESS";

  /** The Constant ACTION_STOP_PROGRESS. */
  public static final String          ACTION_STOP_PROGRESS  = "STOP_PROGRESS";

  /** The sender. */
  @XmlTransient
  private TelegramEscapeByteProcessor sender;

  /** The file version. */
  @XmlAttribute
  private String                      fileVersion           = FILE_VERSION;

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

  /** The voltage limit */
  private BigDecimal                  limit;

  /** The listener. */
  @XmlTransient
  private List<DataEventListener>     listener              = new ArrayList<DataEventListener>();

  /** The modified list. */
  @XmlTransient
  private Queue<Modifiable>           modifiedList          = new ConcurrentLinkedQueue<Modifiable>();

  /** The ready to send. */
  @XmlTransient
  private AtomicBoolean               readyToSend           = new AtomicBoolean( false );

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

  /** The action listener. */
  @XmlTransient
  private ActionListener              actionListener;

  /** The data mode. */
  @XmlTransient
  private DataMode                    dataMode;

  private Component                   parentCompoent;

  private byte                        learnStickMode;

  /**
   * No Arg Constructor for jaxb
   */
  public AviLightConfigData()
  {
    super();
  }

  /**
   * Instantiates a new avi light config data.
   *
   * @param actionListener
   *          the action listener
   */
  public AviLightConfigData( Component parentComponent, ActionListener actionListener )
  {
    super();
    this.parentCompoent = parentComponent;
    this.actionListener = actionListener;
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
   * Adds the data event listener.
   *
   * @param dataEventListener
   *          the data event listener
   */
  public void addDataEventListener( DataEventListener dataEventListener )
  {
    listener.add( dataEventListener );
  }

  /**
   * Removes the data event listener.
   *
   * @param dataEventListener
   *          the data event listener
   */
  public void removeDataEventListener( DataEventListener dataEventListener )
  {
    listener.remove( dataEventListener );
  }

  /**
   * Send event.
   *
   * @param dataEvent
   *          the data event
   */
  private void sendEvent( final DataEvent dataEvent )
  {
    if ( !listener.isEmpty() )
    {
      SwingUtilities.invokeLater( ( ) ->
      {
        for ( DataEventListener dataEventListener : listener )
        {
          dataEventListener.dataEvent( dataEvent );
        }
      } );
    }
  }

  /**
   * Send action event.
   *
   * @param actionEvent
   *          the action event
   */
  private void sendActionEvent( final ActionEvent actionEvent )
  {
    if ( actionListener != null )
    {
      SwingUtilities.invokeLater( ( ) ->
      {
        System.out.println( "Action: " + actionEvent.getActionCommand() + " Source: "
            + actionEvent.getSource() + " ID: " + actionEvent.getID() );
        actionListener.actionPerformed( actionEvent );
      } );
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
    receiverChannelModes = null;
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

      if ( AviLightProtocol.enableDump || AviLightProtocol.LOG_COMMAND.contains( command.getCmd() ) )
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
      case AviLightProtocol.CMD_PING:
        if ( DataMode.StartInfo.equals( dataMode ) )
        {
          if ( ( (CommEventPing) commEvent ).getFirmwareMode() == CommEventPing.BOOTLOADER )
          {
            sendCommand( Command.PING );
          }
          else if ( ( (CommEventPing) commEvent ).getFirmwareMode() == CommEventPing.BOOTLOADER_NO_APP )
          {
            sendActionEvent( new ActionEvent( this, 0, ACTION_ASK_UPGRADE ) );
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
        break;

      case AviLightProtocol.CMD_CONFIG_CHANGED:
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
        sendCommand( Command.RECEIVER_CHANNEL_MODE );

        sendActionEvent( new ActionEvent( "Lade Daten", ( outputChannels + switchChannels )
            * Constants.SEGMENT_COUNT, ACTION_START_PROGRESS ) );
        break;

      case AviLightProtocol.CMD_GET_RECEIVER_CHANNEL_MODE:
        boolean wasInitialized = receiverChannelModes != null;

        receiverChannelModes = ( (CommEventReceiverChannelMode) commEvent ).getReceiverMode();

        if ( !wasInitialized )
        {
          sendCommand( Command.GET_CONTROLLING_CHANNEL );
        }
        else
        {
          readyToSend.set( true );
          sendActionEvent( new ActionEvent( this, 0, ACTION_STOP_PROGRESS ) );
        }

        sendEvent( DataEvent.getDataEvent( DataEvent.Type.InfoDataReceived ) );
        break;

      case AviLightProtocol.CMD_GET_CONTROLLING_CHANNEL:
        controllingChannel = ( (CommEventControllingChannel) commEvent ).getControllingChannel();

        sendCommand( new GetProgramm( (byte) 0, (byte) 0 ) );
        break;

      case AviLightProtocol.CMD_GET_PROGRAMM:

        byte channel = ( (CommEventProgram) commEvent ).getChannel();
        byte segment = ( (CommEventProgram) commEvent ).getSegment();

        sendActionEvent( new ActionEvent( this, ( channel * Constants.SEGMENT_COUNT ) + segment,
            ACTION_PROGRESS ) );

        ProgramDefinition programDefinition = ( (CommEventProgram) commEvent )
            .getProgramDefinition();
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
          readyToSend.set( true );
          sendActionEvent( new ActionEvent( this, 0, ACTION_STOP_PROGRESS ) );

          sendEvent( DataEvent.getDataEvent( DataEvent.Type.ProgramDataReceived ) );
        }
        break;

      case AviLightProtocol.CMD_SET_PROGRAMM:
      case AviLightProtocol.CMD_SET_CONTROLLING_CHANNEL:
        sendNext();
        break;

      case AviLightProtocol.CMD_RECEIVER:
        receiverChannelData = ( (CommEventReceiver) commEvent ).getChannelData();
        sendCommand( Command.VOLTAGE );

        sendEvent( DataEvent.getDataEvent( DataEvent.Type.ReceiverDataReceived ) );
        break;

      case AviLightProtocol.CMD_TERMINAL:
        sendEvent( new TerminalDataEvent( ( (CommEventText) commEvent ).getText() ) );
        readyToSend.set( true );
        break;

      case AviLightProtocol.CMD_GET_VOLTAGE:
        voltage = ( (CommEventVoltage) commEvent ).getVoltage();
        limit = ( (CommEventVoltage) commEvent ).getLimit();
        sendCommand( Command.CMD_GET_LEARN_STICKMODE );

        sendEvent( DataEvent.getDataEvent( Type.VoltageReceived ) );
        break;

      case AviLightProtocol.CMD_SET_BATTERY_LIMIT:
        voltage = ( (CommEventVoltage) commEvent ).getVoltage();
        limit = ( (CommEventVoltage) commEvent ).getLimit();
        readyToSend.set( true );
        sendEvent( DataEvent.getDataEvent( Type.VoltageReceived ) );

        break;

      case AviLightProtocol.CMD_GET_LEARN_STICKMODE:
      case AviLightProtocol.CMD_SET_LEARN_STICKMODE:
        learnStickMode = ( (CommEventLearnStickMode) commEvent ).getLearnStickMode();
        sendEvent( DataEvent.getDataEvent( DataEvent.Type.LearnStickModeReceived ) );
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
    modification( new ModifiableImpl( new SetControllingChannel( index, value ) ) );
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

        modification( new ModifiableImpl( new SetReceiverChannelMode( (byte) index,
            receiverChannelModes[index] ) ) );
      }
    }
  }

  public void setStickLearnMode( byte stickLearnMode )
  {
    modification( new ModifiableImpl( new SetLearnStickMode( stickLearnMode ) ) );
  }

  public void setVoltageLimit( BigDecimal voltage )
  {
    int vi = 0;
    if ( ( BigDecimal.ZERO.compareTo( voltage ) <= 0 )
        && ( new BigDecimal( "28.4" ).compareTo( voltage ) > 0 ) )
    {
      vi = new BigDecimal( "36" ).multiply( voltage, Constants.ROUND_HALF_UP_PRE_0 ).intValue();
    }

    modification( new ModifiableImpl( new SetVoltageLimit( vi ) ) );
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

  /**
   * To array.
   *
   * @param list
   *          the list
   * @return the byte[]
   */
  private byte[] toArray( List<Byte> list )
  {
    byte[] array = new byte[list.size()];

    for ( int i = 0; i < list.size(); i++ )
    {
      array[i] = list.get( i );
    }

    return array;
  }

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

  /**
   * Sets the receiver modes.
   *
   * @param list
   *          the new receiver modes
   */
  public void setReceiverModes( List<Byte> list )
  {
    receiverChannelModes = toArray( list );
  }

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

  /**
   * Sets the controlling channel list.
   *
   * @param list
   *          the new controlling channel list
   */
  public void setControllingChannelList( List<Byte> list )
  {
    controllingChannel = toArray( list );
  }

  /**
   * Gets the voltage.
   *
   * @return the voltage
   */
  public BigDecimal getVoltage()
  {
    return voltage;
  }

  public BigDecimal getLimit()
  {
    return limit;
  }

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
    if ( readyToSend.get() )
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
   */
  public void firmwareUpgrade()
  {

    memory = new Memory();

    try
    {
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
    catch ( Exception e )
    {
      JOptionPane.showMessageDialog( parentCompoent, e.getMessage(), "Fehler",
          JOptionPane.ERROR_MESSAGE );
    }
  }

  /**
   * Start firmware upgrade.
   */
  void startFirmwareUpgrade()
  {
    dataMode = DataMode.FirmwareUpgradeInProgress;
    sendActionEvent( new ActionEvent( "Firmwareupgrade", memory.getMaxPage(), ACTION_START_PROGRESS ) );
    nextPage( 0 );
  }

  public void saveConfig()
  {
    sendCommand( Command.WRITE_TO_EEPROM );
  }

  public void reloadConfig()
  {
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
      sendActionEvent( new ActionEvent( this, pageNo, ACTION_PROGRESS ) );

      System.out.println( "Programming page " + pageNo );
      sendCommand( new CommandWritePage( AviLightProtocol.CMD_WRITE_PAGE, pageNo * Memory.PAGESIZE,
          pageMem ) );
    }
    else
    {
      sendActionEvent( new ActionEvent( this, 0, ACTION_STOP_PROGRESS ) );

      sendCommand( Command.RESET );
      System.out.println( "Programming done" );

      init();
      // readyToSend.set( false );
      // sendCommand( Command.GET_INFO );

    }
  }

}
