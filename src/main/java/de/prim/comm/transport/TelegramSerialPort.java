package de.prim.comm.transport;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import de.prim.avilight.Constants;
import de.prim.comm.CommWrapper;
import de.prim.comm.processor.ByteProcessor;
import de.prim.comm.processor.TelegramSeparationProcessor;

/**
 * The Class TelegramSerialPort.
 */
public class TelegramSerialPort implements SerialPortEventListener, ByteProcessor
{

  /** The telegram separator. */
  private TelegramSeparationProcessor telegramSeparationProcessor;

  /** The serial port. */
  private SerialPort                  serialPort;

  /** The input stream. */
  private InputStream                 inputStream;

  /** The output stream. */
  private OutputStream                outputStream;

  /** The input buffer. */
  private byte[]                      buffer;

  /**
   * Instantiates a new telegram serial port.
   *
   * @param comPort
   *          the com port
   * @param telegramSeparator
   *          the telegram separator
   * @throws NoSuchPortException
   *           the no such port exception
   * @throws PortInUseException
   *           the port in use exception
   * @throws UnsupportedCommOperationException
   *           the unsupported comm operation exception
   * @throws TooManyListenersException
   *           the too many listeners exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public TelegramSerialPort( String comPort, TelegramSeparationProcessor telegramSeparationProcessor )
      throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException,
      TooManyListenersException, IOException
  {
    this.telegramSeparationProcessor = telegramSeparationProcessor;
    serialPort = CommWrapper.openComPort( comPort, Constants.APPLICATION_NAME, 2000 );

    serialPort.setSerialPortParams( getOsDepentedBaudRate(), SerialPort.DATABITS_8,
        SerialPort.STOPBITS_1, SerialPort.PARITY_NONE );
    serialPort.setFlowControlMode( SerialPort.FLOWCONTROL_NONE );
    serialPort.disableReceiveFraming();
    serialPort.addEventListener( this );
    serialPort.notifyOnDataAvailable( true );

    inputStream = this.serialPort.getInputStream();
    buffer = new byte[Constants.BUFFER_SIZE];

    outputStream = this.serialPort.getOutputStream();
  }

  private int getOsDepentedBaudRate()
  {
    if ( "Linux".equals( System.getProperty( "os.name" ) ) )
    {
      return 19200;
    }

    return 38400;
  }

  public void setTelegramSeparator( TelegramSeparationProcessor telegramSeparationProcessor )
  {
    this.telegramSeparationProcessor = telegramSeparationProcessor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * javax.comm.SerialPortEventListener#serialEvent(javax.comm.SerialPortEvent)
   */
  @Override
  public void serialEvent( SerialPortEvent serialPortEvent )
  {
    if ( SerialPortEvent.DATA_AVAILABLE == serialPortEvent.getEventType() )
    {
      try
      {
        if ( inputStream.available() > 0 )
        {
          buffer = new byte[inputStream.available()];
          int read = inputStream.read( buffer );

          telegramSeparationProcessor.processData( buffer, read );
        }
      }
      catch ( IOException e )
      {
        e.printStackTrace();
      }
    }
    else
    {
      System.out.println( "Event: " + serialPortEvent.toString() );
    }
  }

  /**
   * Gets the output stream.
   *
   * @return the output stream
   * @throws IOException
   */
  public OutputStream getOutputStream() throws IOException
  {
    return serialPort.getOutputStream();
  }

  /**
   * Close.
   */
  public void close()
  {
    if ( serialPort != null )
    {
      try
      {
        serialPort.removeEventListener();
        serialPort.close();
      }
      catch ( Exception e )
      {
        // ignore
      }
    }
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName()
  {
    return serialPort.getName();
  }

  @Override
  public void processByte( byte b ) throws IOException
  {
    outputStream.write( b );
  }

}
