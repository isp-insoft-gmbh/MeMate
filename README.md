![MeMate Logo](https://i.imgur.com/HohmXqd.png)

## Description

This application deals with the consumption of drinks in our company.
Every user has his own balance, which gets displayed in the application 
and the user can add balance or buy drinks and the correct amount gets removed.
It's only possible to add at least 1€ to the balance, this should avoid 
the too many small coins being paid into the register, which was very
annoying in the past.

There are 4 core features:

* Loginframe
  * Login with your already existing account
  * Register a new user

* Dashboard
  * You can buy all the Drinks that are currently available from here.
  * Adding balance to your account.
  * Get some informations about deposition and the ingredients of the beverages.

* History
  * Displays every event that occurs in interaction with the application, 
	whether someone consumed a drink or added balance to their account.
  * Normal users can only see their own entries.

* Drinkmanager (Admin only)
  * Allows management of the drink sortiment. (Pricing, names and pictures)
  * Already existing drinks can also be edited or removed.
  
Side Features:

* Consumption rate
  * Contains a graph showing the consumption of bottles per day during the last month.
  * Calculates the average for bottles per day.
  * Filter for drinks.
  
* Credit history
  * Shows the credit history for the past month.

* Weekly and overall scoreboard

* Adminpanel
  * Change the number of avaible drinks.
  * Calculates for each drink when it probably will be empty.
  * Reset userpasswords.
  * Shows how much money is in the cash box and can be changed if necessary.
  * Export user-, history- and drinkdate to xml-files.
  
* Settings
  * Choose prefered color scheme.
  * Toggle Darkmode.
  * Toggle Notifications.
  * Change password.
  * Change display name.


## How to build

Requirements:
* Java 8
* Maven
* A shell (e.g. cmd, powershell or whatever your system uses)

You can get this Project by copying the following line into your shell 
> git clone https://github.com/isp-insoft-gmbh/MeMate

After downloading the project head to its path and type
> mvn package

into your shell. Maven will then execute your tests and build the application.
When it's done you can find a `./target` folder next to the `./src` folders
of server and client. Those include the newly created jar files called
`*-X.Y-shaded.jar`.

## Troubleshooting

If you encounter any Problem while starting the `.exe` its likely because it couldn't find an installed JRE.
To avoid this problem simply create a folder named `./jre` next to the `.exe`.
You can download a JRE [here](https://github.com/ojdkbuild/ojdkbuild/releases/download/java-1.8.0-openjdk-1.8.0.242-1.b08/java-1.8.0-openjdk-jre-1.8.0.242-1.b08.ojdkbuild.windows.x86_64.zip). Unzip the File inside the `./jre` Folder and you're done. 

## Screenshots
<details>
<summary>
Darkmode
</summary>
<details>
<summary>
Dashboard
</summary>	
	
![Dashboard_Darkmode](https://i.imgur.com/MfXdZ7x.png)	
	
</details>
<details>
<summary>
History
</summary>	
	
![History_Darkmode](https://i.imgur.com/ZeTXwoX.png)	
	
</details>
<details>
<summary>
Consumptionrate
</summary>	
	
![Consumptionrate_Darkmode](https://i.imgur.com/RPXrAjb.png)	
	
</details>
<details>
<summary>
Drinkmanager
</summary>	
	
![Drinkmanager_Darkmode](https://i.imgur.com/E4kdCu3.png)	
	
</details>
</details>



<details>
<summary>
Daymode
</summary>
<details>
<summary>
Dashboard
</summary>	
	
![Dashboard_Daymode](https://i.imgur.com/9Qx41Pn.png)	
	
</details>
<details>
<summary>
History
</summary>	
	
![History_Daymode](https://i.imgur.com/GZhZPPa.png)	
	
</details>
<details>
<summary>
Consumptionrate
</summary>	
	
![Consumptionrate_Daymode](https://i.imgur.com/pbOHEXK.png)	
	
</details>
<details>
<summary>
Drinkmanager
</summary>	
	
![Drinkmanager_Daymode](https://i.imgur.com/UXtCp00.png)
	
</details>
</details>
	

## Credits
Application icon by [Stefania Servidio](https://www.graficheria.it/), with some modifications.
AdminView icon by [WishForgeGames](www.wishforge.games)
Loading GIF by [Icons8](https://icons8.com/preloaders/)
All Charts are created with JFreeChart (http://www.jfree.org/jfreechart/)
