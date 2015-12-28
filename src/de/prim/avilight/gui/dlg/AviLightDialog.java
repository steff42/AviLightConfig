package de.prim.avilight.gui.dlg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The Class AviLightDialog.
 */
public abstract class AviLightDialog extends JDialog
{

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1864302802506330378L;

  /** The status label. */
  protected JLabel          status;

  /** The ok button. */
  protected JButton         okButton;

  /**
   * Adds the gui contents.
   */
  protected abstract void addGUIContents();

  /**
   * Instantiates a new avi light dialog.
   *
   * @param owner
   *          the owner
   * @param title
   *          the title
   */
  public AviLightDialog( Frame owner, String title )
  {
    super( owner, title, true );
    setDefaultCloseOperation( DISPOSE_ON_CLOSE );

    prepareData();
    initGui();
    pack();
    setLocationRelativeTo( null );
  }

  /**
   * Inits the gui, calls {@link #addGUIContents()}.
   */
  protected void initGui()
  {
    getContentPane().setLayout( new BorderLayout() );

    addGUIContents();

    addButtons();
  }

  /**
   * Adds the buttons by creating a Button panel.
   */
  protected void addButtons()
  {

    JPanel bottom = new JPanel();
    getContentPane().add( bottom, BorderLayout.SOUTH );
    bottom.setLayout( new BorderLayout() );

    status = new JLabel();
    bottom.add( status, BorderLayout.NORTH );
    Dimension dimension = new Dimension( 500, 16 );
    status.setMinimumSize( dimension );
    status.setPreferredSize( dimension );

    JPanel buttons = new JPanel();
    bottom.add( buttons, BorderLayout.EAST );
    buttons.setLayout( new FlowLayout() );

    okButton = new JButton( "Ok" );
    buttons.add( okButton );
    okButton.setEnabled( false );
    okButton.addActionListener( ActionEvent -> okClicked() );

    JButton cancel = new JButton( "Abbruch" );
    buttons.add( cancel );
    cancel.addActionListener( ActionEvent ->
    {
      cancelClicked();
      dispose();
    } );
  }

  /**
   * Prepare data, called before {@link #addGUIContents()}
   */
  protected void prepareData()
  {
  }

  /**
   * Cancel clicked.
   */
  protected void cancelClicked()
  {
    dispose();
  }

  /**
   * Ok clicked.
   */
  protected void okClicked()
  {
    dispose();
  }

  /**
   * Sets the status fiels and enables the ok button if the text is null.
   *
   * @param text
   *          the new status
   */
  protected void setStatus( String text )
  {
    status.setText( text );
    okButton.setEnabled( text == null || text.isEmpty() );
  }
}
