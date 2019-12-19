# MeMate

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
  * Get some Informations about deposition.

* History
  * Displays every event that occurs in interaction with the application, 
	whether someone consumed a drink or added balance to their account.

* Drinkmanager
  * Allows management of the drink sortiment. (Pricing, names and pictures)
  * Already existing drinks can also be edited or removed.

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

## Credits

Application icon by [Stefania Servidio](https://www.graficheria.it/), with some modifications
Charts in the Stats-Pane are created with JFreeChart (http://www.jfree.org/jfreechart/)

