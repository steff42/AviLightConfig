package de.prim.avilight.view.configtabs;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Optional;

import de.prim.avilight.view.AviLightMainWindow;
import de.prim.avilight.view.events.AviLightDataEvent;
import de.prim.comm.data.AviLightConfigData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;

/**
 * The Class InfoPane.
 */
public class InfoPane extends AviLightTab
{

  /** The content pane. */
  private GridPane                   contentPane;

  /** The firm ware. */
  private Label                      firmWare;

  /** The high current outputs. */
  private Label                      highCurrentOutputs;

  /** The low current outputs. */
  private Label                      lowCurrentOutputs;

  /** The receiver inputs. */
  private Label                      receiverInputs;

  /** The voltage. */
  private Label                      voltage;

  /** The limit. */
  private Label                      limit;

  /** The Constant VOLTAGE_FORMATTER. */
  private static final DecimalFormat VOLTAGE_FORMATTER = new DecimalFormat( "##0.00" );

  /**
   * Instantiates a new info pane.
   *
   * @param aviLightConfigData
   *          the avi light config data
   */
  public InfoPane( AviLightConfigData aviLightConfigData, AviLightMainWindow mainWindow )
  {
    super( aviLightConfigData, mainWindow );

    contentPane = new GridPane();
    contentPane.setAlignment( Pos.TOP_LEFT );
    contentPane.setHgap( 10 );
    contentPane.setVgap( 20 );
    contentPane.setPadding( new Insets( 10 ) );
    getChildren().add( contentPane );

    // Button button;

    contentPane.add( new Label( "Firmware Version" ), 0, 0 );
    firmWare = new Label();
    contentPane.add( firmWare, 1, 0 );

    // button = new JButton( "Firmware upgrade" );
    // panel.add( button );
    // button.addActionListener( new ActionListener()
    // {
    // @Override
    // public void actionPerformed(ActionEvent actionEvent)
    // {
    // InfoPane.this.aviLightConfigData.firmwareUpgrade();
    // }
    // } );

    contentPane.add( new Label( "PWM-Ausgänge" ), 0, 2 );
    highCurrentOutputs = new Label();
    contentPane.add( highCurrentOutputs, 1, 2 );

    contentPane.add( new Label( "Schaltausgänge" ), 0, 3 );
    lowCurrentOutputs = new Label();
    contentPane.add( lowCurrentOutputs, 1, 3 );

    contentPane.add( new Label( "Empfängereingänge" ), 0, 4 );
    receiverInputs = new Label();
    contentPane.add( receiverInputs, 1, 4 );

    contentPane.add( new Label( "Akkuspannung" ), 0, 5 );
    voltage = new Label();
    contentPane.add( voltage, 1, 5 );

    NumberFormat numberFormat = NumberFormat.getInstance();
    numberFormat.setMaximumFractionDigits( 2 );
    numberFormat.setMaximumIntegerDigits( 2 );

    contentPane.add( new Label( "Spannungsgrenze" ), 0, 6 );
    limit = new Label();
    contentPane.add( limit, 1, 6 );

    Button button = new Button( "setzen" );
    contentPane.add( button, 3, 6 );
    button.setOnAction( ActionEvent -> showVoltageLimitDialog() );

    // this.aviLightConfigData.addDataEventListener( this );
  }

  /**
   * Show voltage limit dialog.
   */
  private void showVoltageLimitDialog()
  {
    TextInputDialog dialog = new TextInputDialog( limit.getText() );
    dialog.setTitle( "Spannungsgrenze ändern" );
    dialog.initStyle( StageStyle.UTILITY );
    dialog.setHeaderText( "Bitte eine neue Spannungsgrenze zwischen 0 V und 28,4 V eingeben!" );
    dialog.setContentText( "Neue Spannungsgrenze" );

    Optional<String> result = dialog.showAndWait();

    result.ifPresent( newValue ->
    {
      newValue = newValue.replace( ",", "." );
      try
      {
        BigDecimal newLimit = new BigDecimal( newValue );
        InfoPane.this.aviLightConfigData.setVoltageLimit( newLimit );
      }
      catch ( NumberFormatException e )
      {
        AviLightMainWindow.showErrorMessage( e );
        e.printStackTrace();
      }

    } );
  }

  /** {@inheritDoc} */
  @Override
  public void handleAviLightDataEvent( AviLightDataEvent event )
  {
    if ( AviLightDataEvent.INFO_DATA_RECEIVED == event.getEventType() )
    {
      firmWare.setText( aviLightConfigData.getFirmwareVersion() );
      highCurrentOutputs.setText( String.valueOf( aviLightConfigData.getOutputChannels() ) );
      lowCurrentOutputs.setText( String.valueOf( aviLightConfigData.getSwitchChannels() ) );
      receiverInputs.setText( String.valueOf( aviLightConfigData.getReceiverChannels() ) );
    }
    else if ( AviLightDataEvent.VOLTAGE_RECEIVED == event.getEventType() )
    {
      voltage.setText( VOLTAGE_FORMATTER.format( aviLightConfigData.getVoltage() ) );
    }
    else if ( AviLightDataEvent.BATTERY_LIMIT_RECEIVED == event.getEventType() )
    {
      limit.setText( VOLTAGE_FORMATTER.format( aviLightConfigData.getLimit() ) );
    }
  }

}
