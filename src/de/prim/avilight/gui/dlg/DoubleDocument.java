package de.prim.avilight.gui.dlg;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import de.prim.avilight.Constants;

public class DoubleDocument extends PlainDocument implements FocusListener
{
  private static final long serialVersionUID = 4683112732337036569L;

  private JTextField textField;

  public DoubleDocument(JTextField textField)
  {
    super();
    this.textField = textField;

    textField.setDocument( this );
    textField.setHorizontalAlignment( JTextField.RIGHT );
    textField.addFocusListener( this );
  }

  @Override
  public void insertString(int offs, String str, AttributeSet a)
      throws BadLocationException
  {
    if (str != null && !str.isEmpty())
    {
      StringBuilder sb = new StringBuilder( textField.getText() );
      sb.insert( offs, str );

      String value = sb.toString().replace( ",", "." );
      try
      {
        Double.parseDouble( value );
      }
      catch (Exception e)
      {
        return;
      }
    }
    super.insertString( offs, str, a );
  }

  public JTextField getTextField()
  {
    return textField;
  }

  public Double getValue()
  {
    String value = textField.getText().replace( ",", "." );
    try
    {
      return Double.parseDouble( value );
    }
    catch (Exception e)
    {
      return 0.0;
    }
  }

  public void setValue(double value)
  {
    textField.setText( Constants.NUMBER_FORMAT.format( value ) );
  }

  @Override
  public void focusGained(FocusEvent e)
  {
  }

  @Override
  public void focusLost(FocusEvent e)
  {
    Double value = getValue();
    if (value == null)
    {
      value = 0.0;
    }

    setValue( value );
  }

}
