package net.radioreg.radioPlayer.listener;

import net.radioreg.radioPlayer.Main;
import net.radioreg.radioPlayer.event.AllOrganizationsEvent;
import net.radioreg.radioPlayer.event.UpdateOrganizationEvent;
import net.radioreg.radioPlayer.event.station.AddStationEvent;
import net.radioreg.radioPlayer.event.station.AllStationEvent;
import net.radioreg.radioPlayer.event.station.RemoveStationEvent;
import net.radioreg.radioPlayer.event.station.StationUpdateEvent;
import net.radioreg.radioPlayer.event.station.StreamChangeTrackEvent;
import net.radioreg.radioPlayer.event.station.UpdateStreamEvent;
import net.radioreg.radioPlayer.player.gson.Organization;
import net.radioreg.radioPlayer.player.gson.Song;
import net.radioreg.radioPlayer.player.gson.Stream;
import net.radioreg.radioPlayer.player.gson.UpdateStream;
import net.radioreg.radioPlayer.player.gson.UpdateStreamContent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.labymod.api.event.Subscribe;

public class StreamUpdateListener {

  private final Main addon;

  public StreamUpdateListener() {
    this.addon = Main.get();
  }

  @Subscribe
  public void onAllStationEvent(AllStationEvent event) {
    if (event.streams() != null) {
      this.addon.musicPlayer.streams = Arrays.stream(event.streams()).toList();
    }
  }

  @Subscribe
  public void onAllStationEvent(StationUpdateEvent event) {
    UpdateStreamContent updateStream = event.updateStream();
    List<Stream> newStreams = new ArrayList<>();

    addon.musicPlayer.streams.forEach(stream -> {
      if (stream.id == updateStream.id) {
        Stream newStream = new Stream(stream.id, stream.name, stream.url,
            new Song(updateStream.title, updateStream.artist, updateStream.cover),
            stream.organization);
        newStreams.add(newStream);

        if (Main.get().musicPlayer.isPlaying()) {
          if (updateStream.id == addon.configuration().selectedStream().id) {
            addon.configuration().setSelectedStream(newStream);
            addon.labyAPI().eventBus().fire(new StreamChangeTrackEvent(newStream));
          }
        }
      } else {
        newStreams.add(stream);
      }
    });

    if (addon.getOrganizations().stream().anyMatch(organization ->
        Arrays.stream(organization.streams).anyMatch(stream -> stream.id == updateStream.id))) {
      addon.setOrganizations(addon.getOrganizations().stream()
          .peek(organization -> {
            if (Arrays.stream(organization.streams)
                .anyMatch(stream -> stream.id == updateStream.id)) {
              List<Stream> newStreams1 = new ArrayList<>();

              addon.musicPlayer.streams.forEach(stream -> {
                if (stream.id == updateStream.id) {
                  Stream newStream = new Stream(stream.id, stream.name, stream.url,
                      new Song(updateStream.title, updateStream.artist, updateStream.cover),
                      stream.organization);
                  newStreams1.add(newStream);
                } else {
                  newStreams1.add(stream);
                }
              });

              organization.streams = newStreams.toArray(new Stream[0]);
            }
          })
          .collect(Collectors.toList()));
    }

    addon.musicPlayer.streams = newStreams;
    addon.stationsActivity.updateStations();
  }

  @Subscribe
  public void onUpdateStreamEvent(UpdateStreamEvent event) {
    UpdateStream updateStream = event.stream();
    List<Stream> newStreams = new ArrayList<>();

    addon.musicPlayer.streams.forEach(stream -> {
      if (stream.id == updateStream.id) {
        Stream newStream = new Stream(updateStream.id, updateStream.name, updateStream.url,
            stream.song, stream.organization);
        newStreams.add(newStream);

        if (Main.get().musicPlayer.isPlaying()) {
          if (updateStream.id == addon.configuration().selectedStream().id) {
            addon.configuration().setSelectedStream(newStream);
            if(!stream.url.equalsIgnoreCase(updateStream.url)) {
              addon.musicPlayer.changeNewURL(newStream);
            }
          }
        }
      } else {
        newStreams.add(stream);
      }
    });

    if (addon.getOrganizations().stream().anyMatch(organization ->
        Arrays.stream(organization.streams).anyMatch(stream -> stream.id == updateStream.id))) {
      addon.setOrganizations(addon.getOrganizations().stream()
          .peek(organization -> {
            if (Arrays.stream(organization.streams)
                .anyMatch(stream -> stream.id == updateStream.id)) {
              List<Stream> newStreams1 = new ArrayList<>();

              addon.musicPlayer.streams.forEach(stream -> {
                if (stream.id == updateStream.id) {
                  Stream newStream = new Stream(updateStream.id, updateStream.name, updateStream.url,
                      stream.song, stream.organization);
                  newStreams1.add(newStream);
                } else {
                  newStreams1.add(stream);
                }
              });

              organization.streams = newStreams.toArray(new Stream[0]);
            }
          })
          .collect(Collectors.toList()));
    }

    addon.musicPlayer.streams = newStreams;
    addon.stationsActivity.updateStations();
  }

  @Subscribe
  public void onChangeTrack(StreamChangeTrackEvent event) {
    addon.stationsActivity.updateStations();
  }

  @Subscribe
  public void onChangeTrack(AllOrganizationsEvent event) {
    addon.setOrganizations(Arrays.asList(event.organizations()));
  }

  @Subscribe
  public void onAddStationEvent(AddStationEvent event) {
    addon.musicPlayer.streams.add(event.stream());
    addon.stationsActivity.updateStations();
  }

  @Subscribe
  public void onUpdateOrganizationEvent(UpdateOrganizationEvent event) {
    Organization updatedOrganization = event.organization();
    int idToUpdate = updatedOrganization.id;

    for (int i = 0; i < addon.getOrganizations().size(); i++) {
      Organization org = addon.getOrganizations().get(i);
      if (org.id == idToUpdate) {
        addon.getOrganizations().set(i, updatedOrganization);
        break;
      }
    }
  }

  @Subscribe
  public void onRemoveStationEvent(RemoveStationEvent event) {
    List<Stream> streamsCopy = new ArrayList<>(addon.musicPlayer.streams);
    Iterator<Stream> iterator = streamsCopy.iterator();
    while (iterator.hasNext()) {
      Stream stream = iterator.next();
      if (stream.id == event.stream().id) {
        iterator.remove();
        addon.musicPlayer.streams = streamsCopy;
        if (stream.id == addon.configuration().selectedStream().id
            && addon.musicPlayer.isPlaying()) {
          addon.musicPlayer.stop();
        }
        addon.stationsActivity.updateStations();
        break;
      }
    }
  }

}
