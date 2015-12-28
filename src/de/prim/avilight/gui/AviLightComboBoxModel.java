package de.prim.avilight.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public abstract class AviLightComboBoxModel<T> implements ComboBoxModel<T>
{

  /** The listener. */
  private List<ListDataListener> listener = new ArrayList<ListDataListener>();

  /** The selected item. */
  private T                      selectedItem;

  /**
   * Send event.
   *
   * @param listDataEvent
   *          the list data event
   */
  public void sendEvent( ListDataEvent listDataEvent )
  {
    for ( ListDataListener listDataListener : listener )
    {
      switch ( listDataEvent.getType() )
      {
        case ListDataEvent.CONTENTS_CHANGED:
          listDataListener.contentsChanged( listDataEvent );
          break;

        case ListDataEvent.INTERVAL_ADDED:
          listDataListener.intervalAdded( listDataEvent );
          break;

        case ListDataEvent.INTERVAL_REMOVED:
          listDataListener.intervalRemoved( listDataEvent );
          break;

        default:
          throw new IllegalArgumentException();
      }
    }
  }

  @Override
  public void removeListDataListener( ListDataListener listDataListener )
  {
    listener.remove( listDataListener );
  }

  @Override
  public void addListDataListener( ListDataListener listDataListener )
  {
    listener.add( listDataListener );
  }

  @Override
  public T getSelectedItem()
  {
    return selectedItem;
  }

  /** {@inheritDoc} */
  @SuppressWarnings ( "unchecked")
  @Override
  public void setSelectedItem( Object anItem )
  {
    selectedItem = (T) anItem;
  }

}
