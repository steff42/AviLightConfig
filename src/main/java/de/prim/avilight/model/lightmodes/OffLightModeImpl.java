package de.prim.avilight.model.lightmodes;

/**
 * The Class OffLightModeImpl.
 *
 * @author Steff Lukas
 */
public class OffLightModeImpl implements LightMode
{

  /** {@inheritDoc} */
  @Override
  public String getModeName()
  {
    return "Aus";
  }

  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return getModeName();
  }
}
