package de.prim.comm.event;

public class CommEventEnterProgrammingMode extends CommEvent
{

  public CommEventEnterProgrammingMode( byte source )
  {
    super( source );
  }

  @Override
  public String toString()
  {
    return "CommEventEnterProgrammingMode []";
  }

}
