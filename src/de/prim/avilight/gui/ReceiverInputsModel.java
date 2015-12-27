package de.prim.avilight.gui;

import javax.swing.event.ListDataEvent;

public class ReceiverInputsModel extends AviLightComboBoxModel<String>
{

  /** The inputs. */
  private int inputs;

  /**
   * Instantiates a new receiver inputs model.
   *
   * @param inputs
   *          the inputs
   */
  public ReceiverInputsModel( int inputs )
  {
    super();
    this.inputs = inputs;

    setSelectedItem( getElementAt( 0 ) );
  }

  /**
   * Gets the inputs.
   *
   * @return the inputs
   */
  public int getInputs()
  {
    return inputs;
  }

  /**
   * Sets the inputs.
   *
   * @param inputs
   *          the new inputs
   */
  public void setInputs( int inputs )
  {
    this.inputs = inputs;

    sendEvent( new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, 0, inputs ) );
  }

  /** {@inheritDoc} */
  @Override
  public String getElementAt( int idx )
  {
    if ( idx == 0 )
    {
      return "Kein Eingang";
    }
    else
    {
      return "Eingang " + idx;
    }
  }

  /** {@inheritDoc} */
  @Override
  public int getSize()
  {
    return inputs + 1;
  }

  public Integer getValue( String text )
  {
    if ( text != null )
    {
      for ( int i = 0; i < getSize(); i++ )
      {
        if ( text.equals( getElementAt( i ) ) )
        {
          return i;
        }
      }
    }
    return null;
  }

}
