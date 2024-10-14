package mainapp.components;

import javax.swing.*;
import java.awt.*;

public class PrintDataPanel extends JPanel {

    public PrintDataPanel(JPanel cards) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel label = new JLabel();
        label.setLayout(new FlowLayout(FlowLayout.CENTER));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backButton = new JButton("Back");
        backButton.setLayout(new FlowLayout(FlowLayout.CENTER));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Change panel on click
        backButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) (cards.getLayout());
            cardLayout.show(cards, "Buttons Panel");
        });

        putClientProperty("printDataPanel", label);
        putClientProperty("backButton", backButton);

        add(label);
        add(backButton);
    }
}
