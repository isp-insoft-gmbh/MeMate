/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.isp.memate.Shared.Operation;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.DarkSpinnerUI;
import com.isp.memate.util.DaySpinnerUI;
import com.isp.memate.util.MeMateUIManager;

/**
 * Der DrinkManagerDialog erzeugt einen neuen Frame,
 * wenn im Drinkmanager ein Getränk hinzugefügt oder bearbeitet wird.
 * In diesem Frame kann man den Name, Preis, und ein Bild des Getränks angeben oder editieren.
 * Des weiteren gibt es einen Dialog um Getränkeinformationen zu ergänzen.
 *
 * @author nwe
 * @since 18.10.2019
 */
class DrinkManagerDialog
{
  private final SpinnerNumberModel spinnerModel      = new SpinnerNumberModel( 0, 0, 1000, 0.10 );
  private final JPanel             layout            = new JPanel( new GridBagLayout() );
  private final JSpinner           drinkPriceSpinner = new JSpinner( spinnerModel );
  private final JButton            cancelButton      = MeMateUIManager.createButton( "button", "Abbrechen" );
  private final JFileChooser       fileChooser       = new JFileChooser();
  private final JTextField         drinkNameField    = new JTextField();
  private final JButton            confirmButton     = MeMateUIManager.createButton( "button" );
  private final JLabel             pictureLabel      = new JLabel();
  private String                   drinkPicturePath  = null;
  private ImageIcon                currentImage;
  private JDialog                  dialog;

  /**
   * Erzeugt den Frame und setzt das Layout der vorhandenen Kompnenten.
   *
   * @param owner Parent für den aufzurufenden Dialog
   */
  DrinkManagerDialog( final Window owner )
  {
    final JLabel drinkName = new JLabel( "Name" );
    final JLabel drinkPrice = new JLabel( "Preis" );
    layout.setBorder( new EmptyBorder( 5, 10, 5, 10 ) );

    fileChooser.setFileFilter( new FileNameExtensionFilter( "Bilder", "jpg", "png", "gif" ) );

    final GridBagConstraints drinkNameConstraints = new GridBagConstraints();
    drinkNameConstraints.gridx = 1;
    drinkNameConstraints.gridy = 0;
    drinkNameConstraints.weightx = 1;
    drinkNameConstraints.insets = new Insets( 10, 0, 0, 0 );
    drinkNameConstraints.anchor = GridBagConstraints.LINE_START;
    layout.add( drinkName, drinkNameConstraints );
    final GridBagConstraints drinkNameFieldConstraints = new GridBagConstraints();
    drinkNameFieldConstraints.gridx = 1;
    drinkNameFieldConstraints.gridy = 1;
    drinkNameFieldConstraints.gridwidth = 2;
    drinkNameFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
    drinkNameFieldConstraints.insets = new Insets( 5, 0, 0, 0 );
    layout.add( drinkNameField, drinkNameFieldConstraints );

    final GridBagConstraints drinkPriceConstraints = new GridBagConstraints();
    drinkPriceConstraints.gridx = 1;
    drinkPriceConstraints.gridy = 2;
    drinkPriceConstraints.anchor = GridBagConstraints.LINE_START;
    drinkPriceConstraints.insets = new Insets( 15, 0, 0, 0 );
    layout.add( drinkPrice, drinkPriceConstraints );
    final GridBagConstraints drinkPriceSpinnerConstraints = new GridBagConstraints();
    drinkPriceSpinnerConstraints.gridx = 1;
    drinkPriceSpinnerConstraints.gridy = 3;
    drinkPriceSpinnerConstraints.gridwidth = 2;
    drinkPriceSpinnerConstraints.anchor = GridBagConstraints.LINE_START;
    drinkPriceSpinnerConstraints.fill = GridBagConstraints.HORIZONTAL;
    drinkPriceSpinnerConstraints.insets = new Insets( 5, 0, 0, 0 );
    layout.add( drinkPriceSpinner, drinkPriceSpinnerConstraints );

    final GridBagConstraints drinkPicturePreviewConstraints = new GridBagConstraints();
    drinkPicturePreviewConstraints.gridx = 0;
    drinkPicturePreviewConstraints.gridy = 0;
    drinkPicturePreviewConstraints.gridheight = 5;
    drinkPicturePreviewConstraints.weightx = 0.4;
    layout.add( pictureLabel, drinkPicturePreviewConstraints );

    final JPanel buttonBar = new JPanel();
    buttonBar.setLayout( new BoxLayout( buttonBar, BoxLayout.X_AXIS ) );
    buttonBar.add( confirmButton );
    buttonBar.add( Box.createHorizontalStrut( 5 ) );
    buttonBar.add( cancelButton );
    final GridBagConstraints buttonBarConstraints = new GridBagConstraints();
    buttonBarConstraints.gridx = 1;
    buttonBarConstraints.gridy = 5;
    buttonBarConstraints.gridwidth = 2;
    buttonBarConstraints.insets = new Insets( 5, 0, 0, 0 );
    buttonBarConstraints.anchor = GridBagConstraints.LINE_END;
    layout.add( buttonBar, buttonBarConstraints );

    dialog = new JDialog( owner );
    final JRootPane rootPane = dialog.getRootPane();
    final String quitDialogActionName = "QUIT_DIALOG";
    rootPane.getActionMap().put( quitDialogActionName, new AbstractAction()
    {
      @Override
      public void actionPerformed( final ActionEvent __ )
      {
        dialog.dispose();
      }
    } );
    rootPane.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW )
        .put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ), quitDialogActionName );

    cancelButton.addActionListener( __ -> dialog.dispose() );

    pictureLabel.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseExited( final MouseEvent e )
      {
        pictureLabel.setIcon( currentImage );
      }

      @Override
      public void mouseEntered( final MouseEvent e )
      {
        pictureLabel.setIcon( getEditIcon() );
      }

      @Override
      public void mouseClicked( final MouseEvent e )
      {
        fileChooser.showOpenDialog( owner );
        final File selectedFile = fileChooser.getSelectedFile();
        try
        {
          if ( selectedFile == null )
          {
            pictureLabel.setIcon( currentImage );
            return;
          }
          if ( selectedFile.length() / 1024 > 1000 )
          {
            JOptionPane.showMessageDialog( owner, "Die ausgewählte Bilddatei darf die Dateigröße von 1MB nicht überschreiten",
                "Bildauswahl fehlgeschlagen",
                JOptionPane.ERROR_MESSAGE, null );
            return;
          }
          if ( ImageIO.read( selectedFile ) == null )
          {
            JOptionPane.showMessageDialog( owner, "Bitte wählen Sie eine Bilddatei aus.", "Bildauswahl fehlgeschlagen",
                JOptionPane.ERROR_MESSAGE, null );
            return;
          }
        }
        catch ( HeadlessException | IOException exception )
        {
          ClientLog.newLog( exception.getMessage() );
        }
        drinkPicturePath = selectedFile.getPath();
        final File image = new File( drinkPicturePath );
        final ImageIcon drinkIcon = new ImageIcon( new ImageIcon( image.getPath() ).getImage() );

        final Image drinkImage = drinkIcon.getImage();
        Image scaledImage;

        if ( drinkIcon.getIconHeight() > 140 || drinkIcon.getIconWidth() > 150 )
        {
          final double scale = 140.0 / drinkIcon.getIconHeight();
          final int height = 140;
          int width = (int) (drinkIcon.getIconWidth() * scale);
          if ( width > 150 )
          {
            width = 150;
          }
          scaledImage = drinkImage.getScaledInstance( width, height, Image.SCALE_SMOOTH );
          pictureLabel.setIcon( new ImageIcon( scaledImage ) );
        }
        else
        {
          scaledImage = drinkIcon.getImage().getScaledInstance( 42, 132, Image.SCALE_SMOOTH );
          pictureLabel.setIcon( new ImageIcon( scaledImage ) );
        }
        currentImage = new ImageIcon( scaledImage );
      }
    } );


    SwingUtilities.getRootPane( dialog ).setDefaultButton( confirmButton );
    dialog.add( layout );
    dialog.pack();
    final Dimension oldPreferredSize = dialog.getPreferredSize();
    dialog.setSize( new Dimension( oldPreferredSize.width + 300, oldPreferredSize.height ) );
    dialog.setModal( true );
    dialog.setResizable( false );
    dialog.setLocationRelativeTo( dialog.getOwner() );

    confirmButton.setContentAreaFilled( false );
    confirmButton.setOpaque( true );
    toggleDarkMode( drinkName, drinkPrice, buttonBar );
  }

  private void toggleDarkMode( final JLabel drinkName, final JLabel drinkPrice, final JPanel buttonBar )
  {
    if ( MeMateUIManager.getDarkModeState() )
    {
      layout.setBackground( MeMateUIManager.getBackground( "default" ).getDarkColor() );
      buttonBar.setBackground( MeMateUIManager.getBackground( "default" ).getDarkColor() );
      drinkName.setForeground( Color.white );
      drinkPrice.setForeground( Color.white );
      confirmButton.setBackground( MeMateUIManager.getBackground( "button" ).getDarkColor() );
      confirmButton.setForeground( MeMateUIManager.getForeground( "button" ).getDarkColor() );
    }
    else
    {
      layout.setBackground( new Color( 240, 240, 240 ) );
      buttonBar.setBackground( new Color( 240, 240, 240 ) );
      drinkName.setForeground( Color.black );
      drinkPrice.setForeground( Color.black );
      confirmButton.setBackground( MeMateUIManager.getBackground( "button" ).getDayColor() );
      confirmButton.setForeground( MeMateUIManager.getForeground( "button" ).getDayColor() );
    }
  }

  /**
   * Erzeugt ein neues Icon, welches aus dem Bild des Getränks und dem Bearbeiten-Icon besteht.
   *
   * @return das neue Icon.
   */
  private Icon getEditIcon()
  {
    currentImage = (ImageIcon) pictureLabel.getIcon();
    final BufferedImage image = new BufferedImage( currentImage.getIconWidth(), currentImage.getIconHeight(), BufferedImage.TYPE_INT_ARGB );
    final Graphics gr = image.createGraphics();
    currentImage.paintIcon( null, gr, 0, 0 );
    gr.dispose();
    try
    {
      final BufferedImage overlay = ImageIO.read( getClass().getClassLoader().getResourceAsStream( "edit.png" ) );
      final int w = Math.max( image.getWidth(), overlay.getWidth() );
      final int h = Math.max( image.getHeight(), overlay.getHeight() );
      final BufferedImage combined = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
      final Graphics g = combined.getGraphics();
      g.drawImage( image, 0, 0, null );
      g.drawImage( overlay, 0, 0, null );
      return new ImageIcon( combined );
    }
    catch ( final IOException exception )
    {
      ClientLog.newLog( "Das Bild zum Bearbeiten von Getränken konnte nicht erzeugt werden" );
      ClientLog.newLog( exception.getMessage() );
    }
    return currentImage;
  }

  /**
   * Zeigt einen Dialog zum Bearbeiten der Getränke an. Wenn man die Änderungen speichern
   * möchte, werden alle Eingaben auf ihre Gültigkeit geprüft und anschließend an
   * {@link ServerCommunication} geschickt.
   *
   * @param drink ausgewähltes Getränk
   */
  void showEditDialog( final String drink )
  {
    if ( MeMateUIManager.getDarkModeState() )
    {
      drinkPriceSpinner.setUI( new DarkSpinnerUI() );
      drinkPriceSpinner.setBackground( MeMateUIManager.getBackground( "spinner" ).getDarkColor() );
      drinkPriceSpinner
          .setBorder( BorderFactory.createLineBorder( MeMateUIManager.getBackground( "spinner" ).getDarkColor(), 1 ) );
      drinkNameField.setBackground( MeMateUIManager.getBackground( "spinner" ).getDarkColor() );
      drinkNameField.setForeground( Color.white );
      drinkNameField
          .setBorder( BorderFactory.createLineBorder( MeMateUIManager.getBackground( "spinner" ).getDarkColor().brighter(), 1 ) );
    }
    else
    {
      drinkPriceSpinner.setUI( new DaySpinnerUI() );
      drinkPriceSpinner.setBackground( MeMateUIManager.getBackground( "spinner" ).getDayColor() );
      drinkPriceSpinner.setBorder( BorderFactory.createLineBorder( MeMateUIManager.getBackground( "spinner" ).getDayColor().darker(), 1 ) );
      drinkNameField.setBackground( Color.white );
      drinkNameField.setForeground( Color.black );
      drinkNameField.setBorder( BorderFactory.createLineBorder( MeMateUIManager.getBackground( "spinner" ).getDayColor().darker(), 1 ) );
    }
    MeMateUIManager.registerSpinner( drinkPriceSpinner );
    dialog.setTitle( "Getränk bearbeiten" );
    confirmButton.setText( "Speichern" );
    final String oldName = drink;
    final Float oldPrice = ServerCommunication.getInstance().getPrice( drink );
    drinkNameField.setText( oldName );
    drinkPriceSpinner.setValue( oldPrice );


    final ImageIcon drinkIcon = ServerCommunication.getInstance().getIcon( drink );
    final Image drinkImage = drinkIcon.getImage();
    Image scaledImage;

    if ( drinkIcon.getIconHeight() > 140 || drinkIcon.getIconWidth() > 150 )
    {
      final double scale = 140.0 / drinkIcon.getIconHeight();
      final int height = 140;
      int width = (int) (drinkIcon.getIconWidth() * scale);
      if ( width > 150 )
      {
        width = 150;
      }
      scaledImage = drinkImage.getScaledInstance( width, height, Image.SCALE_SMOOTH );
      pictureLabel.setIcon( new ImageIcon( scaledImage ) );
    }
    else
    {
      pictureLabel.setIcon( new ImageIcon(
          ServerCommunication.getInstance().getIcon( drink ).getImage().getScaledInstance( 42, 132, Image.SCALE_SMOOTH ) ) );
    }


    confirmButton.addActionListener( e ->
    {
      final String newName = drinkNameField.getText();
      final String newPicture = drinkPicturePath;
      final String newPriceAsString = String.valueOf( drinkPriceSpinner.getValue() );
      final Float newPrice = Float.valueOf( newPriceAsString );

      if ( newName.isEmpty() || newName.trim().length() == 0 )
      {
        JOptionPane.showMessageDialog( dialog, "Bitte Getränkenamen eingeben.", "Getränk bearbeiten fehlgeschlagen",
            JOptionPane.ERROR_MESSAGE, null );
      }
      else if ( newPrice.equals( 0f ) )
      {
        JOptionPane.showMessageDialog( dialog, "Bitte gültigen Preis für das Getränk angeben.", "Getränk bearbeiten fehlgeschlagen",
            JOptionPane.ERROR_MESSAGE, null );
      }
      else
      {
        final Integer id = ServerCommunication.getInstance().getID( oldName );
        if ( drinkPicturePath != null )
        {
          BufferedImage bImage;
          final ByteArrayOutputStream bos = new ByteArrayOutputStream();
          try
          {
            bImage = ImageIO.read( new File( newPicture ) );
            ImageIO.write( bImage, "png", bos );
          }
          catch ( final IOException exception )
          {
            ClientLog.newLog( "Das ausgewählte Bild konnte nicht gespeichert werden." );
            ClientLog.newLog( exception.getMessage() );
          }
          final byte[] bytes = bos.toByteArray();
          ServerCommunication.getInstance().updateDrinkInformations( id, Operation.UPDATE_DRINKPICTURE, bytes );
        }
        if ( !newPrice.equals( oldPrice ) )
        {
          ServerCommunication.getInstance().updateDrinkInformations( id, Operation.UPDATE_DRINKPRICE, newPrice );
        }
        if ( !newName.equals( oldName ) )
        {
          ServerCommunication.getInstance().updateDrinkInformations( id, Operation.UPDATE_DRINKNAME, newName );
        }
        dialog.dispose();
        Mainframe.getInstance().getDrinkManager().updateList();
      }
    } );
    final Dimension oldPreferredSize = dialog.getPreferredSize();
    dialog.setSize( new Dimension( oldPreferredSize.width + 150, oldPreferredSize.height ) );
    dialog.setLocationRelativeTo( dialog.getOwner() );
    dialog.setVisible( true );
  }

  /**
   * Zeigt einen Dialog an, in welchem man ein neues Getränk registrieren kann.
   * Es wird ein Name, Preis und Bild als Eingabe gefordert.
   * Sind die Eingaben korrekt, so werden diese an der Server weitergeleitet.
   */
  void showNewDialog()
  {
    if ( MeMateUIManager.getDarkModeState() )
    {
      pictureLabel.setIcon( new ImageIcon( new ImageIcon( getClass().getClassLoader().getResource( "placeholder2.png" ) ).getImage()
          .getScaledInstance( 42, 132, Image.SCALE_SMOOTH ) ) );
      drinkPriceSpinner.setUI( new DarkSpinnerUI() );
      drinkPriceSpinner.setBackground( MeMateUIManager.getBackground( "spinner" ).getDarkColor() );
      drinkPriceSpinner
          .setBorder( BorderFactory.createLineBorder( MeMateUIManager.getBackground( "spinner" ).getDarkColor(), 1 ) );
      drinkNameField.setBackground( MeMateUIManager.getBackground( "spinner" ).getDarkColor() );
      drinkNameField.setForeground( Color.white );
      drinkNameField
          .setBorder( BorderFactory.createLineBorder( MeMateUIManager.getBackground( "spinner" ).getDarkColor().brighter(), 1 ) );
    }
    else
    {
      pictureLabel.setIcon( new ImageIcon( new ImageIcon( getClass().getClassLoader().getResource( "placeholder.png" ) ).getImage()
          .getScaledInstance( 42, 132, Image.SCALE_SMOOTH ) ) );
      drinkPriceSpinner.setUI( new DaySpinnerUI() );
      drinkPriceSpinner.setBackground( MeMateUIManager.getBackground( "spinner" ).getDayColor() );
      drinkPriceSpinner.setBorder( BorderFactory.createLineBorder( MeMateUIManager.getBackground( "spinner" ).getDayColor().darker(), 1 ) );
      drinkNameField.setBackground( Color.white );
      drinkNameField.setForeground( Color.black );
      drinkNameField.setBorder( BorderFactory.createLineBorder( MeMateUIManager.getBackground( "spinner" ).getDayColor().darker(), 1 ) );
    }
    MeMateUIManager.registerSpinner( drinkPriceSpinner );
    dialog.setTitle( "Getränk hinzufügen" );
    confirmButton.setText( "Hinzufügen" );
    confirmButton.addActionListener( __ ->
    {
      final String name = drinkNameField.getText();
      final String priceAsString = String.valueOf( drinkPriceSpinner.getValue() );
      final Float price = Float.valueOf( priceAsString );

      if ( name.isEmpty() || name.trim().length() == 0 )
      {
        JOptionPane.showMessageDialog( dialog, "Bitte Getränkenamen eingeben.", "Getränk hinzufügen fehlgeschlagen",
            JOptionPane.ERROR_MESSAGE, null );
      }
      else if ( drinkPicturePath == null )
      {
        JOptionPane.showMessageDialog( dialog, "Bitte Bild des Getränks auswählen.", "Getränk hinzufügen fehlgeschlagen",
            JOptionPane.ERROR_MESSAGE, null );
      }
      else if ( price.equals( 0f ) )
      {
        JOptionPane.showMessageDialog( dialog, "Bitte gültigen Preis für das Getränk angeben.", "Getränk hinzufügen fehlgeschlagen",
            JOptionPane.ERROR_MESSAGE, null );
      }
      else
      {
        BufferedImage bImage;
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
          bImage = ImageIO.read( new File( drinkPicturePath ) );
          ImageIO.write( bImage, "png", bos );
        }
        catch ( final IOException exception )
        {
          ClientLog.newLog( "Das ausgewählte Bild konnte nicht gespeichert werden." );
          ClientLog.newLog( exception.getMessage() );
        }
        final byte[] bytes = bos.toByteArray();
        ServerCommunication.getInstance()
            .registerNewDrink( new Drink( name, price, drinkPicturePath, -1, bytes, 0, false, null ) );
        dialog.dispose();
        Mainframe.getInstance().getDrinkManager().updateList();
      }
    } );
    final Dimension oldPreferredSize = dialog.getPreferredSize();
    dialog.setSize( new Dimension( oldPreferredSize.width + 150, oldPreferredSize.height ) );
    dialog.setLocationRelativeTo( dialog.getOwner() );
    dialog.setVisible( true );
  }

  /**
   * Zeigt einen Dialog an, um Informationen über das Getränk zu ergänzen.
   * Beispielsweise Zutatenliste, Fettgehalt oder Zuckergehalt.
   */
  void showIngredientsDialog( final int DrinkID )
  {
    layout.removeAll();
    dialog.setTitle( "Inhaltsstoffe hinzufügen" );
    confirmButton.setText( "Hinzufügen" );

    final GridBagConstraints ingredientsLabelConstraints = new GridBagConstraints();
    ingredientsLabelConstraints.gridx = 0;
    ingredientsLabelConstraints.gridy = 0;
    final JLabel ingredientsLabel = new JLabel( "Zutaten" );
    layout.add( ingredientsLabel, ingredientsLabelConstraints );
    final JTextField ingredientsField = new JTextField();
    ingredientsField.setPreferredSize( new Dimension( 200, 20 ) );
    final GridBagConstraints ingredientsFieldConstraints = new GridBagConstraints();
    ingredientsFieldConstraints.gridx = 1;
    ingredientsFieldConstraints.gridy = 0;
    layout.add( ingredientsField, ingredientsFieldConstraints );

    final GridBagConstraints energyKJLabelConstraints = new GridBagConstraints();
    energyKJLabelConstraints.gridx = 0;
    energyKJLabelConstraints.gridy = 1;
    energyKJLabelConstraints.insets = new Insets( 5, 0, 0, 0 );
    final JLabel energyKJLabel = new JLabel( "Energie kJ" );
    layout.add( energyKJLabel, energyKJLabelConstraints );
    final SpinnerModel energykJModel = new SpinnerNumberModel( 0, 0, 500, 1 );
    final JSpinner energykJSpinner = new JSpinner( energykJModel );
    MeMateUIManager.registerSpinner( energykJSpinner );
    final GridBagConstraints energykJSpinnerConstraints = new GridBagConstraints();
    energykJSpinnerConstraints.gridx = 1;
    energykJSpinnerConstraints.gridy = 1;
    energykJSpinnerConstraints.insets = new Insets( 5, 0, 0, 0 );
    energykJSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( energykJSpinner, energykJSpinnerConstraints );

    final GridBagConstraints energyKCALLabelConstraints = new GridBagConstraints();
    energyKCALLabelConstraints.gridx = 0;
    energyKCALLabelConstraints.gridy = 2;
    energyKCALLabelConstraints.insets = new Insets( 5, 0, 0, 0 );
    final JLabel energyKCALLabel = new JLabel( "Energie kcal" );
    layout.add( energyKCALLabel, energyKCALLabelConstraints );
    final SpinnerModel energykCALModel = new SpinnerNumberModel( 0, 0, 500, 1 );
    final JSpinner energykCALSpinner = new JSpinner( energykCALModel );
    MeMateUIManager.registerSpinner( energykCALSpinner );
    final GridBagConstraints energykCALSpinnerConstraints = new GridBagConstraints();
    energykCALSpinnerConstraints.gridx = 1;
    energykCALSpinnerConstraints.gridy = 2;
    energykCALSpinnerConstraints.insets = new Insets( 5, 0, 0, 0 );
    energykCALSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( energykCALSpinner, energykCALSpinnerConstraints );

    final GridBagConstraints fatLabelConstraints = new GridBagConstraints();
    fatLabelConstraints.gridx = 0;
    fatLabelConstraints.gridy = 3;
    fatLabelConstraints.insets = new Insets( 5, 0, 0, 0 );
    final JLabel fatLabel = new JLabel( "Fett" );
    layout.add( fatLabel, fatLabelConstraints );
    final SpinnerModel fatModel = new SpinnerNumberModel( 0, 0, 50, 0.1 );
    final JSpinner fatSpinner = new JSpinner( fatModel );
    MeMateUIManager.registerSpinner( fatSpinner );
    final GridBagConstraints fatSpinnerConstraints = new GridBagConstraints();
    fatSpinnerConstraints.gridx = 1;
    fatSpinnerConstraints.gridy = 3;
    fatSpinnerConstraints.insets = new Insets( 5, 0, 0, 0 );
    fatSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( fatSpinner, fatSpinnerConstraints );

    final GridBagConstraints fattyAcidsLabelConstraints = new GridBagConstraints();
    fattyAcidsLabelConstraints.gridx = 0;
    fattyAcidsLabelConstraints.gridy = 4;
    fattyAcidsLabelConstraints.insets = new Insets( 5, 0, 0, 0 );
    final JLabel fattyAcidsLabel = new JLabel( "gesättigte Fettsäuren" );
    layout.add( fattyAcidsLabel, fattyAcidsLabelConstraints );
    final SpinnerModel fattyAcidsModel = new SpinnerNumberModel( 0, 0, 50, 0.1 );
    final JSpinner fattyAcidsSpinner = new JSpinner( fattyAcidsModel );
    MeMateUIManager.registerSpinner( fattyAcidsSpinner );
    final GridBagConstraints fattyAcidsSpinnerConstraints = new GridBagConstraints();
    fattyAcidsSpinnerConstraints.gridx = 1;
    fattyAcidsSpinnerConstraints.gridy = 4;
    fattyAcidsSpinnerConstraints.insets = new Insets( 5, 0, 0, 0 );
    fattyAcidsSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( fattyAcidsSpinner, fattyAcidsSpinnerConstraints );

    final GridBagConstraints carbsLabelConstraints = new GridBagConstraints();
    carbsLabelConstraints.gridx = 0;
    carbsLabelConstraints.gridy = 5;
    carbsLabelConstraints.insets = new Insets( 5, 0, 0, 0 );
    final JLabel carbsLabel = new JLabel( "Kohlenhydrate" );
    layout.add( carbsLabel, carbsLabelConstraints );
    final SpinnerModel carbsModel = new SpinnerNumberModel( 0, 0, 50, 0.1 );
    final JSpinner carbsSpinner = new JSpinner( carbsModel );
    MeMateUIManager.registerSpinner( carbsSpinner );
    final GridBagConstraints carbsSpinnerConstraints = new GridBagConstraints();
    carbsSpinnerConstraints.gridx = 1;
    carbsSpinnerConstraints.gridy = 5;
    carbsSpinnerConstraints.insets = new Insets( 5, 0, 0, 0 );
    carbsSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( carbsSpinner, carbsSpinnerConstraints );

    final GridBagConstraints sugarLabelConstraints = new GridBagConstraints();
    sugarLabelConstraints.gridx = 0;
    sugarLabelConstraints.gridy = 6;
    sugarLabelConstraints.insets = new Insets( 5, 0, 0, 0 );
    final JLabel sugarLabel = new JLabel( "davon Zucker" );
    layout.add( sugarLabel, sugarLabelConstraints );
    final SpinnerModel sugarModel = new SpinnerNumberModel( 0, 0, 50, 0.1 );
    final JSpinner sugarSpinner = new JSpinner( sugarModel );
    MeMateUIManager.registerSpinner( sugarSpinner );
    final GridBagConstraints sugarSpinnerConstraints = new GridBagConstraints();
    sugarSpinnerConstraints.gridx = 1;
    sugarSpinnerConstraints.gridy = 6;
    sugarSpinnerConstraints.insets = new Insets( 5, 0, 0, 0 );
    sugarSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( sugarSpinner, sugarSpinnerConstraints );

    final GridBagConstraints proteinLabelConstraints = new GridBagConstraints();
    proteinLabelConstraints.gridx = 0;
    proteinLabelConstraints.gridy = 7;
    proteinLabelConstraints.insets = new Insets( 5, 0, 0, 0 );
    final JLabel proteinLabel = new JLabel( "Eiweiß" );
    layout.add( proteinLabel, proteinLabelConstraints );
    final SpinnerModel proteinModel = new SpinnerNumberModel( 0, 0, 50, 0.1 );
    final JSpinner proteinSpinner = new JSpinner( proteinModel );
    MeMateUIManager.registerSpinner( proteinSpinner );
    final GridBagConstraints proteinSpinnerConstraints = new GridBagConstraints();
    proteinSpinnerConstraints.gridx = 1;
    proteinSpinnerConstraints.gridy = 7;
    proteinSpinnerConstraints.insets = new Insets( 5, 0, 0, 0 );
    proteinSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( proteinSpinner, proteinSpinnerConstraints );

    final GridBagConstraints saltLabelConstraints = new GridBagConstraints();
    saltLabelConstraints.gridx = 0;
    saltLabelConstraints.gridy = 8;
    saltLabelConstraints.insets = new Insets( 5, 0, 0, 0 );
    final JLabel saltLabel = new JLabel( "Salz" );
    layout.add( saltLabel, saltLabelConstraints );
    final SpinnerModel saltModel = new SpinnerNumberModel( 0, 0, 50, 0.1 );
    final JSpinner saltSpinner = new JSpinner( saltModel );
    MeMateUIManager.registerSpinner( saltSpinner );
    final GridBagConstraints saltSpinnerConstraints = new GridBagConstraints();
    saltSpinnerConstraints.gridx = 1;
    saltSpinnerConstraints.gridy = 8;
    saltSpinnerConstraints.insets = new Insets( 5, 0, 0, 0 );
    saltSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( saltSpinner, saltSpinnerConstraints );

    final JPanel buttonBar = new JPanel();
    buttonBar.setLayout( new BoxLayout( buttonBar, BoxLayout.X_AXIS ) );
    buttonBar.add( confirmButton );
    buttonBar.add( Box.createHorizontalStrut( 5 ) );
    buttonBar.add( cancelButton );
    final GridBagConstraints buttonBarConstraints = new GridBagConstraints();
    buttonBarConstraints.gridx = 1;
    buttonBarConstraints.gridy = 9;
    buttonBarConstraints.gridwidth = 2;
    buttonBarConstraints.insets = new Insets( 10, 0, 0, 0 );
    buttonBarConstraints.anchor = GridBagConstraints.LINE_END;
    layout.add( buttonBar, buttonBarConstraints );

    toggleDarkMode( ingredientsLabel, ingredientsField, energyKJLabel, energyKCALLabel, fatLabel, fattyAcidsLabel, carbsLabel, sugarLabel,
        proteinLabel,
        saltLabel, buttonBar, energykJSpinner, energykCALSpinner, fatSpinner, fattyAcidsSpinner, carbsSpinner, sugarSpinner, proteinSpinner,
        saltSpinner );

    final ActionListener[] listeners = confirmButton.getActionListeners();
    for ( final ActionListener actionListener : listeners )
    {
      confirmButton.removeActionListener( actionListener );
    }

    confirmButton.addActionListener( e ->
    {

      //TODO implement Illegal Argument Check

      if ( energykJSpinner.getValue() instanceof Number || energykCALSpinner.getValue() instanceof Number
          || fatSpinner.getValue() instanceof Number || fattyAcidsSpinner.getValue() instanceof Number
          || carbsSpinner.getValue() instanceof Number || sugarSpinner.getValue() instanceof Number
          || proteinSpinner.getValue() instanceof Number || saltSpinner.getValue() instanceof Number )
      {
        ServerCommunication.getInstance().registerIngredients( new DrinkIngredients( DrinkID,
            ingredientsField.getText(), (int) energykJSpinner.getValue(), (int) energykCALSpinner.getValue(),
            (Double) fatSpinner.getValue(),
            (Double) fattyAcidsSpinner.getValue(),
            (Double) carbsSpinner.getValue(), (Double) sugarSpinner.getValue(),
            (Double) proteinSpinner.getValue(), (Double) saltSpinner.getValue() ) );
        dialog.dispose();
      }
      else
      {
        JOptionPane.showConfirmDialog( dialog, "Ungültige Eingabe" );
      }
    } );


    final Dimension oldPreferredSize = dialog.getPreferredSize();
    dialog.setSize( new Dimension( oldPreferredSize.width, oldPreferredSize.height ) );
    dialog.setLocationRelativeTo( dialog.getOwner() );
    dialog.setVisible( true );
  }

  /**
   * Ändert den Darkmodestate aller mitgegebenen Komponenten.
   */
  private void toggleDarkMode( final JLabel ingredientsLabel, final JTextField ingredientsField, final JLabel energyKJLabel,
                               final JLabel energyKCALLabel,
                               final JLabel fatLabel,
                               final JLabel fattyAcidsLabel, final JLabel carbsLabel, final JLabel sugarLabel, final JLabel proteinLabel,
                               final JLabel saltLabel,
                               final JPanel buttonBar, final JSpinner energykJSpinner, final JSpinner energykCALSpinner,
                               final JSpinner fatSpinner,
                               final JSpinner fattyAcidsSpinner, final JSpinner carbsSpinner, final JSpinner sugarSpinner,
                               final JSpinner proteinSpinner,
                               final JSpinner saltSpinner )
  {
    final ArrayList<JSpinner> spinnerList = new ArrayList<>();
    spinnerList.add( energykJSpinner );
    spinnerList.add( energykCALSpinner );
    spinnerList.add( fatSpinner );
    spinnerList.add( fattyAcidsSpinner );
    spinnerList.add( carbsSpinner );
    spinnerList.add( sugarSpinner );
    spinnerList.add( proteinSpinner );
    spinnerList.add( saltSpinner );
    if ( MeMateUIManager.getDarkModeState() )
    {
      buttonBar.setBackground( MeMateUIManager.getBackground( "default" ).getDarkColor() );
      ingredientsLabel.setForeground( Color.white );
      energyKCALLabel.setForeground( Color.white );
      energyKJLabel.setForeground( Color.white );
      fatLabel.setForeground( Color.white );
      fattyAcidsLabel.setForeground( Color.white );
      proteinLabel.setForeground( Color.white );
      saltLabel.setForeground( Color.white );
      carbsLabel.setForeground( Color.white );
      sugarLabel.setForeground( Color.white );
      ingredientsField.setBackground( MeMateUIManager.getBackground( "spinner" ).getDarkColor() );
      ingredientsField.setForeground( Color.white );
      ingredientsField
          .setBorder( BorderFactory.createLineBorder( MeMateUIManager.getBackground( "spinner" ).getDarkColor().brighter(), 1 ) );
      for ( final JSpinner spinner : spinnerList )
      {
        spinner.setUI( new DarkSpinnerUI() );
        spinner.setBackground( MeMateUIManager.getBackground( "spinner" ).getDarkColor() );
        spinner.setBorder( BorderFactory.createLineBorder( MeMateUIManager.getBackground( "spinner" ).getDarkColor(), 1 ) );
      }
    }
    else
    {
      buttonBar.setBackground( new Color( 240, 240, 240 ) );
      ingredientsLabel.setForeground( Color.black );
      energyKCALLabel.setForeground( Color.black );
      energyKJLabel.setForeground( Color.black );
      fatLabel.setForeground( Color.black );
      fattyAcidsLabel.setForeground( Color.black );
      proteinLabel.setForeground( Color.black );
      saltLabel.setForeground( Color.black );
      carbsLabel.setForeground( Color.black );
      sugarLabel.setForeground( Color.black );
      ingredientsField.setBackground( Color.white );
      ingredientsField.setForeground( Color.black );
      ingredientsField.setBorder( BorderFactory.createLineBorder( MeMateUIManager.getBackground( "spinner" ).getDayColor().darker(), 1 ) );
      for ( final JSpinner spinner : spinnerList )
      {
        spinner.setUI( new DaySpinnerUI() );
        spinner.setBackground( MeMateUIManager.getBackground( "spinner" ).getDayColor() );
        spinner.setBorder( BorderFactory.createLineBorder( MeMateUIManager.getBackground( "spinner" ).getDayColor().darker(), 1 ) );
      }
    }
  }
}
