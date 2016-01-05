package de.prim.avilight.model.lightmodes;

import java.math.BigDecimal;

/**
 * The Interface LightMode.
 *
 * @author Steff Lukas
 */
public interface LightMode
{

  /**
   * Gets the mode name.
   *
   * @return the mode name
   */
  String getModeName();

  /**
   * Checks for number of.
   *
   * @return true, if successful
   */
  default boolean hasNumberOfFlashes()
  {
    return getMinNumber() >= 0 && getMaxNumber() >= getMinNumber();
  };

  default int getMinNumber()
  {
    return -1;
  }

  default int getMaxNumber()
  {
    return -1;
  }

  /**
   * Checks for duration.
   *
   * @return true, if successful
   */
  default boolean hasDuration()
  {
    return BigDecimal.ZERO.compareTo( getMinDuration() ) < 0
        && getMinDuration().compareTo( getMaxDuration() ) <= 0;
  };

  /**
   * Checks for off duration.
   *
   * @return true, if successful
   */
  default boolean hasOffDuration()
  {
    return BigDecimal.ZERO.compareTo( getMinOffDuration() ) < 0
        && getMinOffDuration().compareTo( getMaxOffDuration() ) <= 0;
  };

  /**
   * Gets the min duration.
   *
   * @return the min duration
   */
  default BigDecimal getMinDuration()
  {
    return BigDecimal.ZERO;
  }

  /**
   * Gets the max duration.
   *
   * @return the max duration
   */
  default BigDecimal getMaxDuration()
  {
    return BigDecimal.ZERO;
  }

  /**
   * Validates the duration.
   *
   * @param duration
   *          the duration
   * @return a String containing an error message if validation failed, null
   *         otherwise.
   */
  default String validateDuration( BigDecimal duration )
  {
    return null;
  }

  /**
   * Gets the min off duration.
   *
   * @return the min off duration
   */
  default BigDecimal getMinOffDuration()
  {
    return BigDecimal.ZERO;
  }

  /**
   * Gets the max off duration.
   *
   * @return the max off duration
   */
  default BigDecimal getMaxOffDuration()
  {
    return BigDecimal.ZERO;
  }

  /**
   * Validates the off duration.
   *
   * @param offDuration
   *          the off duration
   * @return a String containing an error message if validation failed, null
   *         otherwise.
   */
  default String validateOffDuration( BigDecimal offDuration )
  {
    return null;
  }
}
