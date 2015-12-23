package de.prim.avilight.gui.outputs;

import de.prim.avilight.Constants;
import de.prim.comm.data.AviLightConfigData;
import de.prim.comm.event.ProgramDefinition;

public class OutputConfig
{
  public static final int INDEX_CHANNEL = 0;
  public static final int INDEX_INPUT = 1;
  public static final int INDEX_FIRST_SEGMENT = 2;
  public static final int INDEX_LAST_SEGMENT = INDEX_FIRST_SEGMENT
      + Constants.SEGMENT_COUNT - 1;

  private int channel;

  private int input;

  /** The output config data. */
  private ProgramDefinition programDefinitions[];

  /**
   * Instantiates a new output config.
   * 
   * @param channel
   *          the channel
   */
  public OutputConfig(AviLightConfigData aviLightConfigData, int channel)
  {
    super();
    this.channel = channel;
  }

  public Object getValue(int index)
  {
    switch (index)
    {
    case INDEX_CHANNEL:
      return new Integer( 1 + channel );
    case INDEX_INPUT:
      return new Integer( input );
    default:
      return programDefinitions[index - INDEX_FIRST_SEGMENT];

    }
  }

  public void setInput(int input)
  {
    this.input = input;
  }

  public int getInput()
  {
    return input;
  }

  
  public void setProgramDefinitions(ProgramDefinition[] programDefinitions)
  {
    this.programDefinitions = programDefinitions;
  }

  public ProgramDefinition getProgramDefinitions(byte segment)
  {
    return programDefinitions[segment];
  }
}
