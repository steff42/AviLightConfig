package de.prim.avilight.view;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.prim.comm.CommWrapper;
import de.prim.comm.protocol.ProtocolTester;
import de.prim.comm.protocol.ProtocolTester.Mode;
import de.prim.comm.transport.TelegramSerialPort;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

/**
 * The Class ConnectPane.
 *
 * @author Steff Lukas
 */
public class ConnectPane extends AviLightControlledPane
{

  /** The Constant CONNECT_PANE_NAME. */
  public static final String          CONNECT_PANE_NAME = "connectPane";

  // AviLightPaneController controller;

  /** The content pane. */
  BorderPane                          contentPane;

  /** The connect button. */
  Button                              connectButton;

  /** The port list view. */
  ListView<String>                    portListView;

  /** The timeline. */
  Timeline                            timeline;

  /** The selected index. */
  private int                         selectedIndex     = -1;

  /** The port list. */
  private List<String>                portList;

  /** The ports. */
  private Map<String, ProtocolTester> ports;

  /** The selected com port. */
  private String                      selectedComPort;

  /** The selected ProtocolTester. */
  private ProtocolTester              selectedProtocolTester;

  /**
   * Instantiates a new connect pane.
   *
   * @param controller
   *          the controller
   */
  public ConnectPane( AviLightPaneController controller )
  {
    super( controller );

    initContents();
    initConnectPanel();
  }

  /**
   * Inits the contents.
   */
  protected void initContents()
  {
    contentPane = new BorderPane();
    getChildren().add( contentPane );

    HBox top = new HBox( 10 );
    top.setPadding( new Insets( 10 ) );
    top.setBorder( new Border( new BorderStroke( Paint.valueOf( "#000000" ),
        BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths( 0, 0, 1, 0 ) ) ) );
    contentPane.setTop( top );
    top.setAlignment( Pos.CENTER_LEFT );

    Label label = new Label( "Bitte AviLight anstecken und gewünschten Port auswählen." );
    top.getChildren().add( label );

    connectButton = new Button( "Verbinden" );
    connectButton.setDisable( true );
    connectButton.setOnAction( ActionEvent -> connect() );
    top.getChildren().add( connectButton );

    portListView = new ListView<>();
    portListView.setOnMouseClicked( actionEvent -> selectPort( actionEvent ) );
    portListView.getSelectionModel().setSelectionMode( SelectionMode.SINGLE );
    portListView.getSelectionModel().selectedIndexProperty()
        .addListener( (ChangeListener<Number>) ( observable, oldValue, newValue ) ->
        {
          if ( newValue.intValue() >= 0 )
          {
            selectedIndex = newValue.intValue();
            selectedComPort = portList.get( selectedIndex );
            enableOkButton();
          }
        } );
    contentPane.setCenter( portListView );

    timeline = new Timeline( new KeyFrame( Duration.millis( 500 ), ActionEvent -> doTimer() ) );
    timeline.setCycleCount( Animation.INDEFINITE );

  }

  /**
   * Select port.
   *
   * @param actionEvent
   *          the action event
   */
  private void selectPort( MouseEvent actionEvent )
  {
    if ( actionEvent.getClickCount() == 2 )
    {
      connect();
    }
  }

  /**
   * Inits the connect panel.
   */
  public void initConnectPanel()
  {
    cleanupPorts();

    ports = new HashMap<String, ProtocolTester>();
    portList = CommWrapper.listComPorts();
    System.out.println( Arrays.asList( portList.toArray() ) );
    portListView.getItems().clear();

    for ( String portName : portList )
    {
      portListView.getItems().add( portName );
      ports.put( portName, new ProtocolTester( portName ) );
    }

    timeline.play();
  }

  /**
   * Cleanup ports.
   */
  private void cleanupPorts()
  {
    if ( ports != null && !ports.isEmpty() )
    {
      for ( ProtocolTester protocolTester : ports.values() )
      {
        protocolTester.close();
      }
      ports.clear();
    }
  }

  /**
   * Do timeline.
   */
  protected void doTimer()
  {
    ObservableList<String> items = portListView.getItems();
    for ( int i = 0; i < portList.size(); i++ )
    {
      String comPort = portList.get( i );
      Mode mode = ports.get( comPort ).getMode();

      if ( mode != null )
      {
        String currentValue = comPort + " - " + mode.getText();
        if ( !currentValue.equals( items.get( i ) ) )
        {
          items.set( i, currentValue );
        }
      }
    }
    enableOkButton();
  }

  /**
   * Enable ok button.
   */
  protected void enableOkButton()
  {
    if ( selectedComPort != null )
    {

      // portListView.getSelectionModel().select( selectedIndex );

      connectButton.setDisable( !Mode.CONNECTED.equals( ports.get( selectedComPort ).getMode() ) );
    }

    if ( connectButton.isDisabled() )
    {
      selectedComPort = null;
    }
  }

  /**
   * Connect.
   */
  protected void connect()
  {
    timeline.stop();

    selectedProtocolTester = ports.remove( selectedComPort );
    TelegramSerialPort serialPort = selectedProtocolTester.dispose();

    cleanupPorts();
    portListView.getItems().clear();
    connectButton.setDisable( true );
    selectedComPort = null;
    selectedIndex = -1;

    controller.getMainWindow().connect( serialPort );
  }

  /**
   * Gets the selected protocol tester.
   *
   * @return the selected protocol tester
   */
  public ProtocolTester getSelectedProtocolTester()
  {
    return selectedProtocolTester;
  }

  /** {@inheritDoc} */
  @Override
  protected void close()
  {
    timeline.stop();
    cleanupPorts();
  }
}
