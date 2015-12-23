package de.prim.avilight.gui.dlg;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressDialog extends AviLightDialog
{

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6703782444614477748L;
  private JProgressBar progressBar;

  public ProgressDialog(Frame owner, String title, int maxValue)
  {
    super( owner, title );
    //setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
    
    progressBar.setMaximum( maxValue );
  }

  public static ProgressDialog openNonModal(Frame owner, String title, int maxValue)
  {
    final ProgressDialog progressDialog = new ProgressDialog( owner, title ,maxValue);

    progressDialog.setModal( false );

    progressDialog.setVisible( true );

    return progressDialog;
  }

  @Override
  protected void addGUIContents()
  {
    getContentPane().setLayout( new BorderLayout() );

    JPanel panel = new JPanel();
    getContentPane().add( panel );

    progressBar = new JProgressBar();
    panel.add( progressBar );
  }

  public void setValue(int value)
  {
    progressBar.setValue( value );
    invalidate();
  }
  
  protected void addButtons()
  {  
  }
}
