package spotifyapi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.enums.AuthorizationScope;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.SavedTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class SpotifyConnection {

    private static final String clientId = "69988c8c50ba43819bb74ffd852f2852";
    private static final String clientSecret = "9a2eb1989e31438c898a3810c51ce3dc";
    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/");
    private String authorizationCode;

    private SpotifyApi spotifyApi = new SpotifyApi.Builder()
      .setClientId(clientId)
      .setClientSecret(clientSecret)
      .setRedirectUri(redirectUri)
      .build();

    private AuthorizationScope[] authorizationScopes = {
      AuthorizationScope.APP_REMOTE_CONTROL,
      AuthorizationScope.PLAYLIST_MODIFY_PRIVATE,
      AuthorizationScope.PLAYLIST_MODIFY_PUBLIC,
      AuthorizationScope.PLAYLIST_READ_COLLABORATIVE,
      AuthorizationScope.PLAYLIST_READ_PRIVATE,
      AuthorizationScope.STREAMING,
      AuthorizationScope.UGC_IMAGE_UPLOAD,
      AuthorizationScope.USER_FOLLOW_MODIFY,
      AuthorizationScope.USER_FOLLOW_READ,
      AuthorizationScope.USER_LIBRARY_MODIFY,
      AuthorizationScope.USER_LIBRARY_READ,
      AuthorizationScope.USER_MODIFY_PLAYBACK_STATE,
      AuthorizationScope.USER_READ_CURRENTLY_PLAYING,
      AuthorizationScope.USER_READ_EMAIL,
      AuthorizationScope.USER_READ_PLAYBACK_POSITION,
      AuthorizationScope.USER_READ_PLAYBACK_STATE,
      AuthorizationScope.USER_READ_PRIVATE,
      AuthorizationScope.USER_READ_RECENTLY_PLAYED,
      AuthorizationScope.USER_TOP_READ
    };

    public void authorizeUser() {
        try {
            AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri().scope(authorizationScopes).build();
            URI uri = authorizationCodeUriRequest.execute();

            HttpServer server = HttpServer.create(new java.net.InetSocketAddress(8080), 0);
            server.createContext("/", new AuthHandler());
            server.start();

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                // Check if browsing is supported
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(uri);
                } else {
                    System.out.println("BROWSE action is not supported on this platform.");
                }
            } else {
                System.out.println("Desktop class is not supported on this platform.");
            }

            // Wait for the authorization code
            while (authorizationCode == null) {
                System.out.println("Waiting for authorization code...");
                Thread.sleep(100); // Sleep briefly to avoid busy-waiting
            }

            setAccessToken(authorizationCode);
            server.stop(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class AuthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Received request: " + exchange.getRequestURI());

            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.contains("code=")) {
                // Respond to the browser
                authorizationCode = query.split("=")[1];
                String response = "Authorization successful! You can close this window.";
                exchange.sendResponseHeaders(200, response.length());

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                // Handle the case where code is not present
                String response = "Authorization failed!";
                exchange.sendResponseHeaders(400, response.length());

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    public void setAccessToken(String authorizationCode) {
        try {
            AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(authorizationCode).build();
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Artist[] getTopArtists() throws IOException, ParseException, SpotifyWebApiException {
        return spotifyApi.getUsersTopArtists().build().execute().getItems();
    }

    public Track[] getTopTracks() throws IOException, ParseException, SpotifyWebApiException {
        return spotifyApi.getUsersTopTracks().build().execute().getItems();
    }

    public SavedTrack[] getSavedTracks() throws IOException, ParseException, SpotifyWebApiException {
        return spotifyApi.getUsersSavedTracks().build().execute().getItems();
    }

    public String getUsersName() {
        try {
            return spotifyApi.getCurrentUsersProfile().build().execute().getDisplayName();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return "";
        }
    }
}
