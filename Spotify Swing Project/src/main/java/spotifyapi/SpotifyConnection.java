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
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.SavedTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

public class SpotifyConnection {

    private static final String CLIENT_ID = "69988c8c50ba43819bb74ffd852f2852";
    private static final String CLIENT_SECRET = "9a2eb1989e31438c898a3810c51ce3dc";
    private static final URI REDIRECT_URI = SpotifyHttpManager.makeUri("http://localhost:8080/");
    private static final String AUTH_SUCCESS_MSG = "Authorization successful! You can close this window.";
    private static final String AUTH_FAIL_MSG = "Authorization failed!";

    private String authorizationCode;

    private final SpotifyApi spotifyApi = new SpotifyApi.Builder()
      .setClientId(CLIENT_ID)
      .setClientSecret(CLIENT_SECRET)
      .setRedirectUri(REDIRECT_URI)
      .build();

    private final AuthorizationScope[] authorizationScopes = {
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
            URI authUri = spotifyApi.authorizationCodeUri()
              .scope(authorizationScopes)
              .build()
              .execute();

            HttpServer server = createHttpServer();
            openBrowser(authUri);

            // Wait for the authorization code asynchronously
            waitForAuthorizationCode();

            server.stop(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpServer createHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new AuthHandler());
        server.start();
        return server;
    }

    private void openBrowser(URI uri) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(uri);
                } catch (IOException e) {
                    System.err.println("Error opening browser: " + e.getMessage());
                }
            } else {
                System.out.println("BROWSE action is not supported on this platform.");
            }
        } else {
            System.out.println("Desktop class is not supported on this platform.");
        }
    }

    private void waitForAuthorizationCode() throws InterruptedException {
        while (authorizationCode == null) {
            System.out.println("Waiting for authorization code...");
            Thread.sleep(100); // Sleep briefly to avoid busy-waiting
        }
        setAccessToken(authorizationCode);
    }

    private class AuthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.contains("code=")) {
                authorizationCode = query.split("=")[1];
                sendResponse(exchange, 200, AUTH_SUCCESS_MSG);
            } else {
                sendResponse(exchange, 400, AUTH_FAIL_MSG);
            }
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    public void setAccessToken(String authorizationCode) {
        try {
            AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(authorizationCode).build();
            AuthorizationCodeCredentials credentials = authorizationCodeRequest.execute();
            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.err.println("Error setting access token: " + e.getMessage());
        }
    }

    public Artist[] getTopArtists() {
        try {
            return spotifyApi.getUsersTopArtists().build().execute().getItems();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return new Artist[0];
        }
    }

    public Track[] getTopTracks() {
        try {
            return spotifyApi.getUsersTopTracks().build().execute().getItems();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return new Track[0];
        }
    }

    public SavedTrack[] getSavedTracks() {
        try {
            return spotifyApi.getUsersSavedTracks().build().execute().getItems();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return new SavedTrack[0];
        }
    }

    public String getUsersName() {
        try {
            return spotifyApi.getCurrentUsersProfile().build().execute().getDisplayName();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return "";
        }
    }

    public PlaylistSimplified[] getListOfCurrentUsersPlaylists() {
        try {
            return spotifyApi.getListOfCurrentUsersPlaylists().build().execute().getItems();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return new PlaylistSimplified[0];
        }
    }
}