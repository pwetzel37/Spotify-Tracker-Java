package mainapp.components;

import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.SavedTrack;
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
            Object[] data = getDataObjects(dataType);

            JPanel printDataPanel = (JPanel) cards.getComponent(1);
            JLabel dataLabel = (JLabel) printDataPanel.getClientProperty("printDataPanel");

            StringBuilder dataString = new StringBuilder("<html>");
            for (Object obj : data) {
                if (obj instanceof Artist artist) {
                    dataString.append(String.format("<b>%s</b>: %,d followers<br>", artist.getName(), artist.getFollowers().getTotal()));
                } else if (obj instanceof Track track) {
                    dataString.append(String.format("%s - %s (%s)<br>", track.getName(), track.getArtists()[0].getName(), track.getAlbum().getName()));
                } else if (obj instanceof SavedTrack savedTrack) {
                    dataString.append(String.format("%s - %s (%s)<br>", savedTrack.getTrack().getName(), savedTrack.getTrack().getArtists()[0].getName(), savedTrack.getTrack().getAlbum().getName()));
                }
            }
            dataString.append("</html>");

            dataLabel.setText(dataString.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Object[] getDataObjects(String dataType) {
        try {
            return switch (dataType) {
                case "Artists" -> spotifyConnection.getTopArtists();
                case "Tracks" -> spotifyConnection.getTopTracks();
                case "SavedTracks" -> spotifyConnection.getSavedTracks();
                default -> new Object[0]; // Return an empty array for unsupported types
            };
        } catch (Exception ex) {
            System.err.println("Error fetching data for type: " + dataType + ". " + ex.getMessage());
            return new Object[0]; // Return an empty array on error
        }
    }
}
