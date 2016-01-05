package de.prim.avilight.view.controls;

import java.util.Optional;

import de.prim.avilight.model.AviLightOutputConfiguration;
import de.prim.avilight.model.ProgramDefinition;
import de.prim.avilight.view.dialogues.OutputConfigDialog;
import de.prim.comm.data.AviLightConfigData;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * The Class ProgramDataTableCell.
 *
 * @author Steff Lukas
 */
public class ProgramDataTableCell extends TableCell<AviLightOutputConfiguration, ProgramDefinition>
{

  /** The dialog. */
  private OutputConfigDialog dialog;

  private AviLightConfigData aviLightConfigData;

  /**
   * For table column.
   *
   * @param dialog
   *          the dialog
   * @return the callback
   */
  public static Callback<TableColumn<AviLightOutputConfiguration, ProgramDefinition>, TableCell<AviLightOutputConfiguration, ProgramDefinition>> forTableColumn(
      OutputConfigDialog dialog, AviLightConfigData aviLightConfigData )
  {
    return param -> new ProgramDataTableCell( dialog, aviLightConfigData );
  }

  /**
   * Instantiates a new program data table cell.
   *
   * @param dialog
   *          the dialog
   */
  public ProgramDataTableCell( OutputConfigDialog dialog, AviLightConfigData aviLightConfigData )
  {
    super();
    this.dialog = dialog;
    this.aviLightConfigData = aviLightConfigData;
  }

  /** {@inheritDoc} */
  @Override
  public void startEdit()
  {
    if ( !isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable() )
    {
      return;
    }
    super.startEdit();

    if ( isEditing() )
    {
      dialog.initializeDialog( getItem() );
      Optional<ProgramDefinition> result = dialog.showAndWait();
      if ( result.isPresent() )
      {
        commitEdit( result.get() );
      }
      else
      {
        cancelEdit();
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public void commitEdit( ProgramDefinition newValue )
  {
    super.commitEdit( newValue );
    aviLightConfigData.setProgram( newValue );
  }

  /** {@inheritDoc} */
  @Override
  protected void updateItem( ProgramDefinition item, boolean empty )
  {
    super.updateItem( item, empty );
    if ( isEmpty() )
    {
      setText( null );
    }
    else if ( isEditing() )
    {
      dialog.initializeDialog( item );
    }
    else
    {
      setText( item != null ? item.toString() : null );
    }

  }
}
