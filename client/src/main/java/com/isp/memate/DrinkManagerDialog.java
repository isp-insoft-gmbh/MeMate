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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

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
  private final JPanel             addDrinkPanel     = new JPanel( new GridBagLayout() );
  private final JTextField         drinkNameField    = new JTextField();
  private final JTextField         drinkPictureField = new JTextField();
  private final SpinnerNumberModel spinnerModel      = new SpinnerNumberModel( 0, 0, 2, 0.10 );
  private final JSpinner           drinkPriceSpinner = new JSpinner( spinnerModel );
  private final JButton            confirmButton     = new JButton();

  /**
   * Erzeugt den Frame und setzt das Layout der vorhandenen Kompnenten.
   * 
   * @param owner Parent für den aufzurufenden Dialog
   */
  public DrinkManagerDialog( Window owner )
  {
    dialog = new JDialog( owner );
    final JLabel drinkName = new JLabel( "Name" );
    final JLabel drinkPicture = new JLabel( "Bild" );
    final JLabel drinkPrice = new JLabel( "Preis" );
    final JButton cancelButton = new JButton( "Abbrechen" );
    addDrinkPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );

    GridBagConstraints drinkNameConstraints = new GridBagConstraints();
    drinkNameConstraints.gridx = 0;
    drinkNameConstraints.gridy = 0;
    drinkNameConstraints.anchor = GridBagConstraints.LINE_START;
    addDrinkPanel.add( drinkName, drinkNameConstraints );
    GridBagConstraints drinkNameFieldConstraints = new GridBagConstraints();
    drinkNameFieldConstraints.gridx = 1;
    drinkNameFieldConstraints.gridy = 0;
    drinkNameFieldConstraints.gridwidth = 3;
    drinkNameField.setPreferredSize( new Dimension( 200, 20 ) );
    drinkNameFieldConstraints.insets = new Insets( 5, 10, 0, 0 );
    addDrinkPanel.add( drinkNameField, drinkNameFieldConstraints );

    GridBagConstraints drinkPictureConstraints = new GridBagConstraints();
    drinkPictureConstraints.gridx = 0;
    drinkPictureConstraints.gridy = 1;
    drinkPictureConstraints.anchor = GridBagConstraints.LINE_START;
    drinkPictureConstraints.insets = new Insets( 5, 0, 0, 0 );
    addDrinkPanel.add( drinkPicture, drinkPictureConstraints );
    GridBagConstraints drinkPictureFieldConstraints = new GridBagConstraints();
    drinkPictureFieldConstraints.gridx = 1;
    drinkPictureFieldConstraints.gridy = 1;
    drinkPictureFieldConstraints.gridwidth = 3;
    drinkPictureField.setPreferredSize( new Dimension( 200, 20 ) );
    drinkPictureFieldConstraints.insets = new Insets( 5, 10, 0, 0 );
    addDrinkPanel.add( drinkPictureField, drinkPictureFieldConstraints );

    GridBagConstraints drinkPriceConstraints = new GridBagConstraints();
    drinkPriceConstraints.gridx = 0;
    drinkPriceConstraints.gridy = 2;
    drinkPriceConstraints.anchor = GridBagConstraints.LINE_START;
    drinkPriceConstraints.insets = new Insets( 5, 0, 0, 0 );
    addDrinkPanel.add( drinkPrice, drinkPriceConstraints );
    GridBagConstraints drinkPriceSpinnerConstraints = new GridBagConstraints();
    drinkPriceSpinnerConstraints.gridx = 1;
    drinkPriceSpinnerConstraints.gridy = 2;
    drinkPriceSpinnerConstraints.gridwidth = 3;
    drinkPriceSpinnerConstraints.anchor = GridBagConstraints.LINE_START;
    drinkPriceSpinner.setPreferredSize( new Dimension( 50, 20 ) );
    drinkPriceSpinnerConstraints.insets = new Insets( 5, 10, 0, 0 );
    addDrinkPanel.add( drinkPriceSpinner, drinkPriceSpinnerConstraints );

    GridBagConstraints confirmButtonConstraints = new GridBagConstraints();
    confirmButtonConstraints.gridx = 1;
    confirmButtonConstraints.gridy = 3;
    confirmButtonConstraints.insets = new Insets( 5, 10, 0, 0 );
    addDrinkPanel.add( confirmButton, confirmButtonConstraints );

    GridBagConstraints cancelButtonBagConstraints = new GridBagConstraints();
    cancelButtonBagConstraints.gridx = 2;
    cancelButtonBagConstraints.gridy = 3;
    cancelButtonBagConstraints.insets = new Insets( 5, 5, 0, 0 );
    addDrinkPanel.add( cancelButton, cancelButtonBagConstraints );

    cancelButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent __ )
      {
        dialog.dispose();
      }
    } );

    SwingUtilities.getRootPane( dialog ).setDefaultButton( confirmButton );
    dialog.add( addDrinkPanel );
    dialog.pack();
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
