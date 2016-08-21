package com.netbrasoft.gnuob.application.page.tab;

import java.util.ArrayList;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants;
import com.netbrasoft.gnuob.application.contract.ContractTab;
import com.netbrasoft.gnuob.application.customer.CustomerTab;
import com.netbrasoft.gnuob.application.offer.OfferTab;
import com.netbrasoft.gnuob.application.order.OrderTab;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

public class CrmTab extends AbstractTab {

  private static final String NAV_NAV_PILLS_NAV_STACKED_COL_MD_2_CSS_CLASS = "nav nav-pills nav-stacked col-md-2";

  private static final long serialVersionUID = 4835579949680085443L;

  public CrmTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    final BootstrapTabbedPanel<ITab> crmTabbedPanel = new BootstrapTabbedPanel<ITab>(panelId, new ArrayList<ITab>()) {

      private static final long serialVersionUID = -8650291789763661400L;

      @Override
      public String getTabContainerCssClass() {
        return NAV_NAV_PILLS_NAV_STACKED_COL_MD_2_CSS_CLASS;
      }
    };
    final CustomerTab customerTab = new CustomerTab(Model.of(crmTabbedPanel.getString(NetbrasoftApplicationConstants.CUSTOMER_MESSAGE_KEY)));
    final ContractTab contractTab = new ContractTab(Model.of(crmTabbedPanel.getString(NetbrasoftApplicationConstants.CONTRACT_MESSAGE_KEY)));
    final OrderTab orderTab = new OrderTab(Model.of(crmTabbedPanel.getString(NetbrasoftApplicationConstants.ORDER_MESSAGE_KEY)));
    final OfferTab offerTab = new OfferTab(Model.of(crmTabbedPanel.getString(NetbrasoftApplicationConstants.OFFER_MESSAGE_KEY)));

    crmTabbedPanel.getTabs().add(customerTab);
    crmTabbedPanel.getTabs().add(contractTab);
    crmTabbedPanel.getTabs().add(orderTab);
    crmTabbedPanel.getTabs().add(offerTab);
    return crmTabbedPanel;
  }
}
