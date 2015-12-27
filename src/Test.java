import java.util.List;

import de.prim.comm.CommWrapper;

public class Test
{

  /**
   * @param args
   */
  // @SuppressWarnings ( "unchecked")
  public static void main( String[] args ) throws Exception
  {
    // Method m = CommPortIdentifier.class.getDeclaredMethod("loadDriver",
    // new Class<?>[] { String.class });
    // m.setAccessible(true);
    // m
    // .invoke(
    // null,
    // "C:\\Dokumente und Einstellungen\\Jörg\\workspace.jee\\AviationLightConfig\\lib\\javax.comm.properties");
    // System.loadLibrary("win32com");
    // C:\\Dokumente und
    // Einstellungen\\Jörg\\workspace.jee\\AviationLightConfig\\lib\\

    // Enumeration<CommPortIdentifier> enumerator = CommPortIdentifier
    // .getPortIdentifiers();
    //
    // while (enumerator.hasMoreElements())
    // {
    // CommPortIdentifier commPortIdentifier = enumerator.nextElement();
    //
    // System.out.println(commPortIdentifier.getName());
    // }
    //
    // try
    // {
    // CommPortIdentifier commPortIdentifier = CommPortIdentifier
    // .getPortIdentifier("COM1");
    //
    // System.out.println(commPortIdentifier.getName());
    // }
    // catch (Exception e)
    // {
    // System.out.println(e.getClass());
    // }

    List<String> comPorts = CommWrapper.listComPorts();
    System.out.println( comPorts );
  }
}
