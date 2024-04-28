package net.radioreg.radioPlayer.activity;

import net.radioreg.radioPlayer.Main;
import net.radioreg.radioPlayer.activity.stations.StationsListActivity;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.ScreenInstance;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.ScreenRendererWidget;

@AutoActivity
@Link("overview.lss")
public class StationsActivity extends Activity {

  private final TextFieldWidget searchWidget;
  private final StationsListActivity stationsListActivity;
  private final ScreenRendererWidget screenRendererWidget;
  private boolean pseudoReload;

  public StationsActivity() {
    Main addon = Main.get();

    this.stationsListActivity = new StationsListActivity(addon);
    this.screenRendererWidget = (new ScreenRendererWidget()).addId(
        "screen-renderer");
    this.screenRendererWidget.setPreviousScreenHandler((screen) -> screen instanceof OrganizationActivity);
    this.screenRendererWidget.displayScreen(this.stationsListActivity);
    this.screenRendererWidget.addDisplayListener((screen) -> this.reload());

    this.searchWidget = new TextFieldWidget();
    this.searchWidget.placeholder(
        Component.translatable("radioreg.activity.textfield.search"));
    this.searchWidget.addId("search-widget");
    this.searchWidget.updateListener((text) -> {
      ScreenInstance screen = this.screenRendererWidget.getScreen();
      boolean reload = false;
      if (text.isEmpty()) {
        reload = true;
      } else if (this.pseudoReload) {
        reload = true;
      }

      this.stationsListActivity.search(text);

      if (reload && !(screen instanceof OrganizationActivity)) {
        if (this.pseudoReload) {
          this.pseudoReload = false;
        } else {
          this.reload();
        }
      }

      if (screen instanceof OrganizationActivity) {
        this.screenRendererWidget.displayScreen(this.stationsListActivity);
      }
    });
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    this.document.getChildren().clear();
    String text = this.searchWidget.getText();
    String query;
    if (this.screenRendererWidget.getScreen() instanceof StationsListActivity) {
      query = this.stationsListActivity.getSearchQuery();
    } else {
      query = "";
    }

    if (!text.equals(query)) {
      this.pseudoReload = true;
      this.searchWidget.setText(query);
    }

    FlexibleContentWidget container = (new FlexibleContentWidget()).addId(
        "container");
    FlexibleContentWidget header = (new FlexibleContentWidget()).addId(
        "header");

    header.addFlexibleContent(this.searchWidget);

    container.addContent(header);
    container.addFlexibleContent(this.screenRendererWidget);
    this.document.addChild(container);
  }

  public void onCloseScreen() {
    super.onCloseScreen();
  }

  public void updateStations() {
    if (this.isOpen()) {
      Laby.labyAPI().minecraft().executeOnRenderThread(this::reload);
    }
  }
}
