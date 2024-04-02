package de.optischa.radioPlayer.activity;

import de.optischa.radioPlayer.Main;
import de.optischa.radioPlayer.widget.StationWidget;
import java.util.Arrays;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.LabyScreen;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.TilesGridWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.ScreenRendererWidget;

@AutoActivity
@Link("overview.lss")
public class StationsActivity extends Activity {

  private final Main addon;

  public StationsActivity() {
    this.addon = Main.get();
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    FlexibleContentWidget container = new FlexibleContentWidget();
    container.addId("container");

    FlexibleContentWidget header = new FlexibleContentWidget();
    header.addId("header");

    TextFieldWidget textFieldWidget = new TextFieldWidget();
    textFieldWidget.addId("search-widget");

    textFieldWidget.placeholder(Component.text("Search"));

    header.addFlexibleContent(textFieldWidget);

    container.addContent(header);

    ScreenRendererWidget screenRendererWidget = new ScreenRendererWidget();
    screenRendererWidget.addId("screen-renderer");

    TilesGridWidget<StationWidget> tilesGridWidget = new TilesGridWidget<>();

    if (addon.musicPlayer.streams == null) {
      // container.addContent(ComponentWidget.component(Component.text("No Stream found")));
    } else {
      Arrays.stream(addon.musicPlayer.streams).forEach(stream -> {
        StationWidget stationWidget = new StationWidget(stream);
        stationWidget.setPressListener(() -> {
          this.addon.configuration().setSelectedStream(stream);
          this.addon.musicPlayer.play(stream);
          return true;
        });
        tilesGridWidget.addSortedTile(stationWidget);
      });
    }

    //screenRendererWidget.addChild(tilesGridWidget);

    // ScrollWidget scrollWidget = new ScrollWidget();

    container.addContent(tilesGridWidget);

    this.document.addChild(container);
  }

  @Override
  public <T extends LabyScreen> T renew() {
    return null;
  }
}
