package de.prim.avilight.view.dialogues;

import java.math.BigDecimal;

import de.prim.avilight.model.LightModeAlgorithms;
import de.prim.avilight.model.ProgramDefinition;
import de.prim.avilight.model.lightmodes.LightMode;
import de.prim.avilight.view.converter.AviLightBigDecimalConverter;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * The Class OutputConfigDialog.
 *
 * @author Steff Lukas
 */
public class OutputConfigDialog extends Dialog<ProgramDefinition>
{

  /** The channel. */
  private byte                        channel;

  /** The segment. */
  private byte                        segment;

  /** The light mode. */
  private LightMode                   lightMode;

  /** The flashes. */
  private int                         flashes;

  /** The flash duration. */
  private BigDecimal                  flashDuration;

  /** The off duration. */
  private BigDecimal                  offDuration;

  /** The converter. */
  private AviLightBigDecimalConverter converter;

  /** The lightModeCB. */
  private ComboBox<LightMode>         lightModeCB;

  /** The flashesSlider. */
  private Slider                      flashesSlider;

  /** The flash duration. */
  private TextField                   flashDurationField;

  /** The off duration. */
  private TextField                   offDurationField;

  /** The status. */
  private Label                       status;

  private Node                        okButton;

  /**
   * Instantiates a new output config dialog.
   *
   * @param aviLightConfigData
   *          the avi light config data
   * @param data
   *          the data
   */
  public OutputConfigDialog()
  {
    super();
    converter = new AviLightBigDecimalConverter();
    setResizable( true );
    setTitle( "Programm ändern" );
    addGUIContents();

    setResultConverter( param -> convertResult( param ) );
  }

  /**
   * Convert result.
   *
   * @param param
   *          the param
   * @return the program definition
   */
  private ProgramDefinition convertResult( ButtonType param )
  {
    if ( ButtonType.OK == param )
    {
      flashes = (int) ( lightMode.hasNumberOfFlashes() ? flashesSlider.getValue() : 1 );

      if ( lightMode.hasDuration() && !lightMode.hasOffDuration() )
      {
        /*
         * The reason for this swap is, that in case of no off duration the real
         * duration is stored in this field, because it has more space, and
         * therefore longer durations can be saved.
         */
        offDuration = flashDuration;
        flashDuration = BigDecimal.ZERO;
      }
      else if ( !lightMode.hasDuration() )
      {
        flashDuration = BigDecimal.ZERO;
        offDuration = BigDecimal.ZERO;
      }

      return new ProgramDefinition( channel, segment, lightMode, offDuration, flashDuration,
          flashes );
    }

    return null;
  }

  /**
   * Update field values.
   */
  private void updateControls()
  {
    updateFlashesSpinner();
    updateDurationField();
    updateOffDurationField();
  }

  /**
   * Update duration fields.
   */
  private void updateDurationField()
  {
    if ( lightMode.hasDuration() )
    {
      flashDurationField.setText(
          converter.toString( lightMode.hasOffDuration() ? flashDuration : offDuration ) );
      flashDurationField.setDisable( false );
    }
    else
    {
      flashDurationField.setText( null );
      flashDurationField.setDisable( true );
    }
  }

  /**
   * Update off duration field.
   */
  private void updateOffDurationField()
  {
    offDurationField
        .setText( lightMode.hasOffDuration() ? converter.toString( offDuration ) : null );
    offDurationField.setDisable( !lightMode.hasOffDuration() );
  }

  /**
   * Update flashesSlider.
   */
  private void updateFlashesSpinner()
  {
    if ( lightMode.hasNumberOfFlashes() )
    {
      flashesSlider.setMin( lightMode.getMinNumber() );
      flashesSlider.setMax( lightMode.getMaxNumber() );
      flashesSlider.setValue( flashes );
      flashesSlider.setDisable( false );
    }
    else
    {
      flashesSlider.setValue( 0 );
      flashesSlider.setDisable( true );
    }
  }

  /**
   * Adds the gui contents.
   */
  private void addGUIContents()
  {
    GridPane gridPane = new GridPane();
    gridPane.setHgap( 5 );
    gridPane.setVgap( 5 );
    gridPane.setPadding( new Insets( 10 ) );
    gridPane.setMinWidth( 500 );

    getDialogPane().setContent( gridPane );

    gridPane.add( new Label( "Modus:" ), 0, 0 );
    lightModeCB = new ComboBox<>(
        FXCollections.observableArrayList( LightModeAlgorithms.getAllLightModes() ) );
    gridPane.add( lightModeCB, 1, 0 );

    lightModeCB.valueProperty().addListener( ( observable, oldValue, newValue ) ->
    {
      lightMode = newValue;
      updateControls();
      validateControls();
    } );

    gridPane.add( new Label( "Anzahl:" ), 0, 1 );
    flashesSlider = new Slider();
    flashesSlider.setMajorTickUnit( 1 );
    flashesSlider.setMinorTickCount( 0 );
    flashesSlider.setMin( 1 );
    flashesSlider.setMax( 4 );
    flashesSlider.setShowTickMarks( true );
    flashesSlider.setShowTickLabels( true );
    flashesSlider.setSnapToTicks( true );
    flashesSlider.valueProperty()
        .addListener( ( observable, oldValue, newValue ) -> flashes = newValue.intValue() );
    gridPane.add( flashesSlider, 1, 1 );

    gridPane.add( new Label( "Dauer [s]:" ), 0, 2 );
    flashDurationField = new TextField();
    flashDurationField.setAlignment( Pos.CENTER_RIGHT );
    flashDurationField.textProperty().addListener( ( observable, oldValue, newValue ) ->
    {
      flashDuration = converter.fromString( newValue );
      validateControls();
    } );
    gridPane.add( flashDurationField, 1, 2 );

    gridPane.add( new Label( "Pause [s]:" ), 0, 3 );
    offDurationField = new TextField();
    offDurationField.setAlignment( Pos.CENTER_RIGHT );
    offDurationField.textProperty().addListener( ( observable, oldValue, newValue ) ->
    {
      offDuration = converter.fromString( newValue );
      validateControls();
    } );

    gridPane.add( offDurationField, 1, 3 );

    status = new Label();
    status.setWrapText( true );
    status.setPrefHeight( 50 );
    gridPane.add( status, 0, 4, 2, 2 );

    getDialogPane().getButtonTypes().addAll( ButtonType.CANCEL, ButtonType.OK );

    okButton = getDialogPane().lookupButton( ButtonType.OK );
  }

  /**
   * Enable controls.
   */
  private void validateControls()
  {
    StringBuilder statusText = new StringBuilder();
    String currentStatus = lightMode.validateDuration( flashDuration );
    if ( currentStatus != null )
    {
      statusText.append( currentStatus );
    }

    currentStatus = lightMode.validateOffDuration( offDuration );
    if ( currentStatus != null )
    {
      if ( statusText.length() > 0 )
      {
        statusText.append( ", " );
      }
      statusText.append( currentStatus );
    }

    if ( statusText.length() > 0 )
    {
      status.setText( statusText.toString() );
      okButton.setDisable( true );
    }
    else
    {
      status.setText( null );
      okButton.setDisable( false );
    }
  }

  public void initializeDialog( ProgramDefinition programDefinition )
  {
    System.err.println( programDefinition.toString() );
    this.channel = programDefinition.getChannel();
    this.segment = programDefinition.getSegment();
    this.lightModeCB.setValue( programDefinition.getLightMode() );
    this.flashesSlider.setValue( programDefinition.getFlashCount() );
    this.flashDurationField.setText( converter.toString( lightMode.hasOffDuration()
        ? programDefinition.getFlashDuration() : programDefinition.getPeriodDuration() ) );
    this.offDurationField.setText( converter.toString(
        lightMode.hasOffDuration() ? programDefinition.getPeriodDuration() : BigDecimal.ZERO ) );
  }
}
