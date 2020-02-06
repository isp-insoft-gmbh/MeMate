/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.util.MeMateUIManager;

/**
 * In der Adminview kann man das Guthaben des Spaarschweins setzen/sehen.
 * Außerdem kann man die gesamte Historie sehen und die Anzahl der Getränke festlegen.
 * 
 * @author nwe
 * @since 09.12.2019
 *
 */
public class Adminview extends JPanel
{
  private static final Adminview instance              = new Adminview();
  private final JButton          setAdminBalanceButton = MeMateUIManager.createNormalButton( "button" );
  private final JButton          exportButton          = MeMateUIManager.createNormalButton( "button" );
  private final JButton          resetPasswordButton   = MeMateUIManager.createNormalButton( "button" );
  private final JTextField       balanceField          = new JTextField();
  private final JLabel           piggyBankLabel        = MeMateUIManager.createJLabel();
  private final JDialog          passwordFrame         = new JDialog( Mainframe.getInstance() );
  private JPanel                 drinkAmountPanel      = new JPanel( new FlowLayout() );

  /**
   * @return static instance of Adminview
   */
  public static Adminview getInstance()
  {
    return instance;
  }

  /**
   * 
   */
  public Adminview()
  {
    loadDefaultSettings();
  }

  /**
   * 
   */
  private void loadDefaultSettings()
  {
    setAdminBalanceButton.setText( "Guthaben setzen" );
    exportButton.setText( "Daten exportieren" );
    resetPasswordButton.setText( "Passwort ändern" );
    setLayout( new FlowLayout() );
    balanceField.setPreferredSize( new Dimension( 100, 20 ) );
    piggyBankLabel.setFont( piggyBankLabel.getFont().deriveFont( 25f ) );
    piggyBankLabel.setHorizontalAlignment( JLabel.CENTER );
    JPanel piggyBankPanel = MeMateUIManager.createJPanel( "adminButton" );
    piggyBankPanel.setLayout( new GridBagLayout() );

    GridBagConstraints piggybankLabelConstraints = new GridBagConstraints();
    piggybankLabelConstraints.gridx = 0;
    piggybankLabelConstraints.gridy = 0;
    piggybankLabelConstraints.gridwidth = 2;
    piggybankLabelConstraints.insets = new Insets( 5, 5, 5, 5 );
    piggyBankPanel.add( piggyBankLabel, piggybankLabelConstraints );
    GridBagConstraints balanceFieldConstraints = new GridBagConstraints();
    balanceFieldConstraints.gridx = 0;
    balanceFieldConstraints.gridy = 1;
    balanceFieldConstraints.insets = new Insets( 0, 5, 5, 0 );
    balanceFieldConstraints.anchor = GridBagConstraints.LINE_END;
    piggyBankPanel.add( balanceField, balanceFieldConstraints );
    GridBagConstraints setAdminBalanceButtonConstraints = new GridBagConstraints();
    setAdminBalanceButtonConstraints.gridx = 1;
    setAdminBalanceButtonConstraints.gridy = 1;
    setAdminBalanceButtonConstraints.insets = new Insets( 0, 0, 5, 5 );
    setAdminBalanceButtonConstraints.anchor = GridBagConstraints.LINE_END;
    piggyBankPanel.add( setAdminBalanceButton, setAdminBalanceButtonConstraints );
    add( piggyBankPanel );


    JPanel adminPanel = MeMateUIManager.createJPanel( "adminButton" );
    adminPanel.setLayout( new GridBagLayout() );
    GridBagConstraints resetPasswordButtonConstraints = new GridBagConstraints();
    resetPasswordButtonConstraints.gridx = 0;
    resetPasswordButtonConstraints.gridy = 0;
    resetPasswordButtonConstraints.weighty = 1;
    resetPasswordButtonConstraints.fill = GridBagConstraints.BOTH;
    resetPasswordButtonConstraints.insets = new Insets( 5, 5, 5, 5 );
    adminPanel.add( resetPasswordButton, resetPasswordButtonConstraints );
    GridBagConstraints exportButtonConstraints = new GridBagConstraints();
    exportButtonConstraints.gridx = 0;
    exportButtonConstraints.gridy = 1;
    exportButtonConstraints.weighty = 1;
    exportButtonConstraints.fill = GridBagConstraints.BOTH;
    exportButtonConstraints.insets = new Insets( 5, 5, 5, 5 );
    adminPanel.add( exportButton, exportButtonConstraints );
    adminPanel.setPreferredSize( new Dimension( 110, 110 ) );
    add( adminPanel );
    addAllDrinks();

    ActionListener[] exportButtonListener = exportButton.getActionListeners();
    for ( ActionListener actionListener : exportButtonListener )
    {
      exportButton.removeActionListener( actionListener );
    }
    exportButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        new DataExport();
      }
    } );


    ActionListener[] resetPasswordButtonListeners = resetPasswordButton.getActionListeners();
    for ( ActionListener actionListener : resetPasswordButtonListeners )
    {
      resetPasswordButton.removeActionListener( actionListener );
    }
    resetPasswordButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        passwordFrame.setTitle( "Passwort zurücksetzen" );
        String[] user = ServerCommunication.getInstance().getAllUsers();
        JPanel passwordPanel = new JPanel( new GridBagLayout() );
        JButton saveButton = new JButton( "Speichern" );
        JButton abortButton = new JButton( "Abbrechen" );
        JLabel userLabel = new JLabel( "Nutzer auswählen" );
        JLabel newPassword = new JLabel( "Neues Passwort:  " );
        JTextField passwordField = new JTextField();
        JComboBox<String> userComboBox = new JComboBox<>( user );
        passwordPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );

        GridBagConstraints userLabelConstraints = new GridBagConstraints();
        userLabelConstraints.gridx = 0;
        userLabelConstraints.gridy = 0;
        userLabelConstraints.insets = new Insets( 0, 0, 0, 5 );
        passwordPanel.add( userLabel, userLabelConstraints );
        GridBagConstraints userComboBoxConstraints = new GridBagConstraints();
        userComboBoxConstraints.gridx = 1;
        userComboBoxConstraints.gridy = 0;
        userComboBoxConstraints.fill = GridBagConstraints.HORIZONTAL;
        passwordPanel.add( userComboBox, userComboBoxConstraints );
        GridBagConstraints newPasswordConstraints = new GridBagConstraints();
        newPasswordConstraints.gridx = 0;
        newPasswordConstraints.gridy = 1;
        newPasswordConstraints.insets = new Insets( 10, 0, 30, 5 );
        passwordPanel.add( newPassword, newPasswordConstraints );
        GridBagConstraints passwordFieldConstraints = new GridBagConstraints();
        passwordFieldConstraints.gridx = 1;
        passwordFieldConstraints.gridy = 1;
        passwordFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        passwordFieldConstraints.insets = new Insets( 10, 0, 30, 0 );
        passwordPanel.add( passwordField, passwordFieldConstraints );
        GridBagConstraints saveButtonConstraints = new GridBagConstraints();
        saveButtonConstraints.gridx = 0;
        saveButtonConstraints.gridy = 2;
        passwordPanel.add( saveButton, saveButtonConstraints );
        GridBagConstraints abortButtonConstraints = new GridBagConstraints();
        abortButtonConstraints.gridx = 1;
        abortButtonConstraints.gridy = 2;
        passwordPanel.add( abortButton, abortButtonConstraints );


        abortButton.addActionListener( new ActionListener()
        {
          @Override
          public void actionPerformed( ActionEvent e )
          {
            passwordFrame.dispose();
          }
        } );
        saveButton.addActionListener( new ActionListener()
        {
          @Override
          public void actionPerformed( ActionEvent e )
          {
            ServerCommunication.getInstance().changePassword( String.valueOf( userComboBox.getSelectedItem() ),
                Login.getInstance().getHash( passwordField.getText() ) );
            passwordFrame.dispose();
          }
        } );
        passwordFrame.add( passwordPanel );
        passwordFrame.setSize( 300, 160 );
        passwordFrame.setLocationRelativeTo( Mainframe.getInstance() );
        passwordFrame.setVisible( true );
        passwordFrame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
      }
    } );

    ActionListener[] setAdminBalanceButtonListeners = setAdminBalanceButton.getActionListeners();
    for ( ActionListener actionListener : setAdminBalanceButtonListeners )
    {
      setAdminBalanceButton.removeActionListener( actionListener );
    }
    setAdminBalanceButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        String balanceAsString = balanceField.getText();
        Float balance = null;
        if ( isNumber( balanceAsString ) )
        {
          balance = Float.valueOf( balanceAsString );
          ServerCommunication.getInstance().setAdminBalance( Float.valueOf( balance ) );
          piggyBankLabel.setText( String.format( "Im Sparschwein befinden sich %.2f€", balance ) );
        }
        else
        {
          JOptionPane.showMessageDialog( Mainframe.getInstance(), "Bitte geben Sie einen gültigen Wert an." );
        }
      }

      private boolean isNumber( String balanceAsString )
      {
        try
        {
          Float.parseFloat( balanceAsString );
        }
        catch ( Exception exception )
        {
          return false;
        }
        return true;
      }
    } );
    ServerCommunication.getInstance().tellServerToSendPiggybankBalance();
    MeMateUIManager.registerPanel( "default", this );
  }

  /**
   * Fügt für jedes Getränk einen Spinner und ein Speicher-Button an
   */
  private void addAllDrinks()
  {
    for ( String drink : ServerCommunication.getInstance().getDrinkNames() )
    {
      drinkAmountPanel = MeMateUIManager.createJPanel( "adminButton" );
      drinkAmountPanel.setLayout( new GridBagLayout() );
      JLabel drinkNameLabel = MeMateUIManager.createJLabel();
      drinkNameLabel.setText( drink );
      drinkNameLabel.setPreferredSize( new Dimension( 150, 30 ) );
      drinkNameLabel.setFont( drinkNameLabel.getFont().deriveFont( 20f ) );
      GridBagConstraints drinkNameLabelConstraints = new GridBagConstraints();
      drinkNameLabelConstraints.gridy = 0;
      drinkNameLabelConstraints.gridx = 0;
      drinkNameLabelConstraints.anchor = GridBagConstraints.LINE_START;
      drinkNameLabelConstraints.insets = new Insets( 0, 5, 5, 0 );
      drinkAmountPanel.add( drinkNameLabel, drinkNameLabelConstraints );
      SpinnerModel amountSpinnerModel = new SpinnerNumberModel( 0, 0, 50, 1 );
      JSpinner amountSpinner = new JSpinner( amountSpinnerModel );
      amountSpinner.setValue( ServerCommunication.getInstance().getAmount( drink ) );
      GridBagConstraints amountSpinnerConstraints = new GridBagConstraints();
      amountSpinnerConstraints.gridx = 0;
      amountSpinnerConstraints.gridy = 2;
      amountSpinnerConstraints.anchor = GridBagConstraints.LINE_START;
      amountSpinnerConstraints.insets = new Insets( 0, 5, 0, 5 );
      drinkAmountPanel.add( amountSpinner, amountSpinnerConstraints );
      JButton setAmountButton = MeMateUIManager.createNormalButton( "button" );
      setAmountButton.setText( "Anzahl setzen" );
      GridBagConstraints setAmountButtonConstraints = new GridBagConstraints();
      setAmountButtonConstraints.gridx = 1;
      setAmountButtonConstraints.gridy = 2;
      setAmountButtonConstraints.insets = new Insets( 0, 0, 5, 5 );
      drinkAmountPanel.add( setAmountButton, setAmountButtonConstraints );
      JLabel daysLeftLabel = MeMateUIManager.createJLabel();
      daysLeftLabel.setText( String.format( "in etwa %.2f Tagen leer.", getDaysLeft( drink ) ) );
      daysLeftLabel.setFont( daysLeftLabel.getFont().deriveFont( 15f ) );
      GridBagConstraints daysLeftLabelConstraints = new GridBagConstraints();
      daysLeftLabelConstraints.gridx = 0;
      daysLeftLabelConstraints.gridy = 1;
      daysLeftLabelConstraints.insets = new Insets( 0, 5, 5, 0 );
      drinkAmountPanel.add( daysLeftLabel, daysLeftLabelConstraints );

      setAmountButton.addActionListener( new ActionListener()
      {
        @Override
        public void actionPerformed( ActionEvent e )
        {
          String amount = String.valueOf( amountSpinner.getValue() );
          if ( isInteger( amount ) )
          {
            ServerCommunication.getInstance().setDrinkAmount( drink, Integer.valueOf( amount ) );
          }
        }

        private boolean isInteger( String testValue )
        {
          try
          {
            Integer.parseInt( testValue );
          }
          catch ( Exception exception )
          {
            return false;
          }
          return true;
        }
      } );
      add( drinkAmountPanel );
    }
  }

  /**
   * @return
   */
  private Float getDaysLeft( String drink )
  {
    Float amount = 0f;
    String[][] historyData = ServerCommunication.getInstance().getHistoryData( dateType.LONG );
    if ( historyData != null )
    {
      for ( String[] data : historyData )
      {
        String action = data[ 0 ];
        if ( action.contains( "getrunken" ) )
        {
          if ( action.contains( drink ) )
          {
            String dateAsString = data[ 4 ].substring( 0, 10 );
            Date date;
            try
            {
              date = new SimpleDateFormat( "yyyy-MM-dd" ).parse( dateAsString );
              ZonedDateTime now = ZonedDateTime.now();
              ZonedDateTime thirtyDaysAgo = now.minusDays( 30 );
              if ( !date.toInstant().isBefore( thirtyDaysAgo.toInstant() ) )
              {
                amount++;
              }
            }
            catch ( ParseException exception )
            {
              // TODO(nwe|20.01.2020): Fehlerbehandlung muss noch implementiert werden!
            }
          }
        }
      }
    }
    Float averageConsumption = amount / 30f;
    return ServerCommunication.getInstance().getAmount( drink ) / averageConsumption;
  }

  @SuppressWarnings( "javadoc" )
  public void updateDrinkAmounts()
  {
    removeAll();
    loadDefaultSettings();
    repaint();
    revalidate();
  }

  /**
   * Updated das Label für das Spaarschweinguthaben.
   * 
   * @param balance Guthaben
   */
  public void updatePiggybankBalanceLabel( Float balance )
  {
    piggyBankLabel.setText( String.format( "Im Sparschwein befinden sich %.2f€", balance ) );
  }
}
