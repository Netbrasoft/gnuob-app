package com.netbrasoft.gnuob.application.content;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.CONTENT_DATA_PROVIDER_NAME;
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

import com.netbrasoft.gnuob.api.Content;
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
 * Panel for viewing, selecting and editing {@link Content} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class ContentPanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class ContentPanelContainer extends WebMarkupContainer {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class ContentTableContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class AddAjaxLink extends BootstrapAjaxLink<Content> {

        private static final long serialVersionUID = -8317730269644885290L;

        public AddAjaxLink(final String id, final IModel<Content> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.plus);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          final Content content = new Content();
          content.setActive(true);
          AddAjaxLink.this.setDefaultModelObject(content);
          target.add(contentViewOrEditPanel.setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
      class ContentDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
        class ContentDataview extends DataView<Content> {

          @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
          class RemoveAjaxLink extends BootstrapAjaxLink<Content> {

            private static final long serialVersionUID = -8317730269644885290L;

            public RemoveAjaxLink(final String id, final IModel<Content> model, final Buttons.Type type,
                final IModel<String> labelModel) {
              super(id, model, type, labelModel);
              setIconType(GlyphIconType.remove);
              setSize(Buttons.Size.Mini);
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
              try {
                contentDataProvider.remove((Content) RemoveAjaxLink.this.getDefaultModelObject());
              } catch (final RuntimeException e) {
                LOGGER.warn(e.getMessage(), e);
                contentTableContainer.warn(e.getLocalizedMessage());
              } finally {
                target.add(contentPanelContainer.setOutputMarkupId(true));
              }
            }
          }

          private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

          private static final String REMOVE_ID = "remove";

          private static final String CLICK_EVENT = "click";

          private static final String NAME_ID = NAME_PROPERTY;

          private static final String INFO_VALUE = "info";

          private static final String CLASS_ATTRIBUTE = "class";

          private static final long serialVersionUID = -9196412070291572169L;

          private int index;

          protected ContentDataview(final String id, final IDataProvider<Content> dataProvider,
              final long itemsPerPage) {
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
          protected void populateItem(final Item<Content> item) {
            item.setModel(new CompoundPropertyModel<Content>(item.getModelObject()));
            item.add(new Label(NAME_ID));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                target
                    .add(contentDataviewContainer.setDefaultModelObject(item.getModelObject()).setOutputMarkupId(true));
                target.add(contentViewOrEditPanel.setOutputMarkupId(true));
              }
            });
            item.add(new RemoveAjaxLink(REMOVE_ID, item.getModel(), Buttons.Type.Default,
                Model.of(ContentPanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY)))
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

        private static final String CONTENT_DATAVIEW_ID = "contentDataview";

        private static final long serialVersionUID = -6700605975126870961L;

        private static final int ITEMS_PER_PAGE = 5;

        private final ContentDataview contentDataview;

        public ContentDataviewContainer(final String id, final IModel<Content> model) {
          super(id, model);
          contentDataview = new ContentDataview(CONTENT_DATAVIEW_ID, contentDataProvider, ITEMS_PER_PAGE);
        }

        @Override
        protected void onInitialize() {
          add(contentDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String CONTENT_PAGING_NAVIGATOR_MARKUP_ID = "contentPagingNavigator";

      private static final String CONTENT_DATAVIEW_CONTAINER_ID = "contentDataviewContainer";

      private static final String NAME_PROPERTY = "name";

      private static final String ORDER_BY_NAME_ID = "orderByName";

      private static final String ADD_ID = "add";

      private static final String FEEDBACK_ID = "feedback";

      private static final long serialVersionUID = -2899790701873445919L;

      private final NotificationPanel feedbackPanel;

      private final AddAjaxLink addAjaxLink;

      private final OrderByBorder<String> orderByName;

      private final ContentDataviewContainer contentDataviewContainer;

      private final BootstrapPagingNavigator contentPagingNavigator;

      public ContentTableContainer(final String id, final IModel<Content> model) {
        super(id, model);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
        addAjaxLink = new AddAjaxLink(ADD_ID, (IModel<Content>) ContentTableContainer.this.getDefaultModel(),
            Buttons.Type.Primary,
            Model.of(ContentPanel.this.getString(NetbrasoftApplicationConstants.ADD_MESSAGE_KEY)));
        orderByName = new OrderByBorder<String>(ORDER_BY_NAME_ID, NAME_PROPERTY, contentDataProvider);
        contentDataviewContainer = new ContentDataviewContainer(CONTENT_DATAVIEW_CONTAINER_ID,
            (IModel<Content>) ContentTableContainer.this.getDefaultModel());
        contentPagingNavigator =
            new BootstrapPagingNavigator(CONTENT_PAGING_NAVIGATOR_MARKUP_ID, contentDataviewContainer.contentDataview);
      }

      @Override
      protected void onInitialize() {
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(addAjaxLink.setOutputMarkupId(true));
        add(orderByName.setOutputMarkupId(true));
        add(contentDataviewContainer.setOutputMarkupId(true));
        add(contentPagingNavigator.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CONTENT_VIEW_OR_EDIT_PANEL_ID = "contentViewOrEditPanel";

    private static final String CONTENT_TABLE_CONTAINER_ID = "contentTableContainer";

    private static final long serialVersionUID = -3393167428708739654L;

    private final ContentViewOrEditPanel contentViewOrEditPanel;

    private final ContentTableContainer contentTableContainer;

    public ContentPanelContainer(final String id, final IModel<Content> model) {
      super(id, model);
      contentTableContainer = new ContentTableContainer(CONTENT_TABLE_CONTAINER_ID,
          (IModel<Content>) ContentPanelContainer.this.getDefaultModel());
      contentViewOrEditPanel = new ContentViewOrEditPanel(CONTENT_VIEW_OR_EDIT_PANEL_ID,
          (IModel<Content>) ContentPanelContainer.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(contentTableContainer.add(new TableBehavior().hover()).setOutputMarkupId(true));
      add(contentViewOrEditPanel.add(contentViewOrEditPanel.new ContentViewFragment()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final String CONTENT_PANEL_CONTAINER_ID = "contentPanelContainer";

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentPanel.class);

  private static final long serialVersionUID = 3703226064705246155L;

  @SpringBean(name = CONTENT_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Content> contentDataProvider;

  private final ContentPanelContainer contentPanelContainer;

  public ContentPanel(final String id, final IModel<Content> model) {
    super(id, model);
    contentPanelContainer =
        new ContentPanelContainer(CONTENT_PANEL_CONTAINER_ID, (IModel<Content>) ContentPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    contentDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    contentDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    contentDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    contentDataProvider.setType(new Content());
    contentDataProvider.getType().setActive(true);
    if (contentDataProvider.size() > 0) {
      ContentPanel.this.setDefaultModelObject(contentDataProvider.iterator(0, 1).next());
    }
    add(contentPanelContainer.add(new BootstrapBaseBehavior() {

      private static final long serialVersionUID = -4903722864597601489L;

      @Override
      public void onComponentTag(final Component component, final ComponentTag tag) {
        Attributes.addClass(tag, MediumSpanType.SPAN10);
      }
    }).setOutputMarkupId(true));
    super.onInitialize();
  }
}
