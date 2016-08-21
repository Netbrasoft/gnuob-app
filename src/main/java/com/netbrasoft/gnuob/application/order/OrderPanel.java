package com.netbrasoft.gnuob.application.order;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.ORDER_DATA_PROVIDER_NAME;
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

import com.netbrasoft.gnuob.api.Invoice;
import com.netbrasoft.gnuob.api.Order;
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
 * Panel for viewing, selecting and editing {@link Order} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class OrderPanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class OrderPanelContainer extends WebMarkupContainer {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class OrderTableContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class AddAjaxLink extends BootstrapAjaxLink<Order> {

        private static final long serialVersionUID = -8317730269644885290L;

        public AddAjaxLink(final String id, final IModel<Order> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.plus);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          final Order order = new Order();
          order.setActive(true);
          order.setInvoice(new Invoice());
          AddAjaxLink.this.setDefaultModelObject(order);
          target.add(orderViewOrEditPanel.setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
      class OrderDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
        class OrderDataview extends DataView<Order> {

          @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
          class RemoveAjaxLink extends BootstrapAjaxLink<Order> {

            private static final long serialVersionUID = -8317730269644885290L;

            public RemoveAjaxLink(final String id, final IModel<Order> model, final Buttons.Type type,
                final IModel<String> labelModel) {
              super(id, model, type, labelModel);
              setIconType(GlyphIconType.remove);
              setSize(Buttons.Size.Mini);
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
              try {
                orderDataProvider.remove((Order) RemoveAjaxLink.this.getDefaultModelObject());
              } catch (final RuntimeException e) {
                LOGGER.warn(e.getMessage(), e);
                orderTableContainer.warn(e.getLocalizedMessage());
              } finally {
                target.add(orderPanelContainer.setOutputMarkupId(true));
              }
            }
          }

          private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

          private static final String REMOVE_ID = "remove";

          private static final String CLICK_EVENT = "click";

          private static final String CONTRACT_CUSTOMER_LAST_NAME_ID = CONTRACT_CUSTOMER_LAST_NAME_PROPERTY;

          private static final String CONTRACT_CUSTOMER_FIRST_NAME_ID = CONTRACT_CUSTOMER_FIRST_NAME_PROPERTY;

          private static final String CONTRACT_CONTRACT_ID_ID = CONTRACT_CONTRACT_ID_PROPERTY;

          private static final String ORDER_ID_ID = ORDER_ID_PROPERTY;

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = -5039874949058607907L;

          private int index;

          protected OrderDataview(final String id, final IDataProvider<Order> dataProvider, final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<Order> newItem(final String id, final int index, final IModel<Order> model) {
            final Item<Order> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void populateItem(final Item<Order> item) {
            item.setModel(new CompoundPropertyModel<Order>(item.getModelObject()));
            item.add(new Label(ORDER_ID_ID));
            item.add(new Label(CONTRACT_CONTRACT_ID_ID));
            item.add(new Label(CONTRACT_CUSTOMER_FIRST_NAME_ID));
            item.add(new Label(CONTRACT_CUSTOMER_LAST_NAME_ID));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                target.add(orderDataviewContainer.setDefaultModelObject(item.getModelObject()).setOutputMarkupId(true));
                target.add(orderViewOrEditPanel.setOutputMarkupId(true));
              }
            });
            item.add(new RemoveAjaxLink(REMOVE_ID, item.getModel(), Buttons.Type.Default,
                Model.of(OrderPanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY)))
                    .add(new ConfirmationBehavior() {

                      private static final long serialVersionUID = 7744720444161839031L;

                      @Override
                      public void renderHead(final Component component, final IHeaderResponse response) {
                        response
                            .render(
                                $(component)
                                    .chain(CONFIRMATION_FUNCTION_NAME,
                                        new ConfirmationConfig()
                                            .withTitle(
                                                getString(NetbrasoftApplicationConstants.CONFIRMATION_MESSAGE_KEY))
                                            .withSingleton(true).withPopout(true)
                                            .withBtnOkLabel(
                                                getString(NetbrasoftApplicationConstants.CONFIRM_MESSAGE_KEY))
                                            .withBtnCancelLabel(
                                                getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)))
                                    .asDomReadyScript());
                      }
                    }));
          }
        }

        private static final String ORDER_DATAVIEW_ID = "orderDataview";

        private static final long serialVersionUID = -6700605975126870961L;

        private static final int ITEMS_PER_PAGE = 5;

        private final OrderDataview orderDataview;

        public OrderDataviewContainer(final String id, final IModel<Order> model) {
          super(id, model);
          orderDataview = new OrderDataview(ORDER_DATAVIEW_ID, orderDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(orderDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String ORDER_PAGING_NAVIGATOR_MARKUP_ID = "orderPagingNavigator";

      private static final String ORDER_DATAVIEW_CONTAINER_ID = "orderDataviewContainer";

      private static final String ORDER_BY_CONTRACT_ID_ID = "orderByContractId";

      private static final String CONTRACT_CONTRACT_ID_PROPERTY = "contract.contractId";

      private static final String ORDER_ID_PROPERTY = "orderId";

      private static final String ORDER_BY_ORDER_ID_ID = "orderByOrderId";

      private static final String ORDER_BY_LAST_NAME_ID = "orderByLastName";

      private static final String CONTRACT_CUSTOMER_LAST_NAME_PROPERTY = "contract.customer.lastName";

      private static final String CONTRACT_CUSTOMER_FIRST_NAME_PROPERTY = "contract.customer.firstName";

      private static final String ORDER_BY_FIRST_NAME_ID = "orderByFirstName";

      private static final String ADD_ID = "add";

      private static final String FEEDBACK_ID = "feedback";

      private static final long serialVersionUID = 24914318472386879L;

      private final NotificationPanel feedbackPanel;

      private final AddAjaxLink addAjaxLink;

      private final OrderByBorder<String> orderByFirstName;

      private final OrderByBorder<String> orderByLastName;

      private final OrderByBorder<String> orderByOrderId;

      private final OrderByBorder<String> orderByContractId;

      private final OrderDataviewContainer orderDataviewContainer;

      private final BootstrapPagingNavigator orderPagingNavigator;

      public OrderTableContainer(final String id, final IModel<Order> model) {
        super(id, model);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
        addAjaxLink = new AddAjaxLink(ADD_ID, (IModel<Order>) OrderTableContainer.this.getDefaultModel(),
            Buttons.Type.Primary, Model.of(OrderPanel.this.getString(NetbrasoftApplicationConstants.ADD_MESSAGE_KEY)));
        orderByFirstName =
            new OrderByBorder<String>(ORDER_BY_FIRST_NAME_ID, CONTRACT_CUSTOMER_FIRST_NAME_PROPERTY, orderDataProvider);
        orderByLastName =
            new OrderByBorder<String>(ORDER_BY_LAST_NAME_ID, CONTRACT_CUSTOMER_LAST_NAME_PROPERTY, orderDataProvider);
        orderByOrderId = new OrderByBorder<String>(ORDER_BY_ORDER_ID_ID, ORDER_ID_PROPERTY, orderDataProvider);
        orderByContractId =
            new OrderByBorder<String>(ORDER_BY_CONTRACT_ID_ID, CONTRACT_CONTRACT_ID_PROPERTY, orderDataProvider);
        orderDataviewContainer = new OrderDataviewContainer(ORDER_DATAVIEW_CONTAINER_ID,
            (IModel<Order>) OrderTableContainer.this.getDefaultModel());
        orderPagingNavigator =
            new BootstrapPagingNavigator(ORDER_PAGING_NAVIGATOR_MARKUP_ID, orderDataviewContainer.orderDataview);
      }

      @Override
      protected void onInitialize() {
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(addAjaxLink.setOutputMarkupId(true));
        add(orderByFirstName.setOutputMarkupId(true));
        add(orderByLastName.setOutputMarkupId(true));
        add(orderByOrderId.setOutputMarkupId(true));
        add(orderByContractId.setOutputMarkupId(true));
        add(orderDataviewContainer.setOutputMarkupId(true));
        add(orderPagingNavigator.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String ORDER_VIEW_OR_EDIT_PANEL_ID = "orderViewOrEditPanel";

    private static final String ORDER_TABLE_CONTAINER_ID = "orderTableContainer";

    private static final long serialVersionUID = 4490006925509789607L;

    private final OrderViewOrEditPanel orderViewOrEditPanel;

    private final OrderTableContainer orderTableContainer;

    public OrderPanelContainer(final String id, final IModel<Order> model) {
      super(id, model);
      orderTableContainer =
          new OrderTableContainer(ORDER_TABLE_CONTAINER_ID, (IModel<Order>) OrderPanelContainer.this.getDefaultModel());
      orderViewOrEditPanel = new OrderViewOrEditPanel(ORDER_VIEW_OR_EDIT_PANEL_ID,
          (IModel<Order>) OrderPanelContainer.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(orderTableContainer.add(new TableBehavior().hover()).setOutputMarkupId(true));
      add(orderViewOrEditPanel.add(orderViewOrEditPanel.new OrderViewFragment()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final String ORDER_PANEL_CONTAINER_ID = "orderPanelContainer";

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderPanel.class);

  private static final long serialVersionUID = 3703226064705246155L;

  @SpringBean(name = ORDER_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Order> orderDataProvider;

  private final OrderPanelContainer orderPanelContainer;

  public OrderPanel(final String id, final IModel<Order> model) {
    super(id, model);
    orderPanelContainer =
        new OrderPanelContainer(ORDER_PANEL_CONTAINER_ID, (IModel<Order>) OrderPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    orderDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    orderDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    orderDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    orderDataProvider.setType(new Order());
    orderDataProvider.getType().setActive(true);
    if (orderDataProvider.size() > 0) {
      OrderPanel.this.setDefaultModelObject(orderDataProvider.iterator(0, 1).next());
    }
    add(orderPanelContainer.add(new BootstrapBaseBehavior() {

      private static final long serialVersionUID = -4903722864597601489L;

      @Override
      public void onComponentTag(final Component component, final ComponentTag tag) {
        Attributes.addClass(tag, MediumSpanType.SPAN10);
      }
    }).setOutputMarkupId(true));
    super.onInitialize();
  }
}
