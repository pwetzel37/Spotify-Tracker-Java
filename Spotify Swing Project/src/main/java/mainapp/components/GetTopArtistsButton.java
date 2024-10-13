package mainapp.components;

import se.michaelthelin.spotify.model_objects.specification.Artist;
import spotifyapi.SpotifyConnection;

import javax.swing.*;
import java.awt.*;

public class GetTopArtistsButton extends JPanel {

    private Artist[] topArtists;
    private final JPanel cards;
    private final SpotifyConnection spotifyConnection = new SpotifyConnection();

    public GetTopArtistsButton(JPanel cards) {
        this.cards = cards;

        // Set the layout for the panel
        setLayout(new FlowLayout(FlowLayout.CENTER));

        // Create the button
        JButton getTopArtistsButton = new JButton("Get Top Artists");
        // Add an action listener to the button
        getTopArtistsButton.addActionListener(e -> {
            spotifyConnection.getAuthorizationCode();
            updatePrintDataPanel();

            // Change panel
            CardLayout cardLayout = (CardLayout) (cards.getLayout());
            cardLayout.show(cards, "Print Data Panel");
        });

        // Add the button to the panel
        add(getTopArtistsButton);

        // Set the border for spacing
        setBorder(BorderFactory.createEmptyBorder(15, 0, 50, 0));
    }


    /**
     * Updates the printDataPanel with the latest data.
     */
    private void updatePrintDataPanel() {
        try {
            System.out.println("attempting to get top artists");
            topArtists = spotifyConnection.getTopArtists();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JPanel printDataPanel = (JPanel) cards.getComponent(1);
        JLabel artistLabel = (JLabel) printDataPanel.getClientProperty("artistLabel");

        StringBuilder artistString = new StringBuilder("<html>");
        for (Artist artist : topArtists) {
            artistString.append(String.format("<b>%s</b>: %,d followers<br>", artist.getName(), artist.getFollowers().getTotal()));
        }
        artistString.append("</html>");
        artistLabel.setText(artistString.toString());
    }
}
