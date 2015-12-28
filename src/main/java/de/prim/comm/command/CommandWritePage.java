package de.prim.comm.command;

public class CommandWritePage extends MultiByteCommand
{

  public CommandWritePage( byte cmd, int pageAddress, byte[] pageMemory )
  {
    super( cmd );

    data = new byte[2 + pageMemory.length];

    data[0] = (byte) ( 0xff & pageAddress );
    data[1] = (byte) ( 0xff & ( pageAddress >> 8 ) );

    System.arraycopy( pageMemory, 0, data, 2, pageMemory.length );
  }

}
