package de.prim.avilight.model;

import java.util.ArrayList;
import java.util.List;

import de.prim.avilight.utils.Constants;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * The Class AviLightOutputConfiguration.
 */
public class AviLightOutputConfiguration
{

  /** The Constant INDEX_CHANNEL. */
  public static final int                               INDEX_CHANNEL       = 0;

  /** The Constant INDEX_INPUT. */
  public static final int                               INDEX_INPUT         = 1;

  /** The Constant INDEX_FIRST_SEGMENT. */
  public static final int                               INDEX_FIRST_SEGMENT = 2;

  /** The Constant INDEX_LAST_SEGMENT. */
  public static final int                               INDEX_LAST_SEGMENT  = INDEX_FIRST_SEGMENT
      + Constants.SEGMENT_COUNT - 1;

  // private int programSegements;

  /** The output channel. */
  private SimpleIntegerProperty                         outputChannel;

  /** The receiver input. */
  private SimpleObjectProperty<ReceiverInput>           receiverInput;

  /** The output config data. */
  private List<SimpleObjectProperty<ProgramDefinition>> segmentPrograms;

  /**
   * Instantiates a new output config.
   *
   * @param segmentCount
   *          the segment count
   * @param outputChannel
   *          the outputChannel
   * @param receiverInput
   *          the receiver input
   * @param programDefinitions
   *          the program definitions
   * @param noInputProgram
   *          the no input program
   * @param batteryLowProgram
   *          the battery low program
   */
  public AviLightOutputConfiguration( int segmentCount, int outputChannel,
      ReceiverInput receiverInput, ProgramDefinition[] programDefinitions,
      ProgramDefinition noInputProgram, ProgramDefinition batteryLowProgram )
  {
    super();
    this.outputChannel = new SimpleIntegerProperty( outputChannel );
    this.receiverInput = new SimpleObjectProperty<>( receiverInput );
    this.segmentPrograms = new ArrayList<>( outputChannel );

    for ( int i = 0; i < segmentCount; i++ )
    {
      this.segmentPrograms.add( new SimpleObjectProperty<>( programDefinitions[i] ) );
    }
  }

  /**
   * Gets the output channel.
   *
   * @return the output channel
   */
  public int getOutputChannel()
  {
    return outputChannel.get();
  }

  /**
   * Sets the output channel.
   *
   * @param outputChannel
   *          the new output channel
   */
  public void setOutputChannel( int outputChannel )
  {
    this.outputChannel.set( outputChannel );
  }

  /**
   * Gets the output channel property.
   *
   * @return the output channel property
   */
  public Property<Number> getOutputChannelProperty()
  {
    return outputChannel;
  }

  /**
   * Gets the receiver input.
   *
   * @return the receiver input
   */
  public ReceiverInput getReceiverInput()
  {
    return receiverInput.get();
  }

  /**
   * Sets the receiver input.
   *
   * @param receiverInput
   *          the new receiver input
   */
  public void setReceiverInput( ReceiverInput receiverInput )
  {
    this.receiverInput.set( receiverInput );
  }

  /**
   * Gets the receiver input property.
   *
   * @return the receiver input property
   */
  public Property<ReceiverInput> getReceiverInputProperty()
  {
    return receiverInput;
  }

  /**
   * Gets the program definition.
   *
   * @param segment
   *          the segment
   * @return the program definition
   */
  public ProgramDefinition getProgramDefinition( int segment )
  {
    if ( segment < segmentPrograms.size() )
    {
      return segmentPrograms.get( segment ).get();
    }
    else
    {
      return null;
    }
  }

  /**
   * Gets the program definition as property.
   *
   * @param segment
   *          the segment
   * @return the program definition as property
   */
  public Property<ProgramDefinition> getProgramDefinitionAsProperty( int segment )
  {
    if ( segment < segmentPrograms.size() )
    {
      return segmentPrograms.get( segment );
    }
    else
    {
      return null;
    }
  }

  /**
   * Gets the program definition.
   *
   * @return the program definition
   */
  public ProgramDefinition[] getSegmentPrograms()
  {
    ProgramDefinition[] toReturn = new ProgramDefinition[segmentPrograms.size()];
    for ( int i = 0; i < segmentPrograms.size(); i++ )
    {
      toReturn[i] = segmentPrograms.get( i ).get();
    }

    return toReturn;
  }

  /**
   * Sets the program definitions.
   *
   * @param programDefinitions
   *          the new program definitions
   */
  public void setSegmentPrograms( ProgramDefinition[] programDefinitions )
  {
    for ( int i = 0; i < segmentPrograms.size(); i++ )
    {
      segmentPrograms.get( i ).set( programDefinitions[i] );
    }
  }

  /**
   * Sets the segment program for a single segment.
   *
   * @param segment
   *          the segment
   * @param programDefinition
   *          the program definition
   */
  public void setSegmentProgram( byte segment, ProgramDefinition programDefinition )
  {
    SimpleObjectProperty<ProgramDefinition> property = segmentPrograms.get( segment );
    if ( property != null )
    {
      property.setValue( programDefinition );
    }
  }

}
