package de.optischa.radioPlayer.player;

import de.optischa.radioPlayer.Main;
import de.optischa.radioPlayer.activity.StationsActivity;
import net.labymod.api.client.component.Component;
import net.labymod.api.notification.Notification;
import net.labymod.api.notification.Notification.Builder;
import net.labymod.api.notification.Notification.Type;

public class PlayerNotification {

    public static Notification sendInfoNotification(String title, String message) {
      Builder notification = Notification.builder();
      notification.title(Component.translatable(title));
      notification.text(Component.translatable(message));
      notification.duration(5000);
      notification.onClick((notification1) -> {
        Main.get().labyAPI().activityOverlayRegistry().toOverlay(new StationsActivity());
      });
      notification.type(Type.SYSTEM);
      return notification.build();
    }

  public static Notification sendInfoNotification(Component title, Component message) {
    Builder notification = Notification.builder();
    notification.title(title);
    notification.text(message);
    notification.duration(5000);
    notification.onClick((notification1) -> {
      Main.get().labyAPI().activityOverlayRegistry().toOverlay(new StationsActivity());
    });
    notification.type(Type.SYSTEM);
    return notification.build();
  }

  public static Notification sendInfoNotification(Component title, String message) {
    Builder notification = Notification.builder();
    notification.title(title);
    notification.text(Component.translatable(message));
    notification.duration(5000);
    notification.onClick((notification1) -> {
      Main.get().labyAPI().activityOverlayRegistry().toOverlay(new StationsActivity());
    });
    notification.type(Type.SYSTEM);
    return notification.build();
  }

  public static Notification sendInfoNotification(String title, Component message) {
    Builder notification = Notification.builder();
    notification.title(Component.translatable(title));
    notification.text(message);
    notification.duration(5000);
    notification.onClick((notification1) -> {
      Main.get().labyAPI().activityOverlayRegistry().toOverlay(new StationsActivity());
    });
    notification.type(Type.SYSTEM);
    return notification.build();
  }
}
