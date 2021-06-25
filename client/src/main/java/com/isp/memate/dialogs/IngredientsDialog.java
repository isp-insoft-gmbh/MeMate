package com.isp.memate.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import com.formdev.flatlaf.ui.FlatTextBorder;

import com.isp.memate.Cache;
import com.isp.memate.DrinkIngredients;
import com.isp.memate.ServerCommunication;
import com.isp.memate.components.MeMateDialog;

public class IngredientsDialog extends MeMateDialog
{
  private final int       drinkID;
  private final JTextArea ingredientsField;
  private final JSpinner  energykJSpinner;
  private final JSpinner  energykCALSpinner;
  private final JSpinner  fatSpinner;
  private final JSpinner  fattyAcidsSpinner;
  private final JSpinner  carbsSpinner;
  private final JSpinner  sugarSpinner;
  private final JSpinner  proteinSpinner;
  private final JSpinner  saltSpinner;
  private final JSpinner  amountSpinner;
  private final JButton   confirmButton;

  public IngredientsDialog( final int drinkID, final Window owner )
  {
    super( owner );
    this.drinkID = drinkID;
    setTitle( "Inhaltsstoffe hinzufügen" );

    ingredientsField = new JTextArea();
    ingredientsField.setLineWrap( true );
    ingredientsField.setWrapStyleWord( true );
    ingredientsField.setBorder( new FlatTextBorder() );

    energykJSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, 500, 1 ) );
    energykCALSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, 500, 1 ) );
    fatSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, 50, 0.1 ) );
    fattyAcidsSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, 50, 0.1 ) );
    carbsSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, 50, 0.1 ) );
    sugarSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, 50, 0.1 ) );
    proteinSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, 50, 0.1 ) );
    saltSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, 50, 0.1 ) );
    amountSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, 5, 0.01 ) );
    confirmButton = new JButton( getConfirmAction() );
    confirmButton.setText( "Speichern" );

    checkIfDrinkAlreadyHasIngredients();

    layoutComponents();
    setResizable( false );
  }

  /**
   * Checks if ingredients are already existing for the given drink.
   * If this is the case, the values are written to the corresponding components.
   */
  private void checkIfDrinkAlreadyHasIngredients()
  {
    final DrinkIngredients ingredients = Cache.getInstance().getDrinks().get( drinkID ).getDrinkIngredients();

    if ( ingredients != null )
    {
      ingredientsField.setText( ingredients.getIngredients() );
      energykJSpinner.setValue( ingredients.getEnergy_kJ() );
      energykCALSpinner.setValue( ingredients.getEnergy_kcal() );
      fatSpinner.setValue( ingredients.getFat() );
      fattyAcidsSpinner.setValue( ingredients.getFatty_acids() );
      carbsSpinner.setValue( ingredients.getCarbs() );
      sugarSpinner.setValue( ingredients.getSugar() );
      proteinSpinner.setValue( ingredients.getProtein() );
      saltSpinner.setValue( ingredients.getSalt() );
      amountSpinner.setValue( ingredients.getAmount() );
    }
  }

  @Override
  public void layoutComponents()
  {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = 2;
    constraints.gridy = 0;
    constraints.gridheight = 13;
    constraints.weightx = 1;
    constraints.weighty = 1;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.insets = new Insets( 5, 10, 0, 0 );
    layoutContainer.add( ingredientsField, constraints );

    layoutContainer.add( new JLabel( "Energie kJ" ), getLabelConstraints( 3 ) );
    layoutContainer.add( energykJSpinner, getSpinnerConstraints( 3 ) );

    layoutContainer.add( new JLabel( "Energie kcal" ), getLabelConstraints( 4 ) );
    layoutContainer.add( energykCALSpinner, getSpinnerConstraints( 4 ) );

    layoutContainer.add( new JLabel( "Fett" ), getLabelConstraints( 5 ) );
    layoutContainer.add( fatSpinner, getSpinnerConstraints( 5 ) );

    layoutContainer.add( new JLabel( "gesättigte Fettsäuren" ), getLabelConstraints( 6 ) );
    layoutContainer.add( fattyAcidsSpinner, getSpinnerConstraints( 6 ) );

    layoutContainer.add( new JLabel( "Kohlenhydrate" ), getLabelConstraints( 7 ) );
    layoutContainer.add( carbsSpinner, getSpinnerConstraints( 7 ) );

    layoutContainer.add( new JLabel( "davon Zucker" ), getLabelConstraints( 8 ) );
    layoutContainer.add( sugarSpinner, getSpinnerConstraints( 8 ) );

    layoutContainer.add( new JLabel( "Eiweiß" ), getLabelConstraints( 9 ) );
    layoutContainer.add( proteinSpinner, getSpinnerConstraints( 9 ) );

    layoutContainer.add( new JLabel( "Salz" ), getLabelConstraints( 10 ) );
    layoutContainer.add( saltSpinner, getSpinnerConstraints( 10 ) );

    layoutContainer.add( new JLabel( "Literangabe" ), getLabelConstraints( 11 ) );
    layoutContainer.add( amountSpinner, getSpinnerConstraints( 11 ) );

    final JPanel buttonBar = new JPanel();
    buttonBar.setLayout( new BoxLayout( buttonBar, BoxLayout.X_AXIS ) );
    buttonBar.add( confirmButton );
    buttonBar.add( Box.createHorizontalStrut( 5 ) );
    buttonBar.add( abortButton );
    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 14;
    constraints.weightx = 1;
    constraints.weighty = 0;
    constraints.gridwidth = 4;
    constraints.insets = new Insets( 20, 0, 0, 0 );
    constraints.anchor = GridBagConstraints.LINE_END;
    layoutContainer.add( buttonBar, constraints );

    add( layoutContainer );
  }

  private GridBagConstraints getSpinnerConstraints( int gridY )
  {
    final GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = gridY;
    constraints.insets = new Insets( 5, 0, 0, 0 );
    constraints.weightx = 0.1;
    constraints.weighty = 1;
    constraints.gridheight = 1;
    constraints.gridwidth = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.anchor = GridBagConstraints.EAST;
    return constraints;
  }

  private GridBagConstraints getLabelConstraints( int gridY )
  {
    final GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = 0;
    constraints.gridy = gridY;
    constraints.gridheight = 1;
    constraints.gridwidth = 1;
    constraints.weightx = 1;
    constraints.anchor = GridBagConstraints.WEST;
    constraints.fill = GridBagConstraints.NONE;
    constraints.insets = new Insets( 5, 0, 0, 10 );
    return constraints;
  }

  private Action getConfirmAction()
  {
    final Action action = new AbstractAction()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        if ( energykJSpinner.getValue() instanceof Number || energykCALSpinner.getValue() instanceof Number
            || fatSpinner.getValue() instanceof Number || fattyAcidsSpinner.getValue() instanceof Number
            || carbsSpinner.getValue() instanceof Number || sugarSpinner.getValue() instanceof Number
            || proteinSpinner.getValue() instanceof Number || saltSpinner.getValue() instanceof Number
            || amountSpinner.getValue() instanceof Number )
        {
          ServerCommunication.getInstance().registerIngredients( new DrinkIngredients( drinkID,
              ingredientsField.getText(), (int) energykJSpinner.getValue(), (int) energykCALSpinner.getValue(),
              (float) fatSpinner.getValue(),
              (float) fattyAcidsSpinner.getValue(),
              (float) carbsSpinner.getValue(), (float) sugarSpinner.getValue(),
              (float) proteinSpinner.getValue(), (float) saltSpinner.getValue(), (float) amountSpinner.getValue() ) );
          dispose();
        }
        else
        {
          JOptionPane.showConfirmDialog( IngredientsDialog.this, "Ungültige Eingabe" );
        }
      }
    };
    return action;
  }

  @Override
  public void showDialog()
  {
    pack();
    final Dimension dialogSize = new Dimension( 470, 340 );
    setPreferredSize( dialogSize );
    setMinimumSize( dialogSize );
    setLocationRelativeTo( getOwner() );
    setVisible( true );
  }
}
