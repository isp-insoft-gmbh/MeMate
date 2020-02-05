/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.util.MeMateUIManager;

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
    historyTable = new JTable( ServerCommunication.getInstance().getHistoryData( dateType.MIDDLE ), columnNames );
    tableModel = new DefaultTableModel( ServerCommunication.getInstance().getHistoryData( dateType.MIDDLE ), columnNames )
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
    historyTable.setRowHeight( 30 );
    scrollPane.setBorder( BorderFactory.createEmptyBorder() );
    scrollPane.setViewportView( historyTable );
    add( scrollPane, BorderLayout.CENTER );

    historyTable.setSelectionBackground( UIManager.getColor( "AppColor" ) );
    MeMateUIManager.registerPanel( "default", this );
    MeMateUIManager.registerTable( "table", historyTable );
    MeMateUIManager.registerScrollPane( "scroll", scrollPane );
  }


  @SuppressWarnings( "javadoc" )
  public void updateHistory()
  {
    tableModel = new DefaultTableModel( ServerCommunication.getInstance().getHistoryData( dateType.MIDDLE ), columnNames )
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
