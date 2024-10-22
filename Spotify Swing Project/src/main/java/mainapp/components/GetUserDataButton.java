package mainapp.components;

import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.SavedTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import spotifyapi.SpotifyConnection;

import javax.swing.*;
import java.awt.*;

public class GetUserDataButton extends JPanel {

    private final JPanel cards;
    private final SpotifyConnection spotifyConnection;

    public GetUserDataButton(
      JFrame mainAppFrame,
      SpotifyConnection spotifyConnection,
      JPanel cards,
      String buttonLabel,
      String dataType)
    {
        this.cards = cards;
        this.spotifyConnection = spotifyConnection;

        // Set the layout for the panel
        setLayout(new FlowLayout(FlowLayout.CENTER));

        // Create the button with the passed label
        JButton getSpotifyDataButton = new JButton(buttonLabel);

        // Add an action listener to the button
        getSpotifyDataButton.addActionListener(e -> {
            // Change the size of the MainApp frame
            int height = dataType.equals("SavedPlaylist") ? 425 : 900;
            mainAppFrame.setSize(750, height);

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
     * Updates the printDataPanel with the latest data depending on dataType.
     */
    private void updatePrintDataPanel(String dataType) {
        JPanel printDataPanel = (JPanel) cards.getComponent(1);
        JLabel dataLabel = (JLabel) printDataPanel.getClientProperty("printDataPanel");
        dataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dataLabel.setText(buildDataString(fetchData(dataType)));
    }


    /**
     * Builds an HTML-formatted string for display based on the data objects.
     */
    private String buildDataString(Object[] data) {
        StringBuilder dataString = new StringBuilder("<html><div style='text-align: center;'>");

        for (Object obj : data) {
            if (obj instanceof Artist artist) {
                dataString.append(formatArtistData(artist));
            } else if (obj instanceof Track track) {
                dataString.append(formatTrackData(track));
            } else if (obj instanceof SavedTrack savedTrack) {
                dataString.append(formatTrackData(savedTrack.getTrack()));
            } else if (obj instanceof PlaylistSimplified playlist) {
                dataString.append(formatPlaylistData(playlist));
            }
        }

        dataString.append("</div></html>");
        return dataString.toString();
    }

    /**
     * Formats artist data into a string.
     */
    private String formatArtistData(Artist artist) {
        return String.format("<b>%s</b>: %,d followers<br>", artist.getName(), artist.getFollowers().getTotal());
    }

    /**
     * Formats track data into a string.
     */
    private String formatTrackData(Track track) {
        return String.format("%s - %s (%s)<br>", track.getName(), track.getArtists()[0].getName(), track.getAlbum().getName());
    }

    /**
     * Formats playlist data into a string.
     */
    private String formatPlaylistData(PlaylistSimplified playlist) {
        return String.format("%s (%s)<br>", playlist.getName(), playlist.getId());
    }

    /**
     * Fetches data from Spotify based on the dataType passed in.
     */
    private Object[] fetchData(String dataType) {
        return switch (dataType) {
            case "Artists" -> spotifyConnection.getTopArtists();
            case "Tracks" -> spotifyConnection.getTopTracks();
            case "SavedTracks" -> spotifyConnection.getSavedTracks();
            case "SavedPlaylists" -> spotifyConnection.getListOfCurrentUsersPlaylists();
            default -> new Object[0]; // Return an empty array for unsupported types
        };
    }
}
