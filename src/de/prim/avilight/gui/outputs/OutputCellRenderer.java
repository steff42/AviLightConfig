package de.prim.avilight.gui.outputs;

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.prim.comm.data.AviLightConfigData;
import de.prim.comm.data.DataEvent;
import de.prim.comm.data.DataEventListener;
import de.prim.comm.event.CommEventReceiver;
import de.prim.comm.event.CommEventReceiver.ReceiverChannel;

public class OutputCellRenderer extends DefaultTableCellRenderer implements DataEventListener
{

  /** The Constant serialVersionUID. */
  private static final long  serialVersionUID = 515452267753824763L;

  private AviLightConfigData aviLightConfigData;

  private OutputsModel       outputsModel;

  // the segment per receiver input channel or null if invalid
  private Integer            segments[];

  private boolean            voltageLimit;

  public OutputCellRenderer( OutputsModel outputsModel, AviLightConfigData aviLightConfigData )
  {
    super();
    this.outputsModel = outputsModel;
    this.aviLightConfigData = aviLightConfigData;

    aviLightConfigData.addDataEventListener( this );
  }

  @Override
  public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column )
  {
    Component component = super.getTableCellRendererComponent( table, value, isSelected, hasFocus,
        row, column );

    if (segments != null && column >= OutputConfig.INDEX_FIRST_SEGMENT
        && column <= OutputConfig.INDEX_LAST_SEGMENT)
    {
      OutputConfig outputConfig = outputsModel.getOutputConfig( row );

      Integer segment = null;
      if ( voltageLimit )
      {
        segment = 6;
      }
      else
      {
        if ( outputConfig.getInput() > 0 )
        {
          byte[] receiverChannelModes = aviLightConfigData.getReceiverChannelModes();
          if ( ( receiverChannelModes != null )
              && ( receiverChannelModes[outputConfig.getInput() - 1] != 2 ) )
          {
            segment = segments[outputConfig.getInput() - 1];
          }
        }
        if ( segment == null )
        {
          segment = 5;
        }
      }

      if ( ( segment.intValue() + OutputConfig.INDEX_FIRST_SEGMENT ) == column )
      {
        component.setBackground( Color.YELLOW );
      }
      else
      {
        component.setBackground( Color.WHITE );
      }
    }
    else
    {
      component.setBackground( Color.WHITE );
    }

    return component;
  }

  @Override
  public void dataEvent( DataEvent dataEvent )
  {
    switch ( dataEvent.getType() )
    {
      case ReceiverDataReceived:
        ReceiverChannel[] receiverChannels = aviLightConfigData.getReceiverChannelData();
        if ( receiverChannels != null )
        {
          Integer tmp[] = new Integer[receiverChannels.length];

          boolean modified = false;
          for ( int i = 0; i < receiverChannels.length; i++ )
          {
            if ( receiverChannels[i].isValid() )
            {
              tmp[i] = receiverChannels[i].getSegment();
            }

          if (segments == null || segments.length != tmp.length
                || safeEquals( segments[i], tmp[i] ) )
            {
              modified = true;
            }
          }

          segments = tmp;
          if ( modified )
          {
            outputsModel.fireTableDataChanged();
          }
        }
        break;

      case VoltageReceived:
        boolean limited = ( BigDecimal.ZERO.compareTo( aviLightConfigData.getLimit() ) < 0 )
            && ( aviLightConfigData.getVoltage().compareTo( aviLightConfigData.getLimit() ) < 0 );
        if ( limited != voltageLimit )
        {
          voltageLimit = limited;
          outputsModel.fireTableDataChanged();
        }
        break;
      default:
        // Do nothing
        break;
    }
  }

  public void setReceiverInput( CommEventReceiver commEventReceiver )
  {
    ReceiverChannel[] receiverChannels = commEventReceiver.getChannelData();
    Integer tmp[] = new Integer[receiverChannels.length];

    boolean modified = false;
    for ( int i = 0; i < receiverChannels.length; i++ )
    {
      if ( receiverChannels[i].isValid() )
      {
        tmp[i] = receiverChannels[i].getSegment();
      }

      if (segments == null || segments.length != tmp.length
          || safeEquals( segments[i], tmp[i] ) )
      {
        modified = true;
      }
    }

    segments = tmp;
    if ( modified )
    {
      outputsModel.fireTableDataChanged();
    }
  }

  private boolean safeEquals( Integer i1, Integer i2 )
  {
    if ( i1 == null )
    {
      return i2 == null;
    }
    else
    {
      return i1.equals( i2 );
    }
  }

}
