package de.prim.comm;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Wraps the Java Comm API. Ensure -djava.library.path=PATH_TO_win32com.dll and
 * * -callspaths points to comm.jar. The javax.comm.properties must exist in the
 * same directory as comm.jar.
 */
public class CommWrapper
{

  /** The initialized. */
  private static boolean initialized = false;

  /**
   * List com ports.
   *
   * @return the iterator< string>
   */
  @SuppressWarnings ( "unchecked")
  public static List<String> listComPorts()
  {
    initComm();

    List<String> comPorts = new ArrayList<String>();
    Enumeration<CommPortIdentifier> enumerator = CommPortIdentifier.getPortIdentifiers();
    while ( enumerator.hasMoreElements() )
    {
      CommPortIdentifier commPortIdentifier = enumerator.nextElement();

      if ( commPortIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL )
      {
        comPorts.add( commPortIdentifier.getName() );
      }
    }

    return comPorts;
  }

  /**
   * Open the com port.
   *
   * @param name
   *          the name
   * @param appName
   *          the application name
   * @param timeout
   *          the timeout
   * @return the serial port
   * @throws NoSuchPortException
   *           the no such port exception
   * @throws PortInUseException
   *           the port in use exception
   */
  public static SerialPort openComPort( String name, String appName, int timeout )
      throws NoSuchPortException, PortInUseException
  {
    initComm();

    CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier( name );

    return (SerialPort) commPortIdentifier.open( appName, timeout );
  }

  /**
   * Inits the comm by reflection. Due to a bug, the windows path names will not
   * work with driver initialisation.
   */
  private static void initCommByReflection()
  {
    String propFile = findPropertyFile();

    if ( propFile == null )
    {
      System.err
          .println( "'javax.comm.properties' property file not found, missing comm.jar in classpath ?" );
    }
    else
    {
      try
      {
        Method method = CommPortIdentifier.class.getDeclaredMethod( "loadDriver", new Class<?>[]
        { String.class } );
        method.setAccessible( true );
        method.invoke( null, propFile );
      }
      catch ( Exception e )
      {
        System.err.println( "unable to initialize comm by reflection: " + e.getMessage() );
      }
    }
  }

  /**
   * Find the 'javax.comm.properties' property file.
   *
   * @return the string
   */
  private static String findPropertyFile()
  {
    String classPath = System.getProperty( "java.class.path" );
    StringTokenizer token = new StringTokenizer( classPath, ";" );
    while ( token.hasMoreTokens() )
    {
      String path = token.nextToken();
      if ( path.contains( "comm.jar" ) )
      {
        File file = new File( path );
        if ( file.exists() )
        {
          File props = new File( file.getParent(), "javax.comm.properties" );

          if ( props.exists() )
          {
            return props.getPath();
          }
        }
      }
    }

    return null;
  }

  /**
   * Inits the comm.
   */
  @SuppressWarnings ( "unchecked")
  private static void initComm()
  {
    if ( !initialized )
    {
      initialized = true;

      Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();

      if ( !ports.hasMoreElements() )
      {
        initCommByReflection();
      }
    }
  }

}
