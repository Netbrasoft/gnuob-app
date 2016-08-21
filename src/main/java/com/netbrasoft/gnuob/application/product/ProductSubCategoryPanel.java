package com.netbrasoft.gnuob.application.product;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.CATEGORY_DATA_PROVIDER_NAME;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
import com.netbrasoft.gnuob.application.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.application.security.AppRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class ProductSubCategoryPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class ProductSubCategoryEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class CategoryEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class CategoryDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
        class CategoryDataview extends DataView<Category> {

          private static final String CLICK_EVENT = "click";

          private static final String POSITION_ID = "position";

          private static final String NAME_ID = "name";

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = -5039874949058607907L;

          private int index = 0;

          protected CategoryDataview(final String id, final IDataProvider<Category> dataProvider,
              final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<Category> newItem(final String id, final int index, final IModel<Category> model) {
            final Item<Category> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void onConfigure() {
            if (categoryDataProvider.size() > 0) {
              productSubCategoryViewOrEditPanel.setEnabled(true);
              productSubCategoryViewOrEditPanel.removeAll();
              productSubCategoryViewOrEditPanel
                  .setSelectedModel(Model.of(categoryDataProvider.iterator(index, index + (long) 1).next()));
              productSubCategoryViewOrEditPanel.add(productSubCategoryViewOrEditPanel.new SubCategoryEditFragment())
                  .setOutputMarkupId(true);
            } else {
              productSubCategoryViewOrEditPanel.setEnabled(false);
              productSubCategoryViewOrEditPanel.removeAll();
              productSubCategoryViewOrEditPanel.setSelectedModel(Model.of(new Category()));
              productSubCategoryViewOrEditPanel.add(productSubCategoryViewOrEditPanel.new SubCategoryEditFragment())
                  .setOutputMarkupId(true);
            }
            super.onConfigure();
          }

          @Override
          protected void populateItem(final Item<Category> item) {
            item.setModel(new CompoundPropertyModel<Category>(item.getModelObject()));
            item.add(new Label(NAME_ID).setOutputMarkupId(true));
            item.add(new Label(POSITION_ID).setOutputMarkupId(true));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                productSubCategoryViewOrEditPanel.removeAll();
                productSubCategoryViewOrEditPanel.setSelectedModel(item.getModel());
                target.add(categoryDataviewContainer.setOutputMarkupId(true));
                target.add(productSubCategoryViewOrEditPanel
                    .add(productSubCategoryViewOrEditPanel.new SubCategoryEditFragment()).setOutputMarkupId(true));
              }
            });
          }
        }

        private static final String CATEGORY_DATAVIEW_ID = "categoryDataview";

        private static final long serialVersionUID = 4047707639075180742L;

        private static final int ITEMS_PER_PAGE = 10;

        private final CategoryDataview categoryDataview;

        public CategoryDataviewContainer(final String id, final IModel<Product> model) {
          super(id, model);
          categoryDataview = new CategoryDataview(CATEGORY_DATAVIEW_ID, categoryDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(categoryDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String SUB_CATEGORY_VIEW_OR_EDIT_PANEL_ID = "subCategoryViewOrEditPanel";

      private static final String CATEGORY_PAGING_NAVIGATOR_ID = "categoryPagingNavigator";

      private static final String CATEGORY_DATAVIEW_CONTAINER_ID = "categoryDataviewContainer";

      private static final long serialVersionUID = -3198761923054137704L;

      private final CategoryDataviewContainer categoryDataviewContainer;

      private final BootstrapPagingNavigator categoryPagingNavigator;

      private final ProductSubCategoryViewOrEditPanel productSubCategoryViewOrEditPanel;

      public CategoryEditTable(final String id, final IModel<Product> model) {
        super(id, model);
        categoryDataviewContainer = new CategoryDataviewContainer(CATEGORY_DATAVIEW_CONTAINER_ID,
            (IModel<Product>) CategoryEditTable.this.getDefaultModel());
        categoryPagingNavigator =
            new BootstrapPagingNavigator(CATEGORY_PAGING_NAVIGATOR_ID, categoryDataviewContainer.categoryDataview);
        productSubCategoryViewOrEditPanel = new ProductSubCategoryViewOrEditPanel(SUB_CATEGORY_VIEW_OR_EDIT_PANEL_ID,
            (IModel<Product>) CategoryEditTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        add(categoryDataviewContainer.setOutputMarkupId(true));
        add(categoryPagingNavigator.setOutputMarkupId(true));
        add(productSubCategoryViewOrEditPanel.add(productSubCategoryViewOrEditPanel.new SubCategoryEditFragment())
            .setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CATEGORY_EDIT_TABLE_ID = "categoryEditTable";

    private static final String PRODUCT_SUB_CATEGORY_EDIT_FRAGMENT_MARKUP_ID = "productSubCategoryEditFragment";

    private static final String PRODUCT_SUB_CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID = "productSubCategoryViewOrEditFragment";

    private static final long serialVersionUID = -3118710638006841019L;

    private final CategoryEditTable categoryEditTable;

    public ProductSubCategoryEditFragment() {
      super(PRODUCT_SUB_CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID, PRODUCT_SUB_CATEGORY_EDIT_FRAGMENT_MARKUP_ID,
          ProductSubCategoryPanel.this, ProductSubCategoryPanel.this.getDefaultModel());
      categoryEditTable = new CategoryEditTable(CATEGORY_EDIT_TABLE_ID,
          (IModel<Product>) ProductSubCategoryEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(categoryEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class ProductSubCategoryViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class CategoryViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
      class CategoryDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
        class CategoryDataview extends DataView<Category> {

          private static final String CLICK_EVENT = "click";

          private static final String POSITION_ID = "position";

          private static final String NAME_ID = "name";

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = -5039874949058607907L;

          private int index = 0;

          protected CategoryDataview(final String id, final IDataProvider<Category> dataProvider,
              final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<Category> newItem(final String id, final int index, final IModel<Category> model) {
            final Item<Category> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void onConfigure() {
            if (categoryDataProvider.size() > 0) {
              productSubCategoryViewOrEditPanel.removeAll();
              productSubCategoryViewOrEditPanel
                  .setSelectedModel(Model.of(categoryDataProvider.iterator(index, index + (long) 1).next()));
              productSubCategoryViewOrEditPanel.add(productSubCategoryViewOrEditPanel.new SubCategoryViewFragment())
                  .setOutputMarkupId(true);
            } else {
              productSubCategoryViewOrEditPanel.removeAll();
              productSubCategoryViewOrEditPanel.setSelectedModel(Model.of(new Category()));
              productSubCategoryViewOrEditPanel.add(productSubCategoryViewOrEditPanel.new SubCategoryViewFragment())
                  .setOutputMarkupId(true);
            }
            super.onConfigure();
          }

          @Override
          protected void populateItem(final Item<Category> item) {
            item.setModel(new CompoundPropertyModel<Category>(item.getModelObject()));
            item.add(new Label(NAME_ID).setOutputMarkupId(true));
            item.add(new Label(POSITION_ID).setOutputMarkupId(true));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                productSubCategoryViewOrEditPanel.removeAll();
                productSubCategoryViewOrEditPanel.setSelectedModel(item.getModel());
                target.add(categoryDataviewContainer.setOutputMarkupId(true));
                target.add(productSubCategoryViewOrEditPanel
                    .add(productSubCategoryViewOrEditPanel.new SubCategoryViewFragment()).setOutputMarkupId(true));
              }
            });
          }
        }

        private static final String CATEGORY_DATAVIEW_ID = "categoryDataview";

        private static final long serialVersionUID = 4047707639075180742L;

        private static final int ITEMS_PER_PAGE = 10;

        private final CategoryDataview categoryDataview;

        public CategoryDataviewContainer(final String id, final IModel<Product> model) {
          super(id, model);
          categoryDataview = new CategoryDataview(CATEGORY_DATAVIEW_ID, categoryDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(categoryDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String SUB_CATEGORY_VIEW_OR_EDIT_PANEL_ID = "subCategoryViewOrEditPanel";

      private static final String CATEGORY_PAGING_NAVIGATOR_ID = "categoryPagingNavigator";

      private static final String CATEGORY_DATAVIEW_CONTAINER_ID = "categoryDataviewContainer";

      private static final long serialVersionUID = 1254364120794610309L;

      private final CategoryDataviewContainer categoryDataviewContainer;

      private final BootstrapPagingNavigator categoryPagingNavigator;

      private final ProductSubCategoryViewOrEditPanel productSubCategoryViewOrEditPanel;

      public CategoryViewTable(final String id, final IModel<Product> model) {
        super(id, model);
        categoryDataviewContainer = new CategoryDataviewContainer(CATEGORY_DATAVIEW_CONTAINER_ID,
            (IModel<Product>) CategoryViewTable.this.getDefaultModel());
        categoryPagingNavigator =
            new BootstrapPagingNavigator(CATEGORY_PAGING_NAVIGATOR_ID, categoryDataviewContainer.categoryDataview);
        productSubCategoryViewOrEditPanel = new ProductSubCategoryViewOrEditPanel(SUB_CATEGORY_VIEW_OR_EDIT_PANEL_ID,
            (IModel<Product>) CategoryViewTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        add(categoryDataviewContainer.setOutputMarkupId(true));
        add(categoryPagingNavigator.setOutputMarkupId(true));
        add(productSubCategoryViewOrEditPanel.add(productSubCategoryViewOrEditPanel.new SubCategoryViewFragment())
            .setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CATEGORY_VIEW_TABLE_ID = "categoryViewTable";

    private static final String PRODUCT_SUB_CATEGORY_VIEW_FRAGMENT_MARKUP_ID = "productSubCategoryViewFragment";

    private static final String PRODUCT_SUB_CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID = "productSubCategoryViewOrEditFragment";

    private static final long serialVersionUID = 2822717929419097201L;

    private final CategoryViewTable categoryViewTable;

    public ProductSubCategoryViewFragment() {
      super(PRODUCT_SUB_CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID, PRODUCT_SUB_CATEGORY_VIEW_FRAGMENT_MARKUP_ID,
          ProductSubCategoryPanel.this, ProductSubCategoryPanel.this.getDefaultModel());
      categoryViewTable = new CategoryViewTable(CATEGORY_VIEW_TABLE_ID,
          (IModel<Product>) ProductSubCategoryViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(categoryViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = 3703226064705246155L;

  @SpringBean(name = CATEGORY_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Category> categoryDataProvider;

  public ProductSubCategoryPanel(final String id, final IModel<Product> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    categoryDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    categoryDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    categoryDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    categoryDataProvider.setOrderBy(OrderBy.POSITION_A_Z);
    categoryDataProvider.setType(new Category());
    categoryDataProvider.getType().setActive(true);
    super.onInitialize();
  }
}
