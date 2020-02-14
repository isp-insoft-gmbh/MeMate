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

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
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
import com.isp.memate.util.MeMateUIManager;

/**
 * Der {@linkplain DrinkManagerDialog} erzeugt einen neuen Frame,
 * wenn im {@linkplain Drinkmanager} ein Getränk hinzugefügt oder bearbeitet wird.
 * In diesem Frame kann man den Name, Preis, und ein Bild des Getränks angeben oder editieren.
 * Des weiteren gibt es einen Dialog um Getränkeinformationen zu ergänzen.
 * 
 * @author nwe
 * @since 18.10.2019
 */
public class DrinkManagerDialog
{
  private final SpinnerNumberModel spinnerModel      = new SpinnerNumberModel( 0, 0, 1000, 0.10 );
  private final JPanel             layout            = new JPanel( new GridBagLayout() );
  private final JSpinner           drinkPriceSpinner = new JSpinner( spinnerModel );
  private final JButton            cancelButton      = new JButton( "Abbrechen" );
  private final JFileChooser       fileChooser       = new JFileChooser();
  private final JTextField         drinkNameField    = new JTextField();
  private final JButton            confirmButton     = new JButton();
  private final JLabel             pictureLabel      = new JLabel();
  private String                   drinkPicturePath  = null;
  private ImageIcon                currentImage;
  private JDialog                  dialog;

  /**
   * Erzeugt den Frame und setzt das Layout der vorhandenen Kompnenten.
   * 
   * @param owner Parent für den aufzurufenden Dialog
   */
  public DrinkManagerDialog( Window owner )
  {
    final JLabel drinkName = new JLabel( "Name" );
    final JLabel drinkPrice = new JLabel( "Preis" );
    layout.setBorder( new EmptyBorder( 5, 10, 5, 10 ) );

    fileChooser.setFileFilter( new FileNameExtensionFilter( "Bilder", "jpg", "png", "gif" ) );

    GridBagConstraints drinkNameConstraints = new GridBagConstraints();
    drinkNameConstraints.gridx = 1;
    drinkNameConstraints.gridy = 0;
    drinkNameConstraints.weightx = 1;
    drinkNameConstraints.insets = new Insets( 10, 0, 0, 0 );
    drinkNameConstraints.anchor = GridBagConstraints.LINE_START;
    layout.add( drinkName, drinkNameConstraints );
    GridBagConstraints drinkNameFieldConstraints = new GridBagConstraints();
    drinkNameFieldConstraints.gridx = 1;
    drinkNameFieldConstraints.gridy = 1;
    drinkNameFieldConstraints.gridwidth = 2;
    drinkNameFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
    drinkNameFieldConstraints.insets = new Insets( 5, 0, 0, 0 );
    layout.add( drinkNameField, drinkNameFieldConstraints );

    GridBagConstraints drinkPriceConstraints = new GridBagConstraints();
    drinkPriceConstraints.gridx = 1;
    drinkPriceConstraints.gridy = 2;
    drinkPriceConstraints.anchor = GridBagConstraints.LINE_START;
    drinkPriceConstraints.insets = new Insets( 15, 0, 0, 0 );
    layout.add( drinkPrice, drinkPriceConstraints );
    GridBagConstraints drinkPriceSpinnerConstraints = new GridBagConstraints();
    drinkPriceSpinnerConstraints.gridx = 1;
    drinkPriceSpinnerConstraints.gridy = 3;
    drinkPriceSpinnerConstraints.gridwidth = 2;
    drinkPriceSpinnerConstraints.anchor = GridBagConstraints.LINE_START;
    drinkPriceSpinnerConstraints.fill = GridBagConstraints.HORIZONTAL;
    drinkPriceSpinnerConstraints.insets = new Insets( 5, 0, 0, 0 );
    layout.add( drinkPriceSpinner, drinkPriceSpinnerConstraints );

    GridBagConstraints drinkPicturePreviewConstraints = new GridBagConstraints();
    drinkPicturePreviewConstraints.gridx = 0;
    drinkPicturePreviewConstraints.gridy = 0;
    drinkPicturePreviewConstraints.gridheight = 5;
    drinkPicturePreviewConstraints.weightx = 0.4;
    layout.add( pictureLabel, drinkPicturePreviewConstraints );

    JPanel buttonBar = new JPanel();
    buttonBar.setLayout( new BoxLayout( buttonBar, BoxLayout.X_AXIS ) );
    buttonBar.add( confirmButton );
    buttonBar.add( Box.createHorizontalStrut( 5 ) );
    buttonBar.add( cancelButton );
    GridBagConstraints buttonBarConstraints = new GridBagConstraints();
    buttonBarConstraints.gridx = 1;
    buttonBarConstraints.gridy = 5;
    buttonBarConstraints.gridwidth = 2;
    buttonBarConstraints.insets = new Insets( 5, 0, 0, 0 );
    buttonBarConstraints.anchor = GridBagConstraints.LINE_END;
    layout.add( buttonBar, buttonBarConstraints );

    dialog = new JDialog( owner );
    JRootPane rootPane = dialog.getRootPane();
    final String quitDialogActionName = "QUIT_DIALOG";
    rootPane.getActionMap().put( quitDialogActionName, new AbstractAction()
    {
      @Override
      public void actionPerformed( ActionEvent __ )
      {
        dialog.dispose();
      }
    } );
    rootPane.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW )
        .put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ), quitDialogActionName );

    cancelButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent __ )
      {
        dialog.dispose();
      }
    } );

    pictureLabel.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseExited( MouseEvent e )
      {
        pictureLabel.setIcon( currentImage );
      }

      @Override
      public void mouseEntered( MouseEvent e )
      {
        pictureLabel.setIcon( getEditIcon() );
      }

      @Override
      public void mouseClicked( MouseEvent e )
      {
        fileChooser.showOpenDialog( owner );
        File selectedFile = fileChooser.getSelectedFile();
        try
        {
          if ( selectedFile == null )
          {
            pictureLabel.setIcon( currentImage );
            return;
          }
          if ( (selectedFile.length() / 1024) > 1000 )
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
        File image = new File( drinkPicturePath );
        ImageIcon drinkImage =
            new ImageIcon( new ImageIcon( image.getPath() ).getImage().getScaledInstance( 42, 132, Image.SCALE_SMOOTH ) );
        pictureLabel.setIcon( drinkImage );
        currentImage = drinkImage;
      }
    } );


    SwingUtilities.getRootPane( dialog ).setDefaultButton( confirmButton );
    dialog.add( layout );
    dialog.pack();
    Dimension oldPreferredSize = dialog.getPreferredSize();
    dialog.setSize( new Dimension( oldPreferredSize.width + 300, oldPreferredSize.height ) );
    dialog.setModal( true );
    dialog.setResizable( false );
    dialog.setLocationRelativeTo( dialog.getOwner() );

    confirmButton.setContentAreaFilled( false );
    confirmButton.setOpaque( true );
    cancelButton.setContentAreaFilled( false );
    cancelButton.setOpaque( true );
    toggleDarkMode( drinkName, drinkPrice, buttonBar );
  }

  /**
   * @param drinkName
   * @param drinkPrice
   * @param buttonBar
   */
  private void toggleDarkMode( final JLabel drinkName, final JLabel drinkPrice, JPanel buttonBar )
  {
    if ( MeMateUIManager.getDarkModeState() )
    {
      layout.setBackground( MeMateUIManager.getBackground( "default" ).getDarkColor() );
      buttonBar.setBackground( MeMateUIManager.getBackground( "default" ).getDarkColor() );
      drinkName.setForeground( Color.white );
      drinkPrice.setForeground( Color.white );
      confirmButton.setBackground( MeMateUIManager.getBackground( "button" ).getDarkColor() );
      confirmButton.setForeground( MeMateUIManager.getForeground( "button" ).getDarkColor() );
      cancelButton.setBackground( MeMateUIManager.getBackground( "button" ).getDarkColor() );
      cancelButton.setForeground( MeMateUIManager.getForeground( "button" ).getDarkColor() );
    }
    else
    {
      layout.setBackground( new Color( 240, 240, 240 ) );
      buttonBar.setBackground( new Color( 240, 240, 240 ) );
      drinkName.setForeground( Color.black );
      drinkPrice.setForeground( Color.black );
      confirmButton.setBackground( MeMateUIManager.getBackground( "button" ).getDayColor() );
      confirmButton.setForeground( MeMateUIManager.getForeground( "button" ).getDayColor() );
      cancelButton.setBackground( MeMateUIManager.getBackground( "button" ).getDayColor() );
      cancelButton.setForeground( MeMateUIManager.getForeground( "button" ).getDayColor() );
    }
  }

  /**
   * Erzeugt ein neues Icon, welches aus dem Bild des Getränks und dem Bearbeiten-Icon besteht.
   * 
   * @return das neue Icon.
   */
  protected Icon getEditIcon()
  {
    currentImage = (ImageIcon) pictureLabel.getIcon();
    BufferedImage image = new BufferedImage( currentImage.getIconWidth(), currentImage.getIconHeight(), BufferedImage.TYPE_INT_ARGB );
    Graphics gr = image.createGraphics();
    currentImage.paintIcon( null, gr, 0, 0 );
    gr.dispose();
    try
    {
      BufferedImage overlay = ImageIO.read( getClass().getClassLoader().getResourceAsStream( "edit.png" ) );
      int w = Math.max( image.getWidth(), overlay.getWidth() );
      int h = Math.max( image.getHeight(), overlay.getHeight() );
      BufferedImage combined = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
      Graphics g = combined.getGraphics();
      g.drawImage( image, 0, 0, null );
      g.drawImage( overlay, 0, 0, null );
      return new ImageIcon( combined );
    }
    catch ( IOException exception )
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
  public void showEditDialog( String drink )
  {
    dialog.setTitle( "Getränk bearbeiten" );
    confirmButton.setText( "Speichern" );
    String oldName = drink;
    Float oldPrice = ServerCommunication.getInstance().getPrice( drink );
    drinkNameField.setText( oldName );
    drinkPriceSpinner.setValue( oldPrice );
    pictureLabel.setIcon( new ImageIcon(
        ServerCommunication.getInstance().getIcon( drink ).getImage().getScaledInstance( 42, 132, Image.SCALE_SMOOTH ) ) );
    confirmButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        String newName = drinkNameField.getText();
        String newPicture = drinkPicturePath;
        String newPriceAsString = String.valueOf( drinkPriceSpinner.getValue() );
        Float newPrice = Float.valueOf( newPriceAsString );

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
          Integer id = ServerCommunication.getInstance().getID( oldName );
          if ( drinkPicturePath != null )
          {
            BufferedImage bImage;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try
            {
              bImage = ImageIO.read( new File( newPicture ) );
              ImageIO.write( bImage, "png", bos );
            }
            catch ( IOException exception )
            {
              ClientLog.newLog( "Das ausgewählte Bild konnte nicht gespeichert werden." );
              ClientLog.newLog( exception.getMessage() );
            }
            byte[] bytes = bos.toByteArray();
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
      }
    } );
    Dimension oldPreferredSize = dialog.getPreferredSize();
    dialog.setSize( new Dimension( oldPreferredSize.width + 150, oldPreferredSize.height ) );
    dialog.setLocationRelativeTo( dialog.getOwner() );
    dialog.setVisible( true );
  }

  /**
   * Zeigt einen Dialog an, in welchem man ein neues Getränk registrieren kann.
   * Es wird ein Name, Preis und Bild als Eingabe gefordert.
   * Sind die Eingaben korrekt, so werden diese an der Server weitergeleitet.
   */
  public void showNewDialog()
  {
    if ( MeMateUIManager.getDarkModeState() )
    {
      pictureLabel.setIcon( new ImageIcon( new ImageIcon( getClass().getClassLoader().getResource( "placeholder2.png" ) ).getImage()
          .getScaledInstance( 42, 132, Image.SCALE_SMOOTH ) ) );
    }
    else
    {
      pictureLabel.setIcon( new ImageIcon( new ImageIcon( getClass().getClassLoader().getResource( "placeholder.png" ) ).getImage()
          .getScaledInstance( 42, 132, Image.SCALE_SMOOTH ) ) );
    }
    dialog.setTitle( "Getränk hinzufügen" );
    confirmButton.setText( "Hinzufügen" );
    confirmButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent __ )
      {
        String name = drinkNameField.getText();
        String priceAsString = String.valueOf( drinkPriceSpinner.getValue() );
        Float price = Float.valueOf( priceAsString );

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
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          try
          {
            bImage = ImageIO.read( new File( drinkPicturePath ) );
            ImageIO.write( bImage, "png", bos );
          }
          catch ( IOException exception )
          {
            ClientLog.newLog( "Das ausgewählte Bild konnte nicht gespeichert werden." );
            ClientLog.newLog( exception.getMessage() );
          }
          byte[] bytes = bos.toByteArray();
          ServerCommunication.getInstance()
              .registerNewDrink( new Drink( name, price, drinkPicturePath, -1, bytes, 0, false, null ) );
          dialog.dispose();
          Mainframe.getInstance().getDrinkManager().updateList();
        }
      }
    } );
    Dimension oldPreferredSize = dialog.getPreferredSize();
    dialog.setSize( new Dimension( oldPreferredSize.width + 150, oldPreferredSize.height ) );
    dialog.setLocationRelativeTo( dialog.getOwner() );
    dialog.setVisible( true );
  }

  /**
   * Zeigt einen Dialog an, um Informationen über das Getränk zu ergänzen.
   * Beispielsweise Zutatenliste, Fettgehalt oder Zuckergehalt.
   */
  public void showIngredientsDialog( int DrinkID )
  {
    layout.removeAll();
    dialog.setTitle( "Inhaltsstoffe hinzufügen" );
    confirmButton.setText( "Hinzufügen" );

    GridBagConstraints ingredientsLabelConstraints = new GridBagConstraints();
    ingredientsLabelConstraints.gridx = 0;
    ingredientsLabelConstraints.gridy = 0;
    JLabel ingredientsLabel = new JLabel( "Zutaten" );
    layout.add( ingredientsLabel, ingredientsLabelConstraints );
    JTextField ingredientsField = new JTextField();
    ingredientsField.setPreferredSize( new Dimension( 200, 20 ) );
    GridBagConstraints ingredientsFieldConstraints = new GridBagConstraints();
    ingredientsFieldConstraints.gridx = 1;
    ingredientsFieldConstraints.gridy = 0;
    layout.add( ingredientsField, ingredientsFieldConstraints );

    GridBagConstraints energyKJLabelConstraints = new GridBagConstraints();
    energyKJLabelConstraints.gridx = 0;
    energyKJLabelConstraints.gridy = 1;
    JLabel energyKJLabel = new JLabel( "Energie kJ" );
    layout.add( energyKJLabel, energyKJLabelConstraints );
    SpinnerModel energykJModel = new SpinnerNumberModel( 0, 0, 500, 1 );
    JSpinner energykJSpinner = new JSpinner( energykJModel );
    GridBagConstraints energykJSpinnerConstraints = new GridBagConstraints();
    energykJSpinnerConstraints.gridx = 1;
    energykJSpinnerConstraints.gridy = 1;
    energykJSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( energykJSpinner, energykJSpinnerConstraints );

    GridBagConstraints energyKCALLabelConstraints = new GridBagConstraints();
    energyKCALLabelConstraints.gridx = 0;
    energyKCALLabelConstraints.gridy = 2;
    JLabel energyKCALLabel = new JLabel( "Energie kcal" );
    layout.add( energyKCALLabel, energyKCALLabelConstraints );
    SpinnerModel energykCALModel = new SpinnerNumberModel( 0, 0, 500, 1 );
    JSpinner energykCALSpinner = new JSpinner( energykCALModel );
    GridBagConstraints energykCALSpinnerConstraints = new GridBagConstraints();
    energykCALSpinnerConstraints.gridx = 1;
    energykCALSpinnerConstraints.gridy = 2;
    energykCALSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( energykCALSpinner, energykCALSpinnerConstraints );

    GridBagConstraints fatLabelConstraints = new GridBagConstraints();
    fatLabelConstraints.gridx = 0;
    fatLabelConstraints.gridy = 3;
    JLabel fatLabel = new JLabel( "Fett" );
    layout.add( fatLabel, fatLabelConstraints );
    SpinnerModel fatModel = new SpinnerNumberModel( 0, 0, 50, 0.1 );
    JSpinner fatSpinner = new JSpinner( fatModel );
    GridBagConstraints fatSpinnerConstraints = new GridBagConstraints();
    fatSpinnerConstraints.gridx = 1;
    fatSpinnerConstraints.gridy = 3;
    fatSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( fatSpinner, fatSpinnerConstraints );

    GridBagConstraints fattyAcidsLabelConstraints = new GridBagConstraints();
    fattyAcidsLabelConstraints.gridx = 0;
    fattyAcidsLabelConstraints.gridy = 4;
    JLabel fattyAcidsLabel = new JLabel( "gesättigte Fettsäuren" );
    layout.add( fattyAcidsLabel, fattyAcidsLabelConstraints );
    SpinnerModel fattyAcidsModel = new SpinnerNumberModel( 0, 0, 50, 0.1 );
    JSpinner fattyAcidsSpinner = new JSpinner( fattyAcidsModel );
    GridBagConstraints fattyAcidsSpinnerConstraints = new GridBagConstraints();
    fattyAcidsSpinnerConstraints.gridx = 1;
    fattyAcidsSpinnerConstraints.gridy = 4;
    fattyAcidsSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( fattyAcidsSpinner, fattyAcidsSpinnerConstraints );

    GridBagConstraints carbsLabelConstraints = new GridBagConstraints();
    carbsLabelConstraints.gridx = 0;
    carbsLabelConstraints.gridy = 5;
    JLabel carbsLabel = new JLabel( "Kohlenhydrate" );
    layout.add( carbsLabel, carbsLabelConstraints );
    SpinnerModel carbsModel = new SpinnerNumberModel( 0, 0, 50, 0.1 );
    JSpinner carbsSpinner = new JSpinner( carbsModel );
    GridBagConstraints carbsSpinnerConstraints = new GridBagConstraints();
    carbsSpinnerConstraints.gridx = 1;
    carbsSpinnerConstraints.gridy = 5;
    carbsSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( carbsSpinner, carbsSpinnerConstraints );

    GridBagConstraints sugarLabelConstraints = new GridBagConstraints();
    sugarLabelConstraints.gridx = 0;
    sugarLabelConstraints.gridy = 6;
    JLabel sugarLabel = new JLabel( "davon Zucker" );
    layout.add( sugarLabel, sugarLabelConstraints );
    SpinnerModel sugarModel = new SpinnerNumberModel( 0, 0, 50, 0.1 );
    JSpinner sugarSpinner = new JSpinner( sugarModel );
    GridBagConstraints sugarSpinnerConstraints = new GridBagConstraints();
    sugarSpinnerConstraints.gridx = 1;
    sugarSpinnerConstraints.gridy = 6;
    sugarSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( sugarSpinner, sugarSpinnerConstraints );

    GridBagConstraints proteinLabelConstraints = new GridBagConstraints();
    proteinLabelConstraints.gridx = 0;
    proteinLabelConstraints.gridy = 7;
    JLabel proteinLabel = new JLabel( "Eiweiß" );
    layout.add( proteinLabel, proteinLabelConstraints );
    SpinnerModel proteinModel = new SpinnerNumberModel( 0, 0, 50, 0.1 );
    JSpinner proteinSpinner = new JSpinner( proteinModel );
    GridBagConstraints proteinSpinnerConstraints = new GridBagConstraints();
    proteinSpinnerConstraints.gridx = 1;
    proteinSpinnerConstraints.gridy = 7;
    proteinSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( proteinSpinner, proteinSpinnerConstraints );

    GridBagConstraints saltLabelConstraints = new GridBagConstraints();
    saltLabelConstraints.gridx = 0;
    saltLabelConstraints.gridy = 8;
    JLabel saltLabel = new JLabel( "Salz" );
    layout.add( saltLabel, saltLabelConstraints );
    SpinnerModel saltModel = new SpinnerNumberModel( 0, 0, 50, 0.1 );
    JSpinner saltSpinner = new JSpinner( saltModel );
    GridBagConstraints saltSpinnerConstraints = new GridBagConstraints();
    saltSpinnerConstraints.gridx = 1;
    saltSpinnerConstraints.gridy = 8;
    saltSpinnerConstraints.fill = GridBagConstraints.BOTH;
    layout.add( saltSpinner, saltSpinnerConstraints );

    JPanel buttonBar = new JPanel();
    buttonBar.setLayout( new BoxLayout( buttonBar, BoxLayout.X_AXIS ) );
    buttonBar.add( confirmButton );
    buttonBar.add( Box.createHorizontalStrut( 5 ) );
    buttonBar.add( cancelButton );
    GridBagConstraints buttonBarConstraints = new GridBagConstraints();
    buttonBarConstraints.gridx = 1;
    buttonBarConstraints.gridy = 9;
    buttonBarConstraints.gridwidth = 2;
    buttonBarConstraints.insets = new Insets( 5, 0, 0, 0 );
    buttonBarConstraints.anchor = GridBagConstraints.LINE_END;
    layout.add( buttonBar, buttonBarConstraints );

    toggleDarkMode( ingredientsLabel, energyKJLabel, energyKCALLabel, fatLabel, fattyAcidsLabel, carbsLabel, sugarLabel, proteinLabel,
        saltLabel, buttonBar );

    ActionListener[] listeners = confirmButton.getActionListeners();
    for ( ActionListener actionListener : listeners )
    {
      confirmButton.removeActionListener( actionListener );
    }

    confirmButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
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
      }
    } );


    Dimension oldPreferredSize = dialog.getPreferredSize();
    dialog.setSize( new Dimension( oldPreferredSize.width, oldPreferredSize.height ) );
    dialog.setLocationRelativeTo( dialog.getOwner() );
    dialog.setVisible( true );
  }

  /**
   * @param ingredientsLabel
   * @param energyKJLabel
   * @param energyKCALLabel
   * @param fatLabel
   * @param fattyAcidsLabel
   * @param carbsLabel
   * @param sugarLabel
   * @param proteinLabel
   * @param saltLabel
   * @param buttonBar
   */
  private void toggleDarkMode( JLabel ingredientsLabel, JLabel energyKJLabel, JLabel energyKCALLabel, JLabel fatLabel,
                               JLabel fattyAcidsLabel, JLabel carbsLabel, JLabel sugarLabel, JLabel proteinLabel, JLabel saltLabel,
                               JPanel buttonBar )
  {
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
    }
  }
}
