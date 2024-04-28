package net.radioreg.radioPlayer.activity;

import net.radioreg.radioPlayer.Main;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.types.TabbedActivity;
import net.labymod.api.client.gui.screen.widget.widgets.navigation.tab.DefaultComponentTab;

@AutoActivity
public class MainActivity extends TabbedActivity {

  public MainActivity() {
    Main addon = Main.get();

    this.register("radioreg_start", new DefaultComponentTab(Component.translatable("radioreg.navigation.station-menu"), addon.stationsActivity));
  }
}
