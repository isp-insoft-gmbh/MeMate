package com.isp.memate.util;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DrinkPictureCellEditor extends DefaultCellEditor
{
  private static final int CLICK_COUNT_TO_START = 2;
  private JFileChooser     fileChooser;
  private byte[]           newImage;

  public DrinkPictureCellEditor()
  {
    super( new JTextField() );
    setClickCountToStart( CLICK_COUNT_TO_START );

    fileChooser = new JFileChooser();
    fileChooser.setFileFilter( new FileNameExtensionFilter( "Bilder", "jpg", "png", "gif" ) );
  }

  @Override
  public Object getCellEditorValue()
  {
    return newImage;
  }

  @Override
  public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column )
  {
    SwingUtilities.invokeLater( new Runnable()
    {
      public void run()
      {
        if ( fileChooser.showOpenDialog( GUIObjects.mainframe ) == JFileChooser.APPROVE_OPTION )
        {
          final File selectedFile = fileChooser.getSelectedFile();
          try
          {
            if ( selectedFile == null || ImageIO.read( selectedFile ) == null )
            {
              JOptionPane.showMessageDialog( GUIObjects.mainframe, "Bitte wählen Sie eine Bilddatei aus.", "Bildauswahl fehlgeschlagen",
                  JOptionPane.ERROR_MESSAGE, null );
              newImage = null;
              fireEditingStopped();
              return;
            }
            if ( selectedFile.length() / 1024 > 1000 )
            {
              JOptionPane.showMessageDialog( GUIObjects.mainframe,
                  "Die ausgewählte Bilddatei darf die Dateigröße von 1MB nicht überschreiten",
                  "Bildauswahl fehlgeschlagen",
                  JOptionPane.ERROR_MESSAGE, null );
              newImage = null;
              fireEditingStopped();
              return;
            }
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BufferedImage bImage = ImageIO.read( selectedFile );
            ImageIO.write( bImage, "png", bos );
            newImage = bos.toByteArray();
          }
          catch ( HeadlessException | IOException exception )
          {
            ClientLog.newLog( "Das Bild des Getränks konnte nicht aktualisiert werden." );
          }
        }
        else
        {
          newImage = null;
          fireEditingStopped();
          return;
        }
      }
    } );
    return new JLabel( (ImageIcon) value );
  }
}
