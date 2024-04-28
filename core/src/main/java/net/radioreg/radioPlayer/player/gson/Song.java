package net.radioreg.radioPlayer.player.gson;

public class Song {
  public Song(String title, String artist, String cover) {
    this.title = title;
    this.artist = artist;
    this.cover = cover;
  }

  public String title;
  public String artist;
  public String cover;
}
