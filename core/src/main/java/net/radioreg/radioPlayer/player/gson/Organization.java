package net.radioreg.radioPlayer.player.gson;

public class Organization {

  public Organization(int id, String name, String description, String thumbnail, String image, Stream[] streams, OrganizationLink[] links) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.thumbnail = thumbnail;
    this.image = image;
    this.streams = streams;
    this.links = links;
  }

  public int id;
  public String name;
  public String description;
  public String thumbnail;
  public String image;
  public Stream[] streams;
  public OrganizationLink[] links;
}
