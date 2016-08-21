package com.netbrasoft.gnuob.application.security.user;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.USER_DATA_PROVIDER_NAME;
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

import com.netbrasoft.gnuob.api.User;
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
 * Panel for viewing, selecting and editing {@link User} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
public class UserPanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
  class UserPanelContainer extends WebMarkupContainer {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
    class UserTableContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
      class AddAjaxLink extends BootstrapAjaxLink<User> {

        private static final long serialVersionUID = -8317730269644885290L;

        public AddAjaxLink(final String id, final IModel<User> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.plus);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          final User user = new User();
          user.setActive(true);
          AddAjaxLink.this.setDefaultModelObject(user);
          target.add(userViewOrEditPanel.setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
      class UserDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
        class RemoveAjaxLink extends BootstrapAjaxLink<User> {

          private static final long serialVersionUID = -8317730269644885290L;

          public RemoveAjaxLink(final String id, final IModel<User> model, final Buttons.Type type,
              final IModel<String> labelModel) {
            super(id, model, type, labelModel);
            setIconType(GlyphIconType.remove);
            setSize(Buttons.Size.Mini);
          }

          @Override
          public void onClick(final AjaxRequestTarget target) {
            try {
              userDataProvider.remove((User) RemoveAjaxLink.this.getDefaultModelObject());
            } catch (final RuntimeException e) {
              LOGGER.warn(e.getMessage(), e);
              userTableContainer.warn(e.getLocalizedMessage());
            } finally {
              target.add(userTableContainer.setOutputMarkupId(true));
            }
          }
        }

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
        class UserDataview extends DataView<User> {

          private static final String NAME_ID = NAME_PROPERTY;

          private static final String DESCRIPTION_ID = DESCRIPTION_PROPERTY;

          private static final long serialVersionUID = -5039874949058607907L;

          private int index = 0;

          protected UserDataview(final String id, final IDataProvider<User> dataProvider, final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<User> newItem(final String id, final int index, final IModel<User> model) {
            final Item<User> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void populateItem(final Item<User> item) {
            item.setModel(new CompoundPropertyModel<User>(item.getModelObject()));
            item.add(new Label(NAME_ID));
            item.add(new Label(DESCRIPTION_ID));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                target.add(userDataviewContainer.setDefaultModelObject(item.getModelObject()).setOutputMarkupId(true));
                target.add(userViewOrEditPanel.setOutputMarkupId(true));
              }
            });
            item.add(new RemoveAjaxLink(REMOVE_ID, item.getModel(), Buttons.Type.Default,
                Model.of(UserPanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY)))
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

        private static final String USER_DATAVIEW_ID = "userDataview";

        private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

        private static final String REMOVE_ID = "remove";

        private static final String CLICK_EVENT = "click";

        private static final String INFO_VALUE = "info";

        private static final String CLASS_ATTRIBUTE = "class";

        private static final long serialVersionUID = 1658088620417029170L;

        private final UserDataview userDataview;

        public UserDataviewContainer(final String id, final IModel<User> model) {
          super(id, model);
          userDataview = new UserDataview(USER_DATAVIEW_ID, userDataProvider, 5);
        }

        @Override
        protected void onInitialize() {
          add(userDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String USER_PAGING_NAVIGATOR_MARKUP_ID = "userPagingNavigator";

      private static final String USER_DATAVIEW_CONTAINER_ID = "userDataviewContainer";

      private static final String NAME_PROPERTY = "name";

      private static final String ORDER_BY_NAME_ID = "orderByName";

      private static final String DESCRIPTION_PROPERTY = "description";

      private static final String ORDER_BY_DESCRIPTION_ID = "orderByDescription";

      private static final String ADD_ID = "add";

      private static final String FEEDBACK_ID = "feedback";

      private static final long serialVersionUID = 6323041555610152460L;

      private final NotificationPanel feedbackPanel;

      private final AddAjaxLink addAjaxLink;

      private final OrderByBorder<String> orderByName;

      private final OrderByBorder<String> orderByDescription;

      private final UserDataviewContainer userDataviewContainer;

      private final BootstrapPagingNavigator userPagingNavigator;

      public UserTableContainer(final String id, final IModel<User> model) {
        super(id, model);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
        addAjaxLink = new AddAjaxLink(ADD_ID, (IModel<User>) UserTableContainer.this.getDefaultModel(),
            Buttons.Type.Primary, Model.of(UserPanel.this.getString(NetbrasoftApplicationConstants.ADD_MESSAGE_KEY)));
        orderByName = new OrderByBorder<String>(ORDER_BY_NAME_ID, NAME_PROPERTY, userDataProvider);
        orderByDescription = new OrderByBorder<String>(ORDER_BY_DESCRIPTION_ID, DESCRIPTION_PROPERTY, userDataProvider);
        userDataviewContainer = new UserDataviewContainer(USER_DATAVIEW_CONTAINER_ID,
            (IModel<User>) UserTableContainer.this.getDefaultModel());
        userPagingNavigator =
            new BootstrapPagingNavigator(USER_PAGING_NAVIGATOR_MARKUP_ID, userDataviewContainer.userDataview);
      }

      @Override
      protected void onInitialize() {
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(addAjaxLink.setOutputMarkupId(true));
        add(orderByName.setOutputMarkupId(true));
        add(orderByDescription.setOutputMarkupId(true));
        add(userDataviewContainer.setOutputMarkupId(true));
        add(userPagingNavigator.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String USER_TABLE_CONTAINER_ID = "userTableContainer";

    private static final String USER_VIEW_OR_EDIT_PANEL_ID = "userViewOrEditPanel";

    private static final long serialVersionUID = 1147490546680500759L;

    private final UserViewOrEditPanel userViewOrEditPanel;

    private final UserTableContainer userTableContainer;

    public UserPanelContainer(final String id, final IModel<User> model) {
      super(id, model);
      userTableContainer =
          new UserTableContainer(USER_TABLE_CONTAINER_ID, (IModel<User>) UserPanelContainer.this.getDefaultModel());
      userViewOrEditPanel =
          new UserViewOrEditPanel(USER_VIEW_OR_EDIT_PANEL_ID, (IModel<User>) UserPanelContainer.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(userTableContainer.add(new TableBehavior().hover()).setOutputMarkupId(true));
      add(userViewOrEditPanel.add(userViewOrEditPanel.new UserViewFragment()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final String USER_PANEL_CONTAINER_ID = "userPanelContainer";

  private static final Logger LOGGER = LoggerFactory.getLogger(UserPanel.class);

  private static final long serialVersionUID = 3703226064705246155L;

  @SpringBean(name = USER_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<User> userDataProvider;

  private final UserPanelContainer userPanelContainer;

  public UserPanel(final String id, final IModel<User> model) {
    super(id, model);
    userPanelContainer =
        new UserPanelContainer(USER_PANEL_CONTAINER_ID, (IModel<User>) UserPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    userDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    userDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    userDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    userDataProvider.setType(new User());
    userDataProvider.getType().setActive(true);
    if (userDataProvider.size() > 0) {
      UserPanel.this.setDefaultModelObject(userDataProvider.iterator(0, 1).next());
    }
    add(userPanelContainer.add(new BootstrapBaseBehavior() {

      private static final long serialVersionUID = -4903722864597601489L;

      @Override
      public void onComponentTag(final Component component, final ComponentTag tag) {
        Attributes.addClass(tag, MediumSpanType.SPAN10);
      }
    }).setOutputMarkupId(true));
    super.onInitialize();
  }
}
