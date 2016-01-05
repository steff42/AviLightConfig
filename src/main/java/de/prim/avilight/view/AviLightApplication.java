package de.prim.avilight.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Steff Lukas
 */
public class AviLightApplication extends Application
{
  /** {@inheritDoc} */
  @Override
  public void start( Stage primaryStage ) throws Exception
  {
    Scene scene = new Scene( new AviLightMainWindow( primaryStage ) );
    primaryStage.setScene( scene );
    primaryStage.setTitle( "AviLight Konfigurator" );
    primaryStage.show();
  }

  public static void main( String[] args )
  {
    launch( args );
  }

}
