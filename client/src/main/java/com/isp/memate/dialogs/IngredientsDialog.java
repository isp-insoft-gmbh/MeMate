package com.isp.memate.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.formdev.flatlaf.ui.FlatTextBorder;

import com.isp.memate.Cache;
import com.isp.memate.DrinkIngredients;
import com.isp.memate.ServerCommunication;
import com.isp.memate.Shared.Operation;
import com.isp.memate.components.MeMateDialog;
import com.isp.memate.util.MeMateUIManager;

import pl.coderion.model.Nutriments;
import pl.coderion.model.Product;

public class IngredientsDialog extends MeMateDialog
{
  private final int        drinkID;
  private final JTextArea  ingredientsField;
  private final JTextField barcodeField;
  private final JSpinner   energykJSpinner, energykCALSpinner, fatSpinner, fattyAcidsSpinner,
      carbsSpinner, sugarSpinner, proteinSpinner, saltSpinner, amountSpinner;
  private final JButton    confirmButton, magicButton;

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
    barcodeField = createBarcodeField();
    magicButton = createMagicButton();

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
    GridBagConstraints ingredientsFieldConstraints = new GridBagConstraints();
    ingredientsFieldConstraints.gridx = 2;
    ingredientsFieldConstraints.gridy = 1;
    ingredientsFieldConstraints.gridheight = 11;
    ingredientsFieldConstraints.weightx = 1;
    ingredientsFieldConstraints.weighty = 1;
    ingredientsFieldConstraints.fill = GridBagConstraints.BOTH;
    ingredientsFieldConstraints.insets = new Insets( 5, 10, 0, 0 );
    layoutContainer.add( ingredientsField, ingredientsFieldConstraints );

    layoutContainer.add( new JLabel( "Barcode" ), getLabelConstraints( 0 ) );
    layoutContainer.add( barcodeField, getSpinnerConstraints( 0 ) );
    final GridBagConstraints magicButtonConstraints = new GridBagConstraints();
    magicButtonConstraints.gridx = 2;
    magicButtonConstraints.gridy = 0;
    magicButtonConstraints.weightx = 1;
    magicButtonConstraints.weighty = 1;
    magicButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
    magicButtonConstraints.insets = new Insets( 5, 10, 0, 0 );
    layoutContainer.add( magicButton, magicButtonConstraints );


    layoutContainer.add( new JLabel( "Energie kJ" ), getLabelConstraints( 1 ) );
    layoutContainer.add( energykJSpinner, getSpinnerConstraints( 1 ) );

    layoutContainer.add( new JLabel( "Energie kcal" ), getLabelConstraints( 2 ) );
    layoutContainer.add( energykCALSpinner, getSpinnerConstraints( 2 ) );

    layoutContainer.add( new JLabel( "Fett" ), getLabelConstraints( 3 ) );
    layoutContainer.add( fatSpinner, getSpinnerConstraints( 3 ) );

    layoutContainer.add( new JLabel( "gesättigte Fettsäuren" ), getLabelConstraints( 4 ) );
    layoutContainer.add( fattyAcidsSpinner, getSpinnerConstraints( 4 ) );

    layoutContainer.add( new JLabel( "Kohlenhydrate" ), getLabelConstraints( 5 ) );
    layoutContainer.add( carbsSpinner, getSpinnerConstraints( 5 ) );

    layoutContainer.add( new JLabel( "davon Zucker" ), getLabelConstraints( 6 ) );
    layoutContainer.add( sugarSpinner, getSpinnerConstraints( 6 ) );

    layoutContainer.add( new JLabel( "Eiweiß" ), getLabelConstraints( 7 ) );
    layoutContainer.add( proteinSpinner, getSpinnerConstraints( 7 ) );

    layoutContainer.add( new JLabel( "Salz" ), getLabelConstraints( 8 ) );
    layoutContainer.add( saltSpinner, getSpinnerConstraints( 8 ) );

    layoutContainer.add( new JLabel( "Literangabe" ), getLabelConstraints( 9 ) );
    layoutContainer.add( amountSpinner, getSpinnerConstraints( 9 ) );

    final JPanel buttonBar = new JPanel();
    buttonBar.setLayout( new BoxLayout( buttonBar, BoxLayout.X_AXIS ) );
    buttonBar.add( confirmButton );
    buttonBar.add( Box.createHorizontalStrut( 5 ) );
    buttonBar.add( abortButton );
    ingredientsFieldConstraints = new GridBagConstraints();
    ingredientsFieldConstraints.gridx = 1;
    ingredientsFieldConstraints.gridy = 12;
    ingredientsFieldConstraints.weightx = 1;
    ingredientsFieldConstraints.weighty = 0;
    ingredientsFieldConstraints.gridwidth = 4;
    ingredientsFieldConstraints.insets = new Insets( 20, 0, 0, 0 );
    ingredientsFieldConstraints.anchor = GridBagConstraints.LINE_END;
    layoutContainer.add( buttonBar, ingredientsFieldConstraints );

    add( layoutContainer );
  }

  private JTextField createBarcodeField()
  {
    final JTextField textField = new JTextField();
    textField.setText( Cache.getInstance().getDrinks().get( drinkID ).getBarcode() );
    textField.getDocument().addDocumentListener( new DocumentListener()
    {
      @Override
      public void removeUpdate( DocumentEvent __ )
      {
        setMagicButtonState();
      }

      @Override
      public void insertUpdate( DocumentEvent __ )
      {
        setMagicButtonState();
      }

      @Override
      public void changedUpdate( DocumentEvent __ )
      {
        setMagicButtonState();
      }

      private void setMagicButtonState()
      {
        if ( !textField.getText().isEmpty() && textField.getText().trim().length() != 0 )
        {
          magicButton.setEnabled( true );
        }
        else
        {
          magicButton.setEnabled( false );
        }
      }
    } );
    return textField;
  }

  private JButton createMagicButton()
  {
    final JButton button = new JButton( UIManager.getIcon( MeMateUIManager.getDarkModeState() ? "wand.icon.white" : "wand.icon.black" ) );
    button.setToolTipText( "Füllt automatisch alle Inhaltsstoffe aus." );
    button.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent __ )
      {
        final Product product = CreateDrinkDialog.getProductFor( barcodeField.getText() );
        if ( product == null )
        {
          JOptionPane.showConfirmDialog( IngredientsDialog.this, "Für den Barcode wurde leider kein Produkt gefunden", getTitle(),
              JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE );
          return;
        }
        final Nutriments nutriments = product.getNutriments();

        ingredientsField.setText( product.getIngredientsText() );
        energykJSpinner.setValue( nutriments.getEnergyKj() );
        energykCALSpinner.setValue( nutriments.getEnergyKcal() );
        fatSpinner.setValue( nutriments.getFat() );
        fattyAcidsSpinner.setValue( nutriments.getSaturatedFat() );
        carbsSpinner.setValue( nutriments.getCarbohydrates() );
        sugarSpinner.setValue( nutriments.getSugars() );
        proteinSpinner.setValue( nutriments.getProteins() );
        saltSpinner.setValue( nutriments.getSalt() );
      }
    } );
    return button;
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
    constraints.anchor = GridBagConstraints.LINE_END;
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
    constraints.anchor = GridBagConstraints.LINE_START;
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
          if ( !barcodeField.getText().isBlank()
              && !barcodeField.getText().equals( Cache.getInstance().getDrinks().get( drinkID ).getBarcode() ) )
          {
            ServerCommunication.getInstance().updateDrinkInformations( drinkID, Operation.UPDATE_BARCODE, barcodeField.getText() );
          }
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
