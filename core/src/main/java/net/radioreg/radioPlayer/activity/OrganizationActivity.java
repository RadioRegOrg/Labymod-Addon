package net.radioreg.radioPlayer.activity;

import net.radioreg.radioPlayer.player.gson.Organization;
import net.radioreg.radioPlayer.player.gson.OrganizationLink;
import net.radioreg.radioPlayer.widget.GradientImageWidget;
import net.radioreg.radioPlayer.widget.OrganizationLinkWidget;
import net.radioreg.radioPlayer.widget.OrganizationStreamWidget;
import net.labymod.api.Laby;
import net.labymod.api.Textures;
import net.labymod.api.Textures.SpriteCommon;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.activity.Document;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.entry.HorizontalListEntry;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.VariableIconWidget;

@AutoActivity()
@Link("organization.lss")
public class OrganizationActivity extends Activity {

  private final Organization organization;
  private final Activity fallback;
  private final OrganizationStreamWidget organizationStreamWidget;


  public OrganizationActivity(Activity fallback, Organization organization) {
    this.fallback = fallback;
    this.organization = organization;

    this.organizationStreamWidget = new OrganizationStreamWidget(organization.streams);
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
    IconWidget thumbnailWidget = new VariableIconWidget(organization.thumbnail,
        OrganizationActivity::getVariableBrandUrl);
    thumbnailWidget.addId("thumbnail");
    thumbnailWidget.setCleanupOnDispose(true);
    detailsContainer.addChild(thumbnailWidget);

    GradientImageWidget thumbnailGradient = new GradientImageWidget();
    thumbnailGradient.addId("thumbnail");
    detailsContainer.addChild(thumbnailGradient);
    IconWidget icon = new VariableIconWidget(
        Textures.DEFAULT_SERVER_ICON, organization.image == null ? null : organization.image,
        OrganizationActivity::getVariableBrandUrl);
    icon.addId("icon");
    icon.setCleanupOnDispose(true);
    detailsContainer.addChild(icon);

    VerticalListWidget<Widget> infoContainer = new VerticalListWidget<>();
    infoContainer.addId("info-container");
    ComponentWidget name = ComponentWidget.text(organization.name);
    name.addId("name");
    infoContainer.addChild(name);
    ComponentWidget shortDescription = ComponentWidget.text(organization.description);
    shortDescription.addId("short-description");
    infoContainer.addChild(shortDescription);

    HorizontalListWidget horizontalListWidget = new HorizontalListWidget();
    horizontalListWidget.addId("links-container");

    for(OrganizationLink organizationLink : organization.links) {
      OrganizationLinkWidget linkWidget = new OrganizationLinkWidget(organizationLink);
      linkWidget.setPressable(() -> Laby.references().chatExecutor().openUrl(organizationLink.value, true));
      horizontalListWidget.addChild(new HorizontalListEntry(linkWidget));
    }

    infoContainer.addChild(horizontalListWidget);

    detailsContainer.addChild(infoContainer);

    container.addChild(detailsContainer);

    container.addChild(this.organizationStreamWidget);

    ScrollWidget scrollWidget = new ScrollWidget(container);
    DivWidget scrollContainerWidget = (new DivWidget()).addId("scroll-container");
    scrollContainerWidget.addChild(scrollWidget);
    this.document().addChild(scrollContainerWidget);
    ButtonWidget backButton = ButtonWidget.icon(SpriteCommon.BACK_BUTTON, () -> {
      this.fallback.displayScreen(this.fallback);
    });
    backButton.addId("back-button");
    DivWidget topContainer = (DivWidget)(new DivWidget()).addId("top-container");
    topContainer.addChild(backButton);


    this.document().addChild(topContainer);
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
