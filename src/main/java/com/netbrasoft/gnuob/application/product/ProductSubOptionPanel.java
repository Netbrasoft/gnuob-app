package com.netbrasoft.gnuob.application.product;

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

import com.netbrasoft.gnuob.api.Option;
import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.api.SubOption;
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
public class ProductSubOptionPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class ProductSubOptionEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class SubOptionEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class AddAjaxLink extends BootstrapAjaxLink<Product> {

        private static final long serialVersionUID = 9191172039973638020L;

        public AddAjaxLink(final String id, final IModel<Product> model, final Buttons.Type type, final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.plus);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          ProductSubOptionPanel.this.selectedModel.getObject().getSubOptions().add(new SubOption());
          subOptionDataviewContainer.subOptionDataview.index = ProductSubOptionPanel.this.selectedModel.getObject().getSubOptions().size() - 1;
          productSubOptionViewOrEditPanel.removeAll();
          target.add(subOptionDataviewContainer.setOutputMarkupId(true));
          target.add(productSubOptionViewOrEditPanel.add(productSubOptionViewOrEditPanel.new ProductSubOptionEditFragment()).setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class SubOptionDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
        class SubOptionDataview extends DataView<SubOption> {

          @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
          class RemoveAjaxLink extends BootstrapAjaxLink<SubOption> {

            private static final long serialVersionUID = -6950515027229520882L;

            public RemoveAjaxLink(final String id, final IModel<SubOption> model, final Buttons.Type type, final IModel<String> labelModel) {
              super(id, model, type, labelModel);
              setIconType(GlyphIconType.remove);
              setSize(Buttons.Size.Mini);
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
              ProductSubOptionPanel.this.selectedModel.getObject().getSubOptions().remove(RemoveAjaxLink.this.getDefaultModelObject());
              subOptionDataview.index = ProductSubOptionPanel.this.selectedModel.getObject().getSubOptions().size() - 1;
              productSubOptionViewOrEditPanel.removeAll();
              target.add(subOptionDataviewContainer.setOutputMarkupId(true));
              target.add(productSubOptionViewOrEditPanel.add(productSubOptionViewOrEditPanel.new ProductSubOptionEditFragment()).setOutputMarkupId(true));
            }
          }

          private static final String REMOVE_ID = "remove";

          private static final String CLICK_EVENT = "click";

          private static final String DISABLED_ID = "disabled";

          private static final String VALUE_ID = "value";

          private static final String DESCRIPTION_ID = "description";

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = 8343306073164168425L;

          private int index = 0;

          protected SubOptionDataview(final String id, final IDataProvider<SubOption> dataProvider, final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<SubOption> newItem(final String id, final int index, final IModel<SubOption> model) {
            final Item<SubOption> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void onConfigure() {
            if (!ProductSubOptionPanel.this.selectedModel.getObject().getSubOptions().isEmpty()) {
              productSubOptionViewOrEditPanel.setEnabled(true);
              productSubOptionViewOrEditPanel.removeAll();
              productSubOptionViewOrEditPanel.setSelectedModel(Model.of(ProductSubOptionPanel.this.selectedModel.getObject().getSubOptions().get(index)),
                  Model.of(ProductSubOptionPanel.this.selectedModel.getObject()));
              productSubOptionViewOrEditPanel.add(productSubOptionViewOrEditPanel.new ProductSubOptionEditFragment()).setOutputMarkupId(true);
            } else {
              productSubOptionViewOrEditPanel.setEnabled(false);
              productSubOptionViewOrEditPanel.removeAll();
              productSubOptionViewOrEditPanel.setSelectedModel(Model.of(new SubOption()), Model.of(ProductSubOptionPanel.this.selectedModel.getObject()));
              productSubOptionViewOrEditPanel.add(productSubOptionViewOrEditPanel.new ProductSubOptionEditFragment()).setOutputMarkupId(true);
            }
            super.onConfigure();
          }

          @Override
          protected void populateItem(final Item<SubOption> item) {
            item.setModel(new CompoundPropertyModel<SubOption>(item.getModelObject()));
            item.add(new Label(DESCRIPTION_ID).setOutputMarkupId(true));
            item.add(new Label(VALUE_ID).setOutputMarkupId(true));
            item.add(new Label(DISABLED_ID).setOutputMarkupId(true));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                productSubOptionViewOrEditPanel.setSelectedModel(item.getModel(), Model.of(ProductSubOptionPanel.this.selectedModel.getObject()));
                productSubOptionViewOrEditPanel.removeAll();
                target.add(subOptionDataviewContainer.setOutputMarkupId(true));
                target.add(productSubOptionViewOrEditPanel.add(productSubOptionViewOrEditPanel.new ProductSubOptionEditFragment()).setOutputMarkupId(true));
              }
            });
            item.add(new RemoveAjaxLink(REMOVE_ID, item.getModel(), Buttons.Type.Default,
                Model.of(ProductSubOptionPanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY))).add(new ConfirmationBehavior() {

                  private static final long serialVersionUID = 7744720444161839031L;

                  @Override
                  public void renderHead(final Component component, final IHeaderResponse response) {
                    response.render($(component).chain("confirmation",
                        new ConfirmationConfig().withTitle(getString(NetbrasoftApplicationConstants.CONFIRMATION_MESSAGE_KEY)).withSingleton(true).withPopout(true)
                            .withBtnOkLabel(getString(NetbrasoftApplicationConstants.CONFIRM_MESSAGE_KEY))
                            .withBtnCancelLabel(getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)))
                        .asDomReadyScript());
                  }
                }));
          }
        }

        private static final String SUB_OPTION_DATAVIEW_ID = "subOptionDataview";

        private static final long serialVersionUID = -3393999999422653255L;

        private final SubOptionDataview subOptionDataview;

        private final ListDataProvider<SubOption> subOptionListDataProvider;

        public SubOptionDataviewContainer(final String id, final IModel<Product> model) {
          super(id, model);
          subOptionListDataProvider = new ListDataProvider<SubOption>() {

            private static final long serialVersionUID = -3261859241046697057L;

            @Override
            protected List<SubOption> getData() {
              return ProductSubOptionPanel.this.selectedModel.getObject().getSubOptions();
            }
          };
          subOptionDataview = new SubOptionDataview(SUB_OPTION_DATAVIEW_ID, subOptionListDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(subOptionDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String SUB_OPTION_PAGING_NAVIGATOR_ID = "subOptionPagingNavigator";

      private static final String SUB_OPTION_DATAVIEW_CONTAINER_ID = "subOptionDataviewContainer";

      private static final String ADD_ID = "add";

      private static final long serialVersionUID = -5522048858537112825L;

      private final AddAjaxLink addAjaxLink;

      private final SubOptionDataviewContainer subOptionDataviewContainer;

      private final BootstrapPagingNavigator subOptionPagingNavigator;

      private final ProductSubOptionViewOrEditPanel productSubOptionViewOrEditPanel;

      public SubOptionEditTable(final String id, final IModel<Product> model) {
        super(id, model);
        addAjaxLink = new AddAjaxLink(ADD_ID, (IModel<Product>) SubOptionEditTable.this.getDefaultModel(), Buttons.Type.Primary,
            Model.of(ProductSubOptionPanel.this.getString(NetbrasoftApplicationConstants.ADD_MESSAGE_KEY)));
        subOptionDataviewContainer = new SubOptionDataviewContainer(SUB_OPTION_DATAVIEW_CONTAINER_ID, (IModel<Product>) SubOptionEditTable.this.getDefaultModel());
        subOptionPagingNavigator = new BootstrapPagingNavigator(SUB_OPTION_PAGING_NAVIGATOR_ID, subOptionDataviewContainer.subOptionDataview);
        productSubOptionViewOrEditPanel = new ProductSubOptionViewOrEditPanel("productSubOptionViewOrEditPanel", (IModel<Product>) SubOptionEditTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        add(addAjaxLink.setOutputMarkupId(true));
        add(subOptionDataviewContainer.setOutputMarkupId(true));
        add(subOptionPagingNavigator.setOutputMarkupId(true));
        add(productSubOptionViewOrEditPanel.add(productSubOptionViewOrEditPanel.new ProductSubOptionEditFragment()).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SUB_OPTION_EDIT_TABLE_ID = "subOptionEditTable";

    private static final String PRODUCT_SUB_OPTION_EDIT_FRAGMENT_MARKUP_ID = "productSubOptionEditFragment";

    private static final String PRODUCT_SUB_OPTION_VIEW_OR_EDIT_FRAGEMENT_ID = "productSubOptionViewOrEditFragment";

    private static final long serialVersionUID = -3434639671937275356L;

    private final SubOptionEditTable subOptionEditTable;

    public ProductSubOptionEditFragment() {
      super(PRODUCT_SUB_OPTION_VIEW_OR_EDIT_FRAGEMENT_ID, PRODUCT_SUB_OPTION_EDIT_FRAGMENT_MARKUP_ID, ProductSubOptionPanel.this, ProductSubOptionPanel.this.getDefaultModel());
      subOptionEditTable = new SubOptionEditTable(SUB_OPTION_EDIT_TABLE_ID, (IModel<Product>) ProductSubOptionEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(subOptionEditTable.add(new TableBehavior().hover()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class ProductSubOptionViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class SubOptionViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
      class SubOptionDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
        class SubOptionDataview extends DataView<SubOption> {

          private static final String CLICK_EVENT = "click";

          private static final String DISABLED_ID = "disabled";

          private static final String VALUE_ID = "value";

          private static final String DESCRIPTION_ID = "description";

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = 8343306073164168425L;

          private int index = 0;

          protected SubOptionDataview(final String id, final IDataProvider<SubOption> dataProvider, final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<SubOption> newItem(final String id, final int index, final IModel<SubOption> model) {
            final Item<SubOption> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void onConfigure() {
            if (!ProductSubOptionPanel.this.selectedModel.getObject().getSubOptions().isEmpty()) {
              productSubOptionViewOrEditPanel.removeAll();
              productSubOptionViewOrEditPanel.setSelectedModel(Model.of(ProductSubOptionPanel.this.selectedModel.getObject().getSubOptions().get(index)),
                  Model.of(ProductSubOptionPanel.this.selectedModel.getObject()));
              productSubOptionViewOrEditPanel.add(productSubOptionViewOrEditPanel.new ProductSubOptionViewFragment()).setOutputMarkupId(true);
            } else {
              productSubOptionViewOrEditPanel.removeAll();
              productSubOptionViewOrEditPanel.setSelectedModel(Model.of(new SubOption()), Model.of(ProductSubOptionPanel.this.selectedModel.getObject()));
              productSubOptionViewOrEditPanel.add(productSubOptionViewOrEditPanel.new ProductSubOptionViewFragment()).setOutputMarkupId(true);
            }
            super.onConfigure();
          }

          @Override
          protected void populateItem(final Item<SubOption> item) {
            item.setModel(new CompoundPropertyModel<SubOption>(item.getModelObject()));
            item.add(new Label(DESCRIPTION_ID).setOutputMarkupId(true));
            item.add(new Label(VALUE_ID).setOutputMarkupId(true));
            item.add(new Label(DISABLED_ID).setOutputMarkupId(true));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                productSubOptionViewOrEditPanel.setSelectedModel(item.getModel(), Model.of(ProductSubOptionPanel.this.selectedModel.getObject()));
                productSubOptionViewOrEditPanel.removeAll();
                target.add(subOptionDataviewContainer.setOutputMarkupId(true));
                target.add(productSubOptionViewOrEditPanel.add(productSubOptionViewOrEditPanel.new ProductSubOptionViewFragment()).setOutputMarkupId(true));
              }
            });
          }
        }

        private static final String SUB_OPTION_DATAVIEW_ID = "subOptionDataview";

        private static final long serialVersionUID = -3393999999422653255L;

        private final SubOptionDataview subOptionDataview;

        private final ListDataProvider<SubOption> subOptionListDataProvider;

        public SubOptionDataviewContainer(final String id, final IModel<Product> model) {
          super(id, model);
          subOptionListDataProvider = new ListDataProvider<SubOption>() {

            private static final long serialVersionUID = -3261859241046697057L;

            @Override
            protected List<SubOption> getData() {
              return ProductSubOptionPanel.this.selectedModel.getObject().getSubOptions();
            }
          };
          subOptionDataview = new SubOptionDataview(SUB_OPTION_DATAVIEW_ID, subOptionListDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(subOptionDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String PRODUCT_SUB_OPTION_VIEW_OR_EDIT_PANEL_ID = "productSubOptionViewOrEditPanel";

      private static final String SUB_OPTION_PAGING_NAVIGATOR_ID = "subOptionPagingNavigator";

      private static final String SUB_OPTION_DATAVIEW_CONTAINER_ID = "subOptionDataviewContainer";

      private static final long serialVersionUID = -5522048858537112825L;

      private final SubOptionDataviewContainer subOptionDataviewContainer;

      private final BootstrapPagingNavigator subOptionPagingNavigator;

      private final ProductSubOptionViewOrEditPanel productSubOptionViewOrEditPanel;

      public SubOptionViewTable(final String id, final IModel<Product> model) {
        super(id, model);
        subOptionDataviewContainer = new SubOptionDataviewContainer(SUB_OPTION_DATAVIEW_CONTAINER_ID, (IModel<Product>) SubOptionViewTable.this.getDefaultModel());
        subOptionPagingNavigator = new BootstrapPagingNavigator(SUB_OPTION_PAGING_NAVIGATOR_ID, subOptionDataviewContainer.subOptionDataview);
        productSubOptionViewOrEditPanel =
            new ProductSubOptionViewOrEditPanel(PRODUCT_SUB_OPTION_VIEW_OR_EDIT_PANEL_ID, (IModel<Product>) SubOptionViewTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        add(subOptionDataviewContainer.setOutputMarkupId(true));
        add(subOptionPagingNavigator.setOutputMarkupId(true));
        add(productSubOptionViewOrEditPanel.add(productSubOptionViewOrEditPanel.new ProductSubOptionViewFragment()).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SUB_OPTION_VIEW_TABLE_ID = "subOptionViewTable";

    private static final String PRODUCT_SUB_OPTION_VIEW_FRAGMENT_MARKUP_ID = "productSubOptionViewFragment";

    private static final String PRODUCT_SUB_OPTION_VIEW_OR_EDIT_FRAGMENT_ID = "productSubOptionViewOrEditFragment";

    private static final long serialVersionUID = -3434639671937275356L;

    private final SubOptionViewTable subOptionViewTable;

    public ProductSubOptionViewFragment() {
      super(PRODUCT_SUB_OPTION_VIEW_OR_EDIT_FRAGMENT_ID, PRODUCT_SUB_OPTION_VIEW_FRAGMENT_MARKUP_ID, ProductSubOptionPanel.this, ProductSubOptionPanel.this.getDefaultModel());
      subOptionViewTable = new SubOptionViewTable(SUB_OPTION_VIEW_TABLE_ID, (IModel<Product>) ProductSubOptionViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(subOptionViewTable.add(new TableBehavior().hover()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final int ITEMS_PER_PAGE = 10;

  private static final long serialVersionUID = 2594347542853743933L;

  private IModel<Option> selectedModel;

  public ProductSubOptionPanel(final String id, final IModel<Product> model) {
    super(id, model);
    selectedModel = Model.of(new Option());
  }

  public void setSelectedModel(final IModel<Option> selectedModel) {
    this.selectedModel = selectedModel;
  }
}
