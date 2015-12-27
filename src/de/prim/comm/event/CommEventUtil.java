package de.prim.comm.event;

import java.io.UnsupportedEncodingException;

public class CommEventUtil
{

  /**
   * Creates the text event, or if an Conversion error happens an error event
   *
   * @param buffer
   *          the buffer
   * @param size
   *          the size
   * @return the comm event
   */
  public static CommEvent createTextEvent( byte[] buffer, int size )
  {
    try
    {
      String text;
      if ( size > 0 )
      {
        text = new String( buffer, 1, size - 1, "ISO-8859-1" );
      }
      else
      {
        text = "";
      }

      return new CommEventText( buffer[0], text );
    }
    catch ( UnsupportedEncodingException e )
    {
      return new CommEventError( e.getMessage(), buffer, size );
    }

  }
}
