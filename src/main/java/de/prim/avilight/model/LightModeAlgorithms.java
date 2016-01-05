package de.prim.avilight.model;

import java.util.Arrays;
import java.util.List;

import de.prim.avilight.model.lightmodes.FlashLightModeImpl;
import de.prim.avilight.model.lightmodes.LightMode;
import de.prim.avilight.model.lightmodes.OffLightModeImpl;
import de.prim.avilight.model.lightmodes.OnLightModeImpl;
import de.prim.avilight.model.lightmodes.PulseLightModeImpl;

/**
 * This class is the bridge between the object oriented LightModes and the
 * algorithm id (a byte value) that is stored on the micro controller.
 *
 * @author Steff Lukas
 */
public class LightModeAlgorithms
{

  /**
   * The constant holding the constantly off light mode.
   */
  public static final LightMode        OFF_LIGHT_MODE   = new OffLightModeImpl();

  /** The constant holding the flashing light mode. */
  public static final LightMode        FLASH_LIGHT_MODE = new FlashLightModeImpl();

  /** The constant holding the pulsing light mode. */
  public static final LightMode        PULSE_LIGHT_MODE = new PulseLightModeImpl();

  /** The constant holding the constantly on light mode. */
  public static final LightMode        ON_LIGHT_MODE    = new OnLightModeImpl();

  /**
   * The Constant ALL_LIGHT_MODES. This is the actual bridge, the index of each
   * light mode is its algorithm id.<br>
   * <b>To stay compatible with existing AviLight controllers do only append new
   * light modes at the end of the list but never change the position of the
   * existing ones!</b>
   */
  private static final List<LightMode> ALL_LIGHT_MODES  = Arrays.asList( OFF_LIGHT_MODE,
      FLASH_LIGHT_MODE, PULSE_LIGHT_MODE, ON_LIGHT_MODE );

  /**
   * Gets the all light modes.
   *
   * @return the all light modes
   */
  public static LightMode[] getAllLightModes()
  {
    return ALL_LIGHT_MODES.toArray( new LightMode[ALL_LIGHT_MODES.size()] );
  }

  /**
   * Gets the algorithm id from light mode.
   *
   * @param lightMode
   *          the light mode
   * @return the algorithm id from light mode
   */
  public static byte getAlgorithmIdFromLightMode( LightMode lightMode )
  {
    return (byte) ALL_LIGHT_MODES.indexOf( lightMode );
  }

  /**
   * Gets the light mode from algorithm id.
   *
   * @param algorithmId
   *          the algorithm id
   * @return the light mode from algorithm id
   */
  public static LightMode getLightModeFromAlgorithmId( byte algorithmId )
  {
    return ALL_LIGHT_MODES.get( algorithmId );
  }

}
