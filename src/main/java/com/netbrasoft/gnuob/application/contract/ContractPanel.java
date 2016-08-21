package com.netbrasoft.gnuob.application.contract;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.CONTRACT_DATA_PROVIDER_NAME;
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

import com.netbrasoft.gnuob.api.Contract;
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
 * Panel for viewing, selecting and editing {@link Contract} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class ContractPanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class ContractPanelContainer extends WebMarkupContainer {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class ContractTableContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class AddAjaxLink extends BootstrapAjaxLink<Contract> {

        private static final long serialVersionUID = -8317730269644885290L;

        public AddAjaxLink(final String id, final IModel<Contract> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.plus);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          final Contract contract = new Contract();
          contract.setActive(true);
          AddAjaxLink.this.setDefaultModelObject(contract);
          target.add(contractViewOrEditPanel.setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
      class CustomerDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
        class ContractDataview extends DataView<Contract> {

          @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
          class RemoveAjaxLink extends BootstrapAjaxLink<Contract> {

            private static final long serialVersionUID = -8317730269644885290L;

            public RemoveAjaxLink(final String id, final IModel<Contract> model, final Buttons.Type type,
                final IModel<String> labelModel) {
              super(id, model, type, labelModel);
              setIconType(GlyphIconType.remove);
              setSize(Buttons.Size.Mini);
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
              try {
                contractDataProvider.remove((Contract) RemoveAjaxLink.this.getDefaultModelObject());
              } catch (final RuntimeException e) {
                LOGGER.warn(e.getMessage(), e);
                contractTableContainer.warn(e.getLocalizedMessage());
              } finally {
                target.add(contractPanelContainer.setOutputMarkupId(true));
              }
            }
          }

          private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

          private static final String REMOVE_ID = "remove";

          private static final String CLICK_EVENT = "click";

          private static final String CUSTOMER_LAST_NAME_ID = LAST_NAME_PROPERTY;

          private static final String CUSTOMER_FIRST_NAME_ID = FIRST_NAME_PROPERTY;

          private static final String CONTRACT_ID_ID = CONTRACT_ID_PROPERTY;

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = -7876356935046054019L;

          private int index;

          protected ContractDataview(final String id, final IDataProvider<Contract> dataProvider,
              final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<Contract> newItem(final String id, final int index, final IModel<Contract> model) {
            final Item<Contract> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void populateItem(final Item<Contract> item) {
            item.setModel(new CompoundPropertyModel<Contract>(item.getModelObject()));
            item.add(new Label(CONTRACT_ID_ID));
            item.add(new Label(CUSTOMER_FIRST_NAME_ID));
            item.add(new Label(CUSTOMER_LAST_NAME_ID));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                target.add(
                    contractDataviewContainer.setDefaultModelObject(item.getModelObject()).setOutputMarkupId(true));
                target.add(contractViewOrEditPanel.setOutputMarkupId(true));
              }
            });
            item.add(new RemoveAjaxLink(REMOVE_ID, item.getModel(), Buttons.Type.Default,
                Model.of(ContractPanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY)))
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

        private static final String CONTRACT_DATAVIEW_ID = "contractDataview";

        private static final long serialVersionUID = 4315075377801287939L;

        private static final int ITEMS_PER_PAGE = 5;

        private final ContractDataview contractDataview;

        public CustomerDataviewContainer(final String id, final IModel<Contract> model) {
          super(id, model);
          contractDataview = new ContractDataview(CONTRACT_DATAVIEW_ID, contractDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(contractDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String CONTRACT_PAGING_NAVIGATOR_MARKUP_ID = "contractPagingNavigator";

      private static final String CONTRACT_DATAVIEW_CONTAINER_ID = "contractDataviewContainer";

      private static final String CONTRACT_ID_PROPERTY = "contractId";

      private static final String ORDER_BY_CONTRACT_ID_ID = "orderByContractId";

      private static final String LAST_NAME_PROPERTY = "customer.lastName";

      private static final String ORDER_BY_LAST_NAME_ID = "orderByLastName";

      private static final String FIRST_NAME_PROPERTY = "customer.firstName";

      private static final String ORDER_BY_FIRST_NAME_ID = "orderByFirstName";

      private static final String ADD_ID = "add";

      private static final String FEEDBACK_MARKUP_ID = "feedback";

      private static final long serialVersionUID = -1947584316858637811L;

      private final NotificationPanel feedbackPanel;

      private final AddAjaxLink addAjaxLink;

      private final OrderByBorder<String> orderByFirstName;

      private final OrderByBorder<String> orderByLastName;

      private final OrderByBorder<String> orderByContractId;

      private final BootstrapPagingNavigator contractPagingNavigator;

      private final CustomerDataviewContainer contractDataviewContainer;

      public ContractTableContainer(final String id, final IModel<Contract> model) {
        super(id, model);
        feedbackPanel = new NotificationPanel(FEEDBACK_MARKUP_ID);
        addAjaxLink = new AddAjaxLink(ADD_ID, (IModel<Contract>) ContractTableContainer.this.getDefaultModel(),
            Buttons.Type.Primary,
            Model.of(ContractPanel.this.getString(NetbrasoftApplicationConstants.ADD_MESSAGE_KEY)));
        orderByFirstName = new OrderByBorder<String>(ORDER_BY_FIRST_NAME_ID, FIRST_NAME_PROPERTY, contractDataProvider);
        orderByLastName = new OrderByBorder<String>(ORDER_BY_LAST_NAME_ID, LAST_NAME_PROPERTY, contractDataProvider);
        orderByContractId =
            new OrderByBorder<String>(ORDER_BY_CONTRACT_ID_ID, CONTRACT_ID_PROPERTY, contractDataProvider);
        contractDataviewContainer = new CustomerDataviewContainer(CONTRACT_DATAVIEW_CONTAINER_ID,
            (IModel<Contract>) ContractTableContainer.this.getDefaultModel());
        contractPagingNavigator = new BootstrapPagingNavigator(CONTRACT_PAGING_NAVIGATOR_MARKUP_ID,
            contractDataviewContainer.contractDataview);
      }

      @Override
      protected void onInitialize() {
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(addAjaxLink.setOutputMarkupId(true));
        add(orderByFirstName.setOutputMarkupId(true));
        add(orderByLastName.setOutputMarkupId(true));
        add(orderByContractId.setOutputMarkupId(true));
        add(contractDataviewContainer.setOutputMarkupId(true));
        add(contractPagingNavigator.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CONTRACT_VIEW_OR_EDIT_PANEL_ID = "contractViewOrEditPanel";

    private static final String CONTRACT_TABLE_CONTAINER_ID = "contractTableContainer";

    private static final long serialVersionUID = 4101181018830689499L;

    private final ContractViewOrEditPanel contractViewOrEditPanel;

    private final ContractTableContainer contractTableContainer;

    public ContractPanelContainer(final String id, final IModel<Contract> model) {
      super(id, model);
      contractTableContainer = new ContractTableContainer(CONTRACT_TABLE_CONTAINER_ID,
          (IModel<Contract>) ContractPanelContainer.this.getDefaultModel());
      contractViewOrEditPanel = new ContractViewOrEditPanel(CONTRACT_VIEW_OR_EDIT_PANEL_ID,
          (IModel<Contract>) ContractPanelContainer.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(contractTableContainer.add(new TableBehavior().hover()).setOutputMarkupId(true));
      add(contractViewOrEditPanel.add(contractViewOrEditPanel.new ContractViewFragment()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final String CONTRACT_PANEL_CONTAINER_ID = "contractPanelContainer";

  private static final long serialVersionUID = 3703226064705246155L;

  private static final Logger LOGGER = LoggerFactory.getLogger(ContractPanel.class);

  @SpringBean(name = CONTRACT_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Contract> contractDataProvider;

  private final ContractPanelContainer contractPanelContainer;

  public ContractPanel(final String id, final IModel<Contract> model) {
    super(id, model);
    contractPanelContainer = new ContractPanelContainer(CONTRACT_PANEL_CONTAINER_ID,
        (IModel<Contract>) ContractPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    contractDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    contractDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    contractDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    contractDataProvider.setType(new Contract());
    contractDataProvider.getType().setActive(true);
    if (contractDataProvider.size() > 0) {
      ContractPanel.this.setDefaultModelObject(contractDataProvider.iterator(0, 1).next());
    }
    add(contractPanelContainer.add(new BootstrapBaseBehavior() {

      private static final long serialVersionUID = -4903722864597601489L;

      @Override
      public void onComponentTag(final Component component, final ComponentTag tag) {
        Attributes.addClass(tag, MediumSpanType.SPAN10);
      }
    }).setOutputMarkupId(true));
    super.onInitialize();
  }
}
