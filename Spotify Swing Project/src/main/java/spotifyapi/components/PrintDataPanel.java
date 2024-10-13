package spotifyapi.components;

import javax.swing.*;
import java.awt.*;

public class PrintDataPanel extends JPanel {

    public PrintDataPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel artistLabel = new JLabel();
        artistLabel.setLayout(new FlowLayout(FlowLayout.CENTER));
        artistLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        putClientProperty("artistLabel", artistLabel);
        add(artistLabel);
    }
}
