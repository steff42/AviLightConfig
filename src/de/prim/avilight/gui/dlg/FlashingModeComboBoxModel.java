package de.prim.avilight.gui.dlg;

import de.prim.avilight.gui.AviLightComboBoxModel;

public class FlashingModeComboBoxModel extends AviLightComboBoxModel
{
  public static final int MODE_OFF = 0;
  public static final int MODE_FLASH = 1;
  public static final int MODE_PULSE = 2;
  public static final int MODE_ON = 3;
  
  public static final String[] CHANNEL_MODES = { "Aus", "Blitzen", "Puls", "An" };

  /**
   * The Constant HAS_NUMBER_OF defines, if the {@link #CHANNEL_MODES} of the
   * same index, has a numberOf parameter.
   */
  public static final boolean[] HAS_NUMBER_OF = { false, true, false, false };

  /**
   * The Constant HAS_DURATION defines, if the {@link #CHANNEL_MODES} of the
   * same index, has a duration parameter.
   */
  public static final boolean[] HAS_DURATION = { false, true, true, false };

  /** The Constant HAS_OFF_DURATION. */
  public static final boolean[] HAS_OFF_DURATION = { false, true, false, false };

  @Override
  public Object getElementAt(int index)
  {
    return CHANNEL_MODES[index];
  }

  @Override
  public int getSize()
  {
    return CHANNEL_MODES.length;
  }

}
