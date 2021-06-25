package com.isp.memate.components;

import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;


/**
 * Simple JDialog with Escape-Listener.
 * 
 * @author nwe
 * @since 22.06.2021
 */
public abstract class MeMateDialog extends JDialog
{
  protected JButton abortButton;
  protected JPanel  layoutContainer;

  public MeMateDialog( final Window owner )
  {
    super( owner );
  }

  @Override
  protected void dialogInit()
  {
    super.dialogInit();
    initAbortButton();
    layoutContainer = new JPanel( new GridBagLayout() );
    layoutContainer.setBorder( BorderFactory.createEmptyBorder( 5, 10, 5, 10 ) );
    setModal( true );
  }

  private void initAbortButton()
  {
    abortButton = new JButton( "Abbrechen" );
    abortButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        dispose();
      }
    } );
  }

  @Override
  protected JRootPane createRootPane()
  {
    final ActionListener actionListener = new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent actionEvent )
      {
        setVisible( false );
      }
    };
    final JRootPane rootPane = new JRootPane();
    final KeyStroke stroke = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 );
    rootPane.registerKeyboardAction( actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW );
    return rootPane;
  }

  public void showDialog()
  {
    setVisible( true );
  }

  public abstract void layoutComponents();
}
