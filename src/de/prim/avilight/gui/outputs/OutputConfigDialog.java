package de.prim.avilight.gui.outputs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.math.BigDecimal;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.prim.avilight.gui.dlg.AviLightDialog;
import de.prim.avilight.gui.dlg.BigDecimalDocument;
import de.prim.avilight.gui.dlg.FlashingModeComboBoxModel;
import de.prim.avilight.gui.dlg.lightmode.LightMode;
import de.prim.comm.data.AviLightConfigData;
import de.prim.comm.event.ProgramDefinition;

/**
 * The Class OutputConfigDialog.
 */
public class OutputConfigDialog extends AviLightDialog implements DocumentListener
{

  /** The Constant serialVersionUID. */
  private static final long    serialVersionUID = 5355108811917000166L;

  /** The mode. */
  private JComboBox<LightMode> mode;

  /** The flashes. */
  private JSlider              flashes;

  /** The flash duration. */
  private BigDecimalDocument   flashDuration;

  /** The off duration. */
  private BigDecimalDocument   offDuration;

  /** The avi light config data. */
  private AviLightConfigData   aviLightConfigData;

  /** The program definition. */
  private ProgramDefinition    programDefinition;

  /**
   * Instantiates a new output config dialog.
   *
   * @param parent
   *          the parent
   * @param aviLightConfigData
   *          the avi light config data
   * @param data
   *          the data
   */
  public OutputConfigDialog( Frame parent, AviLightConfigData aviLightConfigData,
      ProgramDefinition data )
  {
    super( parent, "Ausgang - Segment" );

    this.aviLightConfigData = aviLightConfigData;
    this.programDefinition = data;

    if ( data.getAlgorithm() < FlashingModeComboBoxModel.CHANNEL_MODES.length )
    {
      mode.setSelectedIndex( data.getAlgorithm() );
    }
    else
    {
      mode.setSelectedIndex( FlashingModeComboBoxModel.MODE_OFF );
    }

    updateControls();
    validateControls();
  }

  /**
   * Update field values.
   */
  private void updateControls()
  {
    int index = mode.getSelectedIndex();
    LightMode lightMode = mode.getItemAt( index );

    updateFlashesSpinner( lightMode );
    updateDurationField( lightMode );
    updateOffDurationField( lightMode );
  }

  /**
   * Update duration fields.
   *
   * @param lightMode
   *          the light mode
   */
  private void updateDurationField( LightMode lightMode )
  {
    if ( lightMode.hasDuration() )
    {
      flashDuration.setValue( lightMode.hasOffDuration() ? programDefinition.getFlashDuration()
          : programDefinition.getDuration() );
      flashDuration.getTextField().setEnabled( true );
    }
    else
    {
      flashDuration.setValue( null );
      flashDuration.getTextField().setEnabled( false );
    }
  }

  private void updateOffDurationField( LightMode lightMode )
  {
    offDuration.setValue( lightMode.hasOffDuration() ? programDefinition.getDuration() : null );
    offDuration.getTextField().setEnabled( lightMode.hasOffDuration() );
  }

  /**
   * Update flashes.
   *
   * @param lightMode
   *          the light mode
   */
  private void updateFlashesSpinner( LightMode lightMode )
  {
    if ( lightMode.hasNumberOfFlashes() )
    {
      flashes.setMinimum( lightMode.getMinNumber() );
      flashes.setMaximum( lightMode.getMaxNumber() );
      flashes.setPaintTicks( true );
      flashes.setPaintLabels( true );
      flashes.setValue( programDefinition.getNumberOfFlashes() );
      flashes.setEnabled( true );
    }
    else
    {
      flashes.setPaintTicks( false );
      flashes.setPaintLabels( false );
      flashes.setValue( 0 );
      flashes.setEnabled( false );
    }
  }

  /** {@inheritDoc} */
  @Override
  protected void addGUIContents()
  {
    getContentPane().setLayout( new BorderLayout() );

    JPanel inputs = new JPanel();
    getContentPane().add( inputs );
    inputs.setLayout( new GridLayout( 4, 2, 5, 5 ) );

    inputs.add( new JLabel( "Modus:" ) );
    mode = new JComboBox<LightMode>( new FlashingModeComboBoxModel() );
    inputs.add( mode );
    mode.addActionListener( ActionEvent ->
    {
      updateControls();
      validateControls();
    } );

    inputs.add( new JLabel( "Anzahl:" ) );
    flashes = new JSlider();
    flashes.setMajorTickSpacing( 1 );
    flashes.setPaintTicks( true );
    flashes.setPaintLabels( true );
    flashes.setSnapToTicks( true );
    inputs.add( flashes );

    inputs.add( new JLabel( "Dauer [s]:" ) );
    JTextField durationTextField = new JTextField( null, 2 );
    flashDuration = new BigDecimalDocument( durationTextField );
    inputs.add( durationTextField );
    flashDuration.getTextField().getDocument().addDocumentListener( this );

    inputs.add( new JLabel( "Pause [s]:" ) );
    JTextField offDurationtextField = new JTextField( null, 2 );
    offDuration = new BigDecimalDocument( offDurationtextField );
    inputs.add( offDurationtextField );
    offDuration.getTextField().getDocument().addDocumentListener( this );
  }

  /**
   * Enable controls.
   */
  protected void validateControls()
  {
    int index = mode.getSelectedIndex();
    LightMode flashingMode = mode.getItemAt( index );

    StringBuilder statusText = new StringBuilder();
    String currentStatus = flashingMode.validateDuration( flashDuration.getValue() );
    if ( currentStatus != null )
    {
      statusText.append( currentStatus );
    }

    currentStatus = flashingMode.validateOffDuration( offDuration.getValue() );
    if ( currentStatus != null )
    {
      if ( statusText.length() > 0 )
      {
        statusText.append( ", " );
      }
      statusText.append( currentStatus );
    }

    // if ( index == FlashingModeComboBoxModel.MODE_FLASH )
    // {
    // if ( flashDuration.getValue().doubleValue() < 0.01 )
    // {
    // statusText = "Mindestdauer is 0.01s";
    // }
    // else if ( flashDuration.getValue().doubleValue() > 0.5 )
    // {
    // statusText = "max. Dauer ist 0,5s";
    // }
    // else if ( offDuration.getValue().doubleValue() < 0.01 )
    // {
    // statusText = "min. Pause ist 0.01s";
    // }
    // else if ( offDuration.getValue().doubleValue() > 500.0 )
    // {
    // statusText = "max. Pause ist 500s";
    // }
    // }
    // else if ( index == FlashingModeComboBoxModel.MODE_PULSE )
    // {
    // if ( flashDuration.getValue().doubleValue() < 0.01 )
    // {
    // statusText = "Mindestdauer is 0.01s";
    // }
    // else if ( flashDuration.getValue().doubleValue() > 500.0 )
    // {
    // statusText = "max. Dauer ist 500s";
    // }
    // }

    setStatus( statusText.toString() );
  }

  /** {@inheritDoc} */
  @Override
  protected void okClicked()
  {
    int selectedIdx = mode.getSelectedIndex();
    LightMode lightMode = mode.getItemAt( selectedIdx );

    programDefinition.setAlgorithm( selectedIdx );

    programDefinition.setNumberOfFlashes( lightMode.hasNumberOfFlashes() ? flashes.getValue() : 1 );

    if ( lightMode.hasDuration() )
    {
      // OffDuration does not really make sense without also having Duration.
      if ( lightMode.hasOffDuration() )
      {
        programDefinition.setFlashDuration( flashDuration.getValue() );

        programDefinition.setDuration( offDuration.getValue() );
      }
      else
      {
        programDefinition.setFlashDuration( BigDecimal.ZERO );

        programDefinition.setDuration( flashDuration.getValue() );
      }
    }
    else
    {
      programDefinition.setFlashDuration( BigDecimal.ZERO );

      programDefinition.setDuration( BigDecimal.ZERO );
    }

    aviLightConfigData.modification( programDefinition );
    super.okClicked();
  }

  /** {@inheritDoc} */
  @Override
  public void changedUpdate( DocumentEvent arg0 )
  {
    // updateFieldValues();
    validateControls();
  }

  /** {@inheritDoc} */
  @Override
  public void insertUpdate( DocumentEvent arg0 )
  {
    // updateFieldValues();
    validateControls();
  }

  /** {@inheritDoc} */
  @Override
  public void removeUpdate( DocumentEvent arg0 )
  {
    // updateFieldValues();
    validateControls();
  }

}
