/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.isp.memate.util.MeMateUIManager;

/**
 * Im Getränkemanager kann der Admin einstellen, welche Getränke es gibt.
 * Man kann neue Getränke hinzufügen, Getränke entfernen
 * und Getränke beearbeiten, sollte sich zum Beispiel der Preis ändern.
 * Außerdem kann man Getränkeinformationen hinzufügen wie z.B Zutatenliste.
 *
 * @author nwe
 * @since 15.10.2019
 */
class Drinkmanager extends JPanel
{
  private String[]          data              = new String[ServerCommunication.getInstance().getDrinkNames().size()];
  private final JButton     addButton         = MeMateUIManager.createButton( "button" );
  private final JButton     editButton        = MeMateUIManager.createButton( "button" );
  private final JButton     removeButton      = MeMateUIManager.createButton( "button" );
  private final JButton     ingredientsButton = MeMateUIManager.createButton( "button" );
  private JList<String>     drinkList         = new JList<>( data );
  private final JScrollPane scrollpane        = new JScrollPane();
  private int               currentSelection;


  /**
   * Passt das Layout an und legt den CellRenderer fest.
   * Dadurch werden sowohl Bilder als auch die Namen/Preise in der Liste angezeigt.
   */
  public Drinkmanager()
  {
    addButton.setText( "Hinzufügen" );
    editButton.setText( "Bearbeiten" );
    removeButton.setText( "Entfernen" );
    ingredientsButton.setText( "Inhaltsstoffe" );
    data = ServerCommunication.getInstance().getDrinkNames().toArray( data );
    setLayout( new BorderLayout() );
    add( scrollpane, BorderLayout.CENTER );
    add( createButtonPanel(), BorderLayout.SOUTH );

    drinkList.setCellRenderer( new DrinkCellRenderer() );
    drinkList.setFixedCellHeight( 150 );
    drinkList.setFont( drinkList.getFont().deriveFont( 20f ) );
    scrollpane.setViewportView( drinkList );
    MeMateUIManager.registerScrollPane( "scroll", scrollpane );

    if ( data.length == 0 )
    {
      editButton.setEnabled( false );
      removeButton.setEnabled( false );
    }
    else
    {
      drinkList.setSelectedIndex( 0 );
      editButton.setEnabled( true );
      removeButton.setEnabled( true );
    }
    drinkList.addListSelectionListener( e -> currentSelection = drinkList.getSelectedIndex() );
    MeMateUIManager.registerPanel( "default", this );
  }

  /**
   * Erzeugt ein JPanel mit den Buttons zum Hinzufügen, Bearbeiten und Entfernen von Getränken.
   *
   * @return JPanel mit den genannten Buttons.
   */
  private JPanel createButtonPanel()
  {
    final JPanel panel = MeMateUIManager.createJPanel();
    final GridBagConstraints gridBagConstraints = new GridBagConstraints();

    panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    panel.setLayout( new GridBagLayout() );
    gridBagConstraints.anchor = GridBagConstraints.LINE_START;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.weightx = 0.5;
    panel.add( addButton, gridBagConstraints );
    gridBagConstraints.gridx = 1;
    gridBagConstraints.weightx = 0;
    panel.add( ingredientsButton, gridBagConstraints );
    gridBagConstraints.gridx = 2;
    gridBagConstraints.weightx = 0;
    panel.add( editButton, gridBagConstraints );
    gridBagConstraints.gridx = 3;
    gridBagConstraints.anchor = GridBagConstraints.LINE_END;
    panel.add( removeButton, gridBagConstraints );

    removeButton.addActionListener( e ->
    {
      final int result = JOptionPane.showConfirmDialog( scrollpane,
          "Wollen Sie wirklich " + drinkList.getSelectedValue() + " löschen?", "Getränk entfernen",
          JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE );
      if ( result == JOptionPane.YES_OPTION )
      {
        ServerCommunication.getInstance().removeDrink( ServerCommunication.getInstance().getID( drinkList.getSelectedValue() ),
            drinkList.getSelectedValue() );
      }
      updateList();
    } );
    ingredientsButton.addActionListener( e ->
    {
      final DrinkManagerDialog ingredientsDialog = new DrinkManagerDialog( SwingUtilities.getWindowAncestor( Drinkmanager.this ) );
      ingredientsDialog.showIngredientsDialog( ServerCommunication.getInstance().getID( drinkList.getSelectedValue() ) );
    } );
    addButton.addActionListener( e ->
    {
      final DrinkManagerDialog addDrinkDialog =
          new DrinkManagerDialog( SwingUtilities.getWindowAncestor( Drinkmanager.this ) );
      addDrinkDialog.showNewDialog();
    } );
    editButton.addActionListener( e ->
    {
      final DrinkManagerDialog editDrinkDialog =
          new DrinkManagerDialog( SwingUtilities.getWindowAncestor( Drinkmanager.this ) );
      editDrinkDialog.showEditDialog( drinkList.getSelectedValue() );
    } );
    return panel;
  }

  /**
   * Sollte es neue Getränke geben so kann hier von Außen die Liste aktualisiert werden.
   */
  void updateList()
  {
    data = new String[ServerCommunication.getInstance().getDrinkNames().size()];
    System.out.println( data );
    data = ServerCommunication.getInstance().getDrinkNames().toArray( data );
    drinkList = new JList<>( data );
    drinkList.setCellRenderer( new DrinkCellRenderer() );
    drinkList.setFixedCellHeight( 150 );
    drinkList.setFont( drinkList.getFont().deriveFont( 20f ) );
    MeMateUIManager.registerList( "default", drinkList );
    scrollpane.setViewportView( drinkList );
    scrollpane.repaint();

    if ( data.length == 0 )
    {
      editButton.setEnabled( false );
      removeButton.setEnabled( false );
    }
    else
    {
      drinkList.setSelectedIndex( currentSelection );
      editButton.setEnabled( true );
      removeButton.setEnabled( true );
    }
    drinkList.addListSelectionListener( e -> currentSelection = drinkList.getSelectedIndex() );
  }
}
