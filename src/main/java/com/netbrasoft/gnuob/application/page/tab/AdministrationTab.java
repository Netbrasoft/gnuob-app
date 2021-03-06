package com.netbrasoft.gnuob.application.page.tab;

import java.util.ArrayList;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.application.security.group.GroupTab;
import com.netbrasoft.gnuob.application.security.site.SiteTab;
import com.netbrasoft.gnuob.application.security.user.UserTab;
import com.netbrasoft.gnuob.application.setting.SettingTab;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

public class AdministrationTab extends AbstractTab {

   private static final long serialVersionUID = 4835579949680085443L;

   private ITab groupTab = new GroupTab(Model.of("Group"));
   private ITab settingTab = new SettingTab(Model.of("Setting"));
   private ITab siteTab = new SiteTab(Model.of("Site"));
   private ITab userTab = new UserTab(Model.of("User"));

   public AdministrationTab(final IModel<String> title) {
      super(title);
   }

   @Override
   public WebMarkupContainer getPanel(final String panelId) {
      BootstrapTabbedPanel<ITab> administrationTabbedPanel = new BootstrapTabbedPanel<ITab>(panelId, new ArrayList<ITab>()) {

         private static final long serialVersionUID = -8650291789763661400L;

         @Override
         public String getTabContainerCssClass() {
            return "nav nav-pills nav-stacked col-md-2";
         }
      };

      administrationTabbedPanel.getTabs().add(userTab);
      administrationTabbedPanel.getTabs().add(groupTab);
      administrationTabbedPanel.getTabs().add(siteTab);
      administrationTabbedPanel.getTabs().add(settingTab);

      return administrationTabbedPanel;
   }
}
