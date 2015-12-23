package de.prim.avilight.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import de.prim.avilight.Constants;
import de.prim.avilight.gui.dlg.ReceiverChannelComboModel;
import de.prim.avilight.gui.utils.SpringUtilities;
import de.prim.comm.data.AviLightConfigData;
import de.prim.comm.data.DataEvent;
import de.prim.comm.data.DataEventListener;
import de.prim.comm.event.CommEventReceiver.ReceiverChannel;

public class ReceiverPanel extends AviLightSuperTab implements
    DataEventListener
{

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6430359563544614699L;

  private static final NumberFormat VALUE_FORMAT;
  static
  {
    VALUE_FORMAT = NumberFormat.getInstance();
    VALUE_FORMAT.setMaximumFractionDigits( 1 );
    VALUE_FORMAT.setMinimumFractionDigits( 1 );
  }

  private JLabel channelValue[];
  private JLabel channelSegment[];
  private JComboBox<Object>[] channelConfig;
  private JCheckBox[] channelLearn;

  @SuppressWarnings("unchecked")
  public ReceiverPanel(final AviLightConfigData aviLightConfigData)
  {
    super( aviLightConfigData, new GridLayout( 1, 0, 10, 10 ) );

    channelValue = new JLabel[Constants.MAX_RECEIVER_CHANNELS];
    channelSegment = new JLabel[Constants.MAX_RECEIVER_CHANNELS];
    channelConfig = new JComboBox[Constants.MAX_RECEIVER_CHANNELS];
    channelLearn = new JCheckBox[Constants.MAX_RECEIVER_CHANNELS];

    JPanel panel = new JPanel( new SpringLayout() );
    add( panel );

    panel.add( new JLabel( "Kanal" ) );
    panel.add( new JLabel( "Knüppelstellung" ) );
    panel.add( new JLabel( "Segment" ) );
    panel.add( new JLabel( "Konfiguration" ) );
    panel.add( new JLabel( "Knüppelwege lernen" ) );

    Dimension minWidth = new Dimension( 50, 10 );
    for (int i = 0; i < Constants.MAX_RECEIVER_CHANNELS; i++)
    {
      JLabel label = new JLabel( "Kanal " + (i + 1) );
      panel.add( label );

      channelValue[i] = new JLabel( "--" );
      channelValue[i].setMinimumSize( minWidth );
      channelValue[i].setPreferredSize( minWidth );
      channelValue[i].setHorizontalAlignment( JLabel.RIGHT );

      panel.add( channelValue[i] );

      channelSegment[i] = new JLabel( "--" );
      channelSegment[i].setHorizontalAlignment( JLabel.RIGHT );
      panel.add( channelSegment[i] );

      channelConfig[i] = new JComboBox<Object>( new ReceiverChannelComboModel() );
      channelConfig[i].setActionCommand( String.valueOf( i ) );
      channelConfig[i].setMaximumSize( new Dimension( 50, 15 ) );
      panel.add( channelConfig[i] );

      channelConfig[i].addActionListener( new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent e)
        {
          int selection = ((JComboBox<Object>) e.getSource())
              .getSelectedIndex();

          aviLightConfigData.setReceiverChannelMode(
              Integer.parseInt( e.getActionCommand() ), selection );
        }
      } );

      channelLearn[i] = new JCheckBox();
      channelLearn[i].setActionCommand( String.valueOf( i ) );
      panel.add( channelLearn[i] );
      channelLearn[i].addActionListener( new ActionListener()
      {

        @Override
        public void actionPerformed(ActionEvent e)
        {
          byte learn = 0;
          for (int i = 0; i < Constants.MAX_RECEIVER_CHANNELS; i++)
          {
            if (channelLearn[i].isSelected())
            {
              learn |= (1 << i);
            }
          }

          aviLightConfigData.setStickLearnMode( learn );
        }
      } );
    }

    SpringUtilities.makeCompactGrid( panel,
        1 + Constants.MAX_RECEIVER_CHANNELS, 5, 6, 6, 6, 6 );

    aviLightConfigData.addDataEventListener( this );
  }

  // @Override
  // public void eventOccured(final CommEvent commEvent)
  // {
  // if (commEvent instanceof CommEventClosed)
  // {
  // for (int i = 0; i < channelValue.length; i++)
  // {
  // channelValue[i].setText( "--" );
  // channelSegment[i].setText( "--" );
  // }
  // }
  // else if (commEvent instanceof CommEventReceiver
  // && commEvent.getSource() == AviLightProtocol.CMD_RECEIVER)
  // {
  // CommEventReceiver receiver = (CommEventReceiver) commEvent;
  // ReceiverChannel[] channel = receiver.getChannelData();
  //
  // if (channel != null)
  // {
  // for (int i = 0; i < channel.length; i++)
  // {
  // if (channel[i].isValid())
  // {
  // channelValue[i]
  // .setText( VALUE_FORMAT.format( channel[i].getValue() * 100.0 / 255 )
  // + " %" );
  //
  // channelSegment[i].setText( String.valueOf( 1 + channel[i]
  // .getSegment() ) );
  // }
  // else
  // {
  // channelValue[i].setText( "--" );
  // channelSegment[i].setText( "--" );
  // }
  // }
  // }
  //
  // sendCommand( Command.GET_RECEIVER );
  // }
  //
  // }

  @Override
  public void dataEvent(DataEvent dataEvent)
  {
    switch (dataEvent.getType())
    {
    case ReceiverDataReceived:
      ReceiverChannel[] channel = aviLightConfigData.getReceiverChannelData();

      if (channel != null)
      {
        for (int i = 0; i < channel.length; i++)
        {
          if (channel[i].isValid())
          {
            channelValue[i]
                .setText( VALUE_FORMAT.format( channel[i].getValue() * 100.0 / 255 )
                    + " %" );

            channelSegment[i].setText( String.valueOf( 1 + channel[i]
                .getSegment() ) );
          }
          else
          {
            channelValue[i].setText( "--" );
            channelSegment[i].setText( "--" );
          }
        }
      }
      break;

    case InfoDataReceived:
      byte[] receiverChannelModes = aviLightConfigData
          .getReceiverChannelModes();
      if (receiverChannelModes != null)
      {
        for (int i = 0; i < receiverChannelModes.length; i++)
        {
          channelConfig[i].setSelectedIndex( receiverChannelModes[i] );
          channelConfig[i].repaint();

          // System.out.println( "RCM " + i + ": " + receiverChannelModes[i] );
        }
      }
      break;

    case LearnStickModeReceived:
      byte learn = aviLightConfigData.getLearnStickMode();
      for (int i = 0; i < Constants.MAX_RECEIVER_CHANNELS; i++)
      {
        boolean learnOn = (learn & (1 << i)) != 0;
        if (learnOn != channelLearn[i].isSelected())
        {
          channelLearn[i].setSelected( learnOn );
        }
      }
      break;
    }
  }
}
