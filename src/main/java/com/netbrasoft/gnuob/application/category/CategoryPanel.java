package com.netbrasoft.gnuob.application.category;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.CATEGORY_DATA_PROVIDER_NAME;
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

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.OrderBy;
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
 * Panel for viewing, selecting and editing {@link Category} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class CategoryPanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class CategoryPanelContainer extends WebMarkupContainer {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class CategoryTableContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class AddAjaxLink extends BootstrapAjaxLink<Category> {

        private static final long serialVersionUID = -8317730269644885290L;

        public AddAjaxLink(final String id, final IModel<Category> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.plus);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          final Category category = new Category();
          category.setActive(true);
          AddAjaxLink.this.setDefaultModelObject(category);
          target.add(categoryViewOrEditPanel.setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
      class CategoryDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
        class CategoryDataview extends DataView<Category> {

          @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
          class RemoveAjaxLink extends BootstrapAjaxLink<Category> {

            private static final long serialVersionUID = -8317730269644885290L;

            public RemoveAjaxLink(final String id, final IModel<Category> model, final Buttons.Type type,
                final IModel<String> labelModel) {
              super(id, model, type, labelModel);
              setIconType(GlyphIconType.remove);
              setSize(Buttons.Size.Mini);
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
              try {
                categoryDataProvider.remove((Category) RemoveAjaxLink.this.getDefaultModelObject());
              } catch (final RuntimeException e) {
                LOGGER.warn(e.getMessage(), e);
                feedbackPanel.warn(e.getLocalizedMessage());
                target.add(feedbackPanel);
              } finally {
                target.add(categoryPanelContainer.setOutputMarkupId(true));
              }
            }
          }

          private static final String EMPTY_VALUE = "";

          private static final String POSITION_ID = POSITION_PROPERTY;

          private static final String NAME_ID = NAME_PROPERTY;

          private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

          private static final String REMOVE_ID = "remove";

          private static final String CLICK_EVENT = "click";

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = -5039874949058607907L;

          private IModel<Category> model;

          protected CategoryDataview(final String id, final IDataProvider<Category> dataProvider,
              final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<Category> newItem(final String id, final int index, final IModel<Category> model) {
            final Item<Category> item = super.newItem(id, index, model);
            if (this.model == null || this.model.getObject().getId() == model.getObject().getId()) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
              this.model = model;
            } else {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, EMPTY_VALUE));
            }
            return item;
          }

          @Override
          protected void populateItem(final Item<Category> item) {
            item.setModel(new CompoundPropertyModel<Category>(item.getModelObject()));
            item.add(new Label(NAME_ID));
            item.add(new Label(POSITION_ID));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                model = item.getModel();
                target.add(
                    categoryDataviewContainer.setDefaultModelObject(item.getModelObject()).setOutputMarkupId(true));
                target.add(categoryViewOrEditPanel.setOutputMarkupId(true));
              }
            });
            item.add(new RemoveAjaxLink(REMOVE_ID, item.getModel(), Buttons.Type.Default,
                Model.of(CategoryPanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY)))
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

        private static final String CATEGORY_DATAVIEW_ID = "categoryDataview";

        private static final long serialVersionUID = 4066874119655951656L;

        private final CategoryDataview categoryDataview;

        public CategoryDataviewContainer(final String id, final IModel<Category> model) {
          super(id, model);
          categoryDataview = new CategoryDataview(CATEGORY_DATAVIEW_ID, categoryDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(categoryDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String NAME_PROPERTY = "name";

      private static final String ORDER_BY_NAME_ID = "orderByName";

      private static final String POSITION_PROPERTY = "position";

      private static final String ORDER_BY_POSITION_ID = "orderByPosition";

      private static final String ADD_ID = "add";

      private static final String FEEDBACK_ID = "feedback";

      private static final long serialVersionUID = -6196639251081246943L;

      private final NotificationPanel feedbackPanel;

      private final AddAjaxLink addAjaxLink;

      private final OrderByBorder<String> orderByposition;

      private final OrderByBorder<String> orderByName;

      private final CategoryDataviewContainer categoryDataviewContainer;

      private final BootstrapPagingNavigator categoryPagingNavigator;

      public CategoryTableContainer(final String id, final IModel<Category> model) {
        super(id, model);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
        addAjaxLink = new AddAjaxLink(ADD_ID, (IModel<Category>) CategoryTableContainer.this.getDefaultModel(),
            Buttons.Type.Primary,
            Model.of(CategoryPanel.this.getString(NetbrasoftApplicationConstants.ADD_MESSAGE_KEY)));
        orderByposition = new OrderByBorder<String>(ORDER_BY_POSITION_ID, POSITION_PROPERTY, categoryDataProvider);
        orderByName = new OrderByBorder<String>(ORDER_BY_NAME_ID, NAME_PROPERTY, categoryDataProvider);
        categoryDataviewContainer = new CategoryDataviewContainer("categoryDataviewContainer",
            (IModel<Category>) CategoryTableContainer.this.getDefaultModel());
        categoryPagingNavigator =
            new BootstrapPagingNavigator("categoryPagingNavigator", categoryDataviewContainer.categoryDataview);
      }

      @Override
      protected void onInitialize() {
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(addAjaxLink.setOutputMarkupId(true));
        add(orderByposition.setOutputMarkupId(true));
        add(orderByName.setOutputMarkupId(true));
        add(categoryDataviewContainer.setOutputMarkupId(true));
        add(categoryPagingNavigator.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CATEGORY_VIEW_OR_EDIT_PANEL_ID = "categoryViewOrEditPanel";

    private static final String CATEGORY_TABLE_CONTAINER_ID = "categoryTableContainer";

    private static final long serialVersionUID = 3737455694760798719L;

    private final CategoryViewOrEditPanel categoryViewOrEditPanel;

    private final CategoryTableContainer categoryTableContainer;

    public CategoryPanelContainer(final String id, final IModel<Category> model) {
      super(id, model);
      categoryTableContainer = new CategoryTableContainer(CATEGORY_TABLE_CONTAINER_ID,
          (IModel<Category>) CategoryPanelContainer.this.getDefaultModel());
      categoryViewOrEditPanel = new CategoryViewOrEditPanel(CATEGORY_VIEW_OR_EDIT_PANEL_ID,
          (IModel<Category>) CategoryPanelContainer.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(categoryTableContainer.add(new TableBehavior().hover()).setOutputMarkupId(true));
      add(categoryViewOrEditPanel.add(categoryViewOrEditPanel.new CategoryViewFragment()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final String CATEGORY_PANEL_CONTAINER_ID = "categoryPanelContainer";

  private static final long serialVersionUID = 3703226064705246155L;

  private static final int ITEMS_PER_PAGE = 2;

  private static final Logger LOGGER = LoggerFactory.getLogger(CategoryPanel.class);

  @SpringBean(name = CATEGORY_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Category> categoryDataProvider;

  private final CategoryPanelContainer categoryPanelContainer;

  public CategoryPanel(final String id, final IModel<Category> model) {
    super(id, model);
    categoryPanelContainer = new CategoryPanelContainer(CATEGORY_PANEL_CONTAINER_ID,
        (IModel<Category>) CategoryPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    categoryDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    categoryDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    categoryDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    categoryDataProvider.setType(new Category());
    categoryDataProvider.getType().setActive(true);
    categoryDataProvider.setOrderBy(OrderBy.POSITION_A_Z);
    if (categoryDataProvider.size() > 0) {
      CategoryPanel.this.setDefaultModelObject(categoryDataProvider.iterator(0, 1).next());
    }
    add(categoryPanelContainer.add(new BootstrapBaseBehavior() {

      private static final long serialVersionUID = -4903722864597601489L;

      @Override
      public void onComponentTag(final Component component, final ComponentTag tag) {
        Attributes.addClass(tag, MediumSpanType.SPAN10);
      }
    }).setOutputMarkupId(true));
    super.onInitialize();
  }
}
