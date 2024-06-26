package net.radioreg.radioPlayer.widget;

import net.radioreg.radioPlayer.Main;
import net.radioreg.radioPlayer.player.gson.Stream;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.TilesGridWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import java.util.List;

@AutoWidget
@Link("overview.lss")
public class OrganizationStreamWidget extends VerticalListWidget<Widget> {
  private final List<Stream> streams;
  private final Main addon;

  public OrganizationStreamWidget(List<Stream> streams) {
    this.streams = streams;
    this.addon = Main.get();
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    TilesGridWidget<StationWidget> grid = new TilesGridWidget<>();

    for (Stream stream : streams) {
      StationWidget stationWidget = new StationWidget(stream, null);
      stationWidget.setActive(addon.configuration().selectedStream().id == stream.id);
      grid.addTile(stationWidget);
    }

    this.addChild(grid);
  }
}
