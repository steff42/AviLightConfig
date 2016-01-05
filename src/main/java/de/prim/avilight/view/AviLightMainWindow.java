package de.prim.avilight.view;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import de.prim.avilight.utils.Constants;
import de.prim.avilight.view.events.AviLightActionEvent;
import de.prim.comm.data.AviLightConfigData;
import de.prim.comm.processor.TelegramEscapeByteProcessor;
import de.prim.comm.processor.TelegramSeparationProcessor;
import de.prim.comm.protocol.AviLightProtocol;
import de.prim.comm.transport.TelegramSerialPort;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * @author Steff Lukas
 */
public class AviLightMainWindow extends VBox implements EventHandler<Event>
{

  private Button                 loadData;

  private Button                 saveData;

  private AviLightPaneController controller;

  private FileChooser            fileChooser;

  private Timeline               communicationTimeline;

  private AviLightConfigData     aviLightConfigData;

  private TelegramSerialPort     telegramSerialPort;

  private Stage                  primaryStage;

  private HBox                   progressPane;

  private ProgressBar            progress;

  private double                 progressMaxValue;

  public AviLightMainWindow( Stage primaryStage )
  {
    super();

    aviLightConfigData = new AviLightConfigData( this );
    this.primaryStage = primaryStage;

    this.primaryStage.addEventHandler( WindowEvent.WINDOW_CLOSE_REQUEST, this );
    addEventHandler( AviLightActionEvent.ANY, this );

    setAlignment( Pos.TOP_LEFT );
    setPrefWidth( 1000 );

    initMenuBar();
    initToolBar();
    initAviLightPaneController( primaryStage );
    initCommunicationTimeline();
  }

  private void initToolBar()
  {
    HBox toolBar = new HBox( 5 );
    toolBar.setAlignment( Pos.CENTER_LEFT );
    toolBar.setPadding( new Insets( 5 ) );
    getChildren().add( 1, toolBar );

    loadData = new Button();
    loadData.setGraphic(
        new ImageView( new Image( getClass().getResourceAsStream( "/images/icons/load.gif" ) ) ) );
    loadData.setTooltip( new Tooltip( "Programm verwerfen und vom Modul neu laden" ) );
    loadData.setDisable( true );
    loadData.setOnAction( ActionEvent -> aviLightConfigData.reloadConfig() );
    toolBar.getChildren().add( loadData );

    saveData = new Button();
    saveData.setGraphic(
        new ImageView( new Image( getClass().getResourceAsStream( "/images/icons/save.gif" ) ) ) );
    saveData.setTooltip( new Tooltip( "Programm auf dem Modul speichern" ) );
    saveData.setDisable( true );
    saveData.setOnAction( ActionEvent -> aviLightConfigData.saveConfig() );
    toolBar.getChildren().add( saveData );

    progressPane = new HBox( 20 );
    progressPane.setPadding( new Insets( 0, 0, 0, 15 ) );
    progressPane.setAlignment( Pos.CENTER_LEFT );
    progressPane.setVisible( false );
    toolBar.getChildren().add( progressPane );

    progressPane.getChildren().add( new Label( "Laden:" ) );
    progress = new ProgressBar();
    progressPane.getChildren().add( progress );

  }

  private void initMenuBar()
  {
    MenuBar menuBar = new MenuBar();
    menuBar.setUseSystemMenuBar( true );
    menuBar.prefWidthProperty().bind( this.widthProperty() );
    getChildren().add( 0, menuBar );

    Menu aviLight = new Menu( "AviLight" );
    menuBar.getMenus().add( aviLight );

    MenuItem load = new MenuItem( "Laden" );
    load.setOnAction( ActionEvent -> load() );
    aviLight.getItems().add( load );

    MenuItem save = new MenuItem( "Speichern" );
    save.setOnAction( ActionEvent -> save() );
    aviLight.getItems().add( save );

    SeparatorMenuItem separator = new SeparatorMenuItem();
    aviLight.getItems().add( separator );

    MenuItem exit = new MenuItem( "Beenden" );
    exit.setOnAction( ActionEvent -> Event.fireEvent( primaryStage,
        new WindowEvent( primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST ) ) );
    aviLight.getItems().add( exit );

    fileChooser = new FileChooser();

  }

  private void initAviLightPaneController( Stage primaryStage )
  {
    controller = new AviLightPaneController( primaryStage, this );
    getChildren().add( 2, controller );

    controller.addPane( ConfigPane.CONFIG_PANE_NAME, new ConfigPane( controller ) );
    controller.addPane( ConnectPane.CONNECT_PANE_NAME, new ConnectPane( controller ) );

    controller.setPane( ConnectPane.CONNECT_PANE_NAME );

  }

  private void initCommunicationTimeline()
  {
    communicationTimeline = new Timeline(
        new KeyFrame( Duration.millis( 500 ), ActionEvent -> checkConnection() ) );
    communicationTimeline.setCycleCount( Animation.INDEFINITE );
  }

  private void checkConnection()
  {
    if ( !aviLightConfigData.cyclicCommunication() )
    {
      disconnect();
    }
  }

  public void connect( TelegramSerialPort telegramSerialPort )
  {
    controller.setPane( ConfigPane.CONFIG_PANE_NAME );

    try
    {
      this.telegramSerialPort = telegramSerialPort;

      TelegramEscapeByteProcessor sender = new TelegramEscapeByteProcessor( telegramSerialPort );

      aviLightConfigData.setSender( sender );

      AviLightProtocol aviLightProtocol = new AviLightProtocol( aviLightConfigData );

      telegramSerialPort.setTelegramSeparator(
          new TelegramSeparationProcessor( aviLightProtocol, Constants.BUFFER_SIZE ) );

      loadData.setDisable( false );
      saveData.setDisable( false );

      aviLightConfigData.init();
      communicationTimeline.play();
    }
    catch ( Exception e )
    {
      showErrorMessage( e );

      e.printStackTrace();
    }
  }

  protected void disconnect()
  {
    closeSerialPort();

    loadData.setDisable( true );
    saveData.setDisable( true );

    controller.setPane( ConnectPane.CONNECT_PANE_NAME );
  }

  private void closeSerialPort()
  {
    communicationTimeline.stop();
    aviLightConfigData.close();

    if ( telegramSerialPort != null )
    {
      telegramSerialPort.close();
      telegramSerialPort = null;
    }
  }

  protected void load()
  {
    fileChooser.setTitle( "Konfiguration Laden" );

    File loadFile = fileChooser.showOpenDialog( controller.getStage() );

    if ( loadFile != null )
    {
      try
      {
        JAXBContext jaxbContext = JAXBContext.newInstance( AviLightConfigData.class );

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        AviLightConfigData data = (AviLightConfigData) unmarshaller.unmarshal( loadFile );

        System.out.println( data );
      }

      catch ( Exception e )
      {
        showErrorMessage( e );

        e.printStackTrace();
      }
    }
  }

  protected void save()
  {
    fileChooser.setTitle( "Konfiguration Speichern" );
    File saveFile = fileChooser.showSaveDialog( controller.getStage() );

    if ( saveFile != null )
    {
      try
      {
        JAXBContext jaxbContext = JAXBContext.newInstance( AviLightConfigData.class );

        Marshaller marshaller = jaxbContext.createMarshaller();

        marshaller.marshal( aviLightConfigData, saveFile );
      }
      catch ( Exception e )
      {
        showErrorMessage( e );

        e.printStackTrace();
      }
    }

  }

  public static void showErrorMessage( Exception e )
  {
    Alert alert = new Alert( AlertType.ERROR );
    alert.setTitle( "Fehler" );
    alert.initStyle( StageStyle.UNDECORATED );
    alert.setHeaderText( e.getClass().getName() );
    alert.setContentText( e.getMessage() == null ? e.getClass().getName() : e.getMessage() );
    alert.getButtonTypes().clear();
    alert.getButtonTypes().add( ButtonType.CLOSE );
    alert.show();
  }

  public AviLightConfigData getAviLightConfigData()
  {
    return aviLightConfigData;
  }

  public TelegramSerialPort getTelegramSerialPort()
  {
    return telegramSerialPort;
  }

  public void setTelegramSerialPort( TelegramSerialPort telegramSerialPort )
  {
    this.telegramSerialPort = telegramSerialPort;
  }

  /** {@inheritDoc} */
  @Override
  public void handle( Event event )
  {
    if ( WindowEvent.WINDOW_CLOSE_REQUEST == event.getEventType() )
    {
      close();
    }
    if ( AviLightActionEvent.ANY == event.getEventType()
        || AviLightActionEvent.ANY == event.getEventType().getSuperType() )
    {
      handleAviLightActionEvent( (AviLightActionEvent) event );
    }

  }

  private void handleAviLightActionEvent( AviLightActionEvent aviLightActionEvent )
  {
    EventType<? extends Event> eventType = aviLightActionEvent.getEventType();

    if ( eventType == AviLightActionEvent.START_PROGRESS )
    {
      progressMaxValue = aviLightActionEvent.getActionData();
      progressPane.setVisible( true );
      progress.setProgress( 0.0 );
    }
    else if ( eventType == AviLightActionEvent.PROGRESS )
    {
      progress.setProgress( aviLightActionEvent.getActionData() / progressMaxValue );
    }
    else if ( eventType == AviLightActionEvent.STOP_PROGRESS )
    {
      progressPane.setVisible( false );
    }
  }

  private void close()
  {
    closeSerialPort();
  }

}
