package de.prim.avilight.gui.outputs;

import javax.swing.table.DefaultTableCellRenderer;

import de.prim.avilight.gui.ReceiverInputsModel;

public class ReceiverInputRenderer extends DefaultTableCellRenderer
{

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5257470685789097419L;

  private ReceiverInputsModel receiverInputsModel;

  /**
   * Instantiates a new receiver input renderer.
   * 
   * @param receiverInputsModel
   *          the receiver inputs model
   */
  public ReceiverInputRenderer(ReceiverInputsModel receiverInputsModel)
  {
    super();
    this.receiverInputsModel = receiverInputsModel;
  }

  @Override
  protected void setValue(Object value)
  {
    if (value != null && value instanceof Integer)
    {
      setText( receiverInputsModel.getElementAt( (Integer) value ).toString() );
    }
  }

}
