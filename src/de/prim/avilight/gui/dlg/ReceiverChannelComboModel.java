package de.prim.avilight.gui.dlg;

import de.prim.avilight.gui.AviLightComboBoxModel;

public class ReceiverChannelComboModel extends AviLightComboBoxModel
{
  public static final String[] MODES = { "Normal", "Invertiert", "Gesperrt" };

  @Override
  public Object getElementAt(int index)
  {
    return MODES[index];
  }

  @Override
  public int getSize()
  {
    return MODES.length;
  }
  
}
