/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.MeMateUIManager;

/**
 * In der Adminview kann man das Guthaben des Spaarschweins setzen/sehen,
 * Passwörter der User ändern, Daten exportieren und die Anzahl der Getränke festlegen.
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
  private final JDialog          passwordFrame         = new JDialog( Mainframe.getInstance() );
  private final JLabel           piggyBankLabel        = MeMateUIManager.createJLabel();
  private final JPanel           upperPanel            = MeMateUIManager.createJPanel();
  private final JPanel           upperUpperPanel       = MeMateUIManager.createJPanel();
  private final JPanel           centerPanel           = MeMateUIManager.createJPanel();
  private JPanel                 drinkAmountPanel      = new JPanel( new FlowLayout() );
  private final JTextField       balanceField          = new JTextField();


  /**
   * @return static instance of Adminview
   */
  public static Adminview getInstance()
  {
    return instance;
  }

  /**
   * Der Konstruktor lädt die Einstellungen für das Adminpanel.
   */
  public Adminview()
  {
    loadDefaultSettings();
  }

  /**
   * Als erstes wird alles bisherige entfernt und nun für die 3 Buttons oben im Adminpanel generiert.
   * Die Buttons werden gelayoutet und anschließend die Buttons mit der Getränkeanzahl hinzugefügt.
   * Dann werden noch ActionListener auf die Buttons angemeldet und die Komponenten hinzugefügt.
   */
  private void loadDefaultSettings()
  {
    JPanel piggyBankPanel = MeMateUIManager.createJPanel( "adminButton" );
    JPanel pwChangePanel = MeMateUIManager.createJPanel( "adminButton" );
    JPanel exportPanel = MeMateUIManager.createJPanel( "adminButton" );

    removeAllAndLayout();
    setToolTipAndText();

    loadPiggyBankPanelSettings( piggyBankPanel );
    loadPWChangePanelSettings( pwChangePanel );
    loadExportPanelSettings( exportPanel );

    layoutHeaderComponents( pwChangePanel, piggyBankPanel, exportPanel );
    addAllDrinks();

    loadExportButtonAction();
    loadResetPasswordButtonAction();
    loadAdminBalanceButtonAction();

    ServerCommunication.getInstance().tellServerToSendPiggybankBalance();
    MeMateUIManager.registerPanel( "default", this );
    add( upperUpperPanel, BorderLayout.NORTH );
    add( centerPanel, BorderLayout.CENTER );
  }

  /**
   * Setzt die Action für das Guthabensetzen des Sparschweins.
   */
  private void loadAdminBalanceButtonAction()
  {
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
  }

  /**
   * Sobald der Admin auf den Passwort reset Button drückt, öffnet sich ein neues
   * Fenster, in welchem man aus allen bisherigen Usern einen auswählen kann und
   * das Passwort festlegen kann.
   */
  private void loadResetPasswordButtonAction()
  {
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
        passwordFrame
            .setIconImage( Toolkit.getDefaultToolkit().getImage( getClass().getClassLoader().getResource( "frameiconblue2.png" ) ) );
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
  }

  /**
   * Setzt die Action für das Exportieren von Daten.
   */
  private void loadExportButtonAction()
  {
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
  }

  private void layoutHeaderComponents( JPanel pwChangePanel, JPanel piggyBankPanel, JPanel exportPanel )
  {
    upperPanel.add( pwChangePanel );
    upperPanel.add( piggyBankPanel );
    upperPanel.add( exportPanel );
    upperUpperPanel.add( upperPanel, BorderLayout.CENTER );
    JSeparator separator = new JSeparator( SwingConstants.HORIZONTAL );
    upperUpperPanel.add( separator, BorderLayout.SOUTH );
    upperPanel.setBorder( new EmptyBorder( 0, 0, 20, 0 ) );
    upperUpperPanel.setBorder( new EmptyBorder( 40, 20, 20, 20 ) );
  }


  private void loadExportPanelSettings( JPanel exportPanel )
  {
    exportPanel.setLayout( new GridBagLayout() );
    GridBagConstraints exportPanelConstraints = new GridBagConstraints();
    exportPanelConstraints.gridx = 0;
    exportPanelConstraints.gridy = 0;
    exportPanelConstraints.weightx = 1;
    exportPanelConstraints.weighty = 1;
    exportPanelConstraints.fill = GridBagConstraints.BOTH;
    exportPanelConstraints.insets = new Insets( 5, 5, 5, 5 );
    exportPanel.setPreferredSize( new Dimension( 150, 90 ) );
    exportPanel.add( exportButton, exportPanelConstraints );
  }


  private void loadPWChangePanelSettings( JPanel pwChangePanel )
  {
    pwChangePanel.setPreferredSize( new Dimension( 100, 90 ) );
    pwChangePanel.setLayout( new GridBagLayout() );
    GridBagConstraints pwChangePanelConstraints = new GridBagConstraints();
    pwChangePanelConstraints.gridx = 0;
    pwChangePanelConstraints.gridy = 0;
    pwChangePanelConstraints.weightx = 1;
    pwChangePanelConstraints.weighty = 1;
    pwChangePanelConstraints.fill = GridBagConstraints.BOTH;
    pwChangePanelConstraints.insets = new Insets( 5, 5, 5, 5 );
    pwChangePanel.add( resetPasswordButton, pwChangePanelConstraints );
    pwChangePanel.setPreferredSize( new Dimension( 150, 90 ) );
  }

  private void loadPiggyBankPanelSettings( JPanel piggyBankPanel )
  {
    piggyBankPanel.setPreferredSize( new Dimension( 430, 90 ) );
    piggyBankPanel.setLayout( new GridBagLayout() );
    piggyBankLabel.setFont( piggyBankLabel.getFont().deriveFont( 25f ) );
    piggyBankLabel.setHorizontalAlignment( JLabel.CENTER );

    GridBagConstraints piggybankLabelConstraints = new GridBagConstraints();
    piggybankLabelConstraints.gridx = 0;
    piggybankLabelConstraints.gridy = 0;
    piggybankLabelConstraints.gridwidth = 2;
    piggybankLabelConstraints.insets = new Insets( 5, 5, 15, 5 );
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
  }


  private void setToolTipAndText()
  {
    setAdminBalanceButton.setText( "Guthaben setzen" );
    resetPasswordButton.setToolTipText( "Passwörter zurücksetzen." );
    exportButton.setToolTipText( "Daten exportieren" );
  }


  private void removeAllAndLayout()
  {
    upperPanel.removeAll();
    centerPanel.removeAll();
    upperUpperPanel.removeAll();
    setLayout( new BorderLayout() );
    upperPanel.setLayout( new WrapLayout() );
    centerPanel.setLayout( new WrapLayout() );
    upperUpperPanel.setLayout( new BorderLayout() );
    balanceField.setPreferredSize( new Dimension( 100, 20 ) );
  }

  /**
   * Erzeugt für jeden vorhandenes Getränk ein eigens Panel mit Name, wann es in etwa leer sein wird
   * und einem Spinner und Button zum Festlegen der Anzahl.
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
      daysLeftLabelConstraints.insets = new Insets( 0, 5, 10, 0 );
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
      drinkAmountPanel.setPreferredSize( new Dimension( 260, 90 ) );
      centerPanel.add( drinkAmountPanel );
    }
  }

  /**
   * Berechnet zuerst den Durchschnittswert der letzten Monats aus.
   * Nun wird die Anzahl an noch vorhandenen Getränken durch den Wert geteilt.
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
              ClientLog.newLog( "Das Datum für die Berechnung der noch übrigen Tage konnte nicht geparst werden" );
              ClientLog.newLog( exception.getMessage() );
            }
          }
        }
      }
    }
    Float averageConsumption = amount / 30f;
    return ServerCommunication.getInstance().getAmount( drink ) / averageConsumption;
  }


  /**
   * Generiert das AdminPanel neu
   */
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

  /**
   * updated die Icons, je nach State des Darkmodes.
   */
  public void updateButtonIcons()
  {
    if ( MeMateUIManager.getDarkModeState() )
    {
      resetPasswordButton.setIcon( new ImageIcon( getClass().getClassLoader().getResource( "password_white.png" ) ) );
      exportButton.setIcon( new ImageIcon( getClass().getClassLoader().getResource( "export_white.png" ) ) );
    }
    else
    {
      resetPasswordButton.setIcon( new ImageIcon( getClass().getClassLoader().getResource( "password_black.png" ) ) );
      exportButton.setIcon( new ImageIcon( getClass().getClassLoader().getResource( "export_black.png" ) ) );
    }
  }
}
