package com.netbrasoft.gnuob.application.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.application.border.ContentBorder;
import com.netbrasoft.gnuob.application.panel.MainMenuPanel;
import com.netbrasoft.gnuob.application.security.AppRoles;

@MountPath("application.html")
@AuthorizeInstantiation({AppRoles.MANAGER, AppRoles.EMPLOYEE, AppRoles.ADMINISTRATOR})
public class MainPage extends BasePage {

  private static final String MAIN_MENU_PANEL_ID = "mainMenuPanel";

  private static final String CONTENT_BORDER_ID = "contentBorder";

  private static final long serialVersionUID = 2104311609974795936L;

  private final MainMenuPanel mainMenuPanel;

  private final ContentBorder contentBorder;

  public MainPage() {
    mainMenuPanel = new MainMenuPanel(MAIN_MENU_PANEL_ID);
    contentBorder = new ContentBorder(CONTENT_BORDER_ID);
  }

  @Override
  protected void onInitialize() {
    contentBorder.add(mainMenuPanel.setOutputMarkupId(true));
    add(contentBorder.setOutputMarkupId(true));

    super.onInitialize();
  }
}
