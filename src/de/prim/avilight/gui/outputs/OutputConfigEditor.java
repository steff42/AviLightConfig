package de.prim.avilight.gui.outputs;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import de.prim.comm.data.AviLightConfigData;
import de.prim.comm.event.ProgramDefinition;

public class OutputConfigEditor extends AbstractCellEditor implements
    TableCellEditor, ActionListener
{

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4591009236613167676L;

  private AviLightConfigData aviLightConfigData;
  private ProgramDefinition data;

  private JButton button;
  private JDialog dialog;
  private Frame frame;
  protected static final String EDIT = "edit";

  public OutputConfigEditor(Frame frame, AviLightConfigData aviLightConfigData)
  {
    super();
    this.frame = frame;
    this.aviLightConfigData = aviLightConfigData;

    button = new JButton();
    button.setActionCommand( EDIT );
    button.addActionListener( this );
    button.setBorderPainted( false );
  }

  public void actionPerformed(ActionEvent e)
  {
    if (EDIT.equals( e.getActionCommand() ))
    {
      dialog = new OutputConfigDialog( frame, aviLightConfigData, data );
      dialog.setVisible( true );

      fireEditingStopped(); // Make the renderer reappear.
    }
  }

  // Implement the one CellEditor method that AbstractCellEditor doesn't.
  public Object getCellEditorValue()
  {
    return data;
  }

  // Implement the one method defined by TableCellEditor.
  public Component getTableCellEditorComponent(JTable table, Object value,
      boolean isSelected, int row, int column)
  {
    data = (ProgramDefinition) value;

    return button;
  }
}
