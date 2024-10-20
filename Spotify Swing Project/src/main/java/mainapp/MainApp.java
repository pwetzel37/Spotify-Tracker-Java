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

        setVisible(true);
        setResizable(false);
        setLayout(new FlowLayout());
        setTitle("Spotify Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Creates the panel for obtaining an authorization code from Spotify.
     */
    private JPanel createButtonsPanel() {
        // Create the main panel with vertical layout to stack the label and buttons
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Create a label
        JLabel label = new JLabel(spotifyConnection.getUsersName());

        // Create the buttons panel with horizontal layout
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        this.setSize(650, 125);

        // Add buttons to the buttons panel
        buttonsPanel.add(new GetUserDataButton(this, spotifyConnection, cards, "Get Top Artists", "Artists"));
        buttonsPanel.add(new GetUserDataButton(this, spotifyConnection, cards, "Get Top Tracks", "Tracks"));
        buttonsPanel.add(new GetUserDataButton(this, spotifyConnection, cards, "Get Saved Tracks", "SavedTracks"));

        // Add the label and the buttons panel to the main panel
        mainPanel.add(label);
        mainPanel.add(buttonsPanel);

        return mainPanel;
    }

    public static void main(String[] args) {
        // Run the Swing application
        SwingUtilities.invokeLater(MainApp::new);
    }
}