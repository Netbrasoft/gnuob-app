package com.netbrasoft.gnuob.application.page.tab;

import java.util.ArrayList;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants;
import com.netbrasoft.gnuob.application.security.group.GroupTab;
import com.netbrasoft.gnuob.application.security.site.SiteTab;
import com.netbrasoft.gnuob.application.security.user.UserTab;
import com.netbrasoft.gnuob.application.setting.SettingTab;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

public class AdministrationTab extends AbstractTab {

  private static final String NAV_NAV_PILLS_NAV_STACKED_COL_MD_2_CSS_CLASS = "nav nav-pills nav-stacked col-md-2";

  private static final long serialVersionUID = 4835579949680085443L;

  public AdministrationTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    final BootstrapTabbedPanel<ITab> administrationTabbedPanel = new BootstrapTabbedPanel<ITab>(panelId, new ArrayList<ITab>()) {

      private static final long serialVersionUID = -8650291789763661400L;

      @Override
      public String getTabContainerCssClass() {
        return NAV_NAV_PILLS_NAV_STACKED_COL_MD_2_CSS_CLASS;
      }
    };
    final SettingTab settingTab = new SettingTab(Model.of(administrationTabbedPanel.getString(NetbrasoftApplicationConstants.SETTING_MESSAGE_KEY)));
    final SiteTab siteTab = new SiteTab(Model.of(administrationTabbedPanel.getString(NetbrasoftApplicationConstants.SITE_MESSAGE_KEY)));
    final GroupTab groupTab = new GroupTab(Model.of(administrationTabbedPanel.getString(NetbrasoftApplicationConstants.GROUP_MESSAGE_KEY)));
    final UserTab userTab = new UserTab(Model.of(administrationTabbedPanel.getString(NetbrasoftApplicationConstants.USER_MESSAGE_KEY)));

    administrationTabbedPanel.getTabs().add(settingTab);
    administrationTabbedPanel.getTabs().add(siteTab);
    administrationTabbedPanel.getTabs().add(groupTab);
    administrationTabbedPanel.getTabs().add(userTab);
    return administrationTabbedPanel;
  }
}
