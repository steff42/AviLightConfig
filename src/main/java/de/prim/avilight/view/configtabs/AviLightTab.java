package de.prim.avilight.view.configtabs;

import de.prim.avilight.view.AviLightMainWindow;
import de.prim.avilight.view.events.AviLightDataEvent;
import de.prim.comm.data.AviLightConfigData;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;

/**
 * @author Steff Lukas
 */
public class AviLightTab extends StackPane implements EventHandler<Event>
{
  protected AviLightConfigData aviLightConfigData;
  protected AviLightMainWindow   mainWindow;

  public AviLightTab( AviLightConfigData aviLightConfigData, AviLightMainWindow mainWindow )
  {
    super();

    this.aviLightConfigData = aviLightConfigData;
    this.mainWindow = mainWindow;
    this.mainWindow.addEventHandler( WindowEvent.WINDOW_CLOSE_REQUEST, this );
    this.mainWindow.addEventHandler( AviLightDataEvent.ANY, this );
  }

  /** {@inheritDoc} */
  @Override
  public void handle( Event event )
  {
    if ( WindowEvent.WINDOW_CLOSE_REQUEST == event.getEventType() )
    {
      close();
    }
    else if ( AviLightDataEvent.ANY == event.getEventType()
        || AviLightDataEvent.ANY == event.getEventType().getSuperType() )
    {
      handleAviLightDataEvent( (AviLightDataEvent) event );
    }
  }

  protected void close()
  {
    // Do nothing here
  }

  protected void handleAviLightDataEvent( AviLightDataEvent event )
  {
    // Do nothing here
  }

}
