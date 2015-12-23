package de.prim.comm.data;

import de.prim.comm.Command.Command;

/**
 * The Class ModifiableImpl.
 */
public class ModifiableImpl implements Modifiable
{

  /** The command. */
  private Command command;

  /**
   * Instantiates a new modifiable impl.
   * 
   * @param command
   *          the command
   */
  public ModifiableImpl(Command command)
  {
    super();
    this.command = command;
  }

  /* (non-Javadoc) */
  @Override
  public Command getCommand()
  {
    return command;
  }

}
