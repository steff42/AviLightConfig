package de.prim.comm.event;

import de.prim.avilight.utils.HexUtils;

public class CommEventError extends CommEvent
{
  private String message;

  public CommEventError(String message, byte[] buffer, int length)
  {
    super( (byte) 0xff );

    this.message = message;
    if (length > 0)
    {
      this.message += HexUtils.toHex( buffer, length );
      if (buffer != null)
      {
        this.message += "\"";
        this.message += new String( buffer, 0, length );
        this.message += "\"";
      }
    }
  }

  public String getMessage()
  {
    return message;
  }

  @Override
  public String toString()
  {
    return "CommEventError [message=" + message + "]";
  }

}
