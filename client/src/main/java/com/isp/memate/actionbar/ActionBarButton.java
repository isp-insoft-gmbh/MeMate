package com.isp.memate.actionbar;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Diese Klasse repräsentiert einen Button für die {@link ActionBar ActionBar}.
 * <p>
 * Der Button zeigt ein Icon mit Text und führt eine mitgegebene Aktion aus.
 * </p>
 *
 * @author nwe
 * @since 03.02.2022
 */
public class ActionBarButton extends HBox
{

  private boolean         marked      = false;
  private final Label     label       = new Label();
  private final Label     markerLabel = new Label();
  private final ImageView imageView   = new ImageView();
  private final Image     icon, pressedIcon;
  private final ActionBar actionBar;
  private final Runnable  runnable;

  private final Background defaultBackground = new Background(
      new BackgroundFill( Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY ) );
  private final Background hoverBackground   = new Background(
      new BackgroundFill( Color.rgb( 169, 169, 169 ), CornerRadii.EMPTY, Insets.EMPTY ) );
  private final Background pressedBackground = new Background(
      new BackgroundFill( Color.rgb( 118, 118, 118 ), CornerRadii.EMPTY, Insets.EMPTY ) );
  private final Background markerBackground  = new Background(
      new BackgroundFill( Color.ROYALBLUE, CornerRadii.EMPTY, Insets.EMPTY ) );

  /**
   * Erstellt einen neuen ActionBarButton
   * 
   * @param icon Icon des Buttons.
   * @param labelText Text des Buttons.
   * @param actionBar Parent-Actionbar
   * @param tooltip ToolTipText
   * @param runnable Aktion, welche der Button ausführen soll.
   */
  public ActionBarButton( final Image icon, final Image pressedIcon, final String labelText, final ActionBar actionBar,
                          final String tooltip,
                          final Runnable runnable, final boolean isBurgerButton )
  {
    this.actionBar = actionBar;
    this.runnable = runnable;
    this.icon = icon;
    this.pressedIcon = pressedIcon;

    imageView.setImage( icon );
    label.setMaxHeight( Double.MAX_VALUE );
    label.setText( labelText );
    label.setStyle( "-fx-font-weight: bold" );

    markerLabel.setMaxHeight( 24 );
    markerLabel.setMinHeight( 24 );
    markerLabel.setMaxWidth( 4 );
    markerLabel.setMinWidth( 4 );

    getChildren().add( markerLabel );
    getChildren().add( imageView );
    if ( !isBurgerButton )
    {
      getChildren().add( label );
    }

    label.managedProperty().bind( label.visibleProperty() );
    imageView.managedProperty().bind( imageView.visibleProperty() );

    if ( !isBurgerButton )
    {
      final Tooltip toolTip = new Tooltip( tooltip );
      Tooltip.install( this, toolTip );
    }

    setSpacing( 5 );
    setPadding( new Insets( 9, 9, 9, 0 ) );
    applyEventHandler( isBurgerButton );
  }

  /**
   * Meldet alle wichigen MouseEvents auf den Button an
   * 
   * @param isBurgerButton gibt an, ob es sich um einen BurgerButton handelt
   */
  private void applyEventHandler( boolean isBurgerButton )
  {
    // Auf MOUSE_CLICKED wird die Aktion des Buttons ausgeführt und die Marker der
    // anderen ActionBarButtons werden zurückgesetzt.
    addEventHandler( MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
    {
      @Override
      public void handle( MouseEvent event )
      {
        setBackground( hoverBackground );
        if ( !isBurgerButton )
        {
          actionBar.resetMarker();
          setMarked( true );
        }
        runnable.run();
        event.consume();
      }
    } );

    // Auf MOUSE_ENTERED wird der hoverBackground gesetzt
    addEventHandler( MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>()
    {
      @Override
      public void handle( MouseEvent event )
      {
        setBackground( hoverBackground );
        if ( !marked )
        {
          markerLabel.setBackground( hoverBackground );
        }
        event.consume();
      }
    } );

    // Auf MOUSE_EXITED wird der defaultBackground gesetzt
    addEventHandler( MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>()
    {
      @Override
      public void handle( MouseEvent event )
      {
        setBackground( defaultBackground );
        imageView.setImage( icon );
        if ( !marked )
        {
          markerLabel.setBackground( defaultBackground );
        }
        event.consume();
      }
    } );

    // Auf MOUSE_PRESSED wird der pressedBackground gesetzt
    addEventHandler( MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
    {
      @Override
      public void handle( MouseEvent event )
      {
        setBackground( pressedBackground );
        imageView.setImage( pressedIcon );
        if ( !marked )
        {
          markerLabel.setBackground( pressedBackground );
        }
        event.consume();
      }
    } );
    // Auf MOUSE_PRESSED wird der pressedBackground gesetzt
    addEventHandler( MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>()
    {
      @Override
      public void handle( MouseEvent event )
      {
        setBackground( hoverBackground );
        imageView.setImage( icon );
        if ( !marked )
        {
          markerLabel.setBackground( hoverBackground );
        }
        event.consume();
      }
    } );
  }

  /**
   * @param show gibt an, ob das Label angezeigt werden soll oder nicht.
   */
  public void showLabel( boolean show )
  {
    label.setVisible( show );
  }

  /**
   * @param marked gibt an ob der Button markiert sein soll oder nicht
   */
  public void setMarked( boolean marked )
  {
    this.marked = marked;
    if ( marked )
    {
      markerLabel.setBackground( markerBackground );
    }
    else
    {
      markerLabel.setBackground( getBackground() );
    }
  }
}
