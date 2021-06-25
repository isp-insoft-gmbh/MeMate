package com.isp.memate.dialogs;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.isp.memate.Cache;
import com.isp.memate.Drink;
import com.isp.memate.DrinkIngredients;
import com.isp.memate.ServerCommunication;
import com.isp.memate.components.MeMateDialog;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.MeMateUIManager;

import pl.coderion.model.Nutriments;
import pl.coderion.model.Product;
import pl.coderion.model.ProductResponse;
import pl.coderion.service.OpenFoodFactsWrapper;
import pl.coderion.service.impl.OpenFoodFactsWrapperImpl;

public class CreateDrinkDialog extends MeMateDialog
{
  private final JSpinner     priceSpinner, amountSpinner;
  private final JTextField   nameField, barcodeField;
  private final JButton      confirmButton, magicButton;
  private final JLabel       pictureLabel, nameLabel, priceLabel, amountLabel, barcodeLabel;
  private final ImageIcon    placeholderImage;
  private final JFileChooser fileChooser;
  private ImageIcon          currentImage;

  public CreateDrinkDialog( final Window owner )
  {
    super( owner );
    setTitle( "Getränk hinzufügen" );
    nameField = new JTextField();
    barcodeField = createBarcodeField();
    nameLabel = new JLabel( "Name" );
    priceLabel = new JLabel( "Preis" );
    amountLabel = new JLabel( "Anzahl" );
    barcodeLabel = new JLabel( "Barcode" );
    priceSpinner = createPriceSpinner();
    amountSpinner = createAmountSpinner();
    confirmButton = createConfirmButton();
    magicButton = createMagicButton();
    placeholderImage = getPlaceholderIcon();
    currentImage = placeholderImage;
    pictureLabel = createPictureLabel();
    fileChooser = createFileChooser();
    layoutComponents();

    SwingUtilities.getRootPane( this ).setDefaultButton( confirmButton );
    setModal( true );
    setResizable( false );
  }


  private JTextField createBarcodeField()
  {
    final JTextField textField = new JTextField();
    textField.getDocument().addDocumentListener( new DocumentListener()
    {
      @Override
      public void removeUpdate( DocumentEvent e )
      {
        setMagicButtonState();
      }

      @Override
      public void insertUpdate( DocumentEvent e )
      {
        setMagicButtonState();
      }

      @Override
      public void changedUpdate( DocumentEvent e )
      {
        setMagicButtonState();
      }

      private void setMagicButtonState()
      {
        if ( !textField.getText().isEmpty() && textField.getText().trim().length() != 0 )
        {
          magicButton.setEnabled( true );
        }
        else
        {
          magicButton.setEnabled( false );
        }
      }
    } );
    return textField;
  }


  private JFileChooser createFileChooser()
  {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter( new FileNameExtensionFilter( "Bilder", "jpg", "png", "gif" ) );
    return fileChooser;
  }

  @Override
  public void showDialog()
  {
    pack();
    setSize( new Dimension( getPreferredSize().width + 150, getPreferredSize().height ) );
    setLocationRelativeTo( getOwner() );
    setVisible( true );
  }

  @Override
  public void layoutComponents()
  {
    final JPanel panel = new JPanel( new GridBagLayout() );
    panel.setBorder( BorderFactory.createEmptyBorder( 5, 10, 5, 10 ) );

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 0;
    constraints.weightx = 1;
    constraints.insets = new Insets( 10, 0, 0, 0 );
    constraints.anchor = GridBagConstraints.LINE_START;
    panel.add( barcodeLabel, constraints );
    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets( 5, 0, 0, 0 );
    panel.add( barcodeField, constraints );
    constraints = new GridBagConstraints();
    constraints.gridx = 2;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    constraints.gridheight = 2;
    constraints.anchor = GridBagConstraints.SOUTHEAST;
    constraints.fill = GridBagConstraints.NONE;
    constraints.insets = new Insets( 5, 0, 0, 0 );
    panel.add( magicButton, constraints );

    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 2;
    constraints.weightx = 1;
    constraints.insets = new Insets( 10, 0, 0, 0 );
    constraints.anchor = GridBagConstraints.LINE_START;
    panel.add( nameLabel, constraints );
    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 3;
    constraints.gridwidth = 2;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets( 5, 0, 0, 0 );
    panel.add( nameField, constraints );

    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 4;
    constraints.anchor = GridBagConstraints.LINE_START;
    constraints.insets = new Insets( 15, 0, 0, 0 );
    panel.add( priceLabel, constraints );
    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 5;
    constraints.gridwidth = 1;
    constraints.anchor = GridBagConstraints.LINE_START;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets( 5, 0, 0, 10 );
    panel.add( priceSpinner, constraints );

    constraints = new GridBagConstraints();
    constraints.gridx = 2;
    constraints.gridy = 4;
    constraints.anchor = GridBagConstraints.LINE_START;
    constraints.insets = new Insets( 15, 0, 0, 0 );
    panel.add( amountLabel, constraints );
    constraints = new GridBagConstraints();
    constraints.gridx = 2;
    constraints.gridy = 5;
    constraints.gridwidth = 1;
    constraints.anchor = GridBagConstraints.LINE_START;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets( 5, 0, 0, 0 );
    panel.add( amountSpinner, constraints );

    constraints = new GridBagConstraints();
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridheight = 7;
    constraints.weightx = 0.4;
    panel.add( pictureLabel, constraints );

    final JPanel buttonBar = new JPanel();
    buttonBar.setLayout( new BoxLayout( buttonBar, BoxLayout.X_AXIS ) );
    buttonBar.add( confirmButton );
    buttonBar.add( Box.createHorizontalStrut( 5 ) );
    buttonBar.add( abortButton );
    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 7;
    constraints.gridwidth = 2;
    constraints.insets = new Insets( 30, 0, 5, 0 );
    constraints.anchor = GridBagConstraints.LINE_END;
    panel.add( buttonBar, constraints );

    add( panel );
  }

  private ImageIcon getPlaceholderIcon()
  {
    final String iconName = MeMateUIManager.getDarkModeState() ? "placeholder2.png" : "placeholder.png";
    return new ImageIcon(
        new ImageIcon( getClass().getClassLoader().getResource( iconName ) ).getImage().getScaledInstance( 42, 132, Image.SCALE_SMOOTH ) );
  }

  private JLabel createPictureLabel()
  {
    final JLabel label = new JLabel();
    label.setIcon( currentImage );

    label.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseExited( final MouseEvent e )
      {
        label.setIcon( currentImage );
      }

      @Override
      public void mouseEntered( final MouseEvent e )
      {
        label.setIcon( getEditIcon() );
      }

      @Override
      public void mouseClicked( final MouseEvent e )
      {
        fileChooser.showOpenDialog( CreateDrinkDialog.this );
        final File selectedFile = fileChooser.getSelectedFile();
        try
        {
          if ( selectedFile == null )
          {
            label.setIcon( currentImage );
            return;
          }
          if ( selectedFile.length() / 1024 > 1000 )
          {
            JOptionPane.showMessageDialog( CreateDrinkDialog.this,
                "Die ausgewählte Bilddatei darf die Dateigröße von 1MB nicht überschreiten",
                "Bildauswahl fehlgeschlagen",
                JOptionPane.ERROR_MESSAGE, null );
            return;
          }
          if ( ImageIO.read( selectedFile ) == null )
          {
            JOptionPane.showMessageDialog( CreateDrinkDialog.this, "Bitte wählen Sie eine Bilddatei aus.", "Bildauswahl fehlgeschlagen",
                JOptionPane.ERROR_MESSAGE, null );
            return;
          }
        }
        catch ( HeadlessException | IOException exception )
        {
          ClientLog.newLog( exception.getMessage() );
        }
        final String drinkPicturePath = selectedFile.getPath();
        final File image = new File( drinkPicturePath );
        final ImageIcon drinkIcon = new ImageIcon( new ImageIcon( image.getPath() ).getImage() );

        scaleAndSetImageIcon( drinkIcon );
      }

    } );
    return label;
  }

  private void scaleAndSetImageIcon( final ImageIcon drinkIcon )
  {
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

  private JButton createConfirmButton()
  {
    final JButton button = new JButton( "Hinzufügen" );
    button.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        createDrink();
      }
    } );
    return button;
  }

  private JButton createMagicButton()
  {
    final JButton button = new JButton( UIManager.getIcon( MeMateUIManager.getDarkModeState() ? "wand.icon.white" : "wand.icon.black" ) );
    button.setToolTipText( "Füllt automatisch den Namen und das Bild aus." );
    button.setEnabled( false );
    button.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent __ )
      {
        final Product product = getProductFor( barcodeField.getText() );
        if ( product == null )
        {
          JOptionPane.showConfirmDialog( CreateDrinkDialog.this, "Für den Barcode wurde leider kein Produkt gefunden", getTitle(),
              JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE );
          return;
        }
        nameField.setText( product.getProductName() );
        Image image = null;
        try
        {
          final URL url = new URL( product.getImageFrontSmallUrl() );
          image = ImageIO.read( url );
          scaleAndSetImageIcon( new ImageIcon( image ) );
        }
        catch ( final IOException ___ )
        {
          ClientLog.newLog( "Das Bild konnte leider nicht bezogen werden." );
        }
      }

    } );
    return button;
  }

  private JSpinner createAmountSpinner()
  {
    final SpinnerNumberModel model = new SpinnerNumberModel( 0, 0, 1000, 1 );
    final JSpinner spinner = new JSpinner( model );
    return spinner;
  }

  private Product getProductFor( final String barcode )
  {
    final OpenFoodFactsWrapper wrapper = new OpenFoodFactsWrapperImpl();
    final ProductResponse productResponse = wrapper.fetchProductByCode( barcode );
    final Product product = productResponse.getProduct();
    return product;
  }

  private JSpinner createPriceSpinner()
  {
    final SpinnerNumberModel model = new SpinnerNumberModel( 0, 0, 1000, 0.10 );
    final JSpinner spinner = new JSpinner( model );
    return spinner;
  }

  private void createDrink()
  {
    final String name = nameField.getText();
    final String barcode = barcodeField.getText();
    final String priceAsString = String.valueOf( priceSpinner.getValue() );
    final Float price = Float.valueOf( priceAsString );
    final ArrayList<String> drinkNames = new ArrayList<>();
    for ( final Drink drink : Cache.getInstance().getDrinks().values() )
    {
      drinkNames.add( drink.getName() );
    }

    if ( barcode.isEmpty() || barcode.trim().length() == 0 )
    {
      showErrorMessageDialog( "Bitte Barcode eingeben." );
    }
    else if ( name.isEmpty() || name.trim().length() == 0 )
    {
      showErrorMessageDialog( "Bitte Getränkenamen eingeben." );
    }
    else if ( currentImage.equals( placeholderImage ) )
    {
      showErrorMessageDialog( "Bitte Bild des Getränks auswählen." );
    }
    else if ( price.equals( 0f ) )
    {
      showErrorMessageDialog( "Bitte gültigen Preis für das Getränk angeben." );
    }
    else if ( drinkNames.contains( name ) )
    {
      showErrorMessageDialog( "Dieser Getränkenamen ist bereits vergeben." );
    }
    else
    {
      BufferedImage bImage;
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      try
      {
        bImage = new BufferedImage(
            currentImage.getIconWidth(),
            currentImage.getIconHeight(),
            BufferedImage.TYPE_INT_ARGB );
        final Graphics g = bImage.createGraphics();
        currentImage.paintIcon( null, g, 0, 0 );
        g.dispose();
        ImageIO.write( bImage, "png", bos );
      }
      catch ( final IOException exception )
      {
        ClientLog.newLog( "Das ausgewählte Bild konnte nicht in ein BufferedImage konvertiert werden." );
        ClientLog.newLog( exception.getMessage() );
      }
      final byte[] bytes = bos.toByteArray();
      boolean hasIngredients = false;
      DrinkIngredients ingredients = null;
      final Product product = getProductFor( barcode );
      if ( product != null )
      {
        hasIngredients = true;
        final Nutriments nutriments = product.getNutriments();
        ingredients = new DrinkIngredients( -1, product.getIngredientsText(), nutriments.getEnergyKj(), nutriments.getEnergyKcal(),
            nutriments.getFat(), nutriments.getSaturatedFat(), nutriments.getCarbohydrates(),
            nutriments.getSugars(), nutriments.getProteins(), nutriments.getSalt(), 0 );
      }
      ServerCommunication.getInstance()
          .registerNewDrink( new Drink( barcode, name, price, -1, bytes, (int) amountSpinner.getValue(), hasIngredients, ingredients ) );
      dispose();
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

  private void showErrorMessageDialog( String message )
  {
    JOptionPane.showMessageDialog( this, message, "Getränk hinzufügen fehlgeschlagen", JOptionPane.ERROR_MESSAGE, null );
  }
}
