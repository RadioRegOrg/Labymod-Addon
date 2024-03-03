package de.optischa.radioPlayer.player;

import com.google.gson.Gson;
import de.optischa.radioPlayer.player.gson.Stream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class Request {
  private static final String protocol = "https";
  private static final String apiAddress = "api.radioreg.net";

  public static Stream[] getStreams() {
    return sendGet("stream", Stream[].class);
  }

  public static Stream getStream(int id) {
    return sendGet("stream/" + id + "/info", Stream.class);
  }

  private static <T> T sendGet(String path, Class<T> tClass) {
    try {
      URL url = new URL(protocol + "://" + apiAddress + "/" + path);
      URLConnection urlConnection = url.openConnection();
      InputStream inputStream = urlConnection.getInputStream();
      String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      return new Gson().fromJson(text, tClass);
    } catch (Exception ignored) {

    }
    return null;
  }

}
