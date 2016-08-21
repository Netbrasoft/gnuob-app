package com.netbrasoft.gnuob.application.panel;

import java.util.ArrayList;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants;
import com.netbrasoft.gnuob.application.page.tab.AdministrationTab;
import com.netbrasoft.gnuob.application.page.tab.AlertTab;
import com.netbrasoft.gnuob.application.page.tab.AllTab;
import com.netbrasoft.gnuob.application.page.tab.CrmTab;
import com.netbrasoft.gnuob.application.page.tab.PmTab;
import com.netbrasoft.gnuob.application.page.tab.ReportTab;
import com.netbrasoft.gnuob.application.security.AppRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE, AppRoles.ADMINISTRATOR})
public class MainMenuPanel extends Panel {

  private static final long serialVersionUID = -1204513473673934094L;

  private static final String MAIN_MENU_TABBED_PANEL_ID = "mainMenuTabbedPanel";

  private final BootstrapTabbedPanel<ITab> mainMenuTabbedPanel;

  public MainMenuPanel(final String id) {
    super(id);
    mainMenuTabbedPanel = new BootstrapTabbedPanel<ITab>(MAIN_MENU_TABBED_PANEL_ID, new ArrayList<ITab>());
  }

  @Override
  protected void onInitialize() {
    final CrmTab crmTab = new CrmTab(Model.of(MainMenuPanel.this.getString(NetbrasoftApplicationConstants.CRM_MESSAGE_KEY)));
    final PmTab pmTab = new PmTab(Model.of(MainMenuPanel.this.getString(NetbrasoftApplicationConstants.PM_MESSAGE_KEY)));
    final AlertTab AlertsTab = new AlertTab(Model.of(MainMenuPanel.this.getString(NetbrasoftApplicationConstants.ALERTS_MESSAGE_KEY)));
    final ReportTab reportTabs = new ReportTab(Model.of(MainMenuPanel.this.getString(NetbrasoftApplicationConstants.REPORTS_MESSAGE_KEY)));
    final AdministrationTab administrationTab = new AdministrationTab(Model.of(MainMenuPanel.this.getString(NetbrasoftApplicationConstants.ADMINISTRATION_MESSAGE_KEY)));
    final AllTab allTab = new AllTab(Model.of(MainMenuPanel.this.getString(NetbrasoftApplicationConstants.ALL_MESSAGE_KEY)));

    mainMenuTabbedPanel.getTabs().add(crmTab);
    mainMenuTabbedPanel.getTabs().add(pmTab);
    mainMenuTabbedPanel.getTabs().add(AlertsTab);
    mainMenuTabbedPanel.getTabs().add(reportTabs);
    mainMenuTabbedPanel.getTabs().add(administrationTab);
    mainMenuTabbedPanel.getTabs().add(allTab);
    add(mainMenuTabbedPanel);

    super.onInitialize();
  }
}
