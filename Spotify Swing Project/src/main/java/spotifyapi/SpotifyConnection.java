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

    // For all requests an access token is needed
    private SpotifyApi spotifyApi = new SpotifyApi.Builder()
      .setClientId(clientId)
      .setClientSecret(clientSecret)
      .setRedirectUri(redirectUri)
      .build();

    public void getAuthorizationCode() {
        try {
            AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri().scope(AuthorizationScope.USER_TOP_READ).build();
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
                Thread.sleep(1000); // Sleep briefly to avoid busy-waiting
            }

            // Print the authorization code and stop server
            System.out.println("Authorization Code: " + authorizationCode);
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
                authorizationCode = query.split("=")[1];
                // Respond to the browser
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
}
