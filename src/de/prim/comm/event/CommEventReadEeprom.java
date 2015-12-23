package de.prim.comm.event;

public class CommEventReadEeprom extends CommEvent
{

  public CommEventReadEeprom(byte source)
  {
    super( source );
  }

  @Override
  public String toString()
  {
    return "CommEventReadEeprom []";
  }

}
