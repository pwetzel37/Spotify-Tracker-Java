package mainapp;

import mainapp.components.GetUserDataButton;
import mainapp.components.PrintDataPanel;
import spotifyapi.SpotifyConnection;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {

    private final JPanel cards = new JPanel(new CardLayout());
    private final SpotifyConnection spotifyConnection = new SpotifyConnection();

    public MainApp() {
        spotifyConnection.authorizeUser();

        JPanel buttonsPanel = createButtonsPanel();
        JPanel printDataPanel = new PrintDataPanel(this, cards);

        cards.add(buttonsPanel, "Buttons Panel");
        cards.add(printDataPanel, "Print Data Panel");

        add(cards);

        setTitle("Spotify Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setVisible(true);
    }

    /**
     * Creates the panel for obtaining an authorization code from Spotify.
     */
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        this.setSize(700, 100);

        // Add components to the panel
        panel.add(new GetUserDataButton(this, spotifyConnection, cards, "Get Top Artists", "Artists"));
        panel.add(new GetUserDataButton(this, spotifyConnection, cards, "Get Top Tracks", "Tracks"));
        panel.add(new GetUserDataButton(this, spotifyConnection, cards, "Get Saved Tracks", "SavedTracks"));

        return panel;
    }

    public static void main(String[] args) {
        // Run the Swing application
        SwingUtilities.invokeLater(MainApp::new);
    }
}