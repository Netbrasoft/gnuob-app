package com.netbrasoft.gnuob.application.offer;

import static de.agilecoders.wicket.jquery.JQuery.$;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.OfferRecord;
import com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants;
import com.netbrasoft.gnuob.application.security.AppRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationConfig;

/**
 * Panel for viewing, selecting and editing {@link OfferRecord} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class OfferRecordPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class OfferRecordEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class OfferRecordEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class AddAjaxLink extends BootstrapAjaxLink<Offer> {

        private static final long serialVersionUID = 9191172039973638020L;

        public AddAjaxLink(final String id, final IModel<Offer> model, final Buttons.Type type, final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.plus);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          ((Offer) AddAjaxLink.this.getDefaultModelObject()).getRecords().add(new OfferRecord());
          offerRecordDataviewContainer.offerRecordDataview.index = ((Offer) AddAjaxLink.this.getDefaultModelObject()).getRecords().size() - 1;
          offerRecordViewOrEditPanel.removeAll();
          target.add(offerRecordDataviewContainer.setOutputMarkupId(true));
          target.add(offerRecordViewOrEditPanel.add(offerRecordViewOrEditPanel.new OfferRecordEditFragment()).setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class OfferRecordDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
        class OfferRecordDataview extends DataView<OfferRecord> {

          @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
          class RemoveAjaxLink extends BootstrapAjaxLink<OfferRecord> {

            private static final long serialVersionUID = -6950515027229520882L;

            public RemoveAjaxLink(final String id, final IModel<OfferRecord> model, final Buttons.Type type, final IModel<String> labelModel) {
              super(id, model, type, labelModel);
              setIconType(GlyphIconType.remove);
              setSize(Buttons.Size.Mini);
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
              ((Offer) OfferRecordDataviewContainer.this.getDefaultModelObject()).getRecords().remove(RemoveAjaxLink.this.getDefaultModelObject());
              index -= 1;
              offerRecordViewOrEditPanel.removeAll();
              target.add(offerRecordDataviewContainer.setOutputMarkupId(true));
              target.add(offerRecordViewOrEditPanel.add(offerRecordViewOrEditPanel.new OfferRecordEditFragment()).setOutputMarkupId(true));
            }

            @Override
            protected void onInitialize() {
              final ConfirmationBehavior confirmationBehavior = new ConfirmationBehavior() {

                private static final long serialVersionUID = 7744720444161839031L;

                @Override
                public void renderHead(final Component component, final IHeaderResponse response) {
                  response.render($(component).chain(CONFIRMATION_FUNCTION_NAME,
                      new ConfirmationConfig().withTitle(getString(NetbrasoftApplicationConstants.CONFIRMATION_MESSAGE_KEY)).withSingleton(true).withPopout(true)
                          .withBtnOkLabel(getString(NetbrasoftApplicationConstants.CONFIRM_MESSAGE_KEY))
                          .withBtnCancelLabel(getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)))
                      .asDomReadyScript());
                }
              };
              add(confirmationBehavior);
              super.onInitialize();
            }
          }

          private static final String CLICK_EVENT = "click";

          private static final String DESCRIPTION_ID = "description";

          private static final String NAME_ID = "name";

          private static final String REMOVE_ID = "remove";

          private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = 8996562822101409998L;

          private int index = 0;

          protected OfferRecordDataview(final String id, final IDataProvider<OfferRecord> dataProvider, final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<OfferRecord> newItem(final String id, final int index, final IModel<OfferRecord> model) {
            final Item<OfferRecord> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void onConfigure() {
            final IModel<Offer> model = (IModel<Offer>) OfferRecordDataviewContainer.this.getDefaultModel();
            if (!model.getObject().getRecords().isEmpty()) {
              offerRecordViewOrEditPanel.setEnabled(true);
              offerRecordViewOrEditPanel.removeAll();
              offerRecordViewOrEditPanel.setSelectedModel(Model.of(model.getObject().getRecords().get(index)));
              offerRecordViewOrEditPanel.add(offerRecordViewOrEditPanel.new OfferRecordEditFragment()).setOutputMarkupId(true);
            } else {
              offerRecordViewOrEditPanel.setEnabled(false);
              offerRecordViewOrEditPanel.removeAll();
              offerRecordViewOrEditPanel.setSelectedModel(Model.of(new OfferRecord()));
              offerRecordViewOrEditPanel.add(offerRecordViewOrEditPanel.new OfferRecordEditFragment()).setOutputMarkupId(true);
            }
            super.onConfigure();
          }

          @Override
          protected void populateItem(final Item<OfferRecord> item) {
            final Label nameLabel = new Label(NAME_ID);
            final Label descriptionLabel = new Label(DESCRIPTION_ID);
            final AjaxEventBehavior ajaxEventBehavior = new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                offerRecordViewOrEditPanel.setSelectedModel(item.getModel());
                offerRecordViewOrEditPanel.removeAll();
                target.add(offerRecordDataviewContainer.setOutputMarkupId(true));
                target.add(offerRecordViewOrEditPanel.add(offerRecordViewOrEditPanel.new OfferRecordEditFragment()).setOutputMarkupId(true));
              }
            };
            final RemoveAjaxLink removeAjaxLink =
                new RemoveAjaxLink(REMOVE_ID, item.getModel(), Buttons.Type.Default, Model.of(OfferRecordPanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY)));

            item.setModel(new CompoundPropertyModel<OfferRecord>(item.getModelObject()));
            item.add(nameLabel);
            item.add(descriptionLabel);
            item.add(ajaxEventBehavior);
            item.add(removeAjaxLink);
          }
        }

        private static final String OFFER_RECORD_DATAVIEW_ID = "offerRecordDataview";

        private static final long serialVersionUID = 7156170012562240536L;

        private final OfferRecordDataview offerRecordDataview;

        private final ListDataProvider<OfferRecord> offerRecordListDataProvider;

        public OfferRecordDataviewContainer(final String id, final IModel<Offer> model) {
          super(id, model);
          offerRecordListDataProvider = new ListDataProvider<OfferRecord>() {

            private static final long serialVersionUID = -3261859241046697057L;

            @Override
            protected List<OfferRecord> getData() {
              return ((Offer) OfferRecordDataviewContainer.this.getDefaultModelObject()).getRecords();
            }
          };
          offerRecordDataview = new OfferRecordDataview(OFFER_RECORD_DATAVIEW_ID, offerRecordListDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(offerRecordDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String OFFER_RECORD_VIEW_OR_EDIT_PANEL_ID = "offerRecordViewOrEditPanel";

      private static final String OFFER_RECORD_PAGING_NAVIGATOR_MARKUP_ID = "offerRecordPagingNavigator";

      private static final String OFFER_RECORD_DATAVIEW_CONTAINER_ID = "offerRecordDataviewContainer";

      private static final String ADD_ID = "add";

      private static final long serialVersionUID = -4165310537311768675L;

      private final AddAjaxLink addAjaxLink;

      private final OfferRecordDataviewContainer offerRecordDataviewContainer;

      private final BootstrapPagingNavigator offerRecordPagingNavigator;

      private final OfferRecordViewOrEditPanel offerRecordViewOrEditPanel;

      public OfferRecordEditTable(final String id, final IModel<Offer> model) {
        super(id, model);
        addAjaxLink = new AddAjaxLink(ADD_ID, (IModel<Offer>) OfferRecordEditTable.this.getDefaultModel(), Buttons.Type.Primary,
            Model.of(OfferRecordPanel.this.getString(NetbrasoftApplicationConstants.ADD_MESSAGE_KEY)));
        offerRecordDataviewContainer = new OfferRecordDataviewContainer(OFFER_RECORD_DATAVIEW_CONTAINER_ID, (IModel<Offer>) OfferRecordEditTable.this.getDefaultModel());
        offerRecordPagingNavigator = new BootstrapPagingNavigator(OFFER_RECORD_PAGING_NAVIGATOR_MARKUP_ID, offerRecordDataviewContainer.offerRecordDataview);
        offerRecordViewOrEditPanel = new OfferRecordViewOrEditPanel(OFFER_RECORD_VIEW_OR_EDIT_PANEL_ID, (IModel<Offer>) OfferRecordEditTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        add(addAjaxLink.setOutputMarkupId(true));
        add(offerRecordDataviewContainer.setOutputMarkupId(true));
        add(offerRecordPagingNavigator.setOutputMarkupId(true));
        add(offerRecordViewOrEditPanel.add(offerRecordViewOrEditPanel.new OfferRecordEditFragment()).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String OFFER_RECORD_EDIT_TABLE_ID = "offerRecordEditTable";

    private static final String OFFER_RECORD_EDIT_FRAGMENT_MARKUP_ID = "offerRecordEditFragment";

    private static final String OFFER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID = "offerRecordViewOrEditFragment";

    private static final long serialVersionUID = -8851058614310416237L;

    private final OfferRecordEditTable offerRecordEditTable;

    public OfferRecordEditFragment() {
      super(OFFER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID, OFFER_RECORD_EDIT_FRAGMENT_MARKUP_ID, OfferRecordPanel.this, OfferRecordPanel.this.getDefaultModel());
      offerRecordEditTable = new OfferRecordEditTable(OFFER_RECORD_EDIT_TABLE_ID, (IModel<Offer>) OfferRecordEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      offerRecordEditTable.add(new TableBehavior().hover());
      add(offerRecordEditTable.setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class OfferRecordViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class OfferRecordViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
      class OfferRecordDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
        class OfferRecordDataview extends DataView<OfferRecord> {

          private static final String CLICK_EVENT = "click";

          private static final String DESCRIPTION_ID = "description";

          private static final String NAME_ID = "name";

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = 8996562822101409998L;

          private int index = 0;

          protected OfferRecordDataview(final String id, final IDataProvider<OfferRecord> dataProvider, final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<OfferRecord> newItem(final String id, final int index, final IModel<OfferRecord> model) {
            final Item<OfferRecord> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void onConfigure() {
            final IModel<Offer> model = (IModel<Offer>) OfferRecordDataviewContainer.this.getDefaultModel();
            if (!model.getObject().getRecords().isEmpty()) {
              offerRecordViewOrEditPanel.removeAll();
              offerRecordViewOrEditPanel.setSelectedModel(Model.of(model.getObject().getRecords().get(index)));
              offerRecordViewOrEditPanel.add(offerRecordViewOrEditPanel.new OfferRecordViewFragment()).setOutputMarkupId(true);
            } else {
              offerRecordViewOrEditPanel.removeAll();
              offerRecordViewOrEditPanel.setSelectedModel(Model.of(new OfferRecord()));
              offerRecordViewOrEditPanel.add(offerRecordViewOrEditPanel.new OfferRecordViewFragment()).setOutputMarkupId(true);
            }
            super.onConfigure();
          }

          @Override
          protected void populateItem(final Item<OfferRecord> item) {
            item.setModel(new CompoundPropertyModel<OfferRecord>(item.getModelObject()));
            item.add(new Label(NAME_ID));
            item.add(new Label(DESCRIPTION_ID));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                offerRecordViewOrEditPanel.setSelectedModel(item.getModel());
                target.add(offerRecordViewOrEditPanel.add(offerRecordViewOrEditPanel.new OfferRecordViewFragment()).setOutputMarkupId(true));
              }
            });
          }
        }

        private static final String OFFER_RECORD_DATAVIEW_ID = "offerRecordDataview";

        private static final long serialVersionUID = 7156170012562240536L;

        private final OfferRecordDataview offerRecordDataview;

        private final ListDataProvider<OfferRecord> offerRecordListDataProvider;

        public OfferRecordDataviewContainer(final String id, final IModel<Offer> model) {
          super(id, model);
          offerRecordListDataProvider = new ListDataProvider<OfferRecord>() {

            private static final long serialVersionUID = -3261859241046697057L;

            @Override
            protected List<OfferRecord> getData() {
              return ((Offer) OfferRecordDataviewContainer.this.getDefaultModelObject()).getRecords();
            }
          };
          offerRecordDataview = new OfferRecordDataview(OFFER_RECORD_DATAVIEW_ID, offerRecordListDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(offerRecordDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String OFFER_RECORD_VIEW_OR_EDIT_PANEL_ID = "offerRecordViewOrEditPanel";

      private static final String OFFER_RECORD_PAGING_NAVIGATOR_MARKUP_ID = "offerRecordPagingNavigator";

      private static final String OFFER_RECORD_DATAVIEW_CONTAINER_ID = "offerRecordDataviewContainer";

      private static final long serialVersionUID = -4165310537311768675L;

      private final OfferRecordDataviewContainer offerRecordDataviewContainer;

      private final BootstrapPagingNavigator offerRecordPagingNavigator;

      private final OfferRecordViewOrEditPanel offerRecordViewOrEditPanel;

      public OfferRecordViewTable(final String id, final IModel<Offer> model) {
        super(id, model);
        offerRecordDataviewContainer = new OfferRecordDataviewContainer(OFFER_RECORD_DATAVIEW_CONTAINER_ID, (IModel<Offer>) OfferRecordViewTable.this.getDefaultModel());
        offerRecordPagingNavigator = new BootstrapPagingNavigator(OFFER_RECORD_PAGING_NAVIGATOR_MARKUP_ID, offerRecordDataviewContainer.offerRecordDataview);
        offerRecordViewOrEditPanel = new OfferRecordViewOrEditPanel(OFFER_RECORD_VIEW_OR_EDIT_PANEL_ID, (IModel<Offer>) OfferRecordViewTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        add(offerRecordDataviewContainer.setOutputMarkupId(true));
        add(offerRecordPagingNavigator.setOutputMarkupId(true));
        add(offerRecordViewOrEditPanel.add(offerRecordViewOrEditPanel.new OfferRecordViewFragment()).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String OFFER_RECORD_VIEW_TABLE_ID = "offerRecordViewTable";

    private static final String OFFER_RECORD_VIEW_FRAGMENT_MARKUP_ID = "offerRecordViewFragment";

    private static final String OFFER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID = "offerRecordViewOrEditFragment";

    private static final long serialVersionUID = -8851058614310416237L;

    private final OfferRecordViewTable offerRecordViewTable;

    public OfferRecordViewFragment() {
      super(OFFER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID, OFFER_RECORD_VIEW_FRAGMENT_MARKUP_ID, OfferRecordPanel.this, OfferRecordPanel.this.getDefaultModel());
      offerRecordViewTable = new OfferRecordViewTable(OFFER_RECORD_VIEW_TABLE_ID, (IModel<Offer>) OfferRecordViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(offerRecordViewTable.add(new TableBehavior().hover()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = -4119480413347414297L;

  private static final long ITEMS_PER_PAGE = 10;

  public OfferRecordPanel(final String id, final IModel<Offer> model) {
    super(id, model);
  }
}
