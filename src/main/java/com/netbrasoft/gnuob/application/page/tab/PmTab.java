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
import com.netbrasoft.gnuob.application.product.ProductTab;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

public class PmTab extends AbstractTab {

  private static final String NAV_NAV_PILLS_NAV_STACKED_COL_MD_2_CSS_CLASS = "nav nav-pills nav-stacked col-md-2";

  private static final long serialVersionUID = 4835579949680085443L;

  public PmTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    final BootstrapTabbedPanel<ITab> productTabbedPanel = new BootstrapTabbedPanel<ITab>(panelId, new ArrayList<ITab>()) {

      private static final long serialVersionUID = -8650291789763661400L;

      @Override
      public String getTabContainerCssClass() {
        return NAV_NAV_PILLS_NAV_STACKED_COL_MD_2_CSS_CLASS;
      }
    };
    final CategoryTab categoryTab = new CategoryTab(Model.of(productTabbedPanel.getString(NetbrasoftApplicationConstants.CATEGORY_MESSAGE_KEY)));
    final ContentTab contentTab = new ContentTab(Model.of(productTabbedPanel.getString(NetbrasoftApplicationConstants.CONTENT_MESSAGE_KEY)));
    final ProductTab productTab = new ProductTab(Model.of(productTabbedPanel.getString(NetbrasoftApplicationConstants.PRODUCT_MESSAGE_KEY)));

    productTabbedPanel.getTabs().add(categoryTab);
    productTabbedPanel.getTabs().add(contentTab);
    productTabbedPanel.getTabs().add(productTab);
    return productTabbedPanel;
  }
}
