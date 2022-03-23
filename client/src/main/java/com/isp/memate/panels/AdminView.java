package com.isp.memate.panels;

import com.isp.memate.Cache;
import com.isp.memate.Drink;
import com.isp.memate.util.DrinkTableObject;
import com.isp.memate.util.GUIObjects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AdminView extends TableView<DrinkTableObject>
{
  public AdminView()
  {
    GUIObjects.currentPanel = this;
    setEditable( true );

    final TableColumn<DrinkTableObject, Image> pictureCol = new TableColumn<>( "Bild" );
    final TableColumn<DrinkTableObject, String> nameCol = new TableColumn<>( "Name" );
    final TableColumn<DrinkTableObject, Integer> amountCol = new TableColumn<>( "Anzahl" );
    final TableColumn<DrinkTableObject, String> priceCol = new TableColumn<>( "Preis" );

    getColumns().addAll( pictureCol, nameCol, amountCol, priceCol );

    final ObservableList<DrinkTableObject> data = FXCollections.observableArrayList();
    for ( final Drink drink : Cache.getInstance().getDrinks().values() )
    {
      data.add( new DrinkTableObject( drink.getPictureInBytes(), drink.getName(), drink.getAmount(), drink.getPrice() ) );
    }


    pictureCol.setCellFactory( param ->
    {
      //Set up the ImageView
      final ImageView imageview = new ImageView();
      imageview.setFitHeight( 220 );
      imageview.setFitWidth( 70 );
      imageview.setSmooth( true );

      //Set up the Table
      final TableCell<DrinkTableObject, Image> cell = new TableCell<DrinkTableObject, Image>()
      {
        @Override
        public void updateItem( Image item, boolean empty )
        {
          if ( item != null )
          {
            imageview.setImage( item );
          }
        }
      };
      // Attach the imageview to the cell
      cell.setGraphic( imageview );
      return cell;
    } );
    pictureCol.setCellValueFactory( new PropertyValueFactory<DrinkTableObject, Image>( "image" ) );

    nameCol.setCellValueFactory(
        new PropertyValueFactory<DrinkTableObject, String>( "name" ) );
    amountCol.setCellValueFactory(
        new PropertyValueFactory<DrinkTableObject, Integer>( "amount" ) );
    priceCol.setCellValueFactory(
        new PropertyValueFactory<DrinkTableObject, String>( "price" ) );
    setItems( data );
    setColumnResizePolicy( CONSTRAINED_RESIZE_POLICY );
  }

  public void setPiggybankBalance( Float newValue )
  {
    //TODO(nwe | 07.03.2022): 
  }
}