package com.netbrasoft.gnuob.application.product;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.api.SubCategory;
import com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants;
import com.netbrasoft.gnuob.application.category.table.SubCategoryTableTree;
import com.netbrasoft.gnuob.application.security.AppRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapBaseBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.agilecoders.wicket.core.util.Attributes;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class ProductSubCategoryViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  public class SubCategoryEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class ProductSubCategoryEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class ProductSubCategoriesDataViewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
        class SubCategoryDataview extends SubCategoryTableTree {

          private static final String CLICK_EVENT = "click";

          private static final String INFO_CLASS_NAME = "info";

          private static final long serialVersionUID = 890348942507232169L;

          public SubCategoryDataview(final String id, final List<? extends IColumn<SubCategory, String>> columns, final ITreeProvider<SubCategory> provider,
              final long rowsPerPage) {
            super(id, columns, provider, rowsPerPage);
          }

          @Override
          protected Item<SubCategory> newRowItem(final String id, final int index, final IModel<SubCategory> model) {
            final Item<SubCategory> item = super.newRowItem(id, index, model);
            for (final SubCategory subCategory : ((IModel<Product>) ProductSubCategoriesDataViewContainer.this.getDefaultModel()).getObject().getSubCategories()) {
              if (subCategory.getId() == model.getObject().getId()) {
                item.add(new BootstrapBaseBehavior() {

                  private static final long serialVersionUID = -4903722864597601489L;

                  @Override
                  public void onComponentTag(final Component component, final ComponentTag tag) {
                    Attributes.addClass(tag, INFO_CLASS_NAME);
                  }
                });
                item.add(new AjaxEventBehavior(CLICK_EVENT) {

                  private static final long serialVersionUID = 1L;

                  @Override
                  public void onEvent(final AjaxRequestTarget target) {
                    ((IModel<Product>) ProductSubCategoriesDataViewContainer.this.getDefaultModel()).getObject().getSubCategories().remove(subCategory);
                    ProductSubCategoryViewOrEditPanel.this.removeAll();
                    target.add(ProductSubCategoryViewOrEditPanel.this.add(ProductSubCategoryViewOrEditPanel.this.new SubCategoryEditFragment()).setOutputMarkupId(true));
                  }
                });
                return item;
              }
            }

            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                ((IModel<Product>) ProductSubCategoriesDataViewContainer.this.getDefaultModel()).getObject().getSubCategories().add(item.getModelObject());
                ProductSubCategoryViewOrEditPanel.this.removeAll();
                target.add(ProductSubCategoryViewOrEditPanel.this.add(ProductSubCategoryViewOrEditPanel.this.new SubCategoryEditFragment()).setOutputMarkupId(true));
              }
            });
            return item;
          }
        }

        private static final String PRODUCT_SUB_CATEGORIES_DATA_VIEW_ID = "productSubCategoriesDataView";

        private static final long serialVersionUID = 3279611195983210197L;

        private final SubCategoryDataview subCategoryDataview;

        public ProductSubCategoriesDataViewContainer(final String id, final IModel<Category> model) {
          super(id, model);
          subCategoryDataview = new SubCategoryDataview(PRODUCT_SUB_CATEGORIES_DATA_VIEW_ID, createColumns(), subCategoryTreeProvider, Integer.MAX_VALUE);
        }

        @Override
        protected void onInitialize() {
          add(subCategoryDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String SUB_CATEGORY_EDIT_FORM_ID = "subCategoryEditForm";

      private static final String PRODUCT_SUB_CATEGORIES_DATA_VIEW_CONTAINER_ID = "productSubCategoriesDataViewContainer";

      private static final long serialVersionUID = 1244529149500926347L;

      private final ProductSubCategoriesDataViewContainer productSubCategoriesDataViewContainer;

      private final BootstrapForm<?> subCategoryEditForm;

      public ProductSubCategoryEditTable(final String id, final IModel<Category> model) {
        super(id, model);
        productSubCategoriesDataViewContainer =
            new ProductSubCategoriesDataViewContainer(PRODUCT_SUB_CATEGORIES_DATA_VIEW_CONTAINER_ID, (IModel<Category>) ProductSubCategoryEditTable.this.getDefaultModel());
        subCategoryEditForm = new BootstrapForm<>(SUB_CATEGORY_EDIT_FORM_ID);
      }

      @Override
      protected void onInitialize() {
        subCategoryEditForm.add(productSubCategoriesDataViewContainer.setOutputMarkupId(true));
        add(subCategoryEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String PRODUCT_SUB_CATEGORY_EDIT_TABLE_ID = "productSubCategoryEditTable";

    private static final String PRODUCT_SUB_CATEGORY_EDIT_FRAGMENT_MARKUP_ID = "productSubCategoryEditFragment";

    private static final String PRODUCT_SUB_CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID = "productSubCategoryViewOrEditFragment";

    private static final long serialVersionUID = 1492400872373227225L;

    private final ProductSubCategoryEditTable productSubCategoryEditTable;

    public SubCategoryEditFragment() {
      super(PRODUCT_SUB_CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID, PRODUCT_SUB_CATEGORY_EDIT_FRAGMENT_MARKUP_ID, ProductSubCategoryViewOrEditPanel.this,
          ProductSubCategoryViewOrEditPanel.this.getDefaultModel());
      productSubCategoryEditTable = new ProductSubCategoryEditTable(PRODUCT_SUB_CATEGORY_EDIT_TABLE_ID, (IModel<Category>) SubCategoryEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(productSubCategoryEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  class SubCategoryTreeProvider implements ITreeProvider<SubCategory> {
    private static final long serialVersionUID = -592161727647897932L;

    @Override
    public void detach() {
      return;
    }

    @Override
    public Iterator<? extends SubCategory> getChildren(final SubCategory node) {
      return node.getSubCategories().iterator();
    }

    @Override
    public Iterator<? extends SubCategory> getRoots() {
      return ProductSubCategoryViewOrEditPanel.this.selectedModel.getObject().getSubCategories().iterator();
    }

    @Override
    public boolean hasChildren(final SubCategory node) {
      return !node.getSubCategories().isEmpty();
    }

    @Override
    public IModel<SubCategory> model(final SubCategory object) {
      return Model.of(object);
    }
  }

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  public class SubCategoryViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class ProductSubCategoryViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
      class ProductSubCategoriesDataViewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
        class SubCategoryDataview extends SubCategoryTableTree {

          private static final String INFO_CLASS_NAME = "info";

          private static final long serialVersionUID = 890348942507232169L;

          public SubCategoryDataview(final String id, final List<? extends IColumn<SubCategory, String>> columns, final ITreeProvider<SubCategory> provider,
              final long rowsPerPage) {
            super(id, columns, provider, rowsPerPage);
          }

          @Override
          protected Item<SubCategory> newRowItem(final String id, final int index, final IModel<SubCategory> model) {
            final Item<SubCategory> item = super.newRowItem(id, index, model);
            for (final SubCategory subCategory : ((IModel<Product>) ProductSubCategoriesDataViewContainer.this.getDefaultModel()).getObject().getSubCategories()) {
              if (subCategory.getId() == model.getObject().getId()) {
                item.add(new BootstrapBaseBehavior() {

                  private static final long serialVersionUID = -4903722864597601489L;

                  @Override
                  public void onComponentTag(final Component component, final ComponentTag tag) {
                    Attributes.addClass(tag, INFO_CLASS_NAME);
                  }
                });
                return item;
              }
            }
            return item;
          }
        }

        private static final String PRODUCT_SUB_CATEGORIES_DATA_VIEW_ID = "productSubCategoriesDataView";

        private static final long serialVersionUID = 3279611195983210197L;

        private final SubCategoryDataview subCategoryDataview;

        public ProductSubCategoriesDataViewContainer(final String id, final IModel<Category> model) {
          super(id, model);
          subCategoryDataview = new SubCategoryDataview(PRODUCT_SUB_CATEGORIES_DATA_VIEW_ID, createColumns(), subCategoryTreeProvider, Integer.MAX_VALUE);
        }

        @Override
        protected void onInitialize() {
          add(subCategoryDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String SUB_CATEGORY_VIEW_FORM_ID = "subCategoryViewForm";

      private static final String PRODUCT_SUB_CATEGORIES_DATA_VIEW_CONTAINER_ID = "productSubCategoriesDataViewContainer";

      private static final long serialVersionUID = -4728519835478043962L;

      private final ProductSubCategoriesDataViewContainer productSubCategoriesDataViewContainer;

      private final BootstrapForm<?> subCategoryViewForm;

      public ProductSubCategoryViewTable(final String id, final IModel<Category> model) {
        super(id, model);
        productSubCategoriesDataViewContainer =
            new ProductSubCategoriesDataViewContainer(PRODUCT_SUB_CATEGORIES_DATA_VIEW_CONTAINER_ID, (IModel<Category>) ProductSubCategoryViewTable.this.getDefaultModel());
        subCategoryViewForm = new BootstrapForm<>(SUB_CATEGORY_VIEW_FORM_ID);
      }

      @Override
      protected void onInitialize() {
        subCategoryViewForm.add(productSubCategoriesDataViewContainer.setOutputMarkupId(true));
        add(subCategoryViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String PRODUCT_SUB_CATEGORY_VIEW_TABLE_ID = "productSubCategoryViewTable";

    private static final String PRODUCT_SUB_CATEGORY_VIEW_FRAGMENT_MARKUP_ID = "productSubCategoryViewFragment";

    private static final String PRODUCT_SUB_CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID = "productSubCategoryViewOrEditFragment";

    private static final long serialVersionUID = 6624858821589938712L;

    private final ProductSubCategoryViewTable productSubCategoryViewTable;

    public SubCategoryViewFragment() {
      super(PRODUCT_SUB_CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID, PRODUCT_SUB_CATEGORY_VIEW_FRAGMENT_MARKUP_ID, ProductSubCategoryViewOrEditPanel.this,
          ProductSubCategoryViewOrEditPanel.this.getDefaultModel());
      productSubCategoryViewTable = new ProductSubCategoryViewTable(PRODUCT_SUB_CATEGORY_VIEW_TABLE_ID, (IModel<Category>) SubCategoryViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(productSubCategoryViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final String DESCRIPTION_PROPERTY_EXPRESSION = "description";

  private static final String SMALL_CSS_CLASS = "small";

  private static final long serialVersionUID = 35319895698886122L;

  private IModel<Category> selectedModel;

  private final SubCategoryTreeProvider subCategoryTreeProvider;

  public ProductSubCategoryViewOrEditPanel(final String id, final IModel<Product> model) {
    super(id, model);
    subCategoryTreeProvider = new SubCategoryTreeProvider();
  }

  private List<IColumn<SubCategory, String>> createColumns() {
    final List<IColumn<SubCategory, String>> columns = new ArrayList<IColumn<SubCategory, String>>();

    columns.add(new TreeColumn<SubCategory, String>(Model.of(ProductSubCategoryViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.VALUE_MESSAGE_KEY))) {
      private static final long serialVersionUID = -8544017108974205690L;

      @Override
      public String getCssClass() {
        return SMALL_CSS_CLASS;
      }
    });

    columns.add(new PropertyColumn<SubCategory, String>(Model.of(ProductSubCategoryViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.DESCRIPTION_MESSAGE_KEY)),
        DESCRIPTION_PROPERTY_EXPRESSION) {
      private static final long serialVersionUID = -1013188144051609487L;

      @Override
      public String getCssClass() {
        return SMALL_CSS_CLASS;
      }
    });

    return columns;
  }

  public void setSelectedModel(final IModel<Category> selectedModel) {
    this.selectedModel = selectedModel;
  }
}
