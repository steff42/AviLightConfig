package de.prim.avilight.model.enums;

/**
 * The Enum StickMode.
 *
 * @author Steff Lukas
 */
public enum StickMode
{

  /** Normal stick mode. */
  NORMAL( "Normal" ),

  /** The stick is inverted. */
  INVERTED( "Invertiert" ),

  /** The stick is disabeled. */
  DISABELED( "Gesperrt" );

  /** The name. */
  private String name;

  /**
   * Instantiates a new stick mode.
   *
   * @param name
   *          the name
   */
  private StickMode( String name )
  {
    this.name = name;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName()
  {
    return name;
  }

  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return name;
  }

}
