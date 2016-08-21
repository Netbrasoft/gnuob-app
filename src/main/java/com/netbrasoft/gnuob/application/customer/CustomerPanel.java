package com.netbrasoft.gnuob.application.customer;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.CUSTOMER_DATA_PROVIDER_NAME;
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

import com.netbrasoft.gnuob.api.Customer;
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
 * Panel for viewing, selecting and editing {@link Customer} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class CustomerPanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class CustomerPanelContainer extends WebMarkupContainer {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class CustomerTableContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class AddAjaxLink extends BootstrapAjaxLink<Customer> {

        private static final long serialVersionUID = -8317730269644885290L;

        public AddAjaxLink(final String id, final IModel<Customer> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.plus);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          final Customer customer = new Customer();
          customer.setActive(true);
          AddAjaxLink.this.setDefaultModelObject(customer);
          target.add(customerViewOrEditPanel.setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
      class CustomerDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
        class CustomerDataview extends DataView<Customer> {

          @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
          class RemoveAjaxLink extends BootstrapAjaxLink<Customer> {

            private static final long serialVersionUID = -8317730269644885290L;

            public RemoveAjaxLink(final String id, final IModel<Customer> model, final Buttons.Type type,
                final IModel<String> labelModel) {
              super(id, model, type, labelModel);
              setIconType(GlyphIconType.remove);
              setSize(Buttons.Size.Mini);
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
              try {
                customerDataProvider.remove((Customer) RemoveAjaxLink.this.getDefaultModelObject());
              } catch (final RuntimeException e) {
                LOGGER.warn(e.getMessage(), e);
                customerTableContainer.warn(e.getLocalizedMessage());
              } finally {
                target.add(customerPanelContainer.setOutputMarkupId(true));
              }
            }
          }

          private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

          private static final String REMOVE_ID = "remove";

          private static final String CLICK_EVENT = "click";

          private static final String LAST_NAME_ID = LAST_NAME_PROPERTY;

          private static final String FIRST_NAME_ID = FIRST_NAME_PROPERTY;

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = -5039874949058607907L;

          private int index;

          protected CustomerDataview(final String id, final IDataProvider<Customer> dataProvider,
              final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<Customer> newItem(final String id, final int index, final IModel<Customer> model) {
            final Item<Customer> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void populateItem(final Item<Customer> item) {
            item.setModel(new CompoundPropertyModel<Customer>(item.getModelObject()));
            item.add(new Label(FIRST_NAME_ID));
            item.add(new Label(LAST_NAME_ID));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                target.add(
                    customerDataviewContainer.setDefaultModelObject(item.getModelObject()).setOutputMarkupId(true));
                target.add(customerViewOrEditPanel.setOutputMarkupId(true));
              }
            });
            item.add(new RemoveAjaxLink(REMOVE_ID, item.getModel(), Buttons.Type.Default,
                Model.of(CustomerPanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY)))
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

        private static final String CUSTOMER_DATAVIEW_ID = "customerDataview";

        private static final long serialVersionUID = -1663362426819980106L;

        private static final int ITEMS_PER_PAGE = 5;

        private final CustomerDataview customerDataview;

        public CustomerDataviewContainer(final String id, final IModel<Customer> model) {
          super(id, model);
          customerDataview = new CustomerDataview(CUSTOMER_DATAVIEW_ID, customerDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(customerDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String CUSTOMER_PAGING_NAVIGATOR_MARKUP_ID = "customerPagingNavigator";

      private static final String CUSTOMER_DATAVIEW_CONTAINER_ID = "customerDataviewContainer";

      private static final String LAST_NAME_PROPERTY = "lastName";

      private static final String ORDER_BY_LAST_NAME_ID = "orderByLastName";

      private static final String FIRST_NAME_PROPERTY = "firstName";

      private static final String ORDER_BY_FIRST_NAME_ID = "orderByFirstName";

      private static final String ADD_ID = "add";

      private static final String FEEDBACK_ID = "feedback";

      private static final long serialVersionUID = 7545082431853829693L;

      private final NotificationPanel feedbackPanel;

      private final AddAjaxLink addAjaxLink;

      private final OrderByBorder<String> orderByFirstName;

      private final OrderByBorder<String> orderByLastName;

      private final CustomerDataviewContainer customerDataviewContainer;

      private final BootstrapPagingNavigator customerPagingNavigator;

      public CustomerTableContainer(final String id, final IModel<Customer> model) {
        super(id, model);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
        addAjaxLink = new AddAjaxLink(ADD_ID, (IModel<Customer>) CustomerTableContainer.this.getDefaultModel(),
            Buttons.Type.Primary,
            Model.of(CustomerPanel.this.getString(NetbrasoftApplicationConstants.ADD_MESSAGE_KEY)));
        orderByFirstName = new OrderByBorder<String>(ORDER_BY_FIRST_NAME_ID, FIRST_NAME_PROPERTY, customerDataProvider);
        orderByLastName = new OrderByBorder<String>(ORDER_BY_LAST_NAME_ID, LAST_NAME_PROPERTY, customerDataProvider);
        customerDataviewContainer = new CustomerDataviewContainer(CUSTOMER_DATAVIEW_CONTAINER_ID,
            (IModel<Customer>) CustomerTableContainer.this.getDefaultModel());
        customerPagingNavigator = new BootstrapPagingNavigator(CUSTOMER_PAGING_NAVIGATOR_MARKUP_ID,
            customerDataviewContainer.customerDataview);
      }

      @Override
      protected void onInitialize() {
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(addAjaxLink.setOutputMarkupId(true));
        add(orderByFirstName.setOutputMarkupId(true));
        add(orderByLastName.setOutputMarkupId(true));
        add(customerDataviewContainer.setOutputMarkupId(true));
        add(customerPagingNavigator.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CUSTOMER_VIEW_OR_EDIT_PANEL_ID = "customerViewOrEditPanel";

    private static final String CUSTOMER_TABLE_CONTAINER_ID = "customerTableContainer";

    private static final long serialVersionUID = 8636350252647277916L;

    private final CustomerViewOrEditPanel customerViewOrEditPanel;

    private final CustomerTableContainer customerTableContainer;

    public CustomerPanelContainer(final String id, final IModel<Customer> model) {
      super(id, model);
      customerTableContainer = new CustomerTableContainer(CUSTOMER_TABLE_CONTAINER_ID,
          (IModel<Customer>) CustomerPanelContainer.this.getDefaultModel());
      customerViewOrEditPanel = new CustomerViewOrEditPanel(CUSTOMER_VIEW_OR_EDIT_PANEL_ID,
          (IModel<Customer>) CustomerPanelContainer.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(customerTableContainer.add(new TableBehavior().hover()).setOutputMarkupId(true));
      add(customerViewOrEditPanel.add(customerViewOrEditPanel.new CustomerViewFragment()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final String CUSTOMER_PANEL_CONTAINER_ID = "customerPanelContainer";

  private static final long serialVersionUID = 3703226064705246155L;

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerPanel.class);

  @SpringBean(name = CUSTOMER_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Customer> customerDataProvider;

  private final CustomerPanelContainer customerPanelContainer;

  public CustomerPanel(final String id, final IModel<Customer> model) {
    super(id, model);
    customerPanelContainer = new CustomerPanelContainer(CUSTOMER_PANEL_CONTAINER_ID,
        (IModel<Customer>) CustomerPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    customerDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    customerDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    customerDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    customerDataProvider.setType(new Customer());
    customerDataProvider.getType().setActive(true);
    if (customerDataProvider.size() > 0) {
      CustomerPanel.this.setDefaultModelObject(customerDataProvider.iterator(0, 1).next());
    }
    add(customerPanelContainer.add(new BootstrapBaseBehavior() {

      private static final long serialVersionUID = -4903722864597601489L;

      @Override
      public void onComponentTag(final Component component, final ComponentTag tag) {
        Attributes.addClass(tag, MediumSpanType.SPAN10);
      }
    }).setOutputMarkupId(true));
    super.onInitialize();
  }
}
