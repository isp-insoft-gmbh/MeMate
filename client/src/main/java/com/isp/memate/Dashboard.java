/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.isp.memate.util.SwingUtil;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Auf dem {@link Dashboard} soll der Nutzer die Auswahl an Getränken sehen und sich nach Bedarf, welche kaufen können.
 * Tut er dies, so wird der Preis des Getränks von seinem Guthaben abgezogen. Außerdem hat der Benutzer auf dem
 * Dashboard die Möglichkeit sein Kontostand aufzuladen.
 * 
 * @author nwe
 * @since 15.10.2019
 */
public class Dashboard extends JPanel
{
    private final Mainframe mainFrame;

    JPanel drinkButtonPanel = createDrinkButtonPanel();
    private static final Dashboard instance = new Dashboard(Mainframe.getInstance());
    
    public static Dashboard getInstance()
    {
        return instance;
    }

    /**
     * Passt Layout, Hintergrund und Borders an.
     * 
     * @param mainFrame Parent-Mainframe für Statuszugriff/updates
     */
    public Dashboard(Mainframe mainFrame)
    {
        this.mainFrame = mainFrame;
        final JScrollPane scrollpane = new JScrollPane(drinkButtonPanel);
        scrollpane.getVerticalScrollBar().setUnitIncrement(16);

        final JLabel consumeLabel = new JLabel("Getränk konsumieren");
        consumeLabel.setHorizontalAlignment(JLabel.CENTER);
        consumeLabel.setFont(consumeLabel.getFont().deriveFont(20f));
        consumeLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        add(consumeLabel, BorderLayout.NORTH);
        add(scrollpane, BorderLayout.CENTER);
        add(createLowerPanel(), BorderLayout.SOUTH);
        setBackground(UIManager.getColor("TabbedPane.highlight"));
    }

    /**
     * Erstellt das lowerPanel und setzt das Layout. Das lowerPanel beinhaltet die Komponenten um das Konto aufzuladen.
     * Außerdem zeigt es noch eine kleine Info zur Einzahlung an.
     */
    private JPanel createLowerPanel()
    {
        final JPanel panel = new JPanel();
        final SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 20, 1);
        final JSpinner valueSpinner = new JSpinner(spinnerModel);
        final JButton aufladenButton = new JButton("Einzahlen");
        final JSeparator seperator = new JSeparator(JSeparator.VERTICAL);
        final JLabel aufladenlabel = new JLabel("Kontostand aufladen");
        final String infoText1 =
            "Einzahlung sind nur in Höhe von gültigen Kombination von 1€ und 2€ Münzen, 5€ Scheinen, 10€ Scheinen und 20€ Scheinen möglich.";
        final String infoText2 =
            "Einmal eingezahltes Guthaben kann nicht wieder ausgezahlt werden und muss durch den Konsum von Getränken aufgebraucht werden.";
        final JLabel infoTextLabel1 = new JLabel(infoText1);
        final JLabel infoTextLabel2 = new JLabel(infoText2);

        final ImageIcon infoIcon = new ImageIcon(getClass().getClassLoader().getResource("infoicon.png"));
        final JLabel infoIconLabel = new JLabel(infoIcon);
        infoIconLabel.setBorder(new MatteBorder(0, 1, 0, 0, UIManager.getColor("separator.background")));

        infoTextLabel1.setToolTipText("<html>" + infoText1 + "<br>" + infoText2 + "</html>");
        infoTextLabel2.setToolTipText("<html>" + infoText1 + "<br>" + infoText2 + "</html>");

        infoTextLabel1.setFont(infoTextLabel1.getFont().deriveFont(13f));
        infoTextLabel2.setFont(infoTextLabel2.getFont().deriveFont(13f));
        aufladenlabel.setFont(aufladenlabel.getFont().deriveFont(15f));

        panel.setLayout(new MigLayout(new LC().flowX().fill().insets("10px", "0", "0", "0")));
        panel.add(aufladenlabel, new CC().spanX(2));
        panel.add(seperator, new CC().spanY().growY());
        panel.add(infoIconLabel, new CC().spanY());
        panel.add(infoTextLabel1, new CC().minWidth("0").pushX().wrap());
        panel.add(valueSpinner, new CC());
        panel.add(aufladenButton, new CC());
        panel.add(infoTextLabel2, new CC().skip(1).minWidth("0").pushX());

        SwingUtil.setPreferredWidth(50, valueSpinner);

        panel.setBackground(UIManager.getColor("TabbedPane.highlight"));

        aufladenButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Object value = valueSpinner.getValue();
                int result =
                    JOptionPane.showConfirmDialog(Dashboard.this, "Wollen Sie wirklich " + value + "€ einzahlen?",
                        "Guthaben hinzufügen", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    ServerCommunication.getInstance()
                        .updateBalance(ServerCommunication.getInstance().balance + (int) value);
                    Mainframe.getInstance().setBalance(ServerCommunication.getInstance().balance + (int) value);
                }
            }
        });
        return panel;
    }

    /**
     * @return Das Panel in welchem man die angebotenen Getränke anklicken kann
     */
    public JPanel createDrinkButtonPanel()
    {
        JPanel panel = new JPanel(new WrapLayout(FlowLayout.LEFT));
        String buttonData = ServerCommunication.getInstance().getDrinkInfo();
        String[] splitedButtonDataStrings = buttonData.split("\\]\\,\\[");
        for (String buttonInfos : splitedButtonDataStrings) {
            buttonInfos = buttonInfos.replace("[", "").replace("],", "");
            String[] splitedInformationAboutButton = buttonInfos.split(",");
            String drinkName = splitedInformationAboutButton[0];
            String drinkPrice = splitedInformationAboutButton[1];
            ImageIcon drinkIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(splitedInformationAboutButton[2])
                .getScaledInstance(70, 220, Image.SCALE_SMOOTH));
            new ImageIcon(new ImageIcon(splitedInformationAboutButton[2]).getImage().getScaledInstance(70, 220,
                Image.SCALE_SMOOTH));
            panel.add(new DrinkConsumptionButton(mainFrame, drinkPrice, drinkName, drinkIcon));
        }
        panel.setBackground(UIManager.getColor("TabbedPane.highlight"));
        return panel;
    }
}
