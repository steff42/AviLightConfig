package de.prim.avilight.gui.outputs;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import de.prim.avilight.OutputType;
import de.prim.avilight.gui.MainWindow;
import de.prim.avilight.gui.ReceiverInputsModel;
import de.prim.comm.data.AviLightConfigData;
import de.prim.comm.data.DataEvent;
import de.prim.comm.data.DataEventListener;
import de.prim.comm.event.CommEventReceiverChannelMode;
import de.prim.comm.event.ProgramDefinition;

/**
 * The Class OutputsModel.
 */
public class OutputsModel extends AbstractTableModel implements
    DataEventListener
{

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4394570266886630971L;

  /** The Constant COLUMN_NAMES. */
  private static final String COLUMN_NAMES[] = { "Ausgang", "Empfängereingang",
      "Segment 1", "Segment 2", "Segment 3", "Segment 4", "Segment 5",
      "kein Eingang", "Akku leer" };

  /**
   * The offset of output index. The hardware starts index with pwm outputs,
   * then switch outputs
   */
  private int offset;

  /** The output type. */
  private OutputType ouputType;

  /** The output config. */
  private OutputConfig[] outputConfig;

  /** The receiver inputs model. */
  private ReceiverInputsModel receiverInputsModel;

  /** The table. */
  private JTable table;

  /** The frame. */
  private MainWindow mainWindow;

  /** The avi light config data. */
  private AviLightConfigData aviLightConfigData;

  /**
   * Instantiates a new outputs model.
   * 
   * @param ouputType
   *          the ouput type
   * @param frame
   *          the frame
   */
  public OutputsModel(OutputType ouputType, MainWindow mainWindow,
      AviLightConfigData aviLightConfigData)
  {
    super();
    this.ouputType = ouputType;
    this.mainWindow = mainWindow;
    this.aviLightConfigData = aviLightConfigData;

    receiverInputsModel = new ReceiverInputsModel( 0 );

    aviLightConfigData.addDataEventListener( this );
  }

  /**
   * Sets the table.
   * 
   * @param table
   *          the new table
   */
  public void setTable(JTable table)
  {
    this.table = table;
    prepareTable();
  }

  /**
   * Prepare table.
   */
  private void prepareTable()
  {
    TableColumn inputColumn = table.getColumnModel().getColumn(
        OutputConfig.INDEX_INPUT );
    JComboBox<Object> comboBox = new JComboBox<Object>( receiverInputsModel );
    inputColumn.setCellEditor( new DefaultCellEditor( comboBox ) );
    inputColumn
        .setCellRenderer( new ReceiverInputRenderer( receiverInputsModel ) );

    for (int i = OutputConfig.INDEX_FIRST_SEGMENT; i < COLUMN_NAMES.length; i++)
    {
      table
          .getColumnModel()
          .getColumn( i )
          .setCellEditor(
              new OutputConfigEditor( mainWindow, aviLightConfigData ) );
    }
  }

  public void clear()
  {
    outputConfig = null;
    fireTableDataChanged();
  }

  @Override
  public void dataEvent(DataEvent dataEvent)
  {
    if (DataEvent.Type.ProgramDataReceived.equals( dataEvent.getType() ))
    {
      ProgramDefinition[][] programDefinitions;

      receiverInputsModel.setInputs( aviLightConfigData.getReceiverChannels() );
      switch (ouputType)
      {
      case Switch:
        offset = aviLightConfigData.getOutputChannels();
        programDefinitions = aviLightConfigData.getSwitchChannelDefinition();
        setOutputs( aviLightConfigData.getSwitchChannels() );
        break;
      case PWM:
        offset = 0;
        programDefinitions = aviLightConfigData.getOutputChannelsDefinition();
        setOutputs( aviLightConfigData.getOutputChannels() );
        break;
      default:
        throw new IllegalArgumentException();
      }

      byte[] channel = aviLightConfigData.getControllingChannel();
      for (int i = 0; i < outputConfig.length; i++)
      {
        outputConfig[i].setInput( channel[i + offset] );
        outputConfig[i].setProgramDefinitions( programDefinitions[i] );
      }

      fireTableDataChanged();
    }
  }

  /**
   * Sets the outputs.
   * 
   * @param outputs
   *          the new outputs
   */
  private void setOutputs(int outputs)
  {
    outputConfig = new OutputConfig[outputs];
    for (int i = 0; i < outputs; i++)
    {
      outputConfig[i] = new OutputConfig( aviLightConfigData, i + offset );
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.AbstractTableModel#getColumnName(int)
   */
  @Override
  public String getColumnName(int idx)
  {
    return COLUMN_NAMES[idx];
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  @Override
  public int getColumnCount()
  {
    return COLUMN_NAMES.length;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getRowCount()
   */
  @Override
  public int getRowCount()
  {
    if (outputConfig == null)
    {
      return 0;
    }
    else
    {
      return outputConfig.length;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
   */
  @Override
  public boolean isCellEditable(int row, int col)
  {
    if (outputConfig != null)
    {
      OutputConfig config = outputConfig[row];

      if (config.getInput() > 0)
      {
        return (col >= OutputConfig.INDEX_INPUT);
      }
      else
      {
        return (col == OutputConfig.INDEX_INPUT)
            || (col >= OutputConfig.INDEX_LAST_SEGMENT - 1);
      }
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
   */
  @Override
  public Class<?> getColumnClass(int columnIndex)
  {
    switch (columnIndex)
    {
    case OutputConfig.INDEX_INPUT:
      return Integer.class;
    default:
      return super.getColumnClass( columnIndex );
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   */
  @Override
  public Object getValueAt(int row, int col)
  {
    return outputConfig[row].getValue( col );
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int,
   * int)
   */
  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
    Object value = aValue;
    switch (columnIndex)
    {
    case OutputConfig.INDEX_INPUT:
      value = receiverInputsModel.getValue( (String) aValue );

      if (value != null && value instanceof Integer)
      {
        aviLightConfigData.setControllingChannel( (byte) (rowIndex + offset),
            ((Integer) value).byteValue() );

        outputConfig[rowIndex].setInput( ((Integer) value).intValue() );
      }
      break;
    }

  }

  public void setChannelMode(CommEventReceiverChannelMode channelMode)
  {
    // TODO Auto-generated method stub

  }

  public OutputConfig getOutputConfig(int index)
  {
    return outputConfig[index];
  }

}
