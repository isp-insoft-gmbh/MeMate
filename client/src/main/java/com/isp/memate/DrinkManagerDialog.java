/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Der {@link DrinkManagerDialog} erzeugt einen neuen Frame, wenn im {@link Drinkmanager} ein Getränk
 * hinzugefügt oder bearbeitet wird. In diesem Frame kann man den Name, Preis, und ein Bild des Getränks
 * angeben oder editieren
 * 
 * @author nwe
 * @since 18.10.2019
 */
public class DrinkManagerDialog
{
  private JDialog                  dialog;
  private final JPanel             layout            = new JPanel( new GridBagLayout() );
  private final JTextField         drinkNameField    = new JTextField();
  private final JTextField         drinkPictureField = new JTextField();
  private final SpinnerNumberModel spinnerModel      = new SpinnerNumberModel( 0, 0, 2, 0.10 );
  private final JSpinner           drinkPriceSpinner = new JSpinner( spinnerModel );
  private final JButton            confirmButton     = new JButton();
  private final JButton            fileChooserButton = new JButton( "Auswählen..." );
  private final JFileChooser       fileChooser       = new JFileChooser();

  /**
   * Erzeugt den Frame und setzt das Layout der vorhandenen Kompnenten.
   * 
   * @param owner Parent für den aufzurufenden Dialog
   */
  public DrinkManagerDialog( Window owner )
  {
    final JLabel drinkName = new JLabel( "Name" );
    final JLabel drinkPicture = new JLabel( "Bild" );
    final JLabel drinkPrice = new JLabel( "Preis" );
    final JButton cancelButton = new JButton( "Abbrechen" );
    layout.setBorder( new EmptyBorder( 5, 10, 5, 10 ) );

    fileChooser.setFileFilter( new FileNameExtensionFilter( "Bilder", "jpg", "png", "gif" ) );

    GridBagConstraints drinkNameConstraints = new GridBagConstraints();
    drinkNameConstraints.gridx = 0;
    drinkNameConstraints.gridy = 0;
    drinkNameConstraints.anchor = GridBagConstraints.LINE_START;
    layout.add( drinkName, drinkNameConstraints );
    GridBagConstraints drinkNameFieldConstraints = new GridBagConstraints();
    drinkNameFieldConstraints.gridx = 1;
    drinkNameFieldConstraints.gridy = 0;
    drinkNameFieldConstraints.gridwidth = 2;
    drinkNameFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
    drinkNameFieldConstraints.insets = new Insets( 5, 10, 0, 0 );
    layout.add( drinkNameField, drinkNameFieldConstraints );

    GridBagConstraints drinkPictureConstraints = new GridBagConstraints();
    drinkPictureConstraints.gridx = 0;
    drinkPictureConstraints.gridy = 1;
    drinkPictureConstraints.anchor = GridBagConstraints.LINE_START;
    drinkPictureConstraints.insets = new Insets( 5, 0, 0, 0 );
    layout.add( drinkPicture, drinkPictureConstraints );
    GridBagConstraints drinkPictureFieldConstraints = new GridBagConstraints();
    drinkPictureFieldConstraints.gridx = 1;
    drinkPictureFieldConstraints.gridy = 1;
    drinkPictureFieldConstraints.weightx = 1.0;
    drinkPictureFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
    drinkPictureFieldConstraints.insets = new Insets( 5, 10, 0, 0 );
    layout.add( drinkPictureField, drinkPictureFieldConstraints );
    GridBagConstraints drinkPictureFileChooserConstraints = new GridBagConstraints();
    drinkPictureFileChooserConstraints.gridx = 2;
    drinkPictureFileChooserConstraints.gridy = 1;
    drinkPictureFileChooserConstraints.anchor = GridBagConstraints.LINE_START;
    drinkPictureFileChooserConstraints.insets = new Insets( 5, 5, 0, 0 );
    layout.add( fileChooserButton, drinkPictureFileChooserConstraints );

    GridBagConstraints drinkPriceConstraints = new GridBagConstraints();
    drinkPriceConstraints.gridx = 0;
    drinkPriceConstraints.gridy = 2;
    drinkPriceConstraints.anchor = GridBagConstraints.LINE_START;
    drinkPriceConstraints.insets = new Insets( 5, 0, 0, 0 );
    layout.add( drinkPrice, drinkPriceConstraints );
    GridBagConstraints drinkPriceSpinnerConstraints = new GridBagConstraints();
    drinkPriceSpinnerConstraints.gridx = 1;
    drinkPriceSpinnerConstraints.gridy = 2;
    drinkPriceSpinnerConstraints.gridwidth = 2;
    drinkPriceSpinnerConstraints.anchor = GridBagConstraints.LINE_START;
    drinkPriceSpinnerConstraints.fill = GridBagConstraints.HORIZONTAL;
    drinkPriceSpinnerConstraints.insets = new Insets( 5, 10, 0, 0 );
    layout.add( drinkPriceSpinner, drinkPriceSpinnerConstraints );

    JPanel buttonBar = new JPanel();
    buttonBar.setLayout( new BoxLayout( buttonBar, BoxLayout.X_AXIS ) );
    buttonBar.add( confirmButton );
    buttonBar.add( Box.createHorizontalStrut( 5 ) );
    buttonBar.add( cancelButton );


    GridBagConstraints buttonBarConstraints = new GridBagConstraints();
    buttonBarConstraints.gridx = 0;
    buttonBarConstraints.gridy = 3;
    buttonBarConstraints.gridwidth = 3;
    buttonBarConstraints.anchor = GridBagConstraints.LINE_END;
    buttonBarConstraints.insets = new Insets( 10, 0, 0, 0 );
    layout.add( buttonBar, buttonBarConstraints );

    dialog = new JDialog( owner );
    JRootPane rootPane = dialog.getRootPane();
    final String quitDialogActionName = "QUIT_DIALOG";
    rootPane
        .getActionMap()
        .put( quitDialogActionName, new AbstractAction()
        {
          @Override
          public void actionPerformed( ActionEvent __ )
          {
            dialog.dispose();
          }
        } );
    rootPane
        .getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW )
        .put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ), quitDialogActionName );

    fileChooserButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent __ )
      {
        fileChooser.showOpenDialog( owner );

      }
    } );

    cancelButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent __ )
      {
        dialog.dispose();
      }
    } );

    SwingUtilities.getRootPane( dialog ).setDefaultButton( confirmButton );
    dialog.add( layout );
    dialog.pack();
    Dimension oldPreferredSize = dialog.getPreferredSize();
    dialog.setSize( new Dimension( oldPreferredSize.width + 300, oldPreferredSize.height ) );
    dialog.setModal( true );
    dialog.setResizable( false );
    dialog.setLocationRelativeTo( dialog.getOwner() );
  }

  /**
   * Shows a dialog for editing the passed drink.
   * 
   * @param drink selected Drink
   */
  public void showEditDialog( String drink )
  {
    dialog.setTitle( "Getränk bearbeiten" );
    confirmButton.setText( "Speichern" );
    drinkNameField.setText( drink );
    drinkPictureField.setText( drink.replace( "MioMio ", "mio" ) + "Icon.png" );
    drinkPriceSpinner.setValue( DrinkInfos.getInstance().getPrice( drink ) );
    dialog.setVisible( true );
  }

  /**
   * Shows a dialog for adding a new drink.
   */
  public void showNewDialog()
  {
    dialog.setTitle( "Getränk hinzufügen" );
    confirmButton.setText( "Hinzufügen" );
    dialog.setVisible( true );
  }
}
