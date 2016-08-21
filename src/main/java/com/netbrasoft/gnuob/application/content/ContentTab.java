package com.netbrasoft.gnuob.application.content;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.api.Content;

public class ContentTab extends AbstractTab {

  private static final long serialVersionUID = 1461232963628306073L;

  public ContentTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    final Content content = new Content();
    content.setActive(true);
    return new ContentPanel(panelId, Model.of(content));
  }
}
