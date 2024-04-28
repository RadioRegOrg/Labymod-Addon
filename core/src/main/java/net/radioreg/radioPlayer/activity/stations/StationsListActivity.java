package net.radioreg.radioPlayer.activity.stations;

import net.radioreg.radioPlayer.Main;
import net.radioreg.radioPlayer.activity.OrganizationActivity;
import net.radioreg.radioPlayer.player.gson.Organization;
import net.radioreg.radioPlayer.player.gson.Stream;
import net.radioreg.radioPlayer.widget.StationWidget;
import java.util.List;
import java.util.Locale;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.action.ListSession;
import net.labymod.api.client.gui.screen.widget.action.Pressable;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.TilesGridWidget;
import net.labymod.api.localization.Internationalization;
import net.labymod.api.util.io.web.result.Result;

@Link("overview.lss")
@AutoActivity
public class StationsListActivity extends Activity {

  private String searchQuery = "";
  private final Main addon;
  private final Internationalization internationalization;
  private final ListSession<Stream> session = new ListSession<>();

  public StationsListActivity(Main addon) {
    this.addon = addon;
    this.internationalization = Laby.references().internationalization();
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    Result<List<Stream>> indexResult = this.getStreams();
    String errorString = null;
    if (indexResult.isEmpty()) {
      if (this.addon.musicPlayer.streams.isEmpty()) {
        errorString = this.internationalization.translate(
            "radioreg.activity.stationslist.search.noIndex");
      } else {
        errorString = this.internationalization.translate(
            "radioreg.activity.stationslist.search.noSearch");
      }
    }

    if (indexResult.hasException()) {
      errorString = this.internationalization.translate("radioreg.misc.errorWithArgs",
          indexResult.exception().getMessage());
    }
    ComponentWidget error;
    if (errorString != null) {
      error = ComponentWidget.text(errorString);
      error.addId("error");
      this.document().addChild(error);
    } else {
      if (!indexResult.isPresent()) {
        error = ComponentWidget.i18n("radioreg.activity.stationslist.search.noFilter");
        error.addId("error");
        this.document().addChild(error);
      }

      TilesGridWidget<StationWidget> grid = new TilesGridWidget<>();
      List<Stream> modifications = indexResult.get();

      for (Stream stream : modifications) {
        List<Organization> organizations = addon.getOrganizations().stream().filter(organization -> organization.id == stream.organization.id).toList();
        StationWidget stationWidget = new StationWidget(stream, this.openProfile(organizations.getFirst()));
        stationWidget.setActive(addon.configuration().selectedStream().id == stream.id);
        grid.addTile(stationWidget);
      }

      this.document().addChild((new ScrollWidget(grid, this.session)).addId("scroll"));
    }
  }

  private Result<List<Stream>> getStreams() {
    List<Stream> streams = this.addon.musicPlayer.streams;
    if (streams.isEmpty()) {
      return Result.empty();
    } else {
      List<Stream> streamList;
      streamList = streams.stream()
          .filter(stream -> stream.name.toLowerCase(Locale.ROOT).contains(this.searchQuery.toLowerCase()))
          .toList();
      return !streamList.isEmpty() ? Result.of(streamList) : Result.empty();
    }
  }

  public String getSearchQuery() {
    return this.searchQuery;
  }

  public void search(String query) {
    this.searchQuery = query;
    this.reload();
  }

  private Pressable openProfile(Organization organization) {
    return () -> this.displayScreen(new OrganizationActivity(this, organization));
  }
}
