package de.prim.avilight.gui.dlg.lightmode;

/**
 * The Class OnLightModeImpl.
 *
 * @author Steff Lukas
 */
public class OnLightModeImpl implements LightMode
{

  /** {@inheritDoc} */
  @Override
  public String getModeName()
  {
    return "An";
  }

  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return getModeName();
  }

}
