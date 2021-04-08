/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate.panels;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.isp.memate.Cache;
import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.HorizontalAlignmentHeaderRenderer;

/**
 * In der Historie soll der Nutzer alle bisherigen Buchungen sehen, egal ob er etwas
 * gekauft oder das Konto aufgeladen wurde. Die Buchungen sollen in Aktion, Konsument,
 * Transaktionsmenge, neuer Kontostand und Datum angezeigt werden.
 * Der normale User sieht nur seine eigenen Aktionen, der Admin jedoch alle.
 * 
 * @author nwe
 * @since 15.10.2019
 */
public class History extends JPanel
{
  private final String[]    columnNames = { "Aktion", "Konsument", "Transakstionsmenge", "Neuer Kontostand", "Datum" };
  private JScrollPane       scrollPane;
  private DefaultTableModel tableModel;
  private JTable            historyTable;
  private String[][]        historyData;

  public History()
  {
    GUIObjects.currentPanel = this;
    initComponents();
    setLayout( new BorderLayout() );
    add( scrollPane, BorderLayout.CENTER );
  }

  private void initComponents()
  {
    historyTable = new JTable();
    historyTable.setAutoCreateRowSorter( true );
    historyTable.setShowVerticalLines( true );

    JTableHeader header = historyTable.getTableHeader();
    header.setReorderingAllowed( false );
    historyTable.setRowHeight( 30 );

    scrollPane = new JScrollPane();
    scrollPane.setBorder( BorderFactory.createEmptyBorder() );
    scrollPane.setViewportView( historyTable );
    historyData = Cache.getInstance().getHistory( dateType.MIDDLE );
    applyTableModel();
  }

  private void applyTableModel()
  {
    tableModel = new DefaultTableModel( historyData, columnNames )
    {
      @Override
      public boolean isCellEditable( int row, int column )
      {
        return false;
      }
    };
    historyTable.setModel( tableModel );
    applyCellRenderer();
  }

  private void applyCellRenderer()
  {
    historyTable.getColumnModel().getColumn( 0 ).setHeaderRenderer( new HorizontalAlignmentHeaderRenderer( SwingConstants.CENTER ) );
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment( JLabel.CENTER );
    historyTable.getColumnModel().getColumn( 0 ).setCellRenderer( centerRenderer );
    historyTable.getColumnModel().getColumn( 1 ).setCellRenderer( centerRenderer );
    historyTable.getColumnModel().getColumn( 2 ).setCellRenderer( centerRenderer );
    historyTable.getColumnModel().getColumn( 3 ).setCellRenderer( centerRenderer );
    historyTable.getColumnModel().getColumn( 4 ).setCellRenderer( centerRenderer );
  }

  //TODO(nwe | 02.12.2020): Sobald der Client neue History-Daten vom Server erhält soll er diese analysieren und wenn es Unterscheide gibt, dann soll die historytable automatisch geupdatet werden, wenn die View bereits geöffnet wurde
  void updateTableModel( String[][] historyData )
  {
    this.historyData = historyData;
    applyTableModel();
  }
}