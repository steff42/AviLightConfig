package de.prim.comm.event;

public class CommEventLearnStickMode extends CommEvent
{
  private byte learnStickMode;

  public CommEventLearnStickMode( byte[] data )
  {
    super( data[0] );
    this.learnStickMode = data[1];
  }

  public byte getLearnStickMode()
  {
    return learnStickMode;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "CommEventLearnStickMode [learnStickMode=" );
    builder.append( learnStickMode );
    builder.append( "]" );
    return builder.toString();
  }

}
