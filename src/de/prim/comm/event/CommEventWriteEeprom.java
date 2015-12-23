package de.prim.comm.event;

public class CommEventWriteEeprom extends CommEvent
{

  public CommEventWriteEeprom(byte source)
  {
    super( source );
  }

  @Override
  public String toString()
  {
    return "CommEventWriteEeprom []";
  }

}
