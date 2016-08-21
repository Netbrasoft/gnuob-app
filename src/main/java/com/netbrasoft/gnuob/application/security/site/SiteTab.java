package com.netbrasoft.gnuob.application.security.site;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.api.Site;

public class SiteTab extends AbstractTab {

  private static final long serialVersionUID = 4835579949680085443L;

  public SiteTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    final Site site = new Site();
    site.setActive(true);
    return new SitePanel(panelId, Model.of(site));
  }
}
