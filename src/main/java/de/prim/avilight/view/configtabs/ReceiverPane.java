package de.prim.avilight.view.configtabs;

import java.text.NumberFormat;

import de.prim.avilight.model.enums.StickMode;
import de.prim.avilight.view.AviLightMainWindow;
import de.prim.avilight.view.events.AviLightDataEvent;
import de.prim.comm.data.AviLightConfigData;
import de.prim.comm.event.CommEventReceiver.ReceiverChannel;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;

public class ReceiverPane extends AviLightTab
{

  private static final NumberFormat VALUE_FORMAT;

  static
  {
    VALUE_FORMAT = NumberFormat.getInstance();
    VALUE_FORMAT.setMaximumFractionDigits( 1 );
    VALUE_FORMAT.setMinimumFractionDigits( 1 );
  }

  GridPane                      contentPane;

  private Label[]               channelValue;
  private Label[]               channelSegment;
  private ComboBox<StickMode>[] channelConfig;
  private CheckBox[]            channelLearn;

  private int                   receiverChannels;

  public ReceiverPane( AviLightConfigData aviLightConfigData, AviLightMainWindow mainWindow )
  {
    super( aviLightConfigData, mainWindow );

    contentPane = new GridPane();
    contentPane.setAlignment( Pos.TOP_LEFT );
    contentPane.setHgap( 10 );
    contentPane.setVgap( 20 );
    contentPane.setPadding( new Insets( 10 ) );
    getChildren().add( contentPane );

    // contentPane.add( new Label( "Kanal" ), 0, 0 );
    contentPane.add( new Label( "Knüppelstellung" ), 1, 0 );
    contentPane.add( new Label( "Segment" ), 2, 0 );
    contentPane.add( new Label( "Konfiguration" ), 3, 0 );
    contentPane.add( new Label( "Knüppelwege lernen" ), 4, 0 );

    buildPaneContent();
  }

  @SuppressWarnings ( "unchecked")
  private void buildPaneContent()
  {
    contentPane.getChildren().remove( 4, contentPane.getChildren().size() );

    channelValue = new Label[receiverChannels];
    channelSegment = new Label[receiverChannels];
    channelConfig = new ComboBox[receiverChannels];
    channelLearn = new CheckBox[receiverChannels];

    for ( int i = 0; i < receiverChannels; i++ )
    {
      Label label = new Label( "Kanal " + ( i + 1 ) );
      contentPane.add( label, 0, i + 1 );

      channelValue[i] = new Label( "--" );
      channelValue[i].setTextAlignment( TextAlignment.RIGHT );

      contentPane.add( channelValue[i], 1, i + 1 );

      channelSegment[i] = new Label( "--" );
      channelSegment[i].setTextAlignment( TextAlignment.RIGHT );
      contentPane.add( channelSegment[i], 2, i + 1 );

      channelConfig[i] = new ComboBox<StickMode>();
      channelConfig[i].getItems().addAll( StickMode.values() );
      contentPane.add( channelConfig[i], 3, i + 1 );

      final int currentChannel = i;
      channelConfig[i].getSelectionModel().selectedIndexProperty().addListener(
          ( ObservableValue<? extends Number> observable, Number oldValue, Number newValue ) ->
          {
            if ( oldValue != newValue )
            {
              aviLightConfigData.setReceiverChannelMode( currentChannel, newValue.intValue() );
            }
          } );

      channelLearn[i] = new CheckBox();
      channelLearn[i].getProperties().put( "index", i );
      contentPane.add( channelLearn[i], 4, i + 1 );
      channelLearn[i].selectedProperty().addListener(
          ( ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue ) ->
          {
            if ( newValue != oldValue )
            {
              aviLightConfigData.setStickLearnMode( currentChannel, newValue.booleanValue() );
              // int selection = 0;
              // for ( int j = 0; j < channelLearn.length; j++ )
              // {
              // if ( channelLearn[j].isSelected() )
              // {
              // selection |= 1 << j;
              // }
              // }
              // aviLightConfigData.setStickLearnMode( (byte) selection );
            }
          } );
    }

  }

  @Override
  protected void handleAviLightDataEvent( AviLightDataEvent event )
  {
    EventType<? extends Event> type = event.getEventType();

    if ( AviLightDataEvent.RECEIVER_DATA_RECEIVED == type )
    {
      ReceiverChannel[] channel = aviLightConfigData.getReceiverChannelData();

      if ( channel != null )
      {
        for ( int i = 0; i < Math.min( channel.length, receiverChannels ); i++ )
        {
          if ( channel[i].isValid() )
          {
            channelValue[i]
                .setText( VALUE_FORMAT.format( ( channel[i].getValue() * 100.0 ) / 255 ) + " %" );

            channelSegment[i].setText( String.valueOf( 1 + channel[i].getSegment() ) );
          }
          else
          {
            channelValue[i].setText( "--" );
            channelSegment[i].setText( "--" );
          }
        }
      }
    }
    else if ( AviLightDataEvent.INFO_DATA_RECEIVED == type )
    {
      if ( receiverChannels != aviLightConfigData.getReceiverChannels() )
      {
        receiverChannels = aviLightConfigData.getReceiverChannels();
        buildPaneContent();
      }
    }
    else if ( AviLightDataEvent.RECEIVER_CHANNEL_MODE_RECEIVED == type )
    {
      byte[] receiverChannelModes = aviLightConfigData.getReceiverChannelModes();
      if ( receiverChannelModes != null )
      {
        for ( int i = 0; i < receiverChannels; i++ )
        {
          channelConfig[i].getSelectionModel().select( receiverChannelModes[i] );
          // System.out.println( "RCM " + i + ": " + receiverChannelModes[i]
          // );
        }
      }
    }
    else if ( AviLightDataEvent.LEARN_STICK_MODE_RECEIVED == type )
    {
      byte learn = aviLightConfigData.getLearnStickMode();
      for ( int i = 0; i < receiverChannels; i++ )
      {
        boolean learnOn = ( learn & ( 1 << i ) ) != 0;
        if ( learnOn != channelLearn[i].isSelected() )
        {
          channelLearn[i].setSelected( learnOn );
        }
      }
    }
  }
}
