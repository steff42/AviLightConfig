package de.prim.avilight.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import de.prim.avilight.Constants;
import de.prim.avilight.OutputType;
import de.prim.avilight.gui.dlg.ConnectPanel;
import de.prim.avilight.gui.dlg.ProgressDialog;
import de.prim.comm.data.AviLightConfigData;
import de.prim.comm.processor.TelegramEscapeByteProcessor;
import de.prim.comm.processor.TelegramSeparationProcessor;
import de.prim.comm.protocol.AviLightProtocol;
import de.prim.comm.protocol.ProtocolTester;
import de.prim.comm.transport.TelegramSerialPort;

public class MainWindow extends JFrame implements ActionListener
{

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2741538161387651064L;

  private TelegramSerialPort telegramSerialPort;

  /** The tab. */
  private JTabbedPane tab;

  private AviLightPanel aviLightPanel;

  private AviLightConfigData aviLightConfigData;

  private JPanel configuration;

  private Timer communicationTimer;

  private ProgressDialog progressDialog;

  private JButton loadData;
  private JButton saveData;

  public MainWindow()
  {
    aviLightConfigData = new AviLightConfigData( this, this );
    initGui();
  }

  private void initGui()
  {
    setDefaultCloseOperation( EXIT_ON_CLOSE );

    Container container = this.getContentPane();

    configuration = new JPanel();
    configuration.setLayout( new BorderLayout() );

    setJMenuBar( initMenu() );

    JToolBar toolbar = new JToolBar( JToolBar.HORIZONTAL );
    container.add( toolbar, BorderLayout.PAGE_START );

    loadData = new JButton();
    loadData.setIcon( new ImageIcon( MainWindow.class
        .getResource( "/resources/load.gif" ) ) );
    loadData.setToolTipText( "Programm verwerfen und vom Modul neu laden" );
    loadData.setEnabled( false );
    loadData.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        aviLightConfigData.reloadConfig();
      }
    } );
    toolbar.add( loadData );

    saveData = new JButton();
    saveData.setIcon( new ImageIcon( MainWindow.class
        .getResource( "/resources/save.gif" ) ) );
    saveData.setToolTipText( "Programm auf dem Modul speichern" );
    saveData.setEnabled( false );
    saveData.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        aviLightConfigData.saveConfig();
      }
    } );
    toolbar.add( saveData );

    tab = new JTabbedPane();
    aviLightPanel = new AviLightPanel( tab, this );
    container.add( aviLightPanel );

    tab.addTab( "Info", new InfoPanel( this, aviLightConfigData ) );

    tab.addTab( "Empfänger", new ReceiverPanel( aviLightConfigData ) );

    // tab.addTab( "PWM Ausgänge", new OutputsPanel( this, aviLightConfigData,
    // OutputType.PWM ) );

    tab.addTab( "Schaltausgänge", new OutputsPanel( this, aviLightConfigData,
        OutputType.Switch ) );

//     TerminalPanel terminalPanel = new TerminalPanel( aviLightConfigData );
//     tab.addTab( "Terminal", terminalPanel );

    tab.setSelectedIndex( 0 );
    Dimension size = new Dimension( 800, 300 );
    setSize( size );
    setMinimumSize( size );

    pack();
    setLocationRelativeTo( null );

    communicationTimer = new Timer( 250, new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        if (!aviLightConfigData.cyclicCommunication())
        {
          disconnected();
        }
      }
    } );
  }

  protected void disconnected()
  {
    communicationTimer.stop();

    aviLightConfigData.close();
    telegramSerialPort.close();
    telegramSerialPort = null;

    aviLightPanel.showConnectPanel();

    loadData.setEnabled( false );
    saveData.setEnabled( false );
  }

  private JMenuBar initMenu()
  {
    JMenuBar menuBar = new JMenuBar();

    JMenu aviLight = new JMenu( "AViLight" );
    menuBar.add( aviLight );

    JMenuItem load = new JMenuItem( "Laden" );
    // aviLight.add( load );
    load.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        load();
      }
    } );

    JMenuItem save = new JMenuItem( "Speichern" );
    save.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        save();
      }
    } );
    // aviLight.add( save );

    return menuBar;
  }

  protected void load()
  {
    FileDialog fileDialog = new FileDialog( this, "Speichern" );
    fileDialog.setVisible( true );
    fileDialog.dispose();

    String directory = fileDialog.getDirectory();
    String file = fileDialog.getFile();

    if (directory != null && file != null)
    {
      try
      {
        File loadFile = new File( directory, file );

        JAXBContext jaxbContext = JAXBContext
            .newInstance( AviLightConfigData.class );

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        AviLightConfigData data = (AviLightConfigData) unmarshaller
            .unmarshal( loadFile );

        System.out.println( data );
      }

      catch (Exception e)
      {
        JOptionPane.showMessageDialog( this, e.getMessage(), "Fehler",
            JOptionPane.ERROR_MESSAGE );

        e.printStackTrace();
      }
    }
  }

  protected void save()
  {
    FileDialog fileDialog = new FileDialog( this, "Speichern" );
    fileDialog.setVisible( true );
    fileDialog.dispose();

    String directory = fileDialog.getDirectory();
    String file = fileDialog.getFile();

    if (directory != null && file != null)
    {
      try
      {
        File saveFile = new File( directory, file );

        JAXBContext jaxbContext = JAXBContext
            .newInstance( AviLightConfigData.class );

        Marshaller marshaller = jaxbContext.createMarshaller();

        marshaller.marshal( aviLightConfigData, saveFile );
      }
      catch (Exception e)
      {
        JOptionPane.showMessageDialog( this, e.getMessage(), "Fehler",
            JOptionPane.ERROR_MESSAGE );

        e.printStackTrace();
      }
    }

  }

  @Override
  public void actionPerformed(ActionEvent event)
  {
    if (event.getActionCommand().equals( ConnectPanel.COMMAND_CONNECT ))
    {
      ProtocolTester protocolTester = (ProtocolTester) event.getSource();

      try
      {
        telegramSerialPort = protocolTester.dispose();

        TelegramEscapeByteProcessor sender = new TelegramEscapeByteProcessor(
            telegramSerialPort );

        aviLightConfigData.setSender( sender );

        AviLightProtocol aviLightProtocol = new AviLightProtocol(
            aviLightConfigData );

        telegramSerialPort
            .setTelegramSeparator( new TelegramSeparationProcessor(
                aviLightProtocol, Constants.BUFFER_SIZE ) );

        loadData.setEnabled( true );
        saveData.setEnabled( true );

        aviLightConfigData.init();
        communicationTimer.start();
      }
      catch (Exception e)
      {
        e.printStackTrace();

        JOptionPane.showMessageDialog( this, e.getMessage() == null ? e
            .getClass().getName() : e.getMessage(), "Fehler",
            JOptionPane.ERROR_MESSAGE );
      }
    }
    else if (AviLightConfigData.ACTION_ASK_UPGRADE.equals( event
        .getActionCommand() ))
    {
      // No App found, only Bootloader, ask for firmware upgrade.
      askForFirmwareUpgrade();

    }
    else if (AviLightConfigData.ACTION_START_PROGRESS.equals( event
        .getActionCommand() ))
    {
      progressDialog = ProgressDialog.openNonModal( this,
          (String) event.getSource(), event.getID() );
    }
    else if (AviLightConfigData.ACTION_STOP_PROGRESS.equals( event
        .getActionCommand() ))
    {
      if (progressDialog != null)
      {
        progressDialog.setVisible( false );
        progressDialog = null;
      }
    }
    else if (AviLightConfigData.ACTION_PROGRESS.equals( event
        .getActionCommand() ))
    {
      if (progressDialog != null)
      {
        progressDialog.setValue( event.getID() );
      }
    }
  }

  private void askForFirmwareUpgrade()
  {
    int confirm = JOptionPane
        .showConfirmDialog(
            this,
            "Keine Firmware geladen auf dem Modul installiert. Soll ein Formwareupgrade durchgeführt werden ?",
            "Warnung", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );

    if (JOptionPane.YES_OPTION == confirm)
    {
      aviLightConfigData.firmwareUpgrade();
    }
    else
    {

    }
  }
}
