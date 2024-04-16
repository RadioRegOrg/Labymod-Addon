package de.optischa.radioPlayer.player.gson;

public class Stream {
  public Stream() {
  }

  public Stream(int id, String name, String url, Song song, Organization organization) {
    this.id = id;
    this.name = name;
    this.url = url;
    this.song = song;
    this.organization = organization;
  }

  public int id;

  public String name;
  public String url;
  public Song song;
  public Organization organization;
}
