package com.netbrasoft.gnuob.application.category;

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

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.Content;
import com.netbrasoft.gnuob.api.SubCategory;
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
public class SubCategoryContentPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class SubCategoryContentEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class ContentEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class AddAjaxLink extends BootstrapAjaxLink<Category> {

        private static final long serialVersionUID = 9191172039973638020L;

        public AddAjaxLink(final String id, final IModel<Category> model, final Buttons.Type type, final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.plus);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          final Content content = new Content();
          content.setActive(true);
          ((Category) AddAjaxLink.this.getDefaultModelObject()).getContents().add(content);
          contentDataviewContainer.contentDataview.index = ((Category) AddAjaxLink.this.getDefaultModelObject()).getContents().size() - 1;
          subCategoryContentViewOrEditPanel.removeAll();
          target.add(contentDataviewContainer.setOutputMarkupId(true));
          target.add(subCategoryContentViewOrEditPanel.add(subCategoryContentViewOrEditPanel.new SubCategoryContentEditFragment()).setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class ContentDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
        class ContentDataview extends DataView<Content> {

          @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
          class RemoveAjaxLink extends BootstrapAjaxLink<Content> {

            private static final long serialVersionUID = -6950515027229520882L;

            public RemoveAjaxLink(final String id, final IModel<Content> model, final Buttons.Type type, final IModel<String> labelModel) {
              super(id, model, type, labelModel);
              setIconType(GlyphIconType.remove);
              setSize(Buttons.Size.Mini);
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
              ((Category) ContentDataviewContainer.this.getDefaultModelObject()).getContents().remove(RemoveAjaxLink.this.getDefaultModelObject());
              contentDataview.index = ((Category) ContentDataviewContainer.this.getDefaultModelObject()).getContents().size() - 1;
              subCategoryContentViewOrEditPanel.removeAll();
              target.add(contentDataviewContainer.setOutputMarkupId(true));
              target.add(subCategoryContentViewOrEditPanel.add(subCategoryContentViewOrEditPanel.new SubCategoryContentEditFragment()).setOutputMarkupId(true));
            }
          }

          private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

          private static final String REMOVE_ID = "remove";

          private static final String CLICK_EVENT = "click";

          private static final String NAME_ID = "name";

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = 2246346365193989354L;

          private int index = 0;

          protected ContentDataview(final String id, final IDataProvider<Content> dataProvider, final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<Content> newItem(final String id, final int index, final IModel<Content> model) {
            final Item<Content> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void onConfigure() {
            final IModel<SubCategory> model = SubCategoryContentPanel.this.selectedModel;
            if (!model.getObject().getContents().isEmpty()) {
              subCategoryContentViewOrEditPanel.setEnabled(true);
              subCategoryContentViewOrEditPanel.removeAll();
              subCategoryContentViewOrEditPanel.setSelectedModel(Model.of(model.getObject().getContents().get(index)));
              subCategoryContentViewOrEditPanel.add(subCategoryContentViewOrEditPanel.new SubCategoryContentEditFragment()).setOutputMarkupId(true);
            } else {
              subCategoryContentViewOrEditPanel.setEnabled(false);
              subCategoryContentViewOrEditPanel.removeAll();
              subCategoryContentViewOrEditPanel.setSelectedModel(Model.of(new Content()));
              subCategoryContentViewOrEditPanel.add(subCategoryContentViewOrEditPanel.new SubCategoryContentEditFragment()).setOutputMarkupId(true);
            }
            super.onConfigure();
          }

          @Override
          protected void populateItem(final Item<Content> item) {
            item.setModel(new CompoundPropertyModel<Content>(item.getModelObject()));
            item.add(new Label(NAME_ID).setOutputMarkupId(true));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                subCategoryContentViewOrEditPanel.setSelectedModel(item.getModel());
                subCategoryContentViewOrEditPanel.removeAll();
                target.add(contentDataviewContainer.setOutputMarkupId(true));
                target.add(subCategoryContentViewOrEditPanel.add(subCategoryContentViewOrEditPanel.new SubCategoryContentEditFragment()).setOutputMarkupId(true));
              }
            });
            item.add(new RemoveAjaxLink(REMOVE_ID, item.getModel(), Buttons.Type.Default,
                Model.of(SubCategoryContentPanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY))).add(new ConfirmationBehavior() {

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

        private static final String CONTENT_DATAVIEW_ID = "contentDataview";

        private static final long serialVersionUID = -9145851789458788364L;

        private static final long ITEMS_PER_PAGE = 10;

        private final ContentDataview contentDataview;

        private final ListDataProvider<Content> contentListDataProvider;

        public ContentDataviewContainer(final String id, final IModel<Category> model) {
          super(id, model);
          contentListDataProvider = new ListDataProvider<Content>() {
            private static final long serialVersionUID = -8053249849363836392L;

            @Override
            protected List<Content> getData() {
              return SubCategoryContentPanel.this.selectedModel.getObject().getContents();
            }
          };
          contentDataview = new ContentDataview(CONTENT_DATAVIEW_ID, contentListDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(contentDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String CONTENT_VIEW_OR_EDIT_PANEL_ID = "contentViewOrEditPanel";

      private static final String CONTENT_PAGING_NAVIGATOR_ID = "contentPagingNavigator";

      private static final String CONTENT_DATAVIEW_CONTAINER_ID = "contentDataviewContainer";

      private static final String ADD_ID = "add";

      private static final long serialVersionUID = 204712986499638981L;

      private final AddAjaxLink addAjaxLink;

      private final ContentDataviewContainer contentDataviewContainer;

      private final BootstrapPagingNavigator contentPagingNavigator;

      private final SubCategoryContentViewOrEditPanel subCategoryContentViewOrEditPanel;

      public ContentEditTable(final String id, final IModel<Category> model) {
        super(id, model);
        addAjaxLink = new AddAjaxLink(ADD_ID, (IModel<Category>) ContentEditTable.this.getDefaultModel(), Buttons.Type.Primary,
            Model.of(SubCategoryContentPanel.this.getString(NetbrasoftApplicationConstants.ADD_MESSAGE_KEY)));
        contentDataviewContainer = new ContentDataviewContainer(CONTENT_DATAVIEW_CONTAINER_ID, (IModel<Category>) ContentEditTable.this.getDefaultModel());
        contentPagingNavigator = new BootstrapPagingNavigator(CONTENT_PAGING_NAVIGATOR_ID, contentDataviewContainer.contentDataview);
        subCategoryContentViewOrEditPanel = new SubCategoryContentViewOrEditPanel(CONTENT_VIEW_OR_EDIT_PANEL_ID, (IModel<Category>) ContentEditTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        add(addAjaxLink.setOutputMarkupId(true));
        add(contentDataviewContainer.setOutputMarkupId(true));
        add(contentPagingNavigator.setOutputMarkupId(true));
        add(subCategoryContentViewOrEditPanel.add(subCategoryContentViewOrEditPanel.new SubCategoryContentEditFragment()).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SUB_CATEGORY_CONTENT_EDIT_FRAGMENT_MARKUP_ID = "subCategoryContentEditFragment";

    private static final String SUB_CATEGORY_CONTENT_VIEW_OR_EDIT_FRAGMENT_ID = "subCategoryContentViewOrEditFragment";

    private static final String CONTENT_EDIT_TABLE_ID = "contentEditTable";

    private static final long serialVersionUID = 8640403483040526601L;

    private final ContentEditTable contentEditTable;

    public SubCategoryContentEditFragment() {
      super(SUB_CATEGORY_CONTENT_VIEW_OR_EDIT_FRAGMENT_ID, SUB_CATEGORY_CONTENT_EDIT_FRAGMENT_MARKUP_ID, SubCategoryContentPanel.this,
          SubCategoryContentPanel.this.getDefaultModel());
      contentEditTable = new ContentEditTable(CONTENT_EDIT_TABLE_ID, (IModel<Category>) SubCategoryContentEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(contentEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class SubCategoryContentViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class ContentViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class ContentDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
        class ContentDataview extends DataView<Content> {

          private static final String CLICK_EVENT = "click";

          private static final String NAME_ID = "name";

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = 2246346365193989354L;

          private int index = 0;

          protected ContentDataview(final String id, final IDataProvider<Content> dataProvider, final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<Content> newItem(final String id, final int index, final IModel<Content> model) {
            final Item<Content> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void onConfigure() {
            final IModel<SubCategory> model = SubCategoryContentPanel.this.selectedModel;
            if (!model.getObject().getContents().isEmpty()) {
              subCategoryContentViewOrEditPanel.removeAll();
              subCategoryContentViewOrEditPanel.setSelectedModel(Model.of(model.getObject().getContents().get(index)));
              subCategoryContentViewOrEditPanel.add(subCategoryContentViewOrEditPanel.new SubCategoryContentViewFragment()).setOutputMarkupId(true);
            } else {
              subCategoryContentViewOrEditPanel.removeAll();
              subCategoryContentViewOrEditPanel.setSelectedModel(Model.of(new Content()));
              subCategoryContentViewOrEditPanel.add(subCategoryContentViewOrEditPanel.new SubCategoryContentViewFragment()).setOutputMarkupId(true);
            }
            super.onConfigure();
          }

          @Override
          protected void populateItem(final Item<Content> item) {
            item.setModel(new CompoundPropertyModel<Content>(item.getModelObject()));
            item.add(new Label(NAME_ID).setOutputMarkupId(true));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                subCategoryContentViewOrEditPanel.setSelectedModel(item.getModel());
                subCategoryContentViewOrEditPanel.removeAll();
                target.add(contentDataviewContainer.setOutputMarkupId(true));
                target.add(subCategoryContentViewOrEditPanel.add(subCategoryContentViewOrEditPanel.new SubCategoryContentViewFragment()).setOutputMarkupId(true));
              }
            });
          }
        }

        private static final String CONTENT_DATAVIEW_ID = "contentDataview";

        private static final long serialVersionUID = -9145851789458788364L;

        private static final long ITEMS_PER_PAGE = 10;

        private final ContentDataview contentDataview;

        private final ListDataProvider<Content> contentListDataProvider;

        public ContentDataviewContainer(final String id, final IModel<Category> model) {
          super(id, model);
          contentListDataProvider = new ListDataProvider<Content>() {
            private static final long serialVersionUID = -8053249849363836392L;

            @Override
            protected List<Content> getData() {
              return SubCategoryContentPanel.this.selectedModel.getObject().getContents();
            }
          };
          contentDataview = new ContentDataview(CONTENT_DATAVIEW_ID, contentListDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(contentDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String CONTENT_VIEW_OR_EDIT_PANEL_ID = "contentViewOrEditPanel";

      private static final String CONTENT_PAGING_NAVIGATOR_ID = "contentPagingNavigator";

      private static final String CONTENT_DATAVIEW_CONTAINER_ID = "contentDataviewContainer";

      private static final long serialVersionUID = 204712986499638981L;

      private final ContentDataviewContainer contentDataviewContainer;

      private final BootstrapPagingNavigator contentPagingNavigator;

      private final SubCategoryContentViewOrEditPanel subCategoryContentViewOrEditPanel;

      public ContentViewTable(final String id, final IModel<Category> model) {
        super(id, model);
        contentDataviewContainer = new ContentDataviewContainer(CONTENT_DATAVIEW_CONTAINER_ID, (IModel<Category>) ContentViewTable.this.getDefaultModel());
        contentPagingNavigator = new BootstrapPagingNavigator(CONTENT_PAGING_NAVIGATOR_ID, contentDataviewContainer.contentDataview);
        subCategoryContentViewOrEditPanel = new SubCategoryContentViewOrEditPanel(CONTENT_VIEW_OR_EDIT_PANEL_ID, (IModel<Category>) ContentViewTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        add(contentDataviewContainer.setOutputMarkupId(true));
        add(contentPagingNavigator.setOutputMarkupId(true));
        add(subCategoryContentViewOrEditPanel.add(subCategoryContentViewOrEditPanel.new SubCategoryContentViewFragment()).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SUB_CATEGORY_CONTENT_VIEW_FRAGMENT_MARKUP_ID = "subCategoryContentViewFragment";

    private static final String SUB_CATEGORY_CONTENT_VIEW_OR_EDIT_FRAGMENT_ID = "subCategoryContentViewOrEditFragment";

    private static final String CONTENT_VIEW_TABLE_ID = "contentViewTable";

    private static final long serialVersionUID = 8640403483040526601L;

    private final ContentViewTable contentViewTable;

    public SubCategoryContentViewFragment() {
      super(SUB_CATEGORY_CONTENT_VIEW_OR_EDIT_FRAGMENT_ID, SUB_CATEGORY_CONTENT_VIEW_FRAGMENT_MARKUP_ID, SubCategoryContentPanel.this,
          SubCategoryContentPanel.this.getDefaultModel());
      contentViewTable = new ContentViewTable(CONTENT_VIEW_TABLE_ID, (IModel<Category>) SubCategoryContentViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(contentViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = 180343040391839545L;

  private IModel<SubCategory> selectedModel;

  public SubCategoryContentPanel(final String id, final IModel<Category> model) {
    super(id, model);
    selectedModel = Model.of(new SubCategory());
  }

  public void setSelectedModel(final IModel<SubCategory> selectedModel) {
    this.selectedModel = selectedModel;
  }
}
