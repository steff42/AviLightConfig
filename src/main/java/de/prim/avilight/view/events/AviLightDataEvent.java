package de.prim.avilight.view.events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * The Class AviLightDataEvent.
 *
 * @author Steff Lukas
 */
public class AviLightDataEvent extends Event
{

  /** The Constant serialVersionUID. */
  private static final long                        serialVersionUID               = -3166189857659739989L;

  /** The Constant ANY, the parent of all AviLightDataEvents. */
  public final static EventType<AviLightDataEvent> ANY                            = new EventType<>(
      Event.ANY, "AVILIGHT_DATA" );

  /** The Constant INFO_DATA_RECEIVED. */
  public final static EventType<AviLightDataEvent> INFO_DATA_RECEIVED             = new EventType<>(
      AviLightDataEvent.ANY, "INFO_DATA_RECEIVED" );

  /** The Constant PROGRAM_DATA_RECEIVED. */
  public final static EventType<AviLightDataEvent> PROGRAM_DATA_RECEIVED          = new EventType<>(
      AviLightDataEvent.ANY, "PROGRAM_DATA_RECEIVED" );

  public final static EventType<AviLightDataEvent> CONTROLLING_CHANNEL_RECEIVED   = new EventType<>(
      AviLightDataEvent.ANY, "CONTROLLING_CHANNEL_RECEIVED" );

  /** The Constant RECEIVER_DATA_RECEIVED. */
  public final static EventType<AviLightDataEvent> RECEIVER_DATA_RECEIVED         = new EventType<>(
      AviLightDataEvent.ANY, "RECEIVER_DATA_RECEIVED" );

  /** The Constant VOLTAGE_RECEIVED. */
  public final static EventType<AviLightDataEvent> VOLTAGE_RECEIVED               = new EventType<>(
      AviLightDataEvent.ANY, "VOLTAGE_RECEIVED" );

  /** The Constant VOLTAGE_RECEIVED. */
  public final static EventType<AviLightDataEvent> BATTERY_LIMIT_RECEIVED         = new EventType<>(
      AviLightDataEvent.ANY, "BATTERY_LIMIT_RECEIVED" );

  /** The Constant LEARN_STICK_MODE_RECEIVED. */
  public final static EventType<AviLightDataEvent> LEARN_STICK_MODE_RECEIVED      = new EventType<>(
      AviLightDataEvent.ANY, "LEARN_STICK_MODE_RECEIVED" );

  /** The Constant RECEIVER_CHANNEL_MODE_RECEIVED. */
  public final static EventType<AviLightDataEvent> RECEIVER_CHANNEL_MODE_RECEIVED = new EventType<>(
      AviLightDataEvent.ANY, "RECEIVER_CHANNEL_MODE_RECEIVED" );

  /**
   * Instantiates a new avi light data event.
   *
   * @param eventType
   *          the event type
   */
  public AviLightDataEvent( EventType<AviLightDataEvent> eventType )
  {
    super( eventType );
  }

}
