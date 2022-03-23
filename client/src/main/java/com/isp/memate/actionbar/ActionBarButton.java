package com.isp.memate.actionbar;

import com.isp.memate.util.MeMateUIManager;
import com.isp.memate.util.PropertyHelper;

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
  private Image           icon;
  private Image           pressedIcon;
  private final ActionBar actionBar;
  private final Runnable  runnable;

  private final Background markerBackground = new Background(
      new BackgroundFill( PropertyHelper.getAppColorProperty(), CornerRadii.EMPTY, Insets.EMPTY ) );

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
    setBackground( MeMateUIManager.getDefaultBackground() );
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
        setBackground( MeMateUIManager.getHoverBackground() );
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
        setBackground( MeMateUIManager.getHoverBackground() );
        if ( !marked )
        {
          markerLabel.setBackground( MeMateUIManager.getHoverBackground() );
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
        setBackground( MeMateUIManager.getDefaultBackground() );
        imageView.setImage( icon );
        if ( !marked )
        {
          markerLabel.setBackground( MeMateUIManager.getDefaultBackground() );
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
        setBackground( MeMateUIManager.getPressedBackground() );
        imageView.setImage( pressedIcon );
        if ( !marked )
        {
          markerLabel.setBackground( MeMateUIManager.getPressedBackground() );
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
        setBackground( MeMateUIManager.getHoverBackground() );
        imageView.setImage( icon );
        if ( !marked )
        {
          markerLabel.setBackground( MeMateUIManager.getHoverBackground() );
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

  public void reverseIcons()
  {
    final Image icon = this.icon;
    this.icon = this.pressedIcon;
    this.pressedIcon = icon;
  }
}
