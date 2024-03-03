package de.optischa.radioPlayer.activity;

import de.optischa.radioPlayer.Main;
import de.optischa.radioPlayer.widget.StationWidget;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.LabyScreen;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import java.util.Arrays;

@AutoActivity
@Link("overview.lss")
public class StationsActivity extends Activity {
  private final Main addon;
  private StationWidget selectStation;
  private final VerticalListWidget<StationWidget> stationWidgetList;

  public StationsActivity() {
    this.addon = Main.get();

    this.stationWidgetList = new VerticalListWidget<>();
    this.stationWidgetList.addId("stations-list");

    this.stationWidgetList.selectable().set(true);

    this.stationWidgetList.setSelectCallback(stationWidget -> {
      StationWidget selectedWidget = this.stationWidgetList.listSession().getSelectedEntry();
      if(this.selectStation != null) {
        this.selectStation.removeId("selected");
      }
      this.selectStation = selectedWidget;
      selectedWidget.addId("selected");
      this.addon.configuration().setSelectedStream(selectedWidget.getStream());
      this.addon.musicPlayer.play(stationWidget.getStream());
    });
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    FlexibleContentWidget container = new FlexibleContentWidget();
    container.addId("stations-container");

    if(addon.musicPlayer.streams == null) {
      container.addContent(ComponentWidget.component(Component.text("No Stream found")));
    } else {

      Arrays.stream(addon.musicPlayer.streams).forEach(stream -> {
        StationWidget stationWidget = new StationWidget(stream);
        if (stream.id == addon.configuration().selectedStream().id) {
          stationWidget.addId("selected");
          this.selectStation = stationWidget;
        }
        this.stationWidgetList.addChild(stationWidget);
      });

      this.stationWidgetList.selectable().set(true);

      container.addFlexibleContent(new ScrollWidget(this.stationWidgetList));

    }

    this.document().addChild(container);
  }

  @Override
  public <T extends LabyScreen> T renew() {
    return null;
  }


}
