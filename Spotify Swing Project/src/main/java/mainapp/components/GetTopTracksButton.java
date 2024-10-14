package mainapp.components;

import se.michaelthelin.spotify.model_objects.specification.Track;
import spotifyapi.SpotifyConnection;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GetTopTracksButton extends JPanel {

    private Track[] topTracks;
    private final JPanel cards;
    private final SpotifyConnection spotifyConnection = new SpotifyConnection();

    public GetTopTracksButton(JPanel cards) {
        this.cards = cards;

        // Set the layout for the panel
        setLayout(new FlowLayout(FlowLayout.CENTER));

        // Create the button
        JButton getTopTracksButton = new JButton("Get Top Tracks");
        // Add an action listener to the button
        getTopTracksButton.addActionListener(e -> {
            spotifyConnection.authorizeUser();
            updatePrintDataPanel();

            // Change panel
            CardLayout cardLayout = (CardLayout) (cards.getLayout());
            cardLayout.show(cards, "Print Data Panel");
        });

        // Add the button to the panel
        add(getTopTracksButton);

        // Set the border for spacing
        setBorder(BorderFactory.createEmptyBorder(15, 0, 50, 0));
    }


    /**
     * Updates the printDataPanel with the latest data.
     */
    private void updatePrintDataPanel() {
        try {
            System.out.println("Attempting to get top tracks...");
            topTracks = spotifyConnection.getTopTracks();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JPanel printDataPanel = (JPanel) cards.getComponent(1);
        JLabel trackLabel = (JLabel) printDataPanel.getClientProperty("printDataPanel");

        StringBuilder trackString = new StringBuilder("<html>");
        for (Track track : topTracks) {
            trackString.append(String.format("%s - %s (%s)<br>", track.getName(), track.getArtists()[0].getName(), track.getAlbum().getName()));
        }
        trackString.append("</html>");
        trackLabel.setText(trackString.toString());
    }
}
