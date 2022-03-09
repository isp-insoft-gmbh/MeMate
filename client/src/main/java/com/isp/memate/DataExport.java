/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.PropertyHelper;

/**
 * Die Klasse wird nur aufgerufen sobald der Admin den Export Button im AdminPanel drückt.
 * Anschließend werden alle Nutzer-, Getränke, und Historydaten in Form von
 * XML-Datein exportiert.
 * 
 * @author nwe
 * @since 23.01.2020
 *
 */
public class DataExport
{
  private final String path           = PropertyHelper.MAIN_FOLDER + File.separator;
  private final String userXMLPath    = path + "users.xml";
  private final String drinksXMLPath  = path + "drinks.xml";
  private final String historyXMLPath = path + "history.xml";
  private User[]       userArray;

  /**
   * Exportiert zuerst Nutzerdaten, dann Getränkedaten und Historydaten.
   */
  public DataExport()
  {
    userExport();
    drinksExport();
    historyExport();
  }

  /**
   * Exportiert die Historydaten.
   */
  private void historyExport()
  {
    final String[][] history = Cache.getInstance().getHistory( dateType.LONG );
    try
    {
      final DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
      final DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
      final Document document = documentBuilder.newDocument();
      final Element root = document.createElement( "Historie" );
      document.appendChild( root );
      for ( int i = 0; i < history.length; i++ )
      {
        final Element logElement = document.createElement( "Log" );
        root.appendChild( logElement );
        final Element user = document.createElement( "Benutzer" );
        user.appendChild( document.createTextNode( history[ i ][ 1 ] ) );
        logElement.appendChild( user );
        final Element action = document.createElement( "Aktion" );
        action.appendChild( document.createTextNode( history[ i ][ 0 ] ) );
        logElement.appendChild( action );
        final Element transaction = document.createElement( "Transaktion" );
        transaction.appendChild( document.createTextNode( history[ i ][ 2 ] ) );
        logElement.appendChild( transaction );
        final Element balance = document.createElement( "Kontostand" );
        balance.appendChild( document.createTextNode( history[ i ][ 3 ] ) );
        logElement.appendChild( balance );
        final Element date = document.createElement( "Datum" );
        date.appendChild( document.createTextNode( history[ i ][ 4 ] ) );
        logElement.appendChild( date );
      }
      final TransformerFactory transformerFactory = TransformerFactory.newInstance();
      final Transformer transformer = transformerFactory.newTransformer();
      final DOMSource domSource = new DOMSource( document );
      final StreamResult streamResult = new StreamResult( new File( historyXMLPath ) );
      transformer.transform( domSource, streamResult );
      ClientLog.newLog( "Done creating History-XML File" );
    }
    catch ( final ParserConfigurationException pce )
    {
      pce.printStackTrace();
    }
    catch ( final TransformerException tfe )
    {
      tfe.printStackTrace();
    }
  }

  /**
   * Exportiert die Getränkedaten.
   */
  private void drinksExport()
  {
    final HashMap<Integer, Drink> drinks = Cache.getInstance().getDrinks();
    try
    {
      final DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
      final DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
      final Document document = documentBuilder.newDocument();
      final Element root = document.createElement( "Getränke" );
      document.appendChild( root );
      for ( final Drink drink : drinks.values() )
      {
        final Element drinkElement = document.createElement( "Getränk" );
        root.appendChild( drinkElement );
        final Element id = document.createElement( "ID" );
        id.appendChild( document.createTextNode( String.valueOf( drink.getId() ) ) );
        drinkElement.appendChild( id );
        final Element name = document.createElement( "Name" );
        name.appendChild( document.createTextNode( drink.getName() ) );
        drinkElement.appendChild( name );
        final Element price = document.createElement( "Preis" );
        price.appendChild( document.createTextNode( String.valueOf( drink.getPrice() ) ) );
        drinkElement.appendChild( price );
        final Element amount = document.createElement( "Anzahl" );
        amount.appendChild( document.createTextNode( String.valueOf( drink.getAmount() ) ) );
        drinkElement.appendChild( amount );
        if ( drink.isIngredients() )
        {
          final Element hasIngredients = document.createElement( "Zutatenangabe" );
          drinkElement.appendChild( hasIngredients );
          final Element ingredients = document.createElement( "Zutaten" );
          ingredients.appendChild( document.createTextNode( drink.getDrinkIngredients().getIngredients() ) );
          hasIngredients.appendChild( ingredients );
          final Element kJ = document.createElement( "kJ" );
          kJ.appendChild( document.createTextNode( String.valueOf( drink.getDrinkIngredients().getEnergy_kJ() ) ) );
          hasIngredients.appendChild( kJ );
          final Element kcal = document.createElement( "kcal" );
          kcal.appendChild( document.createTextNode( String.valueOf( drink.getDrinkIngredients().getEnergy_kcal() ) ) );
          hasIngredients.appendChild( kcal );
          final Element fat = document.createElement( "Fett" );
          fat.appendChild( document.createTextNode( String.valueOf( drink.getDrinkIngredients().getFat() ) ) );
          hasIngredients.appendChild( fat );
          final Element fatty_acids = document.createElement( "gesättigteFettsäuren" );
          fatty_acids.appendChild( document.createTextNode( String.valueOf( drink.getDrinkIngredients().getFatty_acids() ) ) );
          hasIngredients.appendChild( fatty_acids );
          final Element carbs = document.createElement( "Kohlenhydrate" );
          carbs.appendChild( document.createTextNode( String.valueOf( drink.getDrinkIngredients().getCarbs() ) ) );
          hasIngredients.appendChild( carbs );
          final Element sugar = document.createElement( "Zucker" );
          sugar.appendChild( document.createTextNode( String.valueOf( drink.getDrinkIngredients().getProtein() ) ) );
          hasIngredients.appendChild( sugar );
          final Element protein = document.createElement( "Eiweiß" );
          protein.appendChild( document.createTextNode( String.valueOf( drink.getDrinkIngredients().getProtein() ) ) );
          hasIngredients.appendChild( protein );
          final Element salt = document.createElement( "Salz" );
          salt.appendChild( document.createTextNode( String.valueOf( drink.getDrinkIngredients().getSalt() ) ) );
          hasIngredients.appendChild( salt );
        }
      }

      final TransformerFactory transformerFactory = TransformerFactory.newInstance();
      final Transformer transformer = transformerFactory.newTransformer();
      final DOMSource domSource = new DOMSource( document );
      final StreamResult streamResult = new StreamResult( new File( drinksXMLPath ) );
      transformer.transform( domSource, streamResult );
      ClientLog.newLog( "Done creating Drink-XML File" );
    }
    catch ( final ParserConfigurationException pce )
    {
      pce.printStackTrace();
    }
    catch ( final TransformerException tfe )
    {
      tfe.printStackTrace();
    }
  }

  /**
   * Exportiert die Nutzerdaten.
   */
  private void userExport()
  {
    userArray = Cache.getInstance().getFullUserArray();
    try
    {
      final DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
      final DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
      final Document document = documentBuilder.newDocument();
      final Element root = document.createElement( "Benutzer" );
      document.appendChild( root );
      for ( final User user : userArray )
      {
        final Element userElement = document.createElement( "Benutzer" );
        root.appendChild( userElement );
        final Element id = document.createElement( "ID" );
        id.appendChild( document.createTextNode( String.valueOf( user.id ) ) );
        userElement.appendChild( id );
        final Element username = document.createElement( "Benutzername" );
        username.appendChild( document.createTextNode( user.name ) );
        userElement.appendChild( username );
        final Element balance = document.createElement( "Guthaben" );
        balance.appendChild( document.createTextNode( String.valueOf( user.balance ) ) );
        userElement.appendChild( balance );
        final Element password = document.createElement( "Passwort" );
        password.appendChild( document.createTextNode( user.password ) );
        userElement.appendChild( password );
      }

      final TransformerFactory transformerFactory = TransformerFactory.newInstance();
      final Transformer transformer = transformerFactory.newTransformer();
      final DOMSource domSource = new DOMSource( document );
      final StreamResult streamResult = new StreamResult( new File( userXMLPath ) );
      transformer.transform( domSource, streamResult );
      ClientLog.newLog( "Done creating user-XML File" );
    }
    catch ( final ParserConfigurationException pce )
    {
      pce.printStackTrace();
    }
    catch ( final TransformerException tfe )
    {
      tfe.printStackTrace();
    }
  }
}