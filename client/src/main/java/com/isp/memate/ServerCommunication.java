/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

/**
 * @author nwe
 * @since 24.10.2019
 */
public class ServerCommunication
{
    private static final ServerCommunication instance = new ServerCommunication();

    private final Map<String, Float> priceMap = new HashMap<>();

    private final Map<String, ImageIcon> imageMap = new HashMap<>();

    Float balance = 0f;

    Socket socket;

    OutputStream output;

    PrintStream ps;

    InputStream serverAnswer;

    BufferedReader buff;

    String currentUser = null;

    /**
     * @return the static instance of {@link ServerCommunication}
     */
    public static ServerCommunication getInstance()
    {
        return instance;
    }

    public ServerCommunication()
    {
        try {
            socket = new Socket("localhost", 3141);
            output = socket.getOutputStream();
            ps = new PrintStream(output, true);
            serverAnswer = socket.getInputStream();
            buff = new BufferedReader(new InputStreamReader(serverAnswer));
        } catch (IOException __) {
            System.out.println("Server ist nicht an");
        }
        fillPriceAndImageMap();
    }

    /**
     * @param username After Login the username gets updated
     */
    public void updateCurrentUser(String username)
    {
        this.currentUser = username;
    }

    /**
     * Füllt die Preis- und BildMap
     */
    private void fillPriceAndImageMap()
    {
        String allDrinkData = getDrinkInfo();
        String[] splitedDrinkData = allDrinkData.split("\\]\\,\\[");
        for (String drinkData : splitedDrinkData) {
            drinkData = drinkData.replace("[", "").replace("],", "");
            String[] splitedDataOfDrink = drinkData.split(",");
            String name = splitedDataOfDrink[0];
            Float price = Float.valueOf(splitedDataOfDrink[1].replace("€", ""));
            ImageIcon icon = new ImageIcon(splitedDataOfDrink[2]);
            priceMap.put(name, price);
            imageMap.put(name, icon);
        }
    }

    /**
     * @param usernameAndPassword Der String enthält Nutzernamen und Passwort
     * @return loginResult ="Login erfolgreich" "Benutzer nicht gefunden" oder "Passwort falsch"
     */
    public String checkLogin(String usernameAndPassword)
    {
        ps.print(usernameAndPassword);
        try {
            return buff.readLine();
        } catch (IOException exception) {
            System.out.println("Der Login ist fehlgeschlagen");
        }
        return null;
    }

    public Float getBalance(String username)
    {
        String balance = null;
        ps.print("GET_BALANCE_FOR:" + username);
        try {
            balance = buff.readLine();
        } catch (NumberFormatException | IOException exception) {
            System.out.println("Guthaben konnte nicht geladen werden");
        }
        return Float.valueOf(balance);
    }

    /**
     * @return Die nötigen Informationen der DashboardButtons.
     */
    public String getDrinkInfo()
    {
        ps.print("GET_DRINK_INFORMATIONS");

        try {
            return buff.readLine();
        } catch (IOException __) {
            System.out.println("Die Getränkinformationen konnten nicht geladen werden");
        }
        return null;
    }

    /**
     * @return Die Namen der Getränke
     */
    public String[] getDrinkNames()
    {
        StringBuilder drinkNameBuilder = new StringBuilder();
        String allDrinkData = getDrinkInfo();
        String[] splitedDrinkData = allDrinkData.split("\\]\\,\\[");
        for (String drinkData : splitedDrinkData) {
            drinkData = drinkData.replace("[", "").replace("],", "");
            String[] splitedDataOfDrink = drinkData.split(",");
            String name = splitedDataOfDrink[0];
            drinkNameBuilder.append(name).append(",");
        }
        String allDrinkNames = drinkNameBuilder.toString();
        allDrinkNames = allDrinkNames.substring(0, allDrinkNames.length() - 1);
        String[] drinkNames = allDrinkNames.split(",");
        return drinkNames;
    }

    /**
     * Die Methode bekommt vom Server einen String in dem, alle Historydaten sind und dieser String wird in ein 2d-Array
     * konvertiert,damit die Tabelle die Daten verwenden kann.
     * 
     * @return Die Historydaten als 2dimensionales Array
     */
    public String[][] getHistoryData()
    {
        ps.print("GET_HISTORY_DATA");
        String historyData = null;
        String[][] historyDataAsArray = null;
        try {
            historyData = buff.readLine();
        } catch (IOException exception) {
            System.out.println("Die Historie konnte nicht geladen werden");
        }
        historyData = historyData.replace("{", "");
        historyData = historyData.substring(0, historyData.length() - 2);
        String[] splitedHistoryData = historyData.split("},");
        historyDataAsArray = new String[splitedHistoryData.length][splitedHistoryData.length];
        for (int i = 0; i < splitedHistoryData.length; i++) {
            String[] singleData = splitedHistoryData[i].split(",");
            for (int j = 0; j < singleData.length; j++) {
                historyDataAsArray[i][j] = singleData[j];
            }
        }
        return historyDataAsArray;
    }

    /**
     * @param value selected Drink
     * @return price of Drink
     */
    public Float getPrice(Object value)
    {
        return priceMap.get(value);
    }

    /**
     * @param value selected Drink
     * @return icon of drink
     */
    public ImageIcon getIcon(Object value)
    {
        return imageMap.get(value);
    }

    /**
     * @param usernameAndPassword String containing Username and Password
     */
    public void registerNewUser(String usernameAndPassword)
    {
        ps.print("REGISTER_NEW_USER:" + usernameAndPassword);
    }

    /**
     * Updates the userBalance after adding Balance or consuming a drink
     * 
     * @param newBalance Balance
     */
    public void updateBalance(float newBalance)
    {
        ps.print("UPDATE_BALANCE_FOR:" + currentUser + "NEW_BALANCE:" + newBalance);

    }

    /**
     * @param name Name of Drink
     * @param price price of Drink
     * @param picture Picture of Drink
     */
    public void registerNewDrink(String name, Float price, String picture)
    {
        System.out.println(picture);
        ps.print("REGISTER_NEW_DRINK:" + name + "," + price + "," + picture);
        priceMap.clear();
        imageMap.clear();
        System.out.println("test1");
        fillPriceAndImageMap();
        System.out.println("test2");
        Mainframe.getInstance().updateDashboardAndDrinkmanager();
    }

    public void removeDrink(String name)
    {
        ps.print("REMOVE_DRINK:"+name);
        priceMap.clear();
        imageMap.clear();
        fillPriceAndImageMap();
        Mainframe.getInstance().updateDashboardAndDrinkmanager();
            }
}
