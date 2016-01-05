package de.prim.comm.protocol;

import java.io.IOException;
import java.util.TooManyListenersException;

import de.prim.avilight.utils.Constants;
import de.prim.avilight.utils.HexUtils;
import de.prim.comm.command.Command;
import de.prim.comm.processor.DataProcessor;
import de.prim.comm.processor.TelegramEscapeByteProcessor;
import de.prim.comm.processor.TelegramSeparationProcessor;
import de.prim.comm.transport.TelegramSerialPort;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

public class ProtocolTester implements Runnable, DataProcessor
{
  public static enum Mode
  {
    BUSY( "Belegt" ), TIMEOUT( "Keine Antwort" ), ERROR( "Fehler" ), CONNECTED( "Verbunden" );

    private final String text;

    private Mode( String text )
    {
      this.text = text;
    }

    public String getText()
    {
      return text;
    }
  };

  private Mode               mode;

  private TelegramSerialPort telegramSerialPort;

  private Thread             thread;

  private volatile boolean   pingReceived;

  private volatile boolean   closed;

  public ProtocolTester( String comPort )
  {
    super();

    try
    {
      telegramSerialPort = new TelegramSerialPort( comPort,
          new TelegramSeparationProcessor( this, Constants.BUFFER_SIZE ) );

      thread = new Thread( this );
      thread.start();
    }
    catch ( NoSuchPortException e )
    {
      mode = Mode.ERROR;
    }
    catch ( PortInUseException e )
    {
      mode = Mode.BUSY;
    }
    catch ( UnsupportedCommOperationException e )
    {
      mode = Mode.ERROR;
    }
    catch ( TooManyListenersException e )
    {
      mode = Mode.ERROR;
    }
    catch ( IOException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void run()
  {
    try
    {
      TelegramEscapeByteProcessor telegramSender = new TelegramEscapeByteProcessor(
          telegramSerialPort );

      for ( ;; )
      {
        System.out.println( "Pinging " + telegramSerialPort.getName() );
        Command.PING.send( telegramSender );

        try
        {
          Thread.sleep( 5750 );
        }
        catch ( InterruptedException e )
        {
        }

        if ( closed )
        {
          break;
        }
        mode = pingReceived ? Mode.CONNECTED : Mode.TIMEOUT;
      }
    }
    catch ( IOException e )
    {
      mode = Mode.ERROR;
    }

    if ( telegramSerialPort != null )
    {
      telegramSerialPort.close();
      telegramSerialPort = null;
    }
  }

  public void close()
  {
    closed = true;
    if ( thread != null )
    {
      thread.interrupt();
    }

    if ( telegramSerialPort != null )
    {
      telegramSerialPort.close();
      telegramSerialPort = null;
    }
  }

  @Override
  public void processData( byte[] buffer, int length )
  {
    System.out.println( "REC " + length + " Bytes from "
        + ( telegramSerialPort == null ? "" : telegramSerialPort.getName() ) + ": "
        + HexUtils.toHex( buffer, length ) );

    if ( length == 2 && buffer[0] == AviLightProtocol.CMD_PING )
    {
      // System.out.println( "PING Received from " +
      // telegramSerialPort.getName() );
      pingReceived = true;
      if ( thread != null )
      {
        thread.interrupt();
      }
    }
  }

  public TelegramSerialPort dispose()
  {
    TelegramSerialPort tmp = telegramSerialPort;

    telegramSerialPort = null;
    close();

    return tmp;
  }

  public Mode getMode()
  {
    return mode;
  }

}
