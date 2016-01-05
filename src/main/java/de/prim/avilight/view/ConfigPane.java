package de.prim.avilight.view;

import de.prim.avilight.view.configtabs.InfoPane;
import de.prim.avilight.view.configtabs.SwitchChannelPane;
import de.prim.avilight.view.configtabs.ReceiverPane;
import de.prim.comm.data.AviLightConfigData;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * @author Steff Lukas
 */
public class ConfigPane extends AviLightControlledPane
{
  public final static String CONFIG_PANE_NAME = "configPane";

  private TabPane            contentPane;

  public ConfigPane( AviLightPaneController controller )
  {
    super( controller );

    initContents();
  }

  protected void initContents()
  {
    contentPane = new TabPane();
    getChildren().add( contentPane );

    AviLightConfigData aviLightConfigData = controller.getAviLightConfigData();
    System.out.println( "ConfigPane: " + aviLightConfigData );

    Tab info = new Tab( "Info" );
    info.setClosable( false );
    info.setContent( new InfoPane( aviLightConfigData, controller.getMainWindow() ) );
    contentPane.getTabs().add( info );

    Tab receiver = new Tab( "Empfänger" );
    receiver.setClosable( false );
    receiver.setContent( new ReceiverPane( aviLightConfigData, controller.getMainWindow() ) );
    contentPane.getTabs().add( receiver );

    Tab outputs = new Tab( "Schaltausgänge" );
    outputs.setClosable( false );
    outputs.setContent( new SwitchChannelPane( aviLightConfigData, controller.getMainWindow() ) );
    contentPane.getTabs().add( outputs );

  }

  /** {@inheritDoc} */
  @Override
  protected void close()
  {
    // Nothing to do here
  }

}
