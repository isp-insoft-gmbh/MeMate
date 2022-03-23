package com.isp.memate.actionbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.MeMateUIManager;

import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Eine ActionBar ist eine vertikale ButtonBar, welche hauptsächlich für die
 * Navigation innerhalb der Applikation verwendet wird.
 * <p>
 * Es können beliebig viele Buttons hinzugefüt werden. Diese Buttons benötigen
 * lediglich ein Image, einen Text und eine Action zum Ausführen.
 * </p>
 * Buttons können außerdem durch das Hinzufügen von Separators gruppiert werden.
 * Es kann auch ein Spacer hinzugefüt werden, damit die nachfolgenden
 * Komponenten nach unten gesetzt werden.
 * <p>
 * Jede ActionBar hat noch einen "BurgerButton", welcher die Sichtbarkeit der
 * Labels umschalten kann.
 *
 * @author nwe
 * @since 03.02.2022
 */
public class ActionBar extends VBox
{

  private boolean               showLabels = true;
  private final ActionBarButton burgerButton;

  // Diese Liste enthält alle ActionBarButtons, damit beispielsweise beim Wechseln
  // die Marker aktualisiert werden können
  private final ArrayList<ActionBarButton> buttonList = new ArrayList<>();

  public ActionBar( Stage primaryStage )
  {
    GUIObjects.actionBar = this;
    burgerButton = createBurgerButton();
    prefHeightProperty().bind( primaryStage.heightProperty() );
  }

  /**
   * Der BurgerButton wird mit passendem Icon und passender Action erstellt.
   * 
   * @return den erstellten BurgerButton
   */
  private ActionBarButton createBurgerButton()
  {
    Image icon = null;
    Image pressedicon = null;
    try
    {
      icon = new Image( new FileInputStream( new File( "assets/icons/view_black_24.png" ) ) );
      pressedicon = new Image( new FileInputStream( new File( "assets/icons/view_white_24.png" ) ) );
    }
    catch ( final FileNotFoundException e )
    {
      // TODO(nwe|07.03.2022): Fehlerbehandlung muss noch implementiert werden!
    }
    final ActionBarButton button = new ActionBarButton( icon, pressedicon, "", this, "", () -> toggleLabelVisibility(), true );
    return button;
  }

  /**
   * Fügt einen neuen Button in die Bar hinzu.
   *
   * @param icon Icon des Buttons
   * @param labelText Text des Buttons
   * @param tooltip ToolTipText
   * @param runnable Aktion, welche der Button ausführen soll
   */
  public ActionBarButton addButton( final Image icon, final Image pressedIcon, final String labelText, final String tooltip,
                                    final Runnable runnable )
  {
    final ActionBarButton actionBarButton = new ActionBarButton( icon, pressedIcon, labelText, this, tooltip, runnable, false );
    getChildren().add( actionBarButton );
    buttonList.add( actionBarButton );
    if ( buttonList.size() == 1 )
    {
      actionBarButton.setMarked( true );
    }
    return actionBarButton;
  }

  /**
   * Fügt einen Separator zur ActionBar hinzu.
   */
  public void addSeparator()
  {
    final Separator separator = new Separator();
    getChildren().add( separator );
  }

  /**
   * Fügt space zur ActionBar hinzu. Die nachfolgenden Komponenten werden dann
   * unten in der Bar platziert.
   */
  public void addSpace()
  {
    final Region spacer = new Region();
    setVgrow( spacer, Priority.ALWAYS );
    getChildren().add( spacer );
  }

  /**
   * Schaltet die Sichtbarkeit der Labels um
   */
  private void toggleLabelVisibility()
  {
    showLabels( !showLabels );
  }

  /**
   * Der Marker von allen Buttons wird deaktiviert.
   */
  public void resetMarker()
  {
    for ( final ActionBarButton buttonWithLabel : buttonList )
    {
      buttonWithLabel.setMarked( false );
    }
  }

  /*
   * @return der BurgerButton der ActionBar
   */
  public ActionBarButton getBurgerButton()
  {
    return burgerButton;
  }

  public void showLabels( boolean showLabels )
  {
    this.showLabels = showLabels;
    for ( final ActionBarButton buttonWithLabel : buttonList )
    {
      buttonWithLabel.showLabel( showLabels );
    }
    minWidth( getPrefWidth() );
  }

  public void reverseIcons()
  {
    for ( final ActionBarButton actionBarButton : buttonList )
    {
      actionBarButton.reverseIcons();
    }
    burgerButton.reverseIcons();
  }

  public void updateBackground()
  {
    for ( final ActionBarButton actionBarButton : buttonList )
    {
      actionBarButton.setBackground( MeMateUIManager.getDefaultBackground() );
    }
    burgerButton.setBackground( MeMateUIManager.getDefaultBackground() );
  }
}