package de.optischa.radioPlayer.player.gson;

public class UpdateStream {
  public UpdateStream(int id, String title, String artist, String cover) {
    this.id = id;
    this.title = title;
    this.artist = artist;
    this.cover = cover;
  }

  public int id;

  public String title;
  public String artist;
  public String cover;

}
