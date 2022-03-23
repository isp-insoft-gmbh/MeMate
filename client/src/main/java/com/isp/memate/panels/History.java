package com.isp.memate.panels;

import com.isp.memate.Cache;
import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.util.HistoryObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class History extends TableView<HistoryObject>
{
  public History()
  {
    setEditable( false );

    final TableColumn<HistoryObject, String> actionCol = new TableColumn<>( "Aktion" );
    final TableColumn<HistoryObject, String> consumerCol = new TableColumn<>( "Benutzer" );
    final TableColumn<HistoryObject, String> priceCol = new TableColumn<>( "Transaktionsmenge" );
    final TableColumn<HistoryObject, String> newBalanceCol = new TableColumn<>( "Neuer Kontostand" );
    final TableColumn<HistoryObject, String> dateCol = new TableColumn<>( "Datum" );

    getColumns().addAll( actionCol, consumerCol, priceCol, newBalanceCol, dateCol );

    final ObservableList<HistoryObject> data = FXCollections.observableArrayList();
    final String[][] historyData = Cache.getInstance().getHistory( dateType.MIDDLE );
    for ( final String[] eventData : historyData )
    {
      data.add( new HistoryObject( eventData[ 0 ], eventData[ 1 ], eventData[ 2 ], eventData[ 3 ], eventData[ 4 ] ) );
    }

    actionCol.setCellValueFactory(
        new PropertyValueFactory<HistoryObject, String>( "action" ) );
    consumerCol.setCellValueFactory(
        new PropertyValueFactory<HistoryObject, String>( "userName" ) );
    priceCol.setCellValueFactory(
        new PropertyValueFactory<HistoryObject, String>( "price" ) );
    newBalanceCol.setCellValueFactory(
        new PropertyValueFactory<HistoryObject, String>( "newBalance" ) );
    dateCol.setCellValueFactory(
        new PropertyValueFactory<HistoryObject, String>( "date" ) );
    setItems( data );
    setColumnResizePolicy( CONSTRAINED_RESIZE_POLICY );
  }
}
