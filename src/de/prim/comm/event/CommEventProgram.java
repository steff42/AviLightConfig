package de.prim.comm.event;

public class CommEventProgram extends CommEvent
{
  private byte              channel;
  private byte              segment;
  private ProgramDefinition programDefinition;

  public CommEventProgram( byte buffer[] )
  {
    super( buffer[0] );

    channel = buffer[1];
    segment = buffer[2];

    programDefinition = new ProgramDefinition( channel, segment, buffer[3], ( 0xff & buffer[4] )
        + ( ( 0xff & buffer[5] ) << 8 ), buffer[6] );
  }

  public byte getChannel()
  {
    return channel;
  }

  public byte getSegment()
  {
    return segment;
  }

  public byte getAlgorithm()
  {
    return programDefinition.getAlgorithm();
  }

  public int getPeriod()
  {
    return programDefinition.getPeriod();
  }

  public byte getFlash()
  {
    return programDefinition.getFlash();
  }

  public ProgramDefinition getProgramDefinition()
  {
    return programDefinition;
  }

  @Override
  public String toString()
  {
    return "CommEventProgram [channel=" + channel + ", segment=" + segment + ", algorithm="
        + getAlgorithm() + ", period=" + getPeriod() + ", flash=" + getFlash() + "]";
  }

}
