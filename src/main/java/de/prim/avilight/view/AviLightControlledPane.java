package de.prim.avilight.view;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;

/**
 * @author Steff Lukas
 */
public abstract class AviLightControlledPane extends StackPane implements EventHandler<Event>
{
  protected AviLightPaneController controller;

  public AviLightControlledPane( AviLightPaneController controller )
  {
    this.controller = controller;
    this.controller.getStage().addEventHandler( WindowEvent.WINDOW_CLOSE_REQUEST, this );
  }

  public void setController( AviLightPaneController controller )
  {
    this.controller = controller;
  }

  /** {@inheritDoc} */
  @Override
  public void handle( Event event )
  {
    if ( WindowEvent.WINDOW_CLOSE_REQUEST == event.getEventType() )
    {
      close();
    }
  }

  protected abstract void close();

  {
    // Do nothing here
  }
}
