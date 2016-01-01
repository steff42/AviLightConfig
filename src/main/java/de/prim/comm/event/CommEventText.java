package de.prim.comm.event;

public class CommEventText extends CommEvent
{

  private String text;

  public CommEventText( byte source, String text )
  {
    super( source );
    this.text = text;
  }

  public String getText()
  {
    return text;
  }

  @Override
  public String toString()
  {
    return "CommEventText [source=" + getSource() + ", text=" + text + "]";
  }

}
