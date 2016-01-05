package de.prim.avilight.view.configtabs;

import de.prim.avilight.model.AviLightOutputConfiguration;
import de.prim.avilight.model.ProgramDefinition;
import de.prim.avilight.model.ReceiverInput;
import de.prim.avilight.view.AviLightMainWindow;
import de.prim.avilight.view.controls.ProgramDataTableCell;
import de.prim.avilight.view.dialogues.OutputConfigDialog;
import de.prim.avilight.view.events.AviLightDataEvent;
import de.prim.comm.data.AviLightConfigData;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;

/**
 * The Class SwitchChannelPane.
 */
public class SwitchChannelPane extends AviLightTab
{

  /** The availabe receiver inputs. */
  private ReceiverInput[]                                         availabeReceiverInputs;

  /** The number of switch channels. */
  private int                                                     numberOfSwitchChannels;

  /** The output configurations. */
  AviLightOutputConfiguration[]                                   outputConfigurations;

  /** The table. */
  private TableView<AviLightOutputConfiguration>                  table;

  /** The receiver input col. */
  private TableColumn<AviLightOutputConfiguration, ReceiverInput> receiverInputCol;

  /**
   * Instantiates a new switch channel pane.
   *
   * @param aviLightConfigData
   *          the avi light config data
   * @param mainWindow
   *          the main window
   */
  @SuppressWarnings ( "unchecked")
  public SwitchChannelPane( AviLightConfigData aviLightConfigData, AviLightMainWindow mainWindow )
  {
    super( aviLightConfigData, mainWindow );

    this.numberOfSwitchChannels = aviLightConfigData.getSwitchChannels();

    initOutputConfigurations();

    this.availabeReceiverInputs = ReceiverInput
        .getAvailableReceiverInputs( aviLightConfigData.getReceiverChannels() );

    table = new TableView<>();
    table.setEditable( true );
    this.getChildren().add( table );

    TableColumn<AviLightOutputConfiguration, Number> outputChannel = new TableColumn<>( "Ausgang" );
    outputChannel.setCellValueFactory( param -> param.getValue().getOutputChannelProperty() );
    outputChannel.setEditable( false );
    outputChannel.setSortable( false );
    table.getColumns().add( outputChannel );

    receiverInputCol = new TableColumn<>( "Empfängereingang" );
    receiverInputCol.setCellValueFactory( param -> param.getValue().getReceiverInputProperty() );
    receiverInputCol.setCellFactory( ComboBoxTableCell.forTableColumn( availabeReceiverInputs ) );
    receiverInputCol.setOnEditCommit( event -> receiverInputUpdated( event ) );
    receiverInputCol.setEditable( true );
    receiverInputCol.setSortable( false );
    receiverInputCol.setPrefWidth( 150 );
    table.getColumns().add( receiverInputCol );

    OutputConfigDialog dialog = new OutputConfigDialog();
    TableColumn<AviLightOutputConfiguration, ProgramDefinition>[] segments = new TableColumn[7];

    for ( int i = 0; i < 7; i++ )
    {
      segments[i] = new TableColumn<>( getSegmentColumnTitle( i ) );
      segments[i].setSortable( false );
      segments[i].setEditable( true );
      segments[i].setPrefWidth( 200 );

      final int currentSegment = i;
      segments[i].setCellValueFactory(
          param -> param.getValue().getProgramDefinitionAsProperty( currentSegment ) );
      segments[i]
          .setCellFactory( ProgramDataTableCell.forTableColumn( dialog, aviLightConfigData ) );
      table.getColumns().add( segments[i] );
    }

  }

  /**
   * Gets the segment column title.
   *
   * @param segment
   *          the segment
   * @return the segment column title
   */
  private String getSegmentColumnTitle( int segment )
  {
    if ( segment >= 0 && segment < 5 )
    {
      return "Segement " + ( segment + 1 );
    }
    else if ( segment == 5 )
    {
      return "kein Eingang";
    }
    else if ( segment == 6 )
    {
      return "Akku leer";
    }

    return null;
  }

  /**
   * Inits the output configurations.
   */
  private void initOutputConfigurations()
  {
    this.outputConfigurations = new AviLightOutputConfiguration[this.numberOfSwitchChannels];
    for ( int i = 0; i < this.numberOfSwitchChannels; i++ )
    {
      outputConfigurations[i] = new AviLightOutputConfiguration( 7, i + 1, null,
          new ProgramDefinition[7], null, null );
    }
  }

  /**
   * Receiver input updated.
   *
   * @param event
   *          the event
   */
  private void receiverInputUpdated(
      CellEditEvent<AviLightOutputConfiguration, ReceiverInput> event )
  {
    AviLightOutputConfiguration config = event.getRowValue();
    ReceiverInput newValue = event.getNewValue();

    aviLightConfigData.setControllingChannel( (byte) ( config.getOutputChannel() - 1 ),
        (byte) newValue.getReceiverChannelNo() );
  }

  /** {@inheritDoc} */
  @Override
  protected void handleAviLightDataEvent( AviLightDataEvent event )
  {
    if ( AviLightDataEvent.INFO_DATA_RECEIVED == event.getEventType() )
    {
      if ( availabeReceiverInputs.length - 1 != aviLightConfigData.getReceiverChannels() )
      {
        availabeReceiverInputs = ReceiverInput
            .getAvailableReceiverInputs( aviLightConfigData.getReceiverChannels() );
        receiverInputCol
            .setCellFactory( ComboBoxTableCell.forTableColumn( this.availabeReceiverInputs ) );
      }

      if ( numberOfSwitchChannels != aviLightConfigData.getSwitchChannels() )
      {
        numberOfSwitchChannels = aviLightConfigData.getSwitchChannels();
        initOutputConfigurations();
        table.getItems().clear();
      }

    }
    else if ( AviLightDataEvent.CONTROLLING_CHANNEL_RECEIVED == event.getEventType() )
    {
      byte[] controllingChannel = aviLightConfigData.getControllingChannel();
      for ( int i = 0; i < Math.min( outputConfigurations.length, controllingChannel.length ); i++ )
      {
        AviLightOutputConfiguration current = outputConfigurations[i];
        ReceiverInput currentInput = current.getReceiverInput();
        if ( currentInput == null || currentInput.getReceiverChannelNo() != controllingChannel[i] )
        {
          current.setReceiverInput( availabeReceiverInputs[controllingChannel[i]] );
        }
      }
    }
    if ( AviLightDataEvent.PROGRAM_DATA_RECEIVED == event.getEventType() )
    {
      ProgramDefinition[][] outputs = aviLightConfigData.getSwitchChannelDefinition();

      for ( int i = 0; i < outputConfigurations.length; i++ )
      {
        outputConfigurations[i].setSegmentPrograms( outputs[i] );
      }

      table.getItems().clear();
      table.getItems().addAll( outputConfigurations );
    }
  }

}
