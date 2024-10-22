package mainapp.components;

import javax.swing.*;
import java.awt.*;

public class PrintDataPanel extends JPanel {

    public PrintDataPanel(JFrame mainAppFrame, JPanel cards) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel label = new JLabel();
        label.setLayout(new FlowLayout(FlowLayout.CENTER));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            // Change the size of the MainApp frame
            mainAppFrame.setSize(750, 125);

            // Change panel on click
            CardLayout cardLayout = (CardLayout) (cards.getLayout());
            cardLayout.show(cards, "Buttons Panel");
        });

        // Create wrapper to add padding
        JPanel backButtonWrapper = new JPanel();
        backButtonWrapper.add(backButton);
        backButtonWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components to the panel
        add(label);
        add(backButtonWrapper);
        putClientProperty("printDataPanel", label);
        putClientProperty("backButton", backButtonWrapper);
    }
}
