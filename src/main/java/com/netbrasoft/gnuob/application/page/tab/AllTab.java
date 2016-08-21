package com.netbrasoft.gnuob.application.page.tab;

import java.util.ArrayList;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants;
import com.netbrasoft.gnuob.application.category.CategoryTab;
import com.netbrasoft.gnuob.application.content.ContentTab;
import com.netbrasoft.gnuob.application.contract.ContractTab;
import com.netbrasoft.gnuob.application.customer.CustomerTab;
import com.netbrasoft.gnuob.application.offer.OfferTab;
import com.netbrasoft.gnuob.application.order.OrderTab;
import com.netbrasoft.gnuob.application.product.ProductTab;
import com.netbrasoft.gnuob.application.security.group.GroupTab;
import com.netbrasoft.gnuob.application.security.site.SiteTab;
import com.netbrasoft.gnuob.application.security.user.UserTab;
import com.netbrasoft.gnuob.application.setting.SettingTab;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

public class AllTab extends AbstractTab {

  private static final String NAV_NAV_PILLS_NAV_STACKED_COL_MD_2_CSS_CLASS = "nav nav-pills nav-stacked col-md-2";

  private static final long serialVersionUID = -6759649165281101893L;

  public AllTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    final BootstrapTabbedPanel<ITab> allTabbedPanel = new BootstrapTabbedPanel<ITab>(panelId, new ArrayList<ITab>()) {

      private static final long serialVersionUID = -8650291789763661400L;

      @Override
      public String getTabContainerCssClass() {
        return NAV_NAV_PILLS_NAV_STACKED_COL_MD_2_CSS_CLASS;
      }
    };
    final SettingTab settingTab = new SettingTab(Model.of(allTabbedPanel.getString(NetbrasoftApplicationConstants.SETTING_MESSAGE_KEY)));
    final SiteTab siteTab = new SiteTab(Model.of(allTabbedPanel.getString(NetbrasoftApplicationConstants.SITE_MESSAGE_KEY)));
    final GroupTab groupTab = new GroupTab(Model.of(allTabbedPanel.getString(NetbrasoftApplicationConstants.GROUP_MESSAGE_KEY)));
    final UserTab userTab = new UserTab(Model.of(allTabbedPanel.getString(NetbrasoftApplicationConstants.USER_MESSAGE_KEY)));
    final CategoryTab categoryTab = new CategoryTab(Model.of(allTabbedPanel.getString(NetbrasoftApplicationConstants.CATEGORY_MESSAGE_KEY)));
    final ContentTab contentTab = new ContentTab(Model.of(allTabbedPanel.getString(NetbrasoftApplicationConstants.CONTENT_MESSAGE_KEY)));
    final ProductTab productTab = new ProductTab(Model.of(allTabbedPanel.getString(NetbrasoftApplicationConstants.PRODUCT_MESSAGE_KEY)));
    final CustomerTab customerTab = new CustomerTab(Model.of(allTabbedPanel.getString(NetbrasoftApplicationConstants.CUSTOMER_MESSAGE_KEY)));
    final ContractTab contractTab = new ContractTab(Model.of(allTabbedPanel.getString(NetbrasoftApplicationConstants.CONTRACT_MESSAGE_KEY)));
    final OrderTab orderTab = new OrderTab(Model.of(allTabbedPanel.getString(NetbrasoftApplicationConstants.ORDER_MESSAGE_KEY)));
    final OfferTab offerTab = new OfferTab(Model.of(allTabbedPanel.getString(NetbrasoftApplicationConstants.OFFER_MESSAGE_KEY)));

    allTabbedPanel.getTabs().add(settingTab);
    allTabbedPanel.getTabs().add(siteTab);
    allTabbedPanel.getTabs().add(groupTab);
    allTabbedPanel.getTabs().add(userTab);
    allTabbedPanel.getTabs().add(categoryTab);
    allTabbedPanel.getTabs().add(contentTab);
    allTabbedPanel.getTabs().add(productTab);
    allTabbedPanel.getTabs().add(customerTab);
    allTabbedPanel.getTabs().add(contractTab);
    allTabbedPanel.getTabs().add(orderTab);
    allTabbedPanel.getTabs().add(offerTab);
    return allTabbedPanel;
  }
}
