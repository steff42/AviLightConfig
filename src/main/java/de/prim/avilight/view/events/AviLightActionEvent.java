package de.prim.avilight.view.events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * The Class AviLightDataEvent.
 *
 * @author Steff Lukas
 */
public class AviLightActionEvent extends Event
{

  /** The Constant serialVersionUID. */
  private static final long                          serialVersionUID = 1355570148462054639L;

  /** The Constant ANY, the parent of all AviLightDataEvents. */
  public final static EventType<AviLightActionEvent> ANY              = new EventType<>( Event.ANY,
      "AVILIGHT_ACTION" );

  /** No App found, only bootloader. */
  public final static EventType<AviLightActionEvent> ASK_FOR_UPGRADE  = new EventType<>(
      AviLightActionEvent.ANY, "ASK_FOR_UPGRADE" );

  /** The Constant START_PROGRESS. */
  public final static EventType<AviLightActionEvent> START_PROGRESS   = new EventType<>(
      AviLightActionEvent.ANY, "START_PROGRESS" );

  /** The Constant PROGRESS. */
  public final static EventType<AviLightActionEvent> PROGRESS         = new EventType<>(
      AviLightActionEvent.ANY, "ACTION_PROGRESS" );

  /** The Constant STOP_PROGRESS. */
  public final static EventType<AviLightActionEvent> STOP_PROGRESS    = new EventType<>(
      AviLightActionEvent.ANY, "STOP_PROGRESS" );

  /** The action data. */
  private int                                        actionData;

  /**
   * Instantiates a new AviLight data event.
   *
   * @param eventType
   *          the event type
   * @param actionData
   *          the action data
   */
  public AviLightActionEvent( EventType<AviLightActionEvent> eventType, int actionData )
  {
    super( eventType );
    this.actionData = actionData;
  }

  /**
   * Gets the action data.
   *
   * @return the action data
   */
  public int getActionData()
  {
    return actionData;
  }

}
