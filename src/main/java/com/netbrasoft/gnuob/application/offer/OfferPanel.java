package com.netbrasoft.gnuob.application.offer;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.OFFER_DATA_PROVIDER_NAME;
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

import com.netbrasoft.gnuob.api.Offer;
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
 * Panel for viewing, selecting and editing {@link Offer} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class OfferPanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class OfferPanelContainer extends WebMarkupContainer {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class OfferTableContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class AddAjaxLink extends BootstrapAjaxLink<Offer> {

        private static final long serialVersionUID = -8317730269644885290L;

        public AddAjaxLink(final String id, final IModel<Offer> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.plus);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          final Offer offer = new Offer();
          offer.setActive(true);
          AddAjaxLink.this.setDefaultModelObject(offer);
          target.add(offerViewOrEditPanel.setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
      class OfferDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
        class OfferDataview extends DataView<Offer> {

          @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
          class RemoveAjaxLink extends BootstrapAjaxLink<Offer> {

            private static final long serialVersionUID = -8317730269644885290L;

            public RemoveAjaxLink(final String id, final IModel<Offer> model, final Buttons.Type type,
                final IModel<String> labelModel) {
              super(id, model, type, labelModel);
              setIconType(GlyphIconType.remove);
              setSize(Buttons.Size.Mini);
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
              try {
                offerDataProvider.remove((Offer) RemoveAjaxLink.this.getDefaultModelObject());
              } catch (final RuntimeException e) {
                LOGGER.warn(e.getMessage(), e);
                offerTableContainer.warn(e.getLocalizedMessage());
              } finally {
                target.add(offerPanelContainer.setOutputMarkupId(true));
              }
            }
          }

          private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

          private static final String REMOVE_ID = "remove";

          private static final String CLICK_EVENT = "click";

          private static final String CONTRACT_CUSTOMER_LAST_NAME_ID = CONTRACT_CUSTOMER_LAST_NAME_PROPERTY;

          private static final String CONTRACT_CUSTOMER_FIRST_NAME_ID = CONTRACT_CUSTOMER_FIRST_NAME_PROPERTY;

          private static final String CONTRACT_CONTRACT_ID_ID = CONTRACT_CONTRACT_ID_PROPERTY;

          private static final String OFFER_ID_ID = OFFER_ID_PROPERTY;

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = -5039874949058607907L;

          private int index;

          protected OfferDataview(final String id, final IDataProvider<Offer> dataProvider, final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<Offer> newItem(final String id, final int index, final IModel<Offer> model) {
            final Item<Offer> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void populateItem(final Item<Offer> item) {
            item.setModel(new CompoundPropertyModel<Offer>(item.getModelObject()));
            item.add(new Label(OFFER_ID_ID));
            item.add(new Label(CONTRACT_CONTRACT_ID_ID));
            item.add(new Label(CONTRACT_CUSTOMER_FIRST_NAME_ID));
            item.add(new Label(CONTRACT_CUSTOMER_LAST_NAME_ID));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                target.add(offerDataviewContainer.setDefaultModelObject(item.getModelObject()).setOutputMarkupId(true));
                target.add(offerViewOrEditPanel.setOutputMarkupId(true));
              }
            });
            item.add(new RemoveAjaxLink(REMOVE_ID, item.getModel(), Buttons.Type.Default,
                Model.of(OfferPanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY)))
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

        private static final String OFFER_DATAVIEW_ID = "offerDataview";

        private static final long serialVersionUID = -6700605975126870961L;

        private static final int ITEMS_PER_PAGE = 5;

        private final OfferDataview offerDataview;

        public OfferDataviewContainer(final String id, final IModel<Offer> model) {
          super(id, model);
          offerDataview = new OfferDataview(OFFER_DATAVIEW_ID, offerDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(offerDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String OFFER_PAGING_NAVIGATOR_MARKUP_ID = "offerPagingNavigator";

      private static final String OFFER_DATAVIEW_CONTAINER_ID = "offerDataviewContainer";

      private static final String ORDER_BY_CONTRACT_ID_ID = "orderByContractId";

      private static final String CONTRACT_CONTRACT_ID_PROPERTY = "contract.contractId";

      private static final String OFFER_ID_PROPERTY = "offerId";

      private static final String ORDER_BY_OFFER_ID_ID = "orderByOfferId";

      private static final String ORDER_BY_LAST_NAME_ID = "orderByLastName";

      private static final String CONTRACT_CUSTOMER_LAST_NAME_PROPERTY = "contract.customer.lastName";

      private static final String CONTRACT_CUSTOMER_FIRST_NAME_PROPERTY = "contract.customer.firstName";

      private static final String OFFER_BY_FIRST_NAME_ID = "orderByFirstName";

      private static final String ADD_ID = "add";

      private static final String FEEDBACK_ID = "feedback";

      private static final long serialVersionUID = 24914318472386879L;

      private final NotificationPanel feedbackPanel;

      private final AddAjaxLink addAjaxLink;

      private final OrderByBorder<String> orderByFirstName;

      private final OrderByBorder<String> orderByLastName;

      private final OrderByBorder<String> orderByOfferId;

      private final OrderByBorder<String> orderByContractId;

      private final OfferDataviewContainer offerDataviewContainer;

      private final BootstrapPagingNavigator offerPagingNavigator;

      public OfferTableContainer(final String id, final IModel<Offer> model) {
        super(id, model);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
        addAjaxLink = new AddAjaxLink(ADD_ID, (IModel<Offer>) OfferTableContainer.this.getDefaultModel(),
            Buttons.Type.Primary, Model.of(OfferPanel.this.getString(NetbrasoftApplicationConstants.ADD_MESSAGE_KEY)));
        orderByFirstName =
            new OrderByBorder<String>(OFFER_BY_FIRST_NAME_ID, CONTRACT_CUSTOMER_FIRST_NAME_PROPERTY, offerDataProvider);
        orderByLastName =
            new OrderByBorder<String>(ORDER_BY_LAST_NAME_ID, CONTRACT_CUSTOMER_LAST_NAME_PROPERTY, offerDataProvider);
        orderByOfferId = new OrderByBorder<String>(ORDER_BY_OFFER_ID_ID, OFFER_ID_PROPERTY, offerDataProvider);
        orderByContractId =
            new OrderByBorder<String>(ORDER_BY_CONTRACT_ID_ID, CONTRACT_CONTRACT_ID_PROPERTY, offerDataProvider);
        offerDataviewContainer = new OfferDataviewContainer(OFFER_DATAVIEW_CONTAINER_ID,
            (IModel<Offer>) OfferTableContainer.this.getDefaultModel());
        offerPagingNavigator =
            new BootstrapPagingNavigator(OFFER_PAGING_NAVIGATOR_MARKUP_ID, offerDataviewContainer.offerDataview);
      }

      @Override
      protected void onInitialize() {
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(addAjaxLink.setOutputMarkupId(true));
        add(orderByFirstName.setOutputMarkupId(true));
        add(orderByLastName.setOutputMarkupId(true));
        add(orderByOfferId.setOutputMarkupId(true));
        add(orderByContractId.setOutputMarkupId(true));
        add(offerDataviewContainer.setOutputMarkupId(true));
        add(offerPagingNavigator.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String OFFER_VIEW_OR_EDIT_PANEL_ID = "offerViewOrEditPanel";

    private static final String OFFER_TABLE_CONTAINER_ID = "offerTableContainer";

    private static final long serialVersionUID = 4490006925509789607L;

    private final OfferViewOrEditPanel offerViewOrEditPanel;

    private final OfferTableContainer offerTableContainer;

    public OfferPanelContainer(final String id, final IModel<Offer> model) {
      super(id, model);
      offerTableContainer =
          new OfferTableContainer(OFFER_TABLE_CONTAINER_ID, (IModel<Offer>) OfferPanelContainer.this.getDefaultModel());
      offerViewOrEditPanel = new OfferViewOrEditPanel(OFFER_VIEW_OR_EDIT_PANEL_ID,
          (IModel<Offer>) OfferPanelContainer.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(offerTableContainer.add(new TableBehavior().hover()).setOutputMarkupId(true));
      add(offerViewOrEditPanel.add(offerViewOrEditPanel.new OfferViewFragment()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final String OFFER_PANEL_CONTAINER_ID = "offerPanelContainer";

  private static final Logger LOGGER = LoggerFactory.getLogger(OfferPanel.class);

  private static final long serialVersionUID = 3703226064705246155L;

  @SpringBean(name = OFFER_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Offer> offerDataProvider;

  private final OfferPanelContainer offerPanelContainer;

  public OfferPanel(final String id, final IModel<Offer> model) {
    super(id, model);
    offerPanelContainer =
        new OfferPanelContainer(OFFER_PANEL_CONTAINER_ID, (IModel<Offer>) OfferPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    offerDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    offerDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    offerDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    offerDataProvider.setType(new Offer());
    offerDataProvider.getType().setActive(true);
    if (offerDataProvider.size() > 0) {
      OfferPanel.this.setDefaultModelObject(offerDataProvider.iterator(0, 1).next());
    }
    add(offerPanelContainer.add(new BootstrapBaseBehavior() {

      private static final long serialVersionUID = -4903722864597601489L;

      @Override
      public void onComponentTag(final Component component, final ComponentTag tag) {
        Attributes.addClass(tag, MediumSpanType.SPAN10);
      }
    }).setOutputMarkupId(true));
    super.onInitialize();
  }
}
