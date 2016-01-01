package de.prim.avilight.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.prim.comm.command.Command;
import de.prim.comm.data.AviLightConfigData;
import de.prim.comm.data.DataEvent;
import de.prim.comm.data.DataEvent.Type;
import de.prim.comm.data.DataEventListener;
import de.prim.comm.data.ModifiableImpl;
import de.prim.comm.data.TerminalDataEvent;

public class TerminalPanel extends JPanel implements DataEventListener
{

  /** The Constant serialVersionUID. */
  private static final long      serialVersionUID = -1739609020272548213L;

  private JScrollPane            scrollPane;
  private JTextArea              textArea;

  private AviLightConfigData     aviLightConfigData;

  private static final String[]  DEBUG_COMBO      =
                                                  { "Dump Channels", "Dump Globals",
      "Dump Receiver", "Dump EEPROM", "Dump Program Array" };
  private static final Command[] DEBUG_COMMAND    =
                                                  { Command.DUMP_CHANNELS, Command.DUMP_GLOBALS,
      Command.DUMP_RECEIVER, Command.DUMP_EEPROM, Command.DUMP_PROGRAM_ARRAY };

  public TerminalPanel( AviLightConfigData aviLightConfigData )
  {
    this.aviLightConfigData = aviLightConfigData;
    this.aviLightConfigData.addDataEventListener( this );

    textArea = new JTextArea( 20, 40 );

    Font font = new Font( "Monospaced", Font.PLAIN, 12 );
    textArea.setFont( font );

    scrollPane = new JScrollPane( textArea );
    scrollPane.setMinimumSize( new Dimension( 200, 200 ) );

    this.setLayout( new BorderLayout() );

    JButton button = new JButton( "Termnal Löschen" );
    button.addActionListener( event -> textArea.setText( "" ) );

    JPanel top = new JPanel();
    top.setLayout( new BoxLayout( top, BoxLayout.X_AXIS ) );
    top.add( button );

    final JComboBox<Object> type = new JComboBox<Object>( DEBUG_COMBO );
    top.add( type );
    JButton debugButton = new JButton( "Debug" );
    debugButton.addActionListener( actionEvent -> TerminalPanel.this.aviLightConfigData
        .modification( new ModifiableImpl( DEBUG_COMMAND[type.getSelectedIndex()] ) ) );

    top.add( debugButton );

    // add( button, BorderLayout.NORTH );
    add( top, BorderLayout.NORTH );

    add( scrollPane, BorderLayout.CENTER );

    textArea.addKeyListener( new KeyAdapter()
    {
      @Override
      public void keyTyped( KeyEvent e )
      {
        e.consume();
      }

      @Override
      public void keyPressed( KeyEvent e )
      {
        TerminalPanel.this.aviLightConfigData.modification( new ModifiableImpl(
            Command.DUMP_CHANNELS ) );

        System.out.println( "Pressed: " + e.getKeyChar() + " " + e.getKeyCode() );
        e.consume();
      }

      @Override
      public void keyReleased( KeyEvent e )
      {
        e.consume();
      }

    } );
  }

  @Override
  public void dataEvent( DataEvent dataEvent )
  {
    if ( Type.TerminalEvent.equals( dataEvent.getType() ) )
    {
      textArea.append( ( (TerminalDataEvent) dataEvent ).getText() );
      textArea.append( "\r\n" );
    }
  }
}
