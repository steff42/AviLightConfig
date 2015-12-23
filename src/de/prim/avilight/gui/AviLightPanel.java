package de.prim.avilight.gui;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import de.prim.avilight.gui.dlg.ConnectPanel;

public class AviLightPanel extends JPanel implements ActionListener
{

  private static final long serialVersionUID = -5684549354054812537L;

  private static final String PANEL_CONNECT = "ConnectPanel";
  private static final String PANEL_CONFIG = "ConfigPanel";

  private CardLayout cardLayout;

  private ConnectPanel connectPanel;

  private JComponent configPanel;

  private ActionListener actionListener;

  public AviLightPanel(JComponent configPanel, ActionListener actionListener)
  {
    this.configPanel = configPanel;
    this.actionListener = actionListener;
    initGui();
  }

  private void initGui()
  {
    cardLayout = new CardLayout();
    setLayout( cardLayout );

    connectPanel = new ConnectPanel();

    connectPanel.setActionListener( this );
    add( connectPanel, PANEL_CONNECT );
    add( configPanel, PANEL_CONFIG );
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource().equals( connectPanel )
        && e.getActionCommand().equals( ConnectPanel.COMMAND_CONNECT ))
    {
      // ((CardLayout)getLayout()).show( this, PANEL_CONFIG );
      cardLayout.show( this, PANEL_CONFIG );
      // cardLayout.next( this );

      actionListener.actionPerformed( new ActionEvent( connectPanel
          .getSelectedProtocolTester(), 0, ConnectPanel.COMMAND_CONNECT ) );
    }
  }

  public void showConnectPanel()
  {
    cardLayout.show( this, PANEL_CONNECT );
    connectPanel.initConnectPanel();
  }
}
