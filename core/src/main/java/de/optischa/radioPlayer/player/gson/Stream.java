package de.optischa.radioPlayer.player.gson;

public class Stream {
  public Stream() {
  }

  public Stream(int id, String name, String artist, String title, String url, String cover, String thumbnail) {
    this.id = id;
    this.name = name;
    this.artist = artist;
    this.title = title;
    this.url = url;
    this.cover = cover;
    this.thumbnail = thumbnail;
  }

  public int id;

  public String name;

  public String artist;

  public String title;

  public String url;

  public String cover;

  public String thumbnail;
}
