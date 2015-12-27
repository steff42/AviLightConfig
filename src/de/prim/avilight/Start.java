package de.prim.avilight;

import de.prim.avilight.gui.MainWindow;

public class Start
{
  public static void main( String[] args ) throws Exception
  {
    MainWindow mainWindow = new MainWindow();
    mainWindow.setVisible( true );

    // SerialPort serialPort = CommWrapper.openComPort("COM1", "AviLight",
    // 2000);
    // AviLightComm aviLightComm = new AviLightComm(serialPort, mainWindow
    // .getTerminalListener());

  }
}
