/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Im Getränkemanager kann man einstellen, welche Getränke derzeit verfügbar sind und somit im
 * {@link Dashboard} angezeigt werden. Man kann neue Getränke hinzufügen, Getränke entfernen
 * und Getränke beearbeiten, sollte sich zum Beispiel der Preis ändern.
 * 
 * @author nwe
 * @since 15.10.2019
 */
public class Drinkmanager extends JPanel
{
  private String[]                  data              = new String[ServerCommunication.getInstance().getDrinkNames().size()];
  private JList<String>             drinkList         = new JList<>( data );
  private final JScrollPane         scrollpane        = new JScrollPane();
  private int                       currentSelection;
  final JButton                     addButton         = new JButton( "Hinzufügen" );
  final JButton                     editButton        = new JButton( "Bearbeiten" );
  final JButton                     removeButton      = new JButton( "Entfernen" );
  final JButton                     ingredientsButton = new JButton( "Inhaltsstoffe" );
  private static final Drinkmanager instance          = new Drinkmanager();

  /**
   * @return static instance of {@link Drinkmanager}
   */
  public static Drinkmanager getInstance()
  {
    return instance;
  }

  /**
   * Passt das Layout an und legt den CellRenderer fest.
   * Dadurch werden sowohl Bilder als auch die Namen/Preise in der Liste angezeigt.
   */
  public Drinkmanager()
  {
    data = ServerCommunication.getInstance().getDrinkNames().toArray( data );
    setLayout( new BorderLayout() );
    add( scrollpane, BorderLayout.CENTER );
    add( createButtonPanel(), BorderLayout.SOUTH );

    drinkList.setCellRenderer( new DrinkCellRenderer() );
    drinkList.setFixedCellHeight( 150 );
    drinkList.setFont( drinkList.getFont().deriveFont( 20f ) );
    scrollpane.setViewportView( drinkList );

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
    drinkList.addListSelectionListener( new ListSelectionListener()
    {
      @Override
      public void valueChanged( ListSelectionEvent e )
      {
        currentSelection = drinkList.getSelectedIndex();

      }
    } );
  }

  /**
   * Erzeugt ein Jpanel mit den Buttons zum Hinzufügen, Bearbeiten und Entfernen von Getränken.
   *
   * @return JPanel mit den genannten Buttons.
   */
  private JPanel createButtonPanel()
  {

    final JPanel panel = new JPanel();
    final GridBagConstraints gridBagConstraints = new GridBagConstraints();

    panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    panel.setBackground( UIManager.getColor( "TabbedPane.highlight" ) );
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

    removeButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        int result = JOptionPane.showConfirmDialog( scrollpane,
            "Wollen Sie wirklich " + drinkList.getSelectedValue() + " löschen?", "Getränk entfernen",
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE );
        if ( result == JOptionPane.YES_OPTION )
        {
          ServerCommunication.getInstance().removeDrink( ServerCommunication.getInstance().getID( drinkList.getSelectedValue() ),
              drinkList.getSelectedValue() );
        }
      }
    } );
    ingredientsButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        DrinkManagerDialog ingredientsDialog = new DrinkManagerDialog( SwingUtilities.getWindowAncestor( Drinkmanager.this ) );
        ingredientsDialog.showIngredientsDialog( ServerCommunication.getInstance().getID( drinkList.getSelectedValue() ) );
      }
    } );
    addButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        DrinkManagerDialog addDrinkDialog =
            new DrinkManagerDialog( SwingUtilities.getWindowAncestor( Drinkmanager.this ) );
        addDrinkDialog.showNewDialog();
      }
    } );
    editButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        DrinkManagerDialog editDrinkDialog =
            new DrinkManagerDialog( SwingUtilities.getWindowAncestor( Drinkmanager.this ) );
        editDrinkDialog.showEditDialog( drinkList.getSelectedValue() );
      }
    } );
    return panel;
  }


  @SuppressWarnings( "javadoc" )
  public void updateList()
  {
    data = new String[ServerCommunication.getInstance().getDrinkNames().size()];
    data = ServerCommunication.getInstance().getDrinkNames().toArray( data );
    drinkList = new JList<>( data );
    drinkList.setCellRenderer( new DrinkCellRenderer() );
    drinkList.setFixedCellHeight( 150 );
    drinkList.setFont( drinkList.getFont().deriveFont( 20f ) );
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
    drinkList.addListSelectionListener( new ListSelectionListener()
    {
      @Override
      public void valueChanged( ListSelectionEvent e )
      {
        currentSelection = drinkList.getSelectedIndex();

      }
    } );
  }
}
