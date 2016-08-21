package com.netbrasoft.gnuob.application.page.tab;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

public class ReportTab extends AbstractTab {

  private static final long serialVersionUID = 4835579949680085443L;

  public ReportTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    return new WebMarkupContainer(panelId);
  }
}
