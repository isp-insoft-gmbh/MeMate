package com.isp.memate.util;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class Test
{
  static String[]   columnNames = {
      "Name", "Value"
  };
  static Object[][] data        = {
      { "one", 1.0 },
      { "two", 2.0 }
  };

  public static void main( String[] args )
  {
    JFrame frame = new JFrame();
    JTable table = new JTable( data, columnNames );
    //table.setSurrendersFocusOnKeystroke(true);
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc = tcm.getColumn( 1 );
    tc.setCellEditor( new SpinnerEditor() );
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.add( table );
    frame.pack();
    frame.setVisible( true );
  }

  public static class SpinnerEditor extends DefaultCellEditor
  {
    JSpinner               spinner;
    JSpinner.DefaultEditor editor;
    JTextField             textField;
    boolean                valueSet;

    // Initializes the spinner.
    public SpinnerEditor()
    {
      super( new JTextField() );
      spinner = new JSpinner();
      editor = ((JSpinner.DefaultEditor) spinner.getEditor());
      textField = editor.getTextField();
      textField.addFocusListener( new FocusListener()
      {
        public void focusGained( FocusEvent fe )
        {
          System.err.println( "Got focus" );
          //textField.setSelectionStart(0);
          //textField.setSelectionEnd(1);
          SwingUtilities.invokeLater( new Runnable()
          {
            public void run()
            {
              if ( valueSet )
              {
                textField.setCaretPosition( 1 );
              }
            }
          } );
        }

        public void focusLost( FocusEvent fe )
        {
        }
      } );
      textField.addActionListener( new ActionListener()
      {
        public void actionPerformed( ActionEvent ae )
        {
          stopCellEditing();
        }
      } );
    }

    // Prepares the spinner component and returns it.
    public Component getTableCellEditorComponent(
                                                  JTable table, Object value, boolean isSelected, int row, int column )
    {
      if ( !valueSet )
      {
        spinner.setValue( value );
      }
      SwingUtilities.invokeLater( new Runnable()
      {
        public void run()
        {
          textField.requestFocus();
        }
      } );
      return spinner;
    }

    public boolean isCellEditable( EventObject eo )
    {
      System.err.println( "isCellEditable" );
      if ( eo instanceof KeyEvent )
      {
        KeyEvent ke = (KeyEvent) eo;
        System.err.println( "key event: " + ke.getKeyChar() );
        textField.setText( String.valueOf( ke.getKeyChar() ) );
        //textField.select(1,1);
        //textField.setCaretPosition(1);
        //textField.moveCaretPosition(1);
        valueSet = true;
      }
      else
      {
        valueSet = false;
      }
      return true;
    }

    // Returns the spinners current value.
    public Object getCellEditorValue()
    {
      return spinner.getValue();
    }

    public boolean stopCellEditing()
    {
      System.err.println( "Stopping edit" );
      try
      {
        editor.commitEdit();
        spinner.commitEdit();
      }
      catch ( java.text.ParseException e )
      {
        JOptionPane.showMessageDialog( null,
            "Invalid value, discarding." );
      }
      return super.stopCellEditing();
    }
  }
}