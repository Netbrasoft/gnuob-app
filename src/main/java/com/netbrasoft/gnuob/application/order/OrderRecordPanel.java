package com.netbrasoft.gnuob.application.order;

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

import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.OrderRecord;
import com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants;
import com.netbrasoft.gnuob.application.security.AppRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationConfig;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class OrderRecordPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class OrderRecordEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class OrderRecordEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class AddAjaxLink extends BootstrapAjaxLink<Order> {

        private static final long serialVersionUID = 9191172039973638020L;

        public AddAjaxLink(final String id, final IModel<Order> model, final Buttons.Type type, final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.plus);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          ((Order) AddAjaxLink.this.getDefaultModelObject()).getRecords().add(new OrderRecord());
          orderRecordDataviewContainer.orderRecordDataview.index = ((Order) AddAjaxLink.this.getDefaultModelObject()).getRecords().size() - 1;
          orderRecordViewOrEditPanel.removeAll();
          target.add(orderRecordDataviewContainer.setOutputMarkupId(true));
          target.add(orderRecordViewOrEditPanel.add(orderRecordViewOrEditPanel.new OrderRecordEditFragment()).setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class OrderRecordDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
        class OrderRecordDataview extends DataView<OrderRecord> {

          @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
          class RemoveAjaxLink extends BootstrapAjaxLink<OrderRecord> {

            private static final long serialVersionUID = -6950515027229520882L;

            public RemoveAjaxLink(final String id, final IModel<OrderRecord> model, final Buttons.Type type, final IModel<String> labelModel) {
              super(id, model, type, labelModel);
              setIconType(GlyphIconType.remove);
              setSize(Buttons.Size.Mini);
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
              ((Order) OrderRecordDataviewContainer.this.getDefaultModelObject()).getRecords().remove(RemoveAjaxLink.this.getDefaultModelObject());
              orderRecordDataview.index = ((Order) OrderRecordDataviewContainer.this.getDefaultModelObject()).getRecords().size() - 1;
              orderRecordViewOrEditPanel.removeAll();
              target.add(orderRecordDataviewContainer.setOutputMarkupId(true));
              target.add(orderRecordViewOrEditPanel.add(orderRecordViewOrEditPanel.new OrderRecordEditFragment()).setOutputMarkupId(true));
            }
          }

          private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = 8996562822101409998L;

          private int index = 0;

          protected OrderRecordDataview(final String id, final IDataProvider<OrderRecord> dataProvider, final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<OrderRecord> newItem(final String id, final int index, final IModel<OrderRecord> model) {
            final Item<OrderRecord> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void onConfigure() {
            final IModel<Order> model = (IModel<Order>) OrderRecordDataviewContainer.this.getDefaultModel();
            if (!model.getObject().getRecords().isEmpty()) {
              orderRecordViewOrEditPanel.setEnabled(true);
              orderRecordViewOrEditPanel.removeAll();
              orderRecordViewOrEditPanel.setSelectedModel(Model.of(model.getObject().getRecords().get(index)));
              orderRecordViewOrEditPanel.add(orderRecordViewOrEditPanel.new OrderRecordEditFragment()).setOutputMarkupId(true);
            } else {
              orderRecordViewOrEditPanel.setEnabled(false);
              orderRecordViewOrEditPanel.removeAll();
              orderRecordViewOrEditPanel.setSelectedModel(Model.of(new OrderRecord()));
              orderRecordViewOrEditPanel.add(orderRecordViewOrEditPanel.new OrderRecordEditFragment()).setOutputMarkupId(true);
            }
            super.onConfigure();
          }

          @Override
          protected void populateItem(final Item<OrderRecord> item) {
            item.setModel(new CompoundPropertyModel<OrderRecord>(item.getModelObject()));
            item.add(new Label("name"));
            item.add(new Label("description"));
            item.add(new AjaxEventBehavior("click") {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                orderRecordViewOrEditPanel.setSelectedModel(item.getModel());
                orderRecordViewOrEditPanel.removeAll();
                target.add(orderRecordDataview.setOutputMarkupId(true));
                target.add(orderRecordViewOrEditPanel.add(orderRecordViewOrEditPanel.new OrderRecordEditFragment()).setOutputMarkupId(true));
              }
            });
            item.add(
                new RemoveAjaxLink("remove", item.getModel(), Buttons.Type.Default, Model.of(OrderRecordPanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY)))
                    .add(new ConfirmationBehavior() {

                      private static final long serialVersionUID = 7744720444161839031L;

                      @Override
                      public void renderHead(final Component component, final IHeaderResponse response) {
                        response.render($(component).chain(CONFIRMATION_FUNCTION_NAME,
                            new ConfirmationConfig().withTitle(getString(NetbrasoftApplicationConstants.CONFIRMATION_MESSAGE_KEY)).withSingleton(true).withPopout(true)
                                .withBtnOkLabel(getString(NetbrasoftApplicationConstants.CONFIRM_MESSAGE_KEY))
                                .withBtnCancelLabel(getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)))
                            .asDomReadyScript());
                      }
                    }));
          }
        }

        private static final String ORDER_RECORD_DATAVIEW_ID = "orderRecordDataview";

        private static final long serialVersionUID = 7156170012562240536L;

        private final OrderRecordDataview orderRecordDataview;

        private final ListDataProvider<OrderRecord> orderRecordListDataProvider;

        public OrderRecordDataviewContainer(final String id, final IModel<Order> model) {
          super(id, model);
          orderRecordListDataProvider = new ListDataProvider<OrderRecord>() {

            private static final long serialVersionUID = -3261859241046697057L;

            @Override
            protected List<OrderRecord> getData() {
              return ((Order) OrderRecordDataviewContainer.this.getDefaultModelObject()).getRecords();
            }
          };
          orderRecordDataview = new OrderRecordDataview(ORDER_RECORD_DATAVIEW_ID, orderRecordListDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(orderRecordDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String ORDER_RECORD_VIEW_OR_EDIT_PANEL_ID = "orderRecordViewOrEditPanel";

      private static final String ORDER_RECORD_PAGING_NAVIGATOR_MARKUP_ID = "orderRecordPagingNavigator";

      private static final String ORDER_RECORD_DATAVIEW_CONTAINER_ID = "orderRecordDataviewContainer";

      private static final String ADD_ID = "add";

      private static final long serialVersionUID = -4165310537311768675L;

      private final AddAjaxLink addAjaxLink;

      private final OrderRecordDataviewContainer orderRecordDataviewContainer;

      private final BootstrapPagingNavigator orderRecordPagingNavigator;

      private final OrderRecordViewOrEditPanel orderRecordViewOrEditPanel;

      public OrderRecordEditTable(final String id, final IModel<Order> model) {
        super(id, model);
        addAjaxLink = new AddAjaxLink(ADD_ID, (IModel<Order>) OrderRecordEditTable.this.getDefaultModel(), Buttons.Type.Primary,
            Model.of(OrderRecordPanel.this.getString(NetbrasoftApplicationConstants.ADD_MESSAGE_KEY)));
        orderRecordDataviewContainer = new OrderRecordDataviewContainer(ORDER_RECORD_DATAVIEW_CONTAINER_ID, (IModel<Order>) OrderRecordEditTable.this.getDefaultModel());
        orderRecordPagingNavigator = new BootstrapPagingNavigator(ORDER_RECORD_PAGING_NAVIGATOR_MARKUP_ID, orderRecordDataviewContainer.orderRecordDataview);
        orderRecordViewOrEditPanel = new OrderRecordViewOrEditPanel(ORDER_RECORD_VIEW_OR_EDIT_PANEL_ID, (IModel<Order>) OrderRecordEditTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        add(addAjaxLink.setOutputMarkupId(true));
        add(orderRecordDataviewContainer.setOutputMarkupId(true));
        add(orderRecordPagingNavigator.setOutputMarkupId(true));
        add(orderRecordViewOrEditPanel.add(orderRecordViewOrEditPanel.new OrderRecordEditFragment()).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String ORDER_RECORD_EDIT_TABLE_ID = "orderRecordEditTable";

    private static final String ORDER_RECORD_EDIT_FRAGMENT_MARKUP_ID = "orderRecordEditFragment";

    private static final String ORDER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID = "orderRecordViewOrEditFragment";

    private static final long serialVersionUID = -8851058614310416237L;

    private final OrderRecordEditTable orderRecordEditTable;

    public OrderRecordEditFragment() {
      super(ORDER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID, ORDER_RECORD_EDIT_FRAGMENT_MARKUP_ID, OrderRecordPanel.this, OrderRecordPanel.this.getDefaultModel());
      orderRecordEditTable = new OrderRecordEditTable(ORDER_RECORD_EDIT_TABLE_ID, (IModel<Order>) OrderRecordEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(orderRecordEditTable.add(new TableBehavior().hover()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class OrderRecordViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class OrderRecordViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
      class OrderRecordDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
        class OrderRecordDataview extends DataView<OrderRecord> {

          private static final String CLICK_EVENT = "click";

          private static final String DESCRIPTION_ID = "description";

          private static final String NAME_ID = "name";

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = 8996562822101409998L;

          private int index = 0;

          protected OrderRecordDataview(final String id, final IDataProvider<OrderRecord> dataProvider, final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<OrderRecord> newItem(final String id, final int index, final IModel<OrderRecord> model) {
            final Item<OrderRecord> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void onConfigure() {
            final IModel<Order> model = (IModel<Order>) OrderRecordDataviewContainer.this.getDefaultModel();
            if (!model.getObject().getRecords().isEmpty()) {
              orderRecordViewOrEditPanel.removeAll();
              orderRecordViewOrEditPanel.setSelectedModel(Model.of(model.getObject().getRecords().get(index)));
              orderRecordViewOrEditPanel.add(orderRecordViewOrEditPanel.new OrderRecordViewFragment()).setOutputMarkupId(true);
            } else {
              orderRecordViewOrEditPanel.removeAll();
              orderRecordViewOrEditPanel.setSelectedModel(Model.of(new OrderRecord()));
              orderRecordViewOrEditPanel.add(orderRecordViewOrEditPanel.new OrderRecordViewFragment()).setOutputMarkupId(true);
            }
            super.onConfigure();
          }

          @Override
          protected void populateItem(final Item<OrderRecord> item) {
            item.setModel(new CompoundPropertyModel<OrderRecord>(item.getModelObject()));
            item.add(new Label(NAME_ID));
            item.add(new Label(DESCRIPTION_ID));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                orderRecordViewOrEditPanel.setSelectedModel(item.getModel());
                orderRecordViewOrEditPanel.removeAll();
                target.add(orderRecordDataview.setOutputMarkupId(true));
                target.add(orderRecordViewOrEditPanel.add(orderRecordViewOrEditPanel.new OrderRecordViewFragment()).setOutputMarkupId(true));
              }
            });
          }
        }

        private static final String ORDER_RECORD_DATAVIEW_ID = "orderRecordDataview";

        private static final long serialVersionUID = 7156170012562240536L;

        private final OrderRecordDataview orderRecordDataview;

        private final ListDataProvider<OrderRecord> orderRecordListDataProvider;

        public OrderRecordDataviewContainer(final String id, final IModel<Order> model) {
          super(id, model);
          orderRecordListDataProvider = new ListDataProvider<OrderRecord>() {

            private static final long serialVersionUID = -3261859241046697057L;

            @Override
            protected List<OrderRecord> getData() {
              return ((Order) OrderRecordDataviewContainer.this.getDefaultModelObject()).getRecords();
            }
          };
          orderRecordDataview = new OrderRecordDataview(ORDER_RECORD_DATAVIEW_ID, orderRecordListDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(orderRecordDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String ORDER_RECORD_VIEW_OR_EDIT_PANEL_ID = "orderRecordViewOrEditPanel";

      private static final String ORDER_RECORD_PAGING_NAVIGATOR_MARKUP_ID = "orderRecordPagingNavigator";

      private static final String ORDER_RECORD_DATAVIEW_CONTAINER_ID = "orderRecordDataviewContainer";

      private static final long serialVersionUID = -4165310537311768675L;

      private final OrderRecordDataviewContainer orderRecordDataviewContainer;

      private final BootstrapPagingNavigator orderRecordPagingNavigator;

      private final OrderRecordViewOrEditPanel orderRecordViewOrEditPanel;

      public OrderRecordViewTable(final String id, final IModel<Order> model) {
        super(id, model);
        orderRecordDataviewContainer = new OrderRecordDataviewContainer(ORDER_RECORD_DATAVIEW_CONTAINER_ID, (IModel<Order>) OrderRecordViewTable.this.getDefaultModel());
        orderRecordPagingNavigator = new BootstrapPagingNavigator(ORDER_RECORD_PAGING_NAVIGATOR_MARKUP_ID, orderRecordDataviewContainer.orderRecordDataview);
        orderRecordViewOrEditPanel = new OrderRecordViewOrEditPanel(ORDER_RECORD_VIEW_OR_EDIT_PANEL_ID, (IModel<Order>) OrderRecordViewTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        add(orderRecordDataviewContainer.setOutputMarkupId(true));
        add(orderRecordPagingNavigator.setOutputMarkupId(true));
        add(orderRecordViewOrEditPanel.add(orderRecordViewOrEditPanel.new OrderRecordViewFragment()).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String ORDER_RECORD_VIEW_TABLE_ID = "orderRecordViewTable";

    private static final String ORDER_RECORD_VIEW_FRAGMENT_MARKUP_ID = "orderRecordViewFragment";

    private static final String ORDER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID = "orderRecordViewOrEditFragment";

    private static final long serialVersionUID = -8851058614310416237L;

    private final OrderRecordViewTable orderRecordViewTable;

    public OrderRecordViewFragment() {
      super(ORDER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID, ORDER_RECORD_VIEW_FRAGMENT_MARKUP_ID, OrderRecordPanel.this, OrderRecordPanel.this.getDefaultModel());
      orderRecordViewTable = new OrderRecordViewTable(ORDER_RECORD_VIEW_TABLE_ID, (IModel<Order>) OrderRecordViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(orderRecordViewTable.add(new TableBehavior().hover()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = -4119480413347414297L;

  private static final long ITEMS_PER_PAGE = 10;

  public OrderRecordPanel(final String id, final IModel<Order> model) {
    super(id, model);
  }
}
