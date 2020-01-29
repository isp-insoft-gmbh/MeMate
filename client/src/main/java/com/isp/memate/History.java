/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * In der Historie soll der Nutzer alle bisherigen Buchungen (auch von Kollegen) sehen, egal ob etwas
 * gekauft oder das Konto aufgeladen wurde. Die Buchungen sollen in Aktion, Konsument,
 * Transaktionsmenge, neuer Kontostand und Datum angezeigt werden.
 * 
 * @author nwe
 * @since 15.10.2019
 */
public class History extends JPanel
{
  private final String[]       columnNames = { "Aktion", "Konsument", "Transakstionsmenge", "Neuer Kontostand", "Datum" };
  private static final History instance    = new History();
  private JTable               historyTable;
  private DefaultTableModel    tableModel;
  private final JScrollPane    scrollPane  = new JScrollPane();

  /**
   * @return static instance of {@linkplain History}
   */
  public static History getInstance()
  {
    return instance;
  }

  /**
   * Erzeugt einen Table mit Daten von dem Server
   * und fügt diesen dem HistoryPanel hinzu.
   */
  public History()
  {
    super( new BorderLayout() );
    setBackground( Color.BLUE );
    historyTable = new JTable( ServerCommunication.getInstance().getHistoryData( true ), columnNames );
    tableModel = new DefaultTableModel( ServerCommunication.getInstance().getHistoryData( true ), columnNames )
    {
      @Override
      public boolean isCellEditable( int row, int column )
      {
        return false;
      }
    };
    historyTable.setModel( tableModel );
    historyTable.setAutoCreateRowSorter( true );
    historyTable.setShowGrid( false );
    JTableHeader header = historyTable.getTableHeader();
    header.setOpaque( false );
    header.setBackground( header.getBackground().darker() );
    historyTable.setRowHeight( 30 );
    scrollPane.setBorder( BorderFactory.createEmptyBorder() );
    scrollPane.setViewportView( historyTable );
    add( scrollPane, BorderLayout.CENTER );
    setBackground( Color.white );
    scrollPane.setBackground( Color.white );
    scrollPane.getViewport().setBackground( Color.white );
  }


  @SuppressWarnings( "javadoc" )
  public void updateHistory()
  {
    tableModel = new DefaultTableModel( ServerCommunication.getInstance().getHistoryData( true ), columnNames )
    {
      @Override
      public boolean isCellEditable( int row, int column )
      {
        return false;
      }
    };
    historyTable.setModel( tableModel );
    historyTable.getColumnModel().getColumn( 0 ).setHeaderRenderer( new HorizontalAlignmentHeaderRenderer( SwingConstants.CENTER ) );
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment( JLabel.CENTER );
    historyTable.getColumnModel().getColumn( 0 ).setCellRenderer( centerRenderer );
    historyTable.getColumnModel().getColumn( 1 ).setCellRenderer( centerRenderer );
    historyTable.getColumnModel().getColumn( 2 ).setCellRenderer( centerRenderer );
    historyTable.getColumnModel().getColumn( 3 ).setCellRenderer( centerRenderer );
    historyTable.getColumnModel().getColumn( 4 ).setCellRenderer( centerRenderer );
  }
}
