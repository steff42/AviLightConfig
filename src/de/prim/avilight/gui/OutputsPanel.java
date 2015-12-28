package de.prim.avilight.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.prim.avilight.OutputType;
import de.prim.avilight.gui.outputs.OutputCellRenderer;
import de.prim.avilight.gui.outputs.OutputsModel;
import de.prim.comm.data.AviLightConfigData;

public class OutputsPanel extends AviLightSuperTab
{

  /** The Constant serialVersionUID. */
  private static final long    serialVersionUID = -3802162139402721971L;

  protected int                numberOfOutputs;

  protected boolean            initialized;

  protected JPanel             panel;

  protected OutputsModel       outputsModel;

  protected OutputCellRenderer outputCellRenderer;

  public OutputsPanel( MainWindow mainWindow, AviLightConfigData aviLightConfigData,
      OutputType outputType )
  {
    super( aviLightConfigData, new GridLayout( 1, 0, 10, 10 ) );
    this.numberOfOutputs = 0;

    initialized = false;

    outputsModel = new OutputsModel( outputType, mainWindow, aviLightConfigData );
    JTable table = new JTable( outputsModel );

    outputCellRenderer = new OutputCellRenderer( outputsModel, aviLightConfigData );
    table.setDefaultRenderer( Object.class, outputCellRenderer );

    add( new JScrollPane( table ) );
    table.setFillsViewportHeight( true );
    table.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );

    outputsModel.setTable( table );
  }
  // @Override
  // public void eventOccured(CommEvent commEvent)
  // {
  // if (commEvent instanceof CommEventClosed)
  // {
  // outputsModel.clear();
  // initialized = false;
  // }
  // else if (commEvent instanceof CommEventChannelInfo)
  // {
  // final CommEventChannelInfo channelInfo = (CommEventChannelInfo) commEvent;
  // if (!initialized)
  // {
  // outputsModel.setOutputs( channelInfo );
  //
  // sendCommand( Command.GET_CONTROLLING_CHANNEL );
  // initialized = true;
  // }
  // }
  // else if (commEvent instanceof CommEventControllingChannel)
  // {
  // final CommEventControllingChannel channelMode =
  // (CommEventControllingChannel) commEvent;
  // outputsModel.setControllingChannel( channelMode );
  //
  // sendCommand( new GetProgramm( outputsModel.getOffset(), (byte) 0 ) );
  // }
  // else if (commEvent instanceof CommEventProgram)
  // {
  // final CommEventProgram program = (CommEventProgram) commEvent;
  // outputsModel.setProgram( program );
  //
  // if (commEvent.getSource() == AviLightProtocol.CMD_GET_PROGRAMM)
  // {
  // byte channel = program.getChannel();
  // byte segment = (byte) (1 + program.getSegment());
  // if (segment >= Constants.SEGMENT_COUNT)
  // {
  // segment = 0;
  // channel++;
  // }
  //
  // if (channel <= outputsModel.getMaxOutputIndex())
  // {
  // sendCommand( new GetProgramm( channel, segment ) );
  // }
  // }
  // }
  // else if (commEvent instanceof CommEventReceiver)
  // {
  // final CommEventReceiver commEventReceiver = (CommEventReceiver) commEvent;
  // outputCellRenderer.setReceiverInput( commEventReceiver );
  // sendCommand( Command.GET_RECEIVER );
  // }
  // }

}
