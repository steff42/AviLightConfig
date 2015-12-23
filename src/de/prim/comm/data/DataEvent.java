package de.prim.comm.data;

import java.util.HashMap;
import java.util.Map;

public class DataEvent
{
  public static enum Type
  {
    /** Info data changed. */
    InfoDataReceived,
    
    /** Program has been received. */
    ProgramDataReceived, 
    
    /** Receiver channel data has been received */
    ReceiverDataReceived,
    
    /** Terminal Text event */
    TerminalEvent,
    
    /** the voltage has been received */
    VoltageReceived,
    
    LearnStickModeReceived
    
  };

  private Type type;

  private static Map<Type, DataEvent> events = new HashMap<DataEvent.Type, DataEvent>();

  public static synchronized DataEvent getDataEvent(Type type)
  {
    DataEvent dataEvent = events.get( type );

    if (dataEvent == null)
    {
      dataEvent = new DataEvent( type );
      events.put( type, dataEvent );
    }

    return dataEvent;
  }

  /**
   * Instantiates a new data event.
   * 
   * @param type
   *          the type
   */
  protected DataEvent(Type type)
  {
    super();
    this.type = type;
  }

  /**
   * Gets the type.
   * 
   * @return the type
   */
  public Type getType()
  {
    return type;
  }

}
