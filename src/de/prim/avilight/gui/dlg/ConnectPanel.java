package de.prim.avilight.gui.dlg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;

import de.prim.comm.CommWrapper;
import de.prim.comm.protocol.ProtocolTester;
import de.prim.comm.protocol.ProtocolTester.Mode;

public class ConnectPanel extends JPanel
{

  private static final long           serialVersionUID = -6504931574231500744L;

  public static final String          COMMAND_CONNECT  = "connect";

  /** The list. */
  private JList<String>               list;

  /** The list model. */
  private DefaultListModel<String>    listModel;

  /** The selected index. */
  private int                         selectedIndex    = -1;

  /** The port list. */
  private List<String>                portList;

  /** The ports. */
  private Map<String, ProtocolTester> ports;

  /** The selected com port. */
  private String                      selectedComPort;

  /** The timer. */
  private Timer                       timer;

  /** THe connect button */
  private JButton                     connect;

  /** The action listenr, listening to action events like connect */
  private ActionListener              actionListener;

  /** The selected ProtocolTester */
  private ProtocolTester              selectedProtocolTester;

  public ConnectPanel()
  {
    portList = CommWrapper.listComPorts();

    addGUIContents();
    initConnectPanel();
  }

  public void initConnectPanel()
  {
    cleanupPorts();

    ports = new HashMap<String, ProtocolTester>();
    portList = CommWrapper.listComPorts();
    listModel.clear();

    for ( String portName : portList )
    {
      listModel.addElement( portName );
      ports.put( portName, new ProtocolTester( portName ) );
    }

    timer.start();
  }

  private void cleanupPorts()
  {
    if (ports != null && !ports.isEmpty())
    {
      for ( ProtocolTester protocolTester : ports.values() )
      {
        protocolTester.close();
      }
      ports.clear();
    }
  }

  protected void addGUIContents()
  {
    setLayout( new BorderLayout() );

    JPanel top = new JPanel();
    top.setLayout( new BoxLayout( top, BoxLayout.X_AXIS ) );
    top.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ) );
    top.add( new JLabel( "Bitte AviLight anstecken und gewünschten Port auswählen." ) );
    top.add( Box.createRigidArea( new Dimension( 10, 0 ) ) );
    connect = new JButton( "Verbinden" );
    connect.setEnabled( false );
    connect.addActionListener( ActionEvent -> connect() );

    top.add( connect );

    add( top, BorderLayout.NORTH );

    listModel = new DefaultListModel<String>();

    list = new JList<String>( listModel );
    Dimension dimension = new Dimension( 150, 200 );
    list.setMinimumSize( dimension );
    list.setPreferredSize( dimension );
    add( list, BorderLayout.CENTER );

    list.addListSelectionListener( ListSelectionEvent ->
    {
      selectedIndex = list.getSelectedIndex();
      // System.out.println( selectedIndex );
      enableOkButton();
    } );

    list.addMouseListener( new MouseAdapter()
    {

      @Override
      public void mouseClicked( MouseEvent e )
      {
        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
        {
          if ( selectedComPort != null )
          {
            connect();
          }
        }
      }

    } );

    timer = new Timer( 250, actionEvent -> doTimer() );
  }

  protected void doTimer()
  {
    for ( int i = 0; i < portList.size(); i++ )
    {
      String comPort = portList.get( i );
      Mode mode = ports.get( comPort ).getMode();

      if ( mode != null )
      {
        listModel.set( i, comPort + " - " + mode.getText() );
      }
    }

    enableOkButton();
  }

  protected void enableOkButton()
  {
    if ( selectedIndex >= 0 )
    {
      selectedComPort = portList.get( selectedIndex );

      connect.setEnabled( Mode.CONNECTED.equals( ports.get( selectedComPort ).getMode() ) );
    }

    if ( !connect.isEnabled() )
    {
      selectedComPort = null;
    }
  }

  protected void connect()
  {
    timer.stop();

    selectedProtocolTester = ports.remove( selectedComPort );
    cleanupPorts();
    listModel.clear();
    connect.setEnabled( false );

    if ( actionListener != null )
    {
      actionListener.actionPerformed( new ActionEvent( this, 0, COMMAND_CONNECT ) );
    }
  }

  public ActionListener getActionListener()
  {
    return actionListener;
  }

  public void setActionListener( ActionListener actionListener )
  {
    this.actionListener = actionListener;
  }

  public ProtocolTester getSelectedProtocolTester()
  {
    return selectedProtocolTester;
  }

}
