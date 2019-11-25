/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nwe
 * @since 24.10.2019
 */
public class ConnectDatabase
{
    private Connection connect()
    {
        // SQLite connection string
        String url = "jdbc:sqlite:C:\\Users\\admlokal\\Desktop\\Test.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public Float getBalance(String username)
    {
        Float balance = 0f;
        String sql = "SELECT Guthaben,Username FROM Userinfo";
        try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (!rs.getString("Username").equals(username)) {
                rs.next();
            }
            balance = rs.getFloat("Guthaben");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return balance;
    }

    /**
     * @return fills up a list containg all users and passwords
     */
    public Map<String, String> getUserMap()
    {
        Map<String, String> userMap = new HashMap<>();
        String sql = "SELECT Password,Username FROM Userinfo";
        try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (!rs.isClosed()) {
                userMap.put(rs.getString("Username"), rs.getString("Password"));
                rs.next();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return userMap;

    }

    public void registerNewUser(String username, String password)
    {
        String sql = "INSERT INTO Userinfo(Guthaben,Username,Password) VALUES(?,?,?)";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setFloat(1, 0f);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void updateBalance(String username, Float updatedBalance)
    {
        String sql = "UPDATE Userinfo SET Guthaben=? WHERE Username='" + username + "'";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setFloat(1, updatedBalance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * @return A String containing Informations about the drinks, including price,Imagepath and name
     */
    public String getDrinkInformations()
    {
        String picturePath = "C:" + File.separator + "Users" + File.separator + "admlokal" + File.separator + "Pictures"
            + File.separator + "MeMateImages" + File.separator;
        StringBuilder infoBuilder = new StringBuilder();
        String sql = "SELECT Name,Preis,Picture FROM Drinks";
        try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (!rs.isClosed()) {
                rs.next();
                infoBuilder.append("[" + rs.getString("Name") + "," + rs.getFloat("Preis") + "€," + picturePath
                    + rs.getString("Picture") + "],");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return infoBuilder.toString();
    }

    /**
     * @param name Name of Drink
     * @param price Price of Drink
     * @param picture Picture of Drink
     */
    public void registerNewDrink(String name, Float price, String picture)
    {
        String sql = "INSERT INTO Drinks(Name,Preis,Picture) VALUES(?,?,?)";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setFloat(2, price);
            pstmt.setString(3, picture.replace("GET_DRINK_INFORMATIONS", ""));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removeDrink(String drinkname)
    {
        String sql = "DELETE FROM Drinks WHERE Name= ?";

        try (Connection conn = this.connect(); 
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, drinkname);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
