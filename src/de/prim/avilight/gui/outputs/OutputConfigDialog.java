package de.prim.avilight.gui.outputs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.prim.avilight.gui.dlg.AviLightDialog;
import de.prim.avilight.gui.dlg.DoubleDocument;
import de.prim.avilight.gui.dlg.FlashingModeComboBoxModel;
import de.prim.comm.data.AviLightConfigData;
import de.prim.comm.event.ProgramDefinition;

public class OutputConfigDialog extends AviLightDialog implements
    DocumentListener
{

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5355108811917000166L;

  private static final String[] FLASH_COUNT = { "1", "2", "3", "4" };

  private JComboBox<Object> mode;

  private JComboBox<Object> flashes;

  private DoubleDocument flashDuration;

  private DoubleDocument offDuration;

  private AviLightConfigData aviLightConfigData;
  
  private ProgramDefinition programDefinition;

  /**
   * Instantiates a new output config dialog.
   * 
   * @param parent
   *          the parent
   * @param data
   */
  public OutputConfigDialog(Frame parent,AviLightConfigData aviLightConfigData, ProgramDefinition data)
  {
    super( parent, "Ausgang - Segment" );

    this.aviLightConfigData= aviLightConfigData;
    this.programDefinition = data;

    if(data.getAlgorithm() < FlashingModeComboBoxModel.CHANNEL_MODES.length)
    {
      mode.setSelectedIndex( data.getAlgorithm() );
    }
    
    flashes.setSelectedIndex( data.getFlashes() );
    offDuration.setValue( data.getDuration() );
    if (data.getAlgorithm() == FlashingModeComboBoxModel.MODE_PULSE)
    {
      flashDuration.setValue( data.getDuration() );
    }
    else
    {
      flashDuration.setValue( data.getFlashDuration() );
    }

    enableControls();
  }

  @Override
  protected void addGUIContents()
  {
    getContentPane().setLayout( new BorderLayout() );

    JPanel inputs = new JPanel();
    getContentPane().add( inputs );
    inputs.setLayout( new GridLayout( 4, 2, 5, 5 ) );

    inputs.add( new JLabel( "Modus:" ) );
    mode = new JComboBox<Object>( new FlashingModeComboBoxModel() );
    inputs.add( mode );
    mode.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent event)
      {
        enableControls();
      }
    } );

    inputs.add( new JLabel( "Anzahl:" ) );
    flashes = new JComboBox<Object>( FLASH_COUNT );
    inputs.add( flashes );

    inputs.add( new JLabel( "Dauer [s]:" ) );
    JTextField textField = new JTextField( "0", 2 );
    flashDuration = new DoubleDocument( textField );
    inputs.add( textField );
    flashDuration.getTextField().getDocument().addDocumentListener( this );

    inputs.add( new JLabel( "Pause [s]:" ) );
    textField = new JTextField( "0", 2 );
    offDuration = new DoubleDocument( textField );
    inputs.add( textField );
    offDuration.getTextField().getDocument().addDocumentListener( this );
  }

  protected void enableControls()
  {
    int index = mode.getSelectedIndex();
    if (index >= 0)
    {
      flashes.setEnabled( FlashingModeComboBoxModel.HAS_NUMBER_OF[index] );
      flashDuration.getTextField().setEnabled(
          FlashingModeComboBoxModel.HAS_DURATION[index] );
      offDuration.getTextField().setEnabled(
          FlashingModeComboBoxModel.HAS_OFF_DURATION[index] );
    }

    String statusText = null;
    if (index == FlashingModeComboBoxModel.MODE_FLASH)
    {
      if (flashDuration.getValue() < 0.01)
      {
        statusText = "Mindestdauer is 0.01s";
      }
      else if (flashDuration.getValue() > 0.5)
      {
        statusText = "max. Dauer ist 0,5s";
      }
      else if (offDuration.getValue() < 0.01)
      {
        statusText = "min. Pause ist 0.01s";
      }
      else if (offDuration.getValue() > 500.0)
      {
        statusText = "max. Pause ist 500s";
      }
    }
    else if (index == FlashingModeComboBoxModel.MODE_PULSE)
    {
      if (flashDuration.getValue() < 0.01)
      {
        statusText = "Mindestdauer is 0.01s";
      }
      else if (flashDuration.getValue() > 500.0)
      {
        statusText = "max. Dauer ist 500s";
      }
    }

    setStatus( statusText );
  }

  @Override
  protected void okClicked()
  {

    programDefinition.setAlgorithm(  mode.getSelectedIndex() );

    programDefinition.setFlashes( flashes.getSelectedIndex() );
    programDefinition.setDuration( offDuration.getValue() );
    if (programDefinition.getAlgorithm() == FlashingModeComboBoxModel.MODE_PULSE)
    {
      programDefinition.setDuration( flashDuration.getValue() );
    }
    else
    {
      programDefinition.setFlashDuration( flashDuration.getValue() );
    }

    aviLightConfigData.modification( programDefinition );
    super.okClicked();
  }

  @Override
  public void changedUpdate(DocumentEvent arg0)
  {
    enableControls();
  }

  @Override
  public void insertUpdate(DocumentEvent arg0)
  {
    enableControls();
  }

  @Override
  public void removeUpdate(DocumentEvent arg0)
  {
    enableControls();
  }

}
