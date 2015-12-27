package de.prim.comm.event;

public abstract class CommEvent
{
  private byte source;

  public CommEvent( byte source )
  {
    super();
    this.source = source;
  }

  public byte getSource()
  {
    return source;
  }

}
