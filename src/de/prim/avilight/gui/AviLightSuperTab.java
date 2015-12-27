package de.prim.avilight.gui;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import de.prim.comm.data.AviLightConfigData;

/**
 * The Class AviLightPanel is the super class for panels.
 */
public abstract class AviLightSuperTab extends JPanel
{

  /** The Constant serialVersionUID. */
  private static final long    serialVersionUID = 4196891864314185485L;

  protected AviLightConfigData aviLightConfigData;

  /**
   * Instantiates a new avi light panel.
   *
   * @param layoutManager
   *          the layout manager
   */
  public AviLightSuperTab( AviLightConfigData aviLightConfigData, LayoutManager layoutManager )
  {
    super( layoutManager );

    this.aviLightConfigData = aviLightConfigData;
  }

}
