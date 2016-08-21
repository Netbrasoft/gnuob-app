package com.netbrasoft.gnuob.application.security.group;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.GROUP_DATA_PROVIDER_NAME;
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

import com.netbrasoft.gnuob.api.Group;
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
 * Panel for viewing, selecting and editing {@link Group} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
public class GroupPanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
  class GroupPanelContainer extends WebMarkupContainer {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
    class GroupTableContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
      class AddAjaxLink extends BootstrapAjaxLink<Group> {

        private static final long serialVersionUID = -8317730269644885290L;

        public AddAjaxLink(final String id, final IModel<Group> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.plus);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          final Group group = new Group();
          group.setActive(true);
          AddAjaxLink.this.setDefaultModelObject(group);
          target.add(groupViewOrEditPanel.setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
      class GroupDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
        class GroupDataview extends DataView<Group> {

          private static final String NAME_ID = NAME_PROPERTY;

          private static final String DESCRIPTION_ID = DESCRIPTION_PROPERTY;

          private static final long serialVersionUID = -5039874949058607907L;

          private int index = 0;

          protected GroupDataview(final String id, final IDataProvider<Group> dataProvider, final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<Group> newItem(final String id, final int index, final IModel<Group> model) {
            final Item<Group> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void populateItem(final Item<Group> item) {
            item.setModel(new CompoundPropertyModel<Group>(item.getModelObject()));
            item.add(new Label(NAME_ID));
            item.add(new Label(DESCRIPTION_ID));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                target.add(groupDataviewContainer.setDefaultModelObject(item.getModelObject()).setOutputMarkupId(true));
                target.add(groupViewOrEditPanel.setOutputMarkupId(true));
              }
            });
            item.add(new RemoveAjaxLink(REMOVE_ID, item.getModel(), Buttons.Type.Default,
                Model.of(GroupPanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY)))
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

        @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
        class RemoveAjaxLink extends BootstrapAjaxLink<Group> {

          private static final long serialVersionUID = -8317730269644885290L;

          public RemoveAjaxLink(final String id, final IModel<Group> model, final Buttons.Type type,
              final IModel<String> labelModel) {
            super(id, model, type, labelModel);
            setIconType(GlyphIconType.remove);
            setSize(Buttons.Size.Mini);
          }

          @Override
          public void onClick(final AjaxRequestTarget target) {
            try {
              groupDataProvider.remove((Group) RemoveAjaxLink.this.getDefaultModelObject());
            } catch (final RuntimeException e) {
              LOGGER.warn(e.getMessage(), e);
              groupTableContainer.warn(e.getLocalizedMessage());
            } finally {
              target.add(groupTableContainer.setOutputMarkupId(true));
            }
          }
        }

        private static final String GROUP_DATAVIEW_ID = "groupDataview";

        private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

        private static final String REMOVE_ID = "remove";

        private static final String CLICK_EVENT = "click";

        private static final String INFO_VALUE = "info";

        private static final String CLASS_ATTRIBUTE = "class";

        private static final long serialVersionUID = 1658088620417029170L;

        private final GroupDataview groupDataview;

        public GroupDataviewContainer(final String id, final IModel<Group> model) {
          super(id, model);
          groupDataview = new GroupDataview(GROUP_DATAVIEW_ID, groupDataProvider, 5);
        }

        @Override
        protected void onInitialize() {
          add(groupDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String GROUP_PAGING_NAVIGATOR_MARKUP_ID = "groupPagingNavigator";

      private static final String GROUP_DATAVIEW_CONTAINER_ID = "groupDataviewContainer";

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

      private final GroupDataviewContainer groupDataviewContainer;

      private final BootstrapPagingNavigator groupPagingNavigator;

      public GroupTableContainer(final String id, final IModel<Group> model) {
        super(id, model);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
        addAjaxLink = new AddAjaxLink(ADD_ID, (IModel<Group>) GroupTableContainer.this.getDefaultModel(),
            Buttons.Type.Primary, Model.of(GroupPanel.this.getString(NetbrasoftApplicationConstants.ADD_MESSAGE_KEY)));
        orderByName = new OrderByBorder<String>(ORDER_BY_NAME_ID, NAME_PROPERTY, groupDataProvider);
        orderByDescription =
            new OrderByBorder<String>(ORDER_BY_DESCRIPTION_ID, DESCRIPTION_PROPERTY, groupDataProvider);
        groupDataviewContainer = new GroupDataviewContainer(GROUP_DATAVIEW_CONTAINER_ID,
            (IModel<Group>) GroupTableContainer.this.getDefaultModel());
        groupPagingNavigator =
            new BootstrapPagingNavigator(GROUP_PAGING_NAVIGATOR_MARKUP_ID, groupDataviewContainer.groupDataview);
      }

      @Override
      protected void onInitialize() {
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(addAjaxLink.setOutputMarkupId(true));
        add(orderByName.setOutputMarkupId(true));
        add(orderByDescription.setOutputMarkupId(true));
        add(groupDataviewContainer.setOutputMarkupId(true));
        add(groupPagingNavigator.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String GROUP_TABLE_CONTAINER_ID = "groupTableContainer";

    private static final String GROUP_VIEW_OR_EDIT_PANEL_ID = "groupViewOrEditPanel";

    private static final long serialVersionUID = 1147490546680500759L;

    private final GroupViewOrEditPanel groupViewOrEditPanel;

    private final GroupTableContainer groupTableContainer;

    public GroupPanelContainer(final String id, final IModel<Group> model) {
      super(id, model);
      groupTableContainer =
          new GroupTableContainer(GROUP_TABLE_CONTAINER_ID, (IModel<Group>) GroupPanelContainer.this.getDefaultModel());
      groupViewOrEditPanel = new GroupViewOrEditPanel(GROUP_VIEW_OR_EDIT_PANEL_ID,
          (IModel<Group>) GroupPanelContainer.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(groupTableContainer.add(new TableBehavior().hover()).setOutputMarkupId(true));
      add(groupViewOrEditPanel.add(groupViewOrEditPanel.new GroupViewFragment()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final String GROUP_PANEL_CONTAINER_ID = "groupPanelContainer";

  private static final Logger LOGGER = LoggerFactory.getLogger(GroupPanel.class);

  private static final long serialVersionUID = 3703226064705246155L;

  @SpringBean(name = GROUP_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Group> groupDataProvider;

  private final GroupPanelContainer groupPanelContainer;

  public GroupPanel(final String id, final IModel<Group> model) {
    super(id, model);
    groupPanelContainer =
        new GroupPanelContainer(GROUP_PANEL_CONTAINER_ID, (IModel<Group>) GroupPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    groupDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    groupDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    groupDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    groupDataProvider.setType(new Group());
    groupDataProvider.getType().setActive(true);
    if (groupDataProvider.size() > 0) {
      GroupPanel.this.setDefaultModelObject(groupDataProvider.iterator(0, 1).next());
    }
    add(groupPanelContainer.add(new BootstrapBaseBehavior() {

      private static final long serialVersionUID = -4903722864597601489L;

      @Override
      public void onComponentTag(final Component component, final ComponentTag tag) {
        Attributes.addClass(tag, MediumSpanType.SPAN10);
      }
    }).setOutputMarkupId(true));
    super.onInitialize();
  }
}
