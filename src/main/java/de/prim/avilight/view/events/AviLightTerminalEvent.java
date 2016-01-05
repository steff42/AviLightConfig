package de.prim.avilight.view.events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * The Class AviLightDataEvent.
 *
 * @author Steff Lukas
 */
public class AviLightTerminalEvent extends Event
{

  /** The Constant serialVersionUID. */
  private static final long                            serialVersionUID = -918769179626851502L;

  /** The Constant ANY, the parent of all AviLightTermialEvents. */
  public final static EventType<AviLightTerminalEvent> ANY              = new EventType<>(
      Event.ANY, "AVILIGHT_TERMINAL" );

  /** The Constant TERMINAL_EVENT. */
  public final static EventType<AviLightTerminalEvent> TERMINAL_EVENT   = new EventType<>(
      AviLightTerminalEvent.ANY, "TERMINAL_EVENT" );

  private String                                       text;

  /**
   * Instantiates a new avi light data event.
   *
   * @param eventType
   *          the event type
   */
  public AviLightTerminalEvent( EventType<AviLightTerminalEvent> eventType, String text )
  {
    super( eventType );
    this.text = text;
  }

  public String getText()
  {
    return text;
  }

}
