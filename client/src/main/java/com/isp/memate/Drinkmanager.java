/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Im Getränkemanager kann man einstellen, welche Getränke derzeit verfügbar sind und somit im {@link Dashboard}
 * angezeigt werden. Man kann neue Getränke hinzufügen, Getränke entfernen und Getränke beearbeiten, sollte sich zum
 * Beispiel der Preis ändern.
 * 
 * @author nwe
 * @since 15.10.2019
 */
public class Drinkmanager extends JPanel
{
    private String[] data = ServerCommunication.getInstance().getDrinkNames();

    private final JList<String> drinkList = new JList<>(data);

    private final JScrollPane scrollpane = new JScrollPane();

    final JButton addButton = new JButton("Hinzufügen");

    final JButton editButton = new JButton("Bearbeiten");

    final JButton removeButton = new JButton("Entfernen");

    /**
     * Passt das Layout an und legt den CellRenderer fest. Dadurch werden sowohl Bilder als auch die Namen/Preise in der
     * Liste angezeigt.
     */
    public Drinkmanager()
    {
        setLayout(new BorderLayout());
        add(scrollpane, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        drinkList.setCellRenderer(new DrinkCellRenderer());
        drinkList.setFixedCellHeight(150);
        drinkList.setFont(drinkList.getFont().deriveFont(20f));
        scrollpane.setViewportView(drinkList);

        // TODO Make sure we only select data if data is present. In the future, this might fail.
        drinkList.setSelectedIndex(0);
    }

    /**
     * @return JPanel mit den Buttons zum Hinzufügen, Bearbeiten und Entfernen von Getränken
     */
    private JPanel createButtonPanel()
    {
        editButton.setEnabled(false);
        removeButton.setEnabled(false);

        final JPanel panel = new JPanel();
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();

        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setBackground(UIManager.getColor("TabbedPane.highlight"));
        panel.setLayout(new GridBagLayout());
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 0.5;
        panel.add(addButton, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 0;
        panel.add(editButton, gridBagConstraints);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        panel.add(removeButton, gridBagConstraints);

        drinkList.addListSelectionListener(new ListSelectionListener()
        {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                if (e.getLastIndex() == -1) {
                    editButton.setEnabled(false);
                    removeButton.setEnabled(false);
                } else {
                    editButton.setEnabled(true);
                    removeButton.setEnabled(true);
                }
            }
        });

        removeButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int result = JOptionPane.showConfirmDialog(scrollpane,
                    "Wollen Sie wirklich " + drinkList.getSelectedValue() + " löschen?", "Getränk entfernen",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    ServerCommunication.getInstance().removeDrink(drinkList.getSelectedValue());
                }
            }
        });
        addButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DrinkManagerDialog addDrinkDialog =
                    new DrinkManagerDialog(SwingUtilities.getWindowAncestor(Drinkmanager.this));
                addDrinkDialog.showNewDialog();
            }
        });
        editButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DrinkManagerDialog editDrinkDialog =
                    new DrinkManagerDialog(SwingUtilities.getWindowAncestor(Drinkmanager.this));
                editDrinkDialog.showEditDialog(drinkList.getSelectedValue());
            }
        });
        return panel;
    }
}
