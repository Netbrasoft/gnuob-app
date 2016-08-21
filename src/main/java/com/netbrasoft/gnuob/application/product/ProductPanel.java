package com.netbrasoft.gnuob.application.product;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.PRODUCT_DATA_PROVIDER_NAME;
import static de.agilecoders.wicket.jquery.JQuery.$;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
import com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants;
import com.netbrasoft.gnuob.application.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.application.security.AppRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapBaseBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.MediumSpanType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.agilecoders.wicket.core.util.Attributes;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationConfig;

/**
 * Panel for viewing, selecting and editing {@link Product} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class ProductPanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class ProductPanelContainer extends WebMarkupContainer {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class ProductTableContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class AddAjaxLink extends BootstrapAjaxLink<Product> {

        private static final long serialVersionUID = -8317730269644885290L;

        public AddAjaxLink(final String id, final IModel<Product> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.plus);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          final Product product = new Product();
          product.setActive(true);
          AddAjaxLink.this.setDefaultModelObject(product);
          target.add(productViewOrEditPanel.setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
      class ProductDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
        class ProductDataview extends DataView<Product> {

          @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
          class RemoveAjaxLink extends BootstrapAjaxLink<Product> {

            private static final long serialVersionUID = -8317730269644885290L;

            private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

            public RemoveAjaxLink(final String id, final IModel<Product> model, final Buttons.Type type,
                final IModel<String> labelModel) {
              super(id, model, type, labelModel);
              setIconType(GlyphIconType.remove);
              setSize(Buttons.Size.Mini);
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
              try {
                productDataProvider.remove((Product) RemoveAjaxLink.this.getDefaultModelObject());
              } catch (final RuntimeException e) {
                LOGGER.warn(e.getMessage(), e);
                productTableContainer.warn(e.getLocalizedMessage());
              } finally {
                target.add(productTableContainer.setOutputMarkupId(true));
              }
            }

            @Override
            protected void onInitialize() {
              final ConfirmationBehavior confirmationBehavior = new ConfirmationBehavior() {

                private static final long serialVersionUID = 7744720444161839031L;

                @Override
                public void renderHead(final Component component, final IHeaderResponse response) {
                  response.render($(component)
                      .chain(CONFIRMATION_FUNCTION_NAME,
                          new ConfirmationConfig()
                              .withTitle(getString(NetbrasoftApplicationConstants.CONFIRMATION_MESSAGE_KEY))
                              .withSingleton(true).withPopout(true)
                              .withBtnOkLabel(getString(NetbrasoftApplicationConstants.CONFIRM_MESSAGE_KEY))
                              .withBtnCancelLabel(getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)))
                      .asDomReadyScript());
                }
              };
              add(confirmationBehavior);
              super.onInitialize();
            }
          }

          private static final String REMOVE_ID = "remove";

          private static final String CLICK_EVENT = "click";

          private static final String NAME_ID = NAME_PROPERTY;

          private static final String NUMBER_ID = NUMBER_PROPERTY;

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = -5039874949058607907L;

          private int index = 0;

          protected ProductDataview(final String id, final IDataProvider<Product> dataProvider,
              final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<Product> newItem(final String id, final int index, final IModel<Product> model) {
            final Item<Product> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void populateItem(final Item<Product> item) {
            item.setModel(new CompoundPropertyModel<Product>(item.getModelObject()));
            item.add(new Label(NUMBER_ID).setOutputMarkupId(true));
            item.add(new Label(NAME_ID).setOutputMarkupId(true));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                target
                    .add(productDataviewContainer.setDefaultModelObject(item.getModelObject()).setOutputMarkupId(true));
                target.add(productViewOrEditPanel.setOutputMarkupId(true));
              }
            });

            item.add(new RemoveAjaxLink(REMOVE_ID, item.getModel(), Buttons.Type.Default,
                Model.of(ProductPanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY))));
          }
        }

        private static final String PRODUCT_DATAVIEW_ID = "productDataview";

        private static final long serialVersionUID = 7937233338451825416L;

        private static final int ITEMS_PER_PAGE = 5;

        private final ProductDataview productDataview;

        public ProductDataviewContainer(final String id, final IModel<Product> model) {
          super(id, model);
          productDataview = new ProductDataview(PRODUCT_DATAVIEW_ID, productDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(productDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String PRODUCT_PAGING_NAVIGATOR_MARKUP_ID = "productPagingNavigator";

      private static final String PRODUCT_DATAVIEW_CONTAINER_ID = "productDataviewContainer";

      private static final String NAME_PROPERTY = "name";

      private static final String ORDER_BY_NAME_ID = "orderByName";

      private static final String NUMBER_PROPERTY = "number";

      private static final String ORDER_BY_NUMBER_ID = "orderByNumber";

      private static final String ADD_ID = "add";

      private static final String FEEDBACK_ID = "feedback";

      private static final long serialVersionUID = 3705612551357130293L;

      private final NotificationPanel feedbackPanel;

      private final AddAjaxLink addAjaxLink;

      private final OrderByBorder<String> orderByNumber;

      private final OrderByBorder<String> orderByName;

      private final ProductDataviewContainer productDataviewContainer;

      private final BootstrapPagingNavigator productPagingNavigator;

      public ProductTableContainer(final String id, final IModel<Product> model) {
        super(id, model);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
        addAjaxLink = new AddAjaxLink(ADD_ID, (IModel<Product>) ProductTableContainer.this.getDefaultModel(),
            Buttons.Type.Primary,
            Model.of(ProductPanel.this.getString(NetbrasoftApplicationConstants.ADD_MESSAGE_KEY)));
        orderByNumber = new OrderByBorder<String>(ORDER_BY_NUMBER_ID, NUMBER_PROPERTY, productDataProvider);
        orderByName = new OrderByBorder<String>(ORDER_BY_NAME_ID, NAME_PROPERTY, productDataProvider);
        productDataviewContainer = new ProductDataviewContainer(PRODUCT_DATAVIEW_CONTAINER_ID,
            (IModel<Product>) ProductTableContainer.this.getDefaultModel());
        productPagingNavigator =
            new BootstrapPagingNavigator(PRODUCT_PAGING_NAVIGATOR_MARKUP_ID, productDataviewContainer.productDataview);
      }

      @Override
      protected void onInitialize() {
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(addAjaxLink.setOutputMarkupId(true));
        add(orderByName.setOutputMarkupId(true));
        add(orderByNumber.setOutputMarkupId(true));
        add(productDataviewContainer.setOutputMarkupId(true));
        add(productPagingNavigator.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String PRODUCT_VIEW_OR_EDIT_PANEL_ID = "productViewOrEditPanel";

    private static final String PRODUCT_TABLE_CONTAINER_ID = "productTableContainer";

    private static final long serialVersionUID = 2889522881821847572L;

    private final ProductViewOrEditPanel productViewOrEditPanel;

    private final ProductTableContainer productTableContainer;

    public ProductPanelContainer(final String id, final IModel<Product> model) {
      super(id, model);
      productTableContainer = new ProductTableContainer(PRODUCT_TABLE_CONTAINER_ID,
          (IModel<Product>) ProductPanelContainer.this.getDefaultModel());
      productViewOrEditPanel = new ProductViewOrEditPanel(PRODUCT_VIEW_OR_EDIT_PANEL_ID,
          (IModel<Product>) ProductPanelContainer.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(productTableContainer.add(new TableBehavior().hover()).setOutputMarkupId(true));
      add(productViewOrEditPanel.add(productViewOrEditPanel.new ProductViewFragment()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final String PRODUCT_PANEL_CONTAINER_ID = "productPanelContainer";

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductPanel.class);

  private static final long serialVersionUID = 3703226064705246155L;

  @SpringBean(name = PRODUCT_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Product> productDataProvider;

  private final ProductPanelContainer productPanelContainer;

  public ProductPanel(final String id, final IModel<Product> model) {
    super(id, model);
    productPanelContainer =
        new ProductPanelContainer(PRODUCT_PANEL_CONTAINER_ID, (IModel<Product>) ProductPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    productDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    productDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    productDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    productDataProvider.setType(new Product());
    productDataProvider.getType().setActive(true);
    if (productDataProvider.size() > 0) {
      ProductPanel.this.setDefaultModelObject(productDataProvider.iterator(0, 1).next());
    }
    add(productPanelContainer.add(new BootstrapBaseBehavior() {

      private static final long serialVersionUID = -4903722864597601489L;

      @Override
      public void onComponentTag(final Component component, final ComponentTag tag) {
        Attributes.addClass(tag, MediumSpanType.SPAN10);
      }
    }).setOutputMarkupId(true));
    super.onInitialize();
  }
}
