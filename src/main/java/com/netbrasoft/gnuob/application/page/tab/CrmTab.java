package com.netbrasoft.gnuob.application.page.tab;

import java.util.ArrayList;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.application.contract.ContractTab;
import com.netbrasoft.gnuob.application.customer.CustomerTab;
import com.netbrasoft.gnuob.application.offer.OfferTab;
import com.netbrasoft.gnuob.application.order.OrderTab;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

public class CrmTab extends AbstractTab {

   private static final long serialVersionUID = 4835579949680085443L;

   private ITab contractTab = new ContractTab(Model.of("Contract"));
   private ITab customerTab = new CustomerTab(Model.of("Customer"));
   private ITab offerTab = new OfferTab(Model.of("Offer"));
   private ITab orderTab = new OrderTab(Model.of("Order"));

   public CrmTab(final IModel<String> title) {
      super(title);
   }

   @Override
   public WebMarkupContainer getPanel(final String panelId) {
      BootstrapTabbedPanel<ITab> crmTabbedPanel = new BootstrapTabbedPanel<ITab>(panelId, new ArrayList<ITab>()) {

         private static final long serialVersionUID = -8650291789763661400L;

         @Override
         public String getTabContainerCssClass() {
            return "nav nav-pills nav-stacked col-md-2";
         }
      };

      crmTabbedPanel.getTabs().add(customerTab);
      crmTabbedPanel.getTabs().add(contractTab);
      crmTabbedPanel.getTabs().add(orderTab);
      crmTabbedPanel.getTabs().add(offerTab);

      return crmTabbedPanel;
   }
}
