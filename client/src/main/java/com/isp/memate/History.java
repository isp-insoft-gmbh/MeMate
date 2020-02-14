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
  private final String[]    columnNames  = { "Aktion", "Konsument", "Transakstionsmenge", "Neuer Kontostand", "Datum" };
  private final JScrollPane scrollPane   = new JScrollPane();
  private JTable            historyTable = new JTable();
  private DefaultTableModel tableModel;


  /**
   * Erzeugt einen Table mit einigen Einstellungen und setzt diesen Table in ein Scrollpane.
   */
  public History()
  {
    super( new BorderLayout() );
    historyTable.setAutoCreateRowSorter( true );
    historyTable.setShowGrid( false );
    JTableHeader header = historyTable.getTableHeader();
    header.setOpaque( false );
    header.setReorderingAllowed( false );
    historyTable.setRowHeight( 30 );
    scrollPane.setBorder( BorderFactory.createEmptyBorder() );
    scrollPane.setViewportView( historyTable );
    add( scrollPane, BorderLayout.CENTER );

    historyTable.setSelectionBackground( UIManager.getColor( "AppColor" ) );
    MeMateUIManager.registerPanel( "default", this );
    MeMateUIManager.registerTable( "table", historyTable );
    MeMateUIManager.registerScrollPane( "scroll", scrollPane );
  }

  /**
   * Sollte man selber oder ein anderer Nutzer etwas machen, so kann mit dieser
   * Methode die History von Außen geupdated werden.
   */
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
