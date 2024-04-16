package de.optischa.radioPlayer.player.gson;

public class Organization {
  public Organization(int id, String name, String logo, String thumbnail, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.thumbnail = thumbnail;
    this.logo = logo;
  }

  public int id;
  public String name;
  public String logo;
  public String thumbnail;
  public String description;
}
