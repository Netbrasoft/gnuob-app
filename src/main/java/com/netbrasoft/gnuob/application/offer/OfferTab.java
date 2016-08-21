package com.netbrasoft.gnuob.application.offer;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.api.Offer;

public class OfferTab extends AbstractTab {

  private static final long serialVersionUID = 4835579949680085443L;

  public OfferTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    final Offer offer = new Offer();
    offer.setActive(true);
    return new OfferPanel(panelId, Model.of(offer));
  }
}
