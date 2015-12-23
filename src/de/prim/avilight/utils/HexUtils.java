package de.prim.avilight.utils;

import java.io.IOException;

public class HexUtils
{
  /**
   * Parses the hex char.
   * 
   * @param ch
   *          the character to parse
   * @return the byte value
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static byte parseHexChar(char ch)
  {
    if (ch >= '0' && ch <= '9')
    {
      return (byte) (ch - '0');
    }
    else if (ch >= 'A' && ch <= 'F')
    {
      return (byte) (10 + ch - 'A');
    }
    else if (ch >= 'a' && ch <= 'f')
    {
      return (byte) (10 + ch - 'a');
    }

    throw new NumberFormatException( "illegal Hex char: " + ch );
  }
  
  public static String toHex16(int w)
  {
    return toHex( (byte) (0xff & (w >> 8)) ) + toHex( (byte) (0xff & w) );
  }

  public static String toHex(byte b)
  {
    String s;
    int iByte = b & 0xff;

    if (iByte < 16)
    {
      s = "0" + Integer.toHexString( iByte );
    }
    else
    {
      s = Integer.toHexString( iByte );
    }

    return s.toUpperCase();
  }

  public static String toHex(byte[] data)
  {
    return toHex( data, data.length );
  }

  public static String toHex(byte[] data, int size)
  {
    return toHex( data, 0, size );
  }

  public static String toHex(byte[] data, int start, int size)
  {
    StringBuffer sb = new StringBuffer();
    int count = 0;
    for (int i = 0; i < size; i++)
    {
      byte b = data[start + i];
      if (++count > size)
      {
        break;
      }
      if (sb.length() > 0)
      {
        sb.append( ' ' );
      }

      sb.append( toHex( b ) );
    }

    return sb.toString();
  }
}
