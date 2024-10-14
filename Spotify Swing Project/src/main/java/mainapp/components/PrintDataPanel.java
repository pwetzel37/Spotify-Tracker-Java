package mainapp.components;

import javax.swing.*;
import java.awt.*;

public class PrintDataPanel extends JPanel {

    public PrintDataPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel label = new JLabel();
        label.setLayout(new FlowLayout(FlowLayout.CENTER));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        putClientProperty("printDataPanel", label);
        add(label);
    }
}
