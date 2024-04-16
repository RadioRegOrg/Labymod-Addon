package de.optischa.radioPlayer.activity;

import de.optischa.radioPlayer.player.gson.Stream;
import de.optischa.radioPlayer.widget.GradientImageWidget;
import net.labymod.api.Textures;
import net.labymod.api.Textures.SpriteCommon;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.activity.Document;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.VariableIconWidget;

@AutoActivity()
@Link("organization.lss")
public class OrganizationActivity extends Activity {

  private final Stream stream;
  private ScrollWidget scrollWidget;
  private final Activity fallback;


  public OrganizationActivity(Activity fallback, Stream stream) {
    this.fallback = fallback;
    this.stream = stream;
  }

  public void onOpenScreen() {
    ((Document) this.document).playAnimation("fade-in");
    super.onOpenScreen();
  }

  public void onCloseScreen() {
    ((Document) this.document).playAnimation("fade-out");
    super.onCloseScreen();
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    VerticalListWidget<Widget> container = new VerticalListWidget<>();
    container.addId("container");
    DivWidget detailsContainer = new DivWidget();
    detailsContainer.addId("details-container");

    detailsContainer.addId("details-with-thumbnail");
    IconWidget thumbnailWidget = new VariableIconWidget(stream.organization.thumbnail,
        OrganizationActivity::getVariableBrandUrl);
    thumbnailWidget.addId("thumbnail");
    thumbnailWidget.setCleanupOnDispose(true);
    detailsContainer.addChild(thumbnailWidget);

    GradientImageWidget thumbnailGradient = new GradientImageWidget();
    thumbnailGradient.addId("thumbnail");
    detailsContainer.addChild(thumbnailGradient);
    IconWidget icon = new VariableIconWidget(
        Textures.DEFAULT_SERVER_ICON, stream.organization == null ? null : stream.organization.logo,
        OrganizationActivity::getVariableBrandUrl);
    icon.addId("icon");
    icon.setCleanupOnDispose(true);
    detailsContainer.addChild(icon);

    container.addChild(detailsContainer);

    this.scrollWidget = new ScrollWidget(container);
    DivWidget scrollContainerWidget = (DivWidget)(new DivWidget()).addId("scroll-container");
    scrollContainerWidget.addChild(this.scrollWidget);
    ((Document)this.document()).addChild(scrollContainerWidget);
    ButtonWidget backButton = ButtonWidget.icon(SpriteCommon.BACK_BUTTON, () -> {
      this.fallback.displayScreen(this.fallback);
    });
    backButton.addId("back-button");
    DivWidget topContainer = (DivWidget)(new DivWidget()).addId("top-container");
    topContainer.addChild(backButton);
    ((Document)this.document()).addChild(topContainer);
  }

  public static String getVariableBrandUrl(String baseUrl, int width, int height) {
    String url = baseUrl;
    if (height != 0) {
      url = url + "?height=" + height;
    }

    if (width != 0) {
      if (height == 0) {
        url = url + "?";
      } else {
        url = url + "&";
      }

      url = url + "width=" + width;
    }

    return url;
  }
}
