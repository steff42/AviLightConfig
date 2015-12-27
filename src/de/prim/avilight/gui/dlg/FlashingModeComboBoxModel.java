package de.prim.avilight.gui.dlg;

import de.prim.avilight.gui.AviLightComboBoxModel;
import de.prim.avilight.gui.dlg.lightmode.FlashLightModeImpl;
import de.prim.avilight.gui.dlg.lightmode.LightMode;
import de.prim.avilight.gui.dlg.lightmode.OffLightModeImpl;
import de.prim.avilight.gui.dlg.lightmode.OnLightModeImpl;
import de.prim.avilight.gui.dlg.lightmode.PulseLightModeImpl;

public class FlashingModeComboBoxModel extends AviLightComboBoxModel<LightMode>
{
  public static final int         MODE_OFF      = 0;
  // public static final int MODE_FLASH = 1;
  // public static final int MODE_PULSE = 2;
  // public static final int MODE_ON = 3;

  public static final LightMode[] CHANNEL_MODES =
                                                { new OffLightModeImpl(), new FlashLightModeImpl(),
      new PulseLightModeImpl(), new OnLightModeImpl() };

  @Override
  public LightMode getElementAt( int index )
  {
    return CHANNEL_MODES[index];
  }

  @Override
  public int getSize()
  {
    return CHANNEL_MODES.length;
  }

}
