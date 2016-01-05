package de.prim.avilight.view.converter;

import java.math.BigDecimal;

import de.prim.avilight.utils.Constants;
import javafx.util.StringConverter;

/**
 * @author Steff Lukas
 */
public class AviLightBigDecimalConverter extends StringConverter<BigDecimal>
{
  /** {@inheritDoc} */
  @Override
  public BigDecimal fromString( String value )
  {
    if ( value != null && !value.isEmpty() )
    {
      value = value.replace( ",", "." );
      try
      {
        return new BigDecimal( value );
      }
      catch ( Exception e )
      {
        return null;
      }

    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public String toString( BigDecimal value )
  {
    if ( value != null )
    {
      return Constants.NUMBER_FORMAT.format( value );
    }

    return null;
  }

}
