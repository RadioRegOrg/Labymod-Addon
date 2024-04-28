package net.radioreg.radioPlayer.player.gson;

public class UpdateStream {
  public UpdateStream(int id, String name, String url, String[] tags) {
    this.id = id;
    this.tags = tags;
    this.url = url;
    this.name = name;
  }

  public int id;
  public String[] tags;
  public String url;
  public String name;
}
