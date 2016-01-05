package de.prim.avilight.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author Steff Lukas
 */
public class ReceiverInput
{
  private SimpleStringProperty  name;

  private SimpleIntegerProperty receiverChannelNo;

  public ReceiverInput( String name, int receiverChannelNo )
  {
    this.name = new SimpleStringProperty( name );
    this.receiverChannelNo = new SimpleIntegerProperty( receiverChannelNo );
  }

  public String getName()
  {
    return name.get();
  }

  public void setName( String name )
  {
    this.name = new SimpleStringProperty( name );
  }

  public int getReceiverChannelNo()
  {
    return receiverChannelNo.get();
  }

  public void setReceiverChannelNo( int receiverChannelNo )
  {
    this.receiverChannelNo = new SimpleIntegerProperty( receiverChannelNo );
  }

  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return name.get();
  }

  /**
   * Gets the available receiver inputs.
   *
   * @param receiverInputCount
   *          the receiver input count
   * @return the available receiver inputs
   */
  public static ReceiverInput[] getAvailableReceiverInputs( int receiverInputCount )
  {
    ReceiverInput[] inputs = new ReceiverInput[receiverInputCount + 1];
    inputs[0] = new ReceiverInput( "Kein Eingang", 0 );
    for ( int i = 1; i <= receiverInputCount; i++ )
    {
      inputs[i] = new ReceiverInput( "Eingang " + i, i );
    }

    return inputs;

  }

}
