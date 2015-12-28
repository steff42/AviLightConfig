package de.prim.avilight.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import de.prim.avilight.gui.utils.SpringUtilities;
import de.prim.comm.data.AviLightConfigData;
import de.prim.comm.data.DataEvent;
import de.prim.comm.data.DataEventListener;

public class InfoPanel extends AviLightSuperTab implements DataEventListener
{

  /** The Constant serialVersionUID. */
  private static final long          serialVersionUID  = -6172275938081484281L;

  private JLabel                     firmWare;
  private JLabel                     highCurrentOutputs;
  private JLabel                     lowCurrentOutputs;
  private JLabel                     receiverInputs;
  private JLabel                     voltage;
  private JLabel                     limit;

  private static final DecimalFormat VOLTAGE_FORMATTER = new DecimalFormat( "##0.00" );

  public InfoPanel( final MainWindow mainWindow, AviLightConfigData aviLightConfigData )
  {
    super( aviLightConfigData, new GridLayout( 1, 0, 10, 10 ) );

    JButton button;
    JPanel outerPanel = new JPanel( new BorderLayout() );
    add( outerPanel );

    JPanel panel = new JPanel( new SpringLayout() );
    outerPanel.add( panel );

    panel.add( new JLabel( "Firmware Version" ) );
    firmWare = new JLabel();
    panel.add( firmWare );
    panel.add( new JLabel( "" ) );
    // button = new JButton( "Firmware upgrade" );
    // panel.add( button );
    // button.addActionListener( new ActionListener()
    // {
    // @Override
    // public void actionPerformed(ActionEvent actionEvent)
    // {
    // InfoPanel.this.aviLightConfigData.firmwareUpgrade();
    // }
    // } );

    panel.add( new JLabel( "PWM-Ausgänge" ) );
    highCurrentOutputs = new JLabel();
    panel.add( highCurrentOutputs );
    panel.add( new JLabel( "" ) );

    panel.add( new JLabel( "Schaltausgänge" ) );
    lowCurrentOutputs = new JLabel();
    panel.add( lowCurrentOutputs );
    panel.add( new JLabel( "" ) );

    panel.add( new JLabel( "Empfängereingänge" ) );
    receiverInputs = new JLabel();
    panel.add( receiverInputs );
    panel.add( new JLabel( "" ) );

    panel.add( new JLabel( "Akkuspannung" ) );
    voltage = new JLabel();
    panel.add( voltage );
    panel.add( new JLabel( "" ) );

    NumberFormat numberFormat = NumberFormat.getInstance();
    numberFormat.setMaximumFractionDigits( 2 );
    numberFormat.setMaximumIntegerDigits( 2 );

    panel.add( new JLabel( "Spannungsgrenze" ) );
    limit = new JLabel();
    panel.add( limit );
    button = new JButton( "setzen" );
    panel.add( button );
    button.addActionListener( ActionEvent ->
    {
      String newValue = JOptionPane.showInputDialog( InfoPanel.this, "Neue Spannugsgrenze",
          limit.getText() );

      if ( newValue != null )
      {
        newValue = newValue.replace( ",", "." );
        try
        {
          BigDecimal newLimit = new BigDecimal( newValue );
          InfoPanel.this.aviLightConfigData.setVoltageLimit( newLimit );
        }
        catch ( NumberFormatException e )
        {
          JOptionPane.showConfirmDialog( InfoPanel.this, "Fehler", e.getLocalizedMessage(),
              JOptionPane.CANCEL_OPTION, JOptionPane.ERROR_MESSAGE );
        }
      }
    } );

    SpringUtilities.makeCompactGrid( panel, 6, 3, 6, 6, 6, 6 );

    aviLightConfigData.addDataEventListener( this );
  }

  @Override
  public void dataEvent( DataEvent dataEvent )
  {
    switch ( dataEvent.getType() )
    {
      case InfoDataReceived:
        firmWare.setText( aviLightConfigData.getFirmwareVersion() );
        highCurrentOutputs.setText( String.valueOf( aviLightConfigData.getOutputChannels() ) );
        lowCurrentOutputs.setText( String.valueOf( aviLightConfigData.getSwitchChannels() ) );
        receiverInputs.setText( String.valueOf( aviLightConfigData.getReceiverChannels() ) );
        break;

      case VoltageReceived:
        voltage.setText( VOLTAGE_FORMATTER.format( aviLightConfigData.getVoltage() ) );
        limit.setText( VOLTAGE_FORMATTER.format( aviLightConfigData.getLimit() ) );
        break;
      default:
        // Do nothing
        break;
    }
  }

}
