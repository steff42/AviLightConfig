package de.prim.avilight.gui.dlg;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import de.prim.avilight.Constants;

/**
 * The Class BigDecimalDocument.
 */
public class BigDecimalDocument extends PlainDocument implements FocusListener
{

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4683112732337036569L;

  /** The text field. */
  private JTextField        textField;

  /**
   * Instantiates a new big decimal document.
   *
   * @param textField
   *          the text field
   */
  public BigDecimalDocument( JTextField textField )
  {
    super();
    this.textField = textField;

    this.textField.setDocument( this );
    this.textField.setHorizontalAlignment( SwingConstants.RIGHT );
    this.textField.addFocusListener( this );
  }

  /** {@inheritDoc} */
  @Override
  public void insertString( int offs, String str, AttributeSet a ) throws BadLocationException
  {
    if ( ( str != null ) && !str.isEmpty() )
    {
      StringBuilder sb = new StringBuilder( textField.getText() );
      sb.insert( offs, str );

      String value = sb.toString().replace( ",", "." );
      try
      {
        new BigDecimal( value );
      }
      catch ( Exception e )
      {
        return;
      }
    }
    super.insertString( offs, str, a );
  }

  /**
   * Gets the text field.
   *
   * @return the text field
   */
  public JTextField getTextField()
  {
    return textField;
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public BigDecimal getValue()
  {
    BigDecimal toReturn = null;
    String value = textField.getText();
    if ( value != null && !value.isEmpty() )
    {
      value = value.replace( ",", "." );
      try
      {
        toReturn = new BigDecimal( value );
      }
      catch ( Exception e )
      {
        toReturn = BigDecimal.ZERO;
      }
    }
    else
    {
      toReturn = BigDecimal.ZERO;
    }

    return toReturn;
  }

  /**
   * Sets the value.
   *
   * @param value
   *          the new value
   */
  public void setValue( BigDecimal value )
  {
    if ( value != null )
    {
      textField.setText( Constants.NUMBER_FORMAT.format( value ) );
    }
    else
    {
      textField.setText( "" );
    }
  }

  /** {@inheritDoc} */
  @Override
  public void focusGained( FocusEvent e )
  {
    // Do nothing
  }

  /** {@inheritDoc} */
  @Override
  public void focusLost( FocusEvent e )
  {
    // Do nothing
  }

}
