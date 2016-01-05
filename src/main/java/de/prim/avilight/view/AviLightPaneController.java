package de.prim.avilight.view;

import java.util.HashMap;
import java.util.Map;

import de.prim.comm.data.AviLightConfigData;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author Steff Lukas
 */
public class AviLightPaneController extends StackPane
{
  private Stage                               stage;

  private AviLightMainWindow                  mainWindow;

  private AviLightConfigData                aviLightConfigData;

  private Map<String, AviLightControlledPane> panes = new HashMap<>();

  public AviLightPaneController( Stage stage, AviLightMainWindow mainWindow )
  {
    super();
    this.stage = stage;
    this.mainWindow = mainWindow;
    this.aviLightConfigData = mainWindow.getAviLightConfigData();
    System.out.println( "PaneController: " + this.aviLightConfigData );
  }

  public void addPane( String name, AviLightControlledPane screen )
  {

    panes.put( name, screen );
  }

  public boolean removePane( String name )
  {
    if ( panes.remove( name ) == null )
    {
      return false;
    }

    return true;
  }

  public boolean setPane( final String name )
  {
    if ( panes.get( name ) != null )
    {
      if ( !getChildren().isEmpty() )
      {
        getChildren().remove( 0 );
      }
      getChildren().add( 0, panes.get( name ) );
      return true;
    }

    return false;
  }

  public AviLightControlledPane getPane( final String name )
  {
    return panes.get( name );
  }

  public Stage getStage()
  {
    return this.stage;
  }

  public AviLightMainWindow getMainWindow()
  {
    return mainWindow;
  }

  public AviLightConfigData getAviLightConfigData()
  {
    return aviLightConfigData;
  }
}
