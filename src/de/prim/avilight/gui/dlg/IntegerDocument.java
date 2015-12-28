package de.prim.avilight.gui.dlg;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class IntegerDocument extends PlainDocument
{

  /**
   *
   */
  private static final long serialVersionUID = 8753869199982063626L;

  @Override
  public void insertString( int offs, String str, AttributeSet a ) throws BadLocationException
  {
    try
    {
      Integer.parseInt( str );
    }
    catch ( Exception e )
    {
      return;
    }

    super.insertString( offs, str, a );
  }

}
