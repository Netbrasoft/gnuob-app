package com.netbrasoft.gnuob.application.category;

import static de.agilecoders.wicket.jquery.JQuery.$;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.SubCategory;
import com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants;
import com.netbrasoft.gnuob.application.category.table.SubCategoryTableTree;
import com.netbrasoft.gnuob.application.security.AppRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationConfig;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class SubCategoryPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class SubCategoryEditFragement extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class SubCategoryEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class SubCategoriesDataViewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
        class SubCategoryDataview extends SubCategoryTableTree {

          @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
          class RemoveAjaxLink extends BootstrapAjaxLink<SubCategory> {

            private static final String REMOVE_ID = "remove";

            private static final long serialVersionUID = -8317730269644885290L;

            public RemoveAjaxLink(final IModel<SubCategory> model) {
              super(REMOVE_ID, model, Buttons.Type.Default, Model.of(SubCategoryPanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY)));
              setIconType(GlyphIconType.remove);
              setSize(Buttons.Size.Mini);
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
              try {
                removeSubCategory(((Category) SubCategoryPanel.this.getDefaultModelObject()).getSubCategories(), (SubCategory) getDefaultModelObject());
              } catch (final RuntimeException e) {
                LOGGER.warn(e.getMessage(), e);
                warn(e.getLocalizedMessage());
              } finally {
                target.add(subCategoriesDataViewContainer.setOutputMarkupId(true));
              }
            }

            private void removeSubCategory(final List<SubCategory> subCategories, final SubCategory subCategory) {
              if (subCategories.contains(subCategory)) {
                subCategories.remove(subCategory);
              } else {
                for (final SubCategory sub : subCategories) {
                  removeSubCategory(sub.getSubCategories(), subCategory);
                }
              }
            }
          }

          @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
          class RemoveSubCategoryPanel extends Panel {

            private static final long serialVersionUID = 1136516888736878750L;

            private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

            public RemoveSubCategoryPanel(final String id, final IModel<SubCategory> model) {
              super(id, model);
            }

            @Override
            protected void onInitialize() {
              add(new RemoveAjaxLink((IModel<SubCategory>) getDefaultModel()).add(new ConfirmationBehavior() {

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
              super.onInitialize();
            }
          }

          private static final String EMPTY_VALUE = "";

          private static final String CLICK_EVENT = "click";

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = 7493141095626794439L;

          private Item<SubCategory> item;

          public SubCategoryDataview(final String id, final List<? extends IColumn<SubCategory, String>> columns, final ITreeProvider<SubCategory> provider,
              final long rowsPerPage) {
            super(id, columns, provider, rowsPerPage);
          }

          @Override
          protected Item<SubCategory> newRowItem(final String id, final int index, final IModel<SubCategory> model) {
            final Item<SubCategory> item = super.newRowItem(id, index, model);
            if (SubCategoryDataview.this.item == null || SubCategoryDataview.this.item.getIndex() == index) {
              SubCategoryDataview.this.item = (Item<SubCategory>) item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 3378951512633587481L;

              @Override
              protected void onEvent(final AjaxRequestTarget target) {
                SubCategoryDataview.this.item.add(new AttributeModifier(CLASS_ATTRIBUTE, EMPTY_VALUE));
                SubCategoryDataview.this.item = (Item<SubCategory>) item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
                subCategoryViewOrEditPanelToolbarPanel.setSelectedModel(item.getModel());
                target.add(subCategoriesDataViewContainer.setOutputMarkupId(true));
              }
            });
            return item;
          }

          @Override
          protected void onConfigure() {
            subCategoryViewOrEditPanelToolbarPanel.removeAll();
            subCategoryViewOrEditPanelToolbarPanel.add(subCategoryViewOrEditPanelToolbarPanel.new SubCategoryEditFragment()).setOutputMarkupId(true);
            super.onConfigure();
          }
        }

        private static final String SUB_CATEGORY_DATAVIEW_ID = "subCategoryDataview";

        private static final long serialVersionUID = -5435165517396427026L;

        private final SubCategoryDataview subCategoryDataview;

        private final SubCategoryViewOrEditToolbarPanel subCategoryViewOrEditPanelToolbarPanel;

        public SubCategoriesDataViewContainer(final String id, final IModel<Category> model) {
          super(id, model);
          subCategoryDataview = new SubCategoryDataview(SUB_CATEGORY_DATAVIEW_ID, createColumns(), subCategoryTreeProvider, Integer.MAX_VALUE);
          subCategoryViewOrEditPanelToolbarPanel =
              new SubCategoryViewOrEditToolbarPanel((IModel<Category>) SubCategoriesDataViewContainer.this.getDefaultModel(), subCategoryDataview.getTable());
        }

        @Override
        protected void onInitialize() {
          subCategoryDataview.getTable().addBottomToolbar(
              (AbstractToolbar) subCategoryViewOrEditPanelToolbarPanel.add(subCategoryViewOrEditPanelToolbarPanel.new SubCategoryEditFragment()).setOutputMarkupId(true));
          add(subCategoryDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String SUB_CATEGORIES_DATA_VIEW_CONTAINER_ID = "subCategoriesDataViewContainer";

      private static final long serialVersionUID = 3120969231215664092L;

      private final SubCategoriesDataViewContainer subCategoriesDataViewContainer;

      public SubCategoryEditTable(final String id, final IModel<Category> model) {
        super(id, model);
        subCategoriesDataViewContainer = new SubCategoriesDataViewContainer(SUB_CATEGORIES_DATA_VIEW_CONTAINER_ID, (IModel<Category>) SubCategoryEditTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        add(subCategoriesDataViewContainer.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SUB_CATEGORY_EDIT_TABLE_ID = "subCategoryEditTable";

    private static final String SUB_CATEGORY_EDIT_FRAGEMENT_MARKUP_ID = "subCategoryEditFragement";

    private static final String SUB_CATEGORY_VIEW_OR_EDIT_FRAGEMENT_ID = "subCategoryViewOrEditFragement";

    private static final long serialVersionUID = 3162058383568556008L;

    private final SubCategoryEditTable subCategoryEditTable;

    public SubCategoryEditFragement() {
      super(SUB_CATEGORY_VIEW_OR_EDIT_FRAGEMENT_ID, SUB_CATEGORY_EDIT_FRAGEMENT_MARKUP_ID, SubCategoryPanel.this, SubCategoryPanel.this.getDefaultModel());
      subCategoryEditTable = new SubCategoryEditTable(SUB_CATEGORY_EDIT_TABLE_ID, (IModel<Category>) SubCategoryEditFragement.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(subCategoryEditTable.setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  class SubCategoryTreeProvider implements ITreeProvider<SubCategory> {
    private static final long serialVersionUID = -592161727647897932L;

    public SubCategoryTreeProvider() {}

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
      return ((Category) SubCategoryPanel.this.getDefaultModelObject()).getSubCategories().iterator();
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
  class SubCategoryViewFragement extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class SubCategoryViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
      class SubCategoriesDataViewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
        class SubCategoryDataview extends SubCategoryTableTree {

          private static final String CLICK_EVENT = "click";

          private static final long serialVersionUID = 7493141095626794439L;

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private Item<SubCategory> item;

          public SubCategoryDataview(final String id, final List<? extends IColumn<SubCategory, String>> columns, final ITreeProvider<SubCategory> provider,
              final long rowsPerPage) {
            super(id, columns, provider, rowsPerPage);
          }

          @Override
          protected Item<SubCategory> newRowItem(final String id, final int index, final IModel<SubCategory> model) {
            final Item<SubCategory> item = super.newRowItem(id, index, model);
            if (SubCategoryDataview.this.item == null || SubCategoryDataview.this.item.getIndex() == index) {
              SubCategoryDataview.this.item = (Item<SubCategory>) item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 3378951512633587481L;

              @Override
              protected void onEvent(final AjaxRequestTarget target) {
                SubCategoryDataview.this.item.add(new AttributeModifier(CLASS_ATTRIBUTE, ""));
                SubCategoryDataview.this.item = (Item<SubCategory>) item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
                subCategoryViewOrEditPanelToolbarPanel.setSelectedModel(item.getModel());
                target.add(subCategoriesDataViewContainer.setOutputMarkupId(true));
              }
            });
            return item;
          }

          @Override
          protected void onConfigure() {
            subCategoryViewOrEditPanelToolbarPanel.removeAll();
            subCategoryViewOrEditPanelToolbarPanel.add(subCategoryViewOrEditPanelToolbarPanel.new SubCategoryViewFragment()).setOutputMarkupId(true);
            super.onConfigure();
          }
        }

        private static final String SUB_CATEGORY_DATAVIEW_ID = "subCategoryDataview";

        private static final long serialVersionUID = 8773466731948831695L;

        private final SubCategoryDataview subCategoryDataview;

        private final SubCategoryViewOrEditToolbarPanel subCategoryViewOrEditPanelToolbarPanel;

        public SubCategoriesDataViewContainer(final String id, final IModel<Category> model) {
          super(id, model);
          subCategoryDataview = new SubCategoryDataview(SUB_CATEGORY_DATAVIEW_ID, createColumns(), subCategoryTreeProvider, Integer.MAX_VALUE);
          subCategoryViewOrEditPanelToolbarPanel =
              new SubCategoryViewOrEditToolbarPanel((IModel<Category>) SubCategoriesDataViewContainer.this.getDefaultModel(), subCategoryDataview.getTable());
        }

        @Override
        protected void onInitialize() {
          subCategoryDataview.getTable().addBottomToolbar(
              (AbstractToolbar) subCategoryViewOrEditPanelToolbarPanel.add(subCategoryViewOrEditPanelToolbarPanel.new SubCategoryViewFragment()).setOutputMarkupId(true));
          add(subCategoryDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String SUB_CATEGORIES_DATA_VIEW_CONTAINER_ID = "subCategoriesDataViewContainer";

      private static final long serialVersionUID = 2197029795367976110L;

      private final SubCategoriesDataViewContainer subCategoriesDataViewContainer;

      public SubCategoryViewTable(final String id, final IModel<Category> model) {
        super(id, model);
        subCategoriesDataViewContainer = new SubCategoriesDataViewContainer(SUB_CATEGORIES_DATA_VIEW_CONTAINER_ID, (IModel<Category>) SubCategoryViewTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        add(subCategoriesDataViewContainer.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SUB_CATEGORY_VIEW_TABLE_ID = "subCategoryViewTable";

    private static final String SUB_CATEGORY_VIEW_FRAGEMENT_MARKUP_ID = "subCategoryViewFragement";

    private static final String SUB_CATEGORY_VIEW_OR_EDIT_FRAGEMENT_ID = "subCategoryViewOrEditFragement";

    private static final long serialVersionUID = 3162058383568556008L;

    private final SubCategoryViewTable subCategoryViewTable;

    public SubCategoryViewFragement() {
      super(SUB_CATEGORY_VIEW_OR_EDIT_FRAGEMENT_ID, SUB_CATEGORY_VIEW_FRAGEMENT_MARKUP_ID, SubCategoryPanel.this, SubCategoryPanel.this.getDefaultModel());
      subCategoryViewTable = new SubCategoryViewTable(SUB_CATEGORY_VIEW_TABLE_ID, (IModel<Category>) SubCategoryViewFragement.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(subCategoryViewTable.setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final String SMALL_CSS_CLASS = "small";

  private static final String DESCRIPTION_PROPERTY_EXPRESSION = "description";

  private static final Logger LOGGER = LoggerFactory.getLogger(SubCategoryPanel.class);

  private static final long serialVersionUID = 4492979061717676247L;

  private final SubCategoryTreeProvider subCategoryTreeProvider;

  public SubCategoryPanel(final String id, final IModel<Category> model) {
    super(id, model);
    subCategoryTreeProvider = new SubCategoryTreeProvider();
  }

  private List<IColumn<SubCategory, String>> createColumns() {
    final List<IColumn<SubCategory, String>> columns = new ArrayList<IColumn<SubCategory, String>>();

    columns.add(new TreeColumn<SubCategory, String>(Model.of(SubCategoryPanel.this.getString(NetbrasoftApplicationConstants.VALUE_MESSAGE_KEY))) {
      private static final long serialVersionUID = -8544017108974205690L;

      @Override
      public String getCssClass() {
        return SMALL_CSS_CLASS;
      }
    });

    columns.add(new PropertyColumn<SubCategory, String>(Model.of(SubCategoryPanel.this.getString(NetbrasoftApplicationConstants.DESCRIPTION_MESSAGE_KEY)),
        DESCRIPTION_PROPERTY_EXPRESSION) {
      private static final long serialVersionUID = -1013188144051609487L;

      @Override
      public String getCssClass() {
        return SMALL_CSS_CLASS;
      }
    });

    return columns;
  }
}
