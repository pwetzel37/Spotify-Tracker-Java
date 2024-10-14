package mainapp.components;

import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Track;
import spotifyapi.SpotifyConnection;

import javax.swing.*;
import java.awt.*;

public class GetSpotifyDataButton extends JPanel {

    private final JPanel cards;
    private final SpotifyConnection spotifyConnection = new SpotifyConnection();

    public GetSpotifyDataButton(JPanel cards, String buttonLabel, String dataType) {
        this.cards = cards;

        // Set the layout for the panel
        setLayout(new FlowLayout(FlowLayout.CENTER));

        // Create the button with the passed label
        JButton getSpotifyDataButton = new JButton(buttonLabel);

        // Add an action listener to the button
        getSpotifyDataButton.addActionListener(e -> {
            spotifyConnection.authorizeUser();
            updatePrintDataPanel(dataType);

            // Change panel
            CardLayout cardLayout = (CardLayout) (cards.getLayout());
            cardLayout.show(cards, "Print Data Panel");
        });

        // Add the button to the panel
        add(getSpotifyDataButton);

        // Set the border for spacing
        setBorder(BorderFactory.createEmptyBorder(15, 0, 50, 0));
    }

    /**
     * Updates the printDataPanel with the latest data, either Artists or Tracks depending on dataType.
     */
    private void updatePrintDataPanel(String dataType) {
        try {
            System.out.println("Attempting to get data...");
            Object[] data = null;
            if (dataType.equals("Artist")) {
                data = spotifyConnection.getTopArtists();
            } else if (dataType.equals("Track")) {
                data = spotifyConnection.getTopTracks();
            }

            JPanel printDataPanel = (JPanel) cards.getComponent(1);
            JLabel dataLabel = (JLabel) printDataPanel.getClientProperty("printDataPanel");

            StringBuilder dataString = new StringBuilder("<html>");
            if (dataType.equals("Artist")) {
                for (Object obj : data) {
                    Artist artist = (Artist) obj;
                    dataString.append(String.format("<b>%s</b>: %,d followers<br>", artist.getName(), artist.getFollowers().getTotal()));
                }
            } else if (dataType.equals("Track")) {
                for (Object obj : data) {
                    Track track = (Track) obj;
                    dataString.append(String.format("%s - %s (%s)<br>", track.getName(), track.getArtists()[0].getName(), track.getAlbum().getName()));
                }
            }
            dataString.append("</html>");
            dataLabel.setText(dataString.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
