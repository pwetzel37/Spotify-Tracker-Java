package mainapp;

import spotifyapi.components.GetTopArtistsButton;
import spotifyapi.components.PrintDataPanel;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {

    private final JPanel cards = new JPanel(new CardLayout());

    public MainApp() {
        JPanel getAuthorizationCodePanel = createGetAuthorizationCodePanel();
        JPanel printDataPanel = new PrintDataPanel();

        cards.add(getAuthorizationCodePanel, "Get Authorization Code Panel");
        cards.add(printDataPanel, "Print Data Panel");

        add(cards);

        setTitle("Spotify Tracker");
        setSize(600, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setVisible(true);
    }

    /**
     * Creates the panel for obtaining an authorization code from Spotify.
     */
    private JPanel createGetAuthorizationCodePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Add components to the panel
        panel.add(new GetTopArtistsButton(cards));

        return panel;
    }

    public static void main(String[] args) {
        // Run the Swing application
        SwingUtilities.invokeLater(MainApp::new);
    }
}