package de.prim.comm.data;

public class TerminalDataEvent extends DataEvent
{
  private String text;

  public TerminalDataEvent( String text )
  {
    super( DataEvent.Type.TerminalEvent );

    this.text = text;
  }

  public String getText()
  {
    return text;
  }

}
