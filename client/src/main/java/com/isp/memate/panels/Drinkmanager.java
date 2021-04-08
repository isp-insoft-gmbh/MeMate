package com.isp.memate.panels;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import com.isp.memate.ServerCommunication;
import com.isp.memate.dialogs.DrinkManagerDialog;
import com.isp.memate.util.TableSpinnerCellEditor;
import com.isp.memate.util.DrinkManagerTableModel;
import com.isp.memate.util.DrinkPictureCellEditor;
import com.isp.memate.util.DrinkPriceCellRenderer;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.HorizontalAlignmentHeaderRenderer;

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
  }

  private void initComponents()
  {
    model = new DrinkManagerTableModel();
    table = new JTable( model );
    table.setAutoCreateRowSorter( true );
    table.setShowVerticalLines( true );

    JTableHeader header = table.getTableHeader();
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
        Point point = event.getPoint();
        int currentRow = table.rowAtPoint( point );
        table.setRowSelectionInterval( currentRow, currentRow );
      }
    } );
    applyCellRenderer();
    addTablePopUpMenu();
  }


  private void addTablePopUpMenu()
  {
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem newDrink = new JMenuItem( "Getränk hinzufügen" );
    JMenuItem changeIngredients = new JMenuItem( "Inhaltsstoffe bearbeiten" );
    JMenuItem deleteDrink = new JMenuItem( "Getränk löschen" );

    newDrink.addActionListener( e ->
    {
      final DrinkManagerDialog addDrinkDialog =
          new DrinkManagerDialog( SwingUtilities.getWindowAncestor( DrinkManager.this ) );
      addDrinkDialog.showNewDialog();
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
      final DrinkManagerDialog ingredientsDialog = new DrinkManagerDialog( SwingUtilities.getWindowAncestor( DrinkManager.this ) );
      ingredientsDialog.showIngredientsDialog( model.getDrinkAt( table.getSelectedRow() ).getId() );
    } );

    popupMenu.add( newDrink );
    popupMenu.add( deleteDrink );
    popupMenu.add( changeIngredients );
    table.setComponentPopupMenu( popupMenu );
  }

  private void applyCellRenderer()
  {
    table.getColumnModel().getColumn( 0 ).setHeaderRenderer( new HorizontalAlignmentHeaderRenderer( SwingConstants.CENTER ) );
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment( JLabel.CENTER );
    table.getColumnModel().getColumn( 0 ).setCellEditor( new DrinkPictureCellEditor() );
    table.getColumnModel().getColumn( 1 ).setCellRenderer( centerRenderer );
    table.getColumnModel().getColumn( 2 ).setCellRenderer( centerRenderer );
    table.getColumnModel().getColumn( 2 ).setCellEditor( new DefaultCellEditor( new JTextField() ) );
    table.getColumnModel().getColumn( 3 ).setCellRenderer( centerRenderer );
    SpinnerModel amountModel = new SpinnerNumberModel( 0, 0, Integer.MAX_VALUE, 1 );
    table.getColumnModel().getColumn( 3 ).setCellEditor( new TableSpinnerCellEditor( amountModel ) );
    table.getColumnModel().getColumn( 4 ).setCellRenderer( new DrinkPriceCellRenderer() );
    SpinnerModel priceModel = new SpinnerNumberModel( 0.0, 0.0, Double.MAX_VALUE, 0.1 );
    table.getColumnModel().getColumn( 4 ).setCellEditor( new TableSpinnerCellEditor( priceModel ) );
  }
}
