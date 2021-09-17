package com.isp.memate.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;

import org.apache.commons.lang3.math.NumberUtils;

public class TableSpinnerCellEditor extends DefaultCellEditor
{
  JSpinner               spinner;
  JSpinner.DefaultEditor editor;
  JTextField             textField;
  boolean                valueSet;

  public TableSpinnerCellEditor( SpinnerModel spinnerModel )
  {
    super( new JTextField() );
    spinner = new JSpinner( spinnerModel );
    editor = ((JSpinner.DefaultEditor) spinner.getEditor());
    textField = editor.getTextField();
    textField.addFocusListener( new FocusListener()
    {
      public void focusGained( FocusEvent fe )
      {
        if ( valueSet )
        {
          textField.setCaretPosition( 1 );
        }
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

  @Override
  public Object getCellEditorValue()
  {
    return spinner.getValue();
  }

  @Override
  public boolean isCellEditable( EventObject anEvent )
  {
    if ( anEvent instanceof MouseEvent )
    {
      return ((MouseEvent) anEvent).getClickCount() >= 2;
    }
    return true;
  }

  @Override
  public boolean stopCellEditing()
  {
    if ( spinner.getValue() instanceof Number && NumberUtils.isParsable( editor.getTextField().getText().replace( ',', '.' ) ) )
    {
      try
      {
        editor.commitEdit();
        spinner.commitEdit();
      }
      catch ( ParseException e )
      {
        e.printStackTrace();
      }
      return super.stopCellEditing();
    }
    //No Number
    return true;
  }

  @Override
  public void cancelCellEditing()
  {
  }

  @Override
  public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column )
  {
    if ( !valueSet )
    {
      spinner.setValue( value );
    }
    textField.requestFocus();
    return spinner;
  }
}
