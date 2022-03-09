package com.isp.memate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.isp.memate.actionbar.ActionBar;
import com.isp.memate.actionbar.ActionBarButton;
import com.isp.memate.panels.ConsumptionRateView;
import com.isp.memate.panels.Dashboard;
import com.isp.memate.panels.History;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.PropertyHelper;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MainFrame extends Stage
{
  private Node             rightNode     = null;
  private final HBox       content       = new HBox();
  private final BorderPane header        = new BorderPane();
  private final Label      usernameLabel = new Label( "" );
  private final Label      balanceLabel  = new Label( "" );

  public MainFrame()
  {
    GUIObjects.mainframe = this;
    setTitle( "MeMate" );
    //    rightComponent.prefHeightProperty().bind( this.heightProperty() );
    //    rightComponent.setMaxWidth( Double.MAX_VALUE );
    //    rightComponent.setMinWidth( 50 );

    Image dashboardIcon = null;
    Image dashboardPressedIcon = null;
    Image historyIcon = null;
    Image historyPressedIcon = null;
    Image consumptionIcon = null;
    Image consumptionPressedIcon = null;
    Image creditHistoryIcon = null;
    Image creditHistoryPressedIcon = null;
    Image scoreboardIcon = null;
    Image scoreboardPressedIcon = null;
    Image undoIcon = null;
    Image undoPressedIcon = null;
    Image settingsIcon = null;
    Image settingsPressedIcon = null;
    Image logoutIcon = null;
    Image logoutPressedIcon = null;
    try
    {
      dashboardIcon = new Image( new FileInputStream( new File( "assets/icons/dashboard_black.png" ) ) );
      dashboardPressedIcon = new Image( new FileInputStream( new File( "assets/icons/dashboard_white.png" ) ) );
      historyIcon = new Image( new FileInputStream( new File( "assets/icons/history_black.png" ) ) );
      historyPressedIcon = new Image( new FileInputStream( new File( "assets/icons/history_white.png" ) ) );
      consumptionIcon = new Image( new FileInputStream( new File( "assets/icons/consumption_black.png" ) ) );
      consumptionPressedIcon = new Image( new FileInputStream( new File( "assets/icons/consumption_white.png" ) ) );
      creditHistoryIcon = new Image( new FileInputStream( new File( "assets/icons/creditHistory_black.png" ) ) );
      creditHistoryPressedIcon = new Image( new FileInputStream( new File( "assets/icons/creditHistory_white.png" ) ) );
      scoreboardIcon = new Image( new FileInputStream( new File( "assets/icons/social_black.png" ) ) );
      scoreboardPressedIcon = new Image( new FileInputStream( new File( "assets/icons/social_white.png" ) ) );
      undoIcon = new Image( new FileInputStream( new File( "assets/icons/back_black.png" ) ) );
      undoPressedIcon = new Image( new FileInputStream( new File( "assets/icons/back_white.png" ) ) );
      settingsIcon = new Image( new FileInputStream( new File( "assets/icons/settings_black.png" ) ) );
      settingsPressedIcon = new Image( new FileInputStream( new File( "assets/icons/settings_white.png" ) ) );
      logoutIcon = new Image( new FileInputStream( new File( "assets/icons/logout_black.png" ) ) );
      logoutPressedIcon = new Image( new FileInputStream( new File( "assets/icons/logout_white.png" ) ) );
    }
    catch ( final FileNotFoundException e )
    {
      // TODO(nwe|07.03.2022): Fehlerbehandlung muss noch implementiert werden!
    }

    final ActionBar bar = new ActionBar( this );
    bar.addButton( dashboardIcon, dashboardPressedIcon, "Dashboard", "Öffnet das Dashboard", new Runnable()
    {
      @Override
      public void run()
      {
        setRightNode( new Dashboard() );
      }
    } );
    bar.addButton( historyIcon, historyPressedIcon, "Historie", "Öffnet die Historie", new Runnable()
    {
      @Override
      public void run()
      {
        setRightNode( new History() );
      }
    } );
    bar.addButton( consumptionIcon, consumptionPressedIcon, "Verbrauchsrate", "Hier können Sie ihren durchschnittlichen Konsum sehen",
        new Runnable()
        {
          @Override
          public void run()
          {
            setRightNode( new ConsumptionRateView() );
          }
        } );
    bar.addButton( creditHistoryIcon, creditHistoryPressedIcon, "Guthabenverlauf", "Hier können Sie den Verlauf ihres Guthabens betrachten",
        new Runnable()
        {
          @Override
          public void run()
          {
            //        setRightNode( new Dashboard() );
          }
        } );
    bar.addButton( scoreboardIcon, scoreboardPressedIcon, "Scoreboard", "Öffnet das Scoreboard",
        new Runnable()
        {
          @Override
          public void run()
          {
            //        setRightNode( new Dashboard() );
          }
        } );
    bar.addSpace();
    final ActionBarButton undoButton =
        bar.addButton( undoIcon, undoPressedIcon, "Rückgängig", "Letzte Aktion rückgänig machen", new Runnable()
        {
          @Override
          public void run()
          {
            //        setRightNode( new Dashboard() );
          }
        } );
    undoButton.setDisable( true );
    bar.addButton( settingsIcon, settingsPressedIcon, "Einstellungen", "Öffnet die Einstellungen", new Runnable()
    {
      @Override
      public void run()
      {
        //        setRightNode( new Dashboard() );
      }
    } );
    bar.addButton( logoutIcon, logoutPressedIcon, "Logout", "Ausloggen", new Runnable()
    {
      @Override
      public void run()
      {
        //        setRightNode( new Dashboard() );
      }
    } );
    bar.showLabels( false );

    initHeader( bar.getBurgerButton() );

    final VBox vBox = new VBox();
    HBox.setHgrow( bar, Priority.NEVER );
    content.getChildren().add( bar );

    vBox.getChildren().add( header );
    vBox.getChildren().add( content );
    setRightNode( new Dashboard() );

    setScene( new Scene( vBox ) );
    setHeight( 800 );
    setWidth( 1200 );
  }

  private void initHeader( ActionBarButton burgerButton )
  {
    usernameLabel.setFont( new Font( 16 ) );
    balanceLabel.setFont( new Font( 16 ) );
    usernameLabel.setTextAlignment( TextAlignment.CENTER );
    balanceLabel.setTextAlignment( TextAlignment.CENTER );
    usernameLabel.prefHeightProperty().bind( header.heightProperty() );
    balanceLabel.prefHeightProperty().bind( header.heightProperty() );

    final BorderPane rightHeader = new BorderPane();
    rightHeader.setPadding( new Insets( 0, 5, 0, 5 ) );
    rightHeader.setLeft( usernameLabel );
    rightHeader.setRight( balanceLabel );
    header.setLeft( burgerButton );
    header.setCenter( rightHeader );
    rightHeader
        .setBackground( new Background( new BackgroundFill( PropertyHelper.getAppColorProperty(), CornerRadii.EMPTY, Insets.EMPTY ) ) );
  }

  private void setRightNode( Node node )
  {
    content.getChildren().remove( rightNode );
    rightNode = node;
    content.getChildren().add( rightNode );
    HBox.setHgrow( rightNode, Priority.ALWAYS );
  }

  public void updateBalanceLabel( Float newValue )
  {
    System.out.println( "UPDATE BALANCE" );
    System.out.println( newValue );
    balanceLabel.setText( String.format( "Kontostand: %.2f€", newValue ) );
  }

  public void setHelloLabelText( String newValue )
  {
    usernameLabel.setText( "Hallo " + newValue );

  }

  public void setUndoButtonEnabled( boolean enabled )
  {
    // TODO(nwe|07.03.2022): Methode muss noch implementiert werden!

  }
}