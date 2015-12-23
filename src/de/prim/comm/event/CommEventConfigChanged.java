package de.prim.comm.event;

public class CommEventConfigChanged  extends CommEvent
{

  public CommEventConfigChanged(byte source)
  {
    super( source );
  }

  @Override
  public String toString()
  {
    return "CommEventConfigChanged []";
  }

  
}
