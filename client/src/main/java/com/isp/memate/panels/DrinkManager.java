package com.isp.memate.panels;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import com.isp.memate.ServerCommunication;
import com.isp.memate.dialogs.CreateDrinkDialog;
import com.isp.memate.dialogs.IngredientsDialog;
import com.isp.memate.util.DrinkManagerTableModel;
import com.isp.memate.util.DrinkPictureCellEditor;
import com.isp.memate.util.DrinkPriceCellRenderer;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.HorizontalAlignmentHeaderRenderer;
import com.isp.memate.util.TableSpinnerCellEditor;

/**
 * TODO Javadoc
 * 
 * @author nwe
 * @since 24.03.2021
 */
public class DrinkManager extends JPanel
{
  private JScrollPane    scrollPane;
  private JTable         table;
  DrinkManagerTableModel model;

  public DrinkManager()
  {
    GUIObjects.currentPanel = this;
    initComponents();
    setLayout( new BorderLayout() );
    add( scrollPane, BorderLayout.CENTER );
    checkIfModelIsEmpty();
  }


  private void initComponents()
  {
    model = new DrinkManagerTableModel();
    table = new JTable( model );
    table.setAutoCreateRowSorter( true );
    table.setShowVerticalLines( true );

    final JTableHeader header = table.getTableHeader();
    header.setReorderingAllowed( false );
    table.setRowHeight( 150 );

    scrollPane = new JScrollPane();
    scrollPane.setBorder( BorderFactory.createEmptyBorder() );
    scrollPane.setViewportView( table );

    table.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mousePressed( MouseEvent event )
      {
        // selects the row at which point the mouse is clicked
        final Point point = event.getPoint();
        final int currentRow = table.rowAtPoint( point );
        table.setRowSelectionInterval( currentRow, currentRow );
      }
    } );
    applyCellRenderer();
    addTablePopUpMenu();
  }


  private void addTablePopUpMenu()
  {
    final JPopupMenu popupMenu = new JPopupMenu();
    final JMenuItem newDrink = new JMenuItem( "Getränk hinzufügen" );
    final JMenuItem changeIngredients = new JMenuItem( "Inhaltsstoffe bearbeiten" );
    final JMenuItem deleteDrink = new JMenuItem( "Getränk löschen" );

    newDrink.addActionListener( e ->
    {
      final CreateDrinkDialog dialog = new CreateDrinkDialog( GUIObjects.mainframe );
      dialog.showDialog();
    } );
    deleteDrink.addActionListener( e ->
    {
      final int result = JOptionPane.showConfirmDialog( DrinkManager.this,
          "Wollen Sie wirklich " + model.getDrinkAt( table.getSelectedRow() ).getName() + " löschen?", "Getränk entfernen",
          JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
      if ( result == JOptionPane.YES_OPTION )
      {
        ServerCommunication.getInstance().removeDrink( model.getDrinkAt( table.getSelectedRow() ).getId() );
      }
    } );
    changeIngredients.addActionListener( e ->
    {
      final IngredientsDialog dialog = new IngredientsDialog( model.getDrinkAt( table.getSelectedRow() ).getId(), GUIObjects.mainframe );
      dialog.showDialog();
    } );

    popupMenu.add( newDrink );
    popupMenu.add( deleteDrink );
    popupMenu.add( changeIngredients );
    table.setComponentPopupMenu( popupMenu );
  }

  private void applyCellRenderer()
  {
    table.getColumnModel().getColumn( 0 ).setHeaderRenderer( new HorizontalAlignmentHeaderRenderer( SwingConstants.CENTER ) );
    final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment( JLabel.CENTER );
    table.getColumnModel().getColumn( 0 ).setCellEditor( new DrinkPictureCellEditor() );
    table.getColumnModel().getColumn( 1 ).setCellRenderer( centerRenderer );
    table.getColumnModel().getColumn( 2 ).setCellRenderer( centerRenderer );
    table.getColumnModel().getColumn( 2 ).setCellEditor( new DefaultCellEditor( new JTextField() ) );
    table.getColumnModel().getColumn( 3 ).setCellRenderer( centerRenderer );
    final SpinnerModel amountModel = new SpinnerNumberModel( 0, 0, Integer.MAX_VALUE, 1 );
    table.getColumnModel().getColumn( 3 ).setCellEditor( new TableSpinnerCellEditor( amountModel ) );
    table.getColumnModel().getColumn( 4 ).setCellRenderer( new DrinkPriceCellRenderer() );
    final SpinnerModel priceModel = new SpinnerNumberModel( 0.0, 0.0, Double.MAX_VALUE, 0.1 );
    table.getColumnModel().getColumn( 4 ).setCellEditor( new TableSpinnerCellEditor( priceModel ) );
  }

  private void checkIfModelIsEmpty()
  {
    if ( model.getRowCount() == 0 )
    {
      remove( scrollPane );
      final JPanel noDrinksPanel = new JPanel( new GridBagLayout() );
      final JLabel label = new JLabel( "Es wurden keine Getränke gefunden :(" );
      label.setFont( label.getFont().deriveFont( Font.BOLD, 16f ) );
      noDrinksPanel.add( label,
          new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets( 0, 0, 10, 0 ), 0, 0 ) );
      final JButton createDrinkButton = new JButton( "Hier klicken, um ein Getränk zu erstellen" );
      createDrinkButton.addActionListener( new ActionListener()
      {
        @Override
        public void actionPerformed( ActionEvent e )
        {
          final CreateDrinkDialog dialog = new CreateDrinkDialog( GUIObjects.mainframe );
          dialog.showDialog();
        }
      } );
      noDrinksPanel.add( createDrinkButton,
          new GridBagConstraints( 0, 1, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
      add( noDrinksPanel, BorderLayout.CENTER );
    }
  }
}
