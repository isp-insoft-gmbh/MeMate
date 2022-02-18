/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.isp.memate.Cache;
import com.isp.memate.DataExport;
import com.isp.memate.ServerCommunication;
import com.isp.memate.components.MeMateDialog;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.MeMateUIManager;
import com.isp.memate.util.Util;
import com.isp.memate.util.WrapLayout;

/**
 * In der Adminview kann man das Guthaben des Spaarschweins setzen/sehen,
 * Passwörter der User ändern, Daten exportieren und die Anzahl der Getränke festlegen.
 *
 * @author nwe
 * @since 09.12.2019
 */
public class Adminview extends JPanel
{
  private final JButton    exportButton          =
      MeMateUIManager.createIconButton( new ImageIcon( getClass().getClassLoader().getResource( "export_white.png" ) ),
          new ImageIcon( getClass().getClassLoader().getResource( "export_black.png" ) ) );
  private final JButton    resetPasswordButton   =
      MeMateUIManager.createIconButton( new ImageIcon( getClass().getClassLoader().getResource( "password_white.png" ) ),
          new ImageIcon( getClass().getClassLoader().getResource( "password_black.png" ) ) );
  private final JButton    setAdminBalanceButton = new JButton();
  private MeMateDialog     passwordFrame;
  private final JLabel     piggyBankLabel        = new JLabel();
  private final JPanel     upperPanel            = new JPanel();
  private final JPanel     upperUpperPanel       = new JPanel();
  private final JPanel     centerPanel           = new JPanel();
  private final JTextField balanceField          = new JTextField();
  Cache                    cache                 = Cache.getInstance();

  /**
   * Der Konstruktor lädt die Einstellungen für das Adminpanel.
   */
  public Adminview()
  {
    GUIObjects.currentPanel = this;
    loadDefaultSettings();
  }

  /**
   * Als erstes wird alles bisherige entfernt und nun für die 3 Buttons oben im Adminpanel generiert.
   * Die Buttons werden gelayoutet und anschließend die Buttons mit der Getränkeanzahl hinzugefügt.
   * Dann werden noch ActionListener auf die Buttons angemeldet und die Komponenten hinzugefügt.
   */
  private void loadDefaultSettings()
  {
    final JPanel piggyBankPanel = MeMateUIManager.createJPanelWithThinBorder();
    final JPanel pwChangePanel = new JPanel();
    final JPanel exportPanel = new JPanel();

    removeAllAndLayout();
    setToolTipAndText();

    loadPiggyBankPanelSettings( piggyBankPanel );
    loadPWChangePanelSettings( pwChangePanel );
    loadExportPanelSettings( exportPanel );

    layoutHeaderComponents( pwChangePanel, piggyBankPanel, exportPanel );

    loadExportButtonAction();
    loadResetPasswordButtonAction();
    loadAdminBalanceButtonAction();

    ServerCommunication.getInstance().tellServerToSendPiggybankBalance();
    add( upperUpperPanel, BorderLayout.NORTH );
    final JScrollPane scrollpane = new JScrollPane( centerPanel );
    scrollpane.getVerticalScrollBar().setUnitIncrement( 12 );
    scrollpane.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    scrollpane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
    add( scrollpane, BorderLayout.CENTER );
  }

  /**
   * Setzt die Action für das Guthabensetzen des Sparschweins.
   */
  private void loadAdminBalanceButtonAction()
  {
    final ActionListener[] setAdminBalanceButtonListeners = setAdminBalanceButton.getActionListeners();
    for ( final ActionListener actionListener : setAdminBalanceButtonListeners )
    {
      setAdminBalanceButton.removeActionListener( actionListener );
    }
    setAdminBalanceButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( final ActionEvent e )
      {
        final String balanceAsString = balanceField.getText();
        Float balance = null;
        if ( isNumber( balanceAsString ) )
        {
          balance = Float.valueOf( balanceAsString );
          ServerCommunication.getInstance().setAdminBalance( Float.valueOf( balance ) );
          piggyBankLabel.setText( String.format( "Im Sparschwein befinden sich %.2f€", balance ) );
        }
        else
        {
          JOptionPane.showMessageDialog( GUIObjects.mainframe, "Bitte geben Sie einen gültigen Wert an." );
        }
      }

      private boolean isNumber( final String balanceAsString )
      {
        try
        {
          Float.parseFloat( balanceAsString );
        }
        catch ( final Exception exception )
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
    final ActionListener[] resetPasswordButtonListeners = resetPasswordButton.getActionListeners();
    for ( final ActionListener actionListener : resetPasswordButtonListeners )
    {
      resetPasswordButton.removeActionListener( actionListener );
    }
    resetPasswordButton.addActionListener( e ->
    {
      final String[] user = Cache.getInstance().getUserArray();
      final JPanel passwordPanel = new JPanel( new GridBagLayout() );
      final JButton saveButton = new JButton( "Speichern" );
      final JButton abortButton = new JButton( "Abbrechen" );
      final JLabel userLabel = new JLabel( "Nutzer auswählen" );
      final JLabel newPassword = new JLabel( "Neues Passwort:  " );
      final JTextField passwordField = new JTextField();
      final JComboBox<String> userComboBox = new JComboBox<>( user );
      passwordPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );

      final GridBagConstraints userLabelConstraints = new GridBagConstraints();
      userLabelConstraints.gridx = 0;
      userLabelConstraints.gridy = 0;
      userLabelConstraints.insets = new Insets( 0, 0, 0, 5 );
      passwordPanel.add( userLabel, userLabelConstraints );
      final GridBagConstraints userComboBoxConstraints = new GridBagConstraints();
      userComboBoxConstraints.gridx = 1;
      userComboBoxConstraints.gridy = 0;
      userComboBoxConstraints.fill = GridBagConstraints.HORIZONTAL;
      passwordPanel.add( userComboBox, userComboBoxConstraints );
      final GridBagConstraints newPasswordConstraints = new GridBagConstraints();
      newPasswordConstraints.gridx = 0;
      newPasswordConstraints.gridy = 1;
      newPasswordConstraints.insets = new Insets( 10, 0, 30, 5 );
      passwordPanel.add( newPassword, newPasswordConstraints );
      final GridBagConstraints passwordFieldConstraints = new GridBagConstraints();
      passwordFieldConstraints.gridx = 1;
      passwordFieldConstraints.gridy = 1;
      passwordFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
      passwordFieldConstraints.insets = new Insets( 10, 0, 30, 0 );
      passwordPanel.add( passwordField, passwordFieldConstraints );
      final GridBagConstraints saveButtonConstraints = new GridBagConstraints();
      saveButtonConstraints.gridx = 0;
      saveButtonConstraints.gridy = 2;
      passwordPanel.add( saveButton, saveButtonConstraints );
      final GridBagConstraints abortButtonConstraints = new GridBagConstraints();
      abortButtonConstraints.gridx = 1;
      abortButtonConstraints.gridy = 2;
      passwordPanel.add( abortButton, abortButtonConstraints );

      passwordFrame = new MeMateDialog( GUIObjects.mainframe )
      {
        @Override
        public void layoutComponents()
        {
          layoutContainer.add( passwordPanel, BorderLayout.CENTER );
        }
      };
      passwordFrame.setTitle( "Passwort zurücksetzen" );
      passwordFrame.setIconImage( GUIObjects.mainframe.getIconImage() );

      abortButton.addActionListener( e1 -> passwordFrame.dispose() );
      saveButton.addActionListener( e1 ->
      {
        ServerCommunication.getInstance().changePassword( String.valueOf( userComboBox.getSelectedItem() ),
            Util.getHash( passwordField.getText() ) );
        passwordFrame.dispose();
      } );
      passwordFrame.add( passwordPanel );
      passwordFrame.setSize( 300, 160 );
      passwordFrame.setLocationRelativeTo( GUIObjects.mainframe );
      passwordFrame.setVisible( true );
      passwordFrame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
    } );
  }

  /**
   * Setzt die Action für das Exportieren von Daten.
   */
  private void loadExportButtonAction()
  {
    final ActionListener[] exportButtonListener = exportButton.getActionListeners();
    for ( final ActionListener actionListener : exportButtonListener )
    {
      exportButton.removeActionListener( actionListener );
    }
    exportButton.addActionListener( e ->
    {
      new DataExport();
      JOptionPane.showMessageDialog( this, "Die Daten wurden erfolgreich exportiert\nund liegen im MeMate AppData Ordner",
          "Daten exportieren", JOptionPane.INFORMATION_MESSAGE );
    } );
  }

  private void layoutHeaderComponents( final JPanel pwChangePanel, final JPanel piggyBankPanel, final JPanel exportPanel )
  {
    upperPanel.add( pwChangePanel );
    upperPanel.add( piggyBankPanel );
    upperPanel.add( exportPanel );
    upperUpperPanel.add( upperPanel, BorderLayout.CENTER );
    final JSeparator separator = new JSeparator( SwingConstants.HORIZONTAL );
    upperUpperPanel.add( separator, BorderLayout.SOUTH );
    upperPanel.setBorder( new EmptyBorder( 0, 0, 20, 0 ) );
    upperUpperPanel.setBorder( new EmptyBorder( 40, 20, 20, 20 ) );
  }


  private void loadExportPanelSettings( final JPanel exportPanel )
  {
    exportPanel.setLayout( new GridBagLayout() );
    final GridBagConstraints exportPanelConstraints = new GridBagConstraints();
    exportPanelConstraints.gridx = 0;
    exportPanelConstraints.gridy = 0;
    exportPanelConstraints.weightx = 1;
    exportPanelConstraints.weighty = 1;
    exportPanelConstraints.fill = GridBagConstraints.BOTH;
    exportPanelConstraints.insets = new Insets( 5, 5, 5, 5 );
    exportPanel.setPreferredSize( new Dimension( 150, 90 ) );
    exportPanel.add( exportButton, exportPanelConstraints );
  }


  private void loadPWChangePanelSettings( final JPanel pwChangePanel )
  {
    pwChangePanel.setPreferredSize( new Dimension( 100, 90 ) );
    pwChangePanel.setLayout( new GridBagLayout() );
    final GridBagConstraints pwChangePanelConstraints = new GridBagConstraints();
    pwChangePanelConstraints.gridx = 0;
    pwChangePanelConstraints.gridy = 0;
    pwChangePanelConstraints.weightx = 1;
    pwChangePanelConstraints.weighty = 1;
    pwChangePanelConstraints.fill = GridBagConstraints.BOTH;
    pwChangePanelConstraints.insets = new Insets( 5, 5, 5, 5 );
    pwChangePanel.add( resetPasswordButton, pwChangePanelConstraints );
    pwChangePanel.setPreferredSize( new Dimension( 150, 90 ) );
  }

  private void loadPiggyBankPanelSettings( final JPanel piggyBankPanel )
  {
    setPiggybankBalance( Cache.getInstance().getPiggyBankBalance() );
    piggyBankPanel.setPreferredSize( new Dimension( 460, 90 ) );
    piggyBankPanel.setLayout( new GridBagLayout() );
    piggyBankLabel.setFont( piggyBankLabel.getFont().deriveFont( 25f ) );
    piggyBankLabel.setHorizontalAlignment( SwingConstants.CENTER );

    final GridBagConstraints piggybankLabelConstraints = new GridBagConstraints();
    piggybankLabelConstraints.gridx = 0;
    piggybankLabelConstraints.gridy = 0;
    piggybankLabelConstraints.gridwidth = 2;
    piggybankLabelConstraints.weightx = 0;
    piggybankLabelConstraints.weighty = 0;
    piggybankLabelConstraints.insets = new Insets( 5, 5, 15, 5 );
    piggyBankPanel.add( piggyBankLabel, piggybankLabelConstraints );
    final GridBagConstraints balanceFieldConstraints = new GridBagConstraints();
    balanceFieldConstraints.gridx = 0;
    balanceFieldConstraints.gridy = 1;
    balanceFieldConstraints.weightx = 1;
    balanceFieldConstraints.weighty = 1;
    balanceFieldConstraints.insets = new Insets( 0, 5, 5, 0 );
    balanceFieldConstraints.anchor = GridBagConstraints.LINE_START;
    piggyBankPanel.add( balanceField, balanceFieldConstraints );
    final GridBagConstraints setAdminBalanceButtonConstraints = new GridBagConstraints();
    setAdminBalanceButtonConstraints.gridx = 1;
    setAdminBalanceButtonConstraints.gridy = 1;
    setAdminBalanceButtonConstraints.weightx = 0;
    setAdminBalanceButtonConstraints.weighty = 0;
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
   * Updates the label for the piggybankbalance
   *
   * @param balance new balance
   */
  public void setPiggybankBalance( final Float balance )
  {
    piggyBankLabel.setText( String.format( "Im Sparschwein befinden sich %.2f€", balance ) );
  }
}
