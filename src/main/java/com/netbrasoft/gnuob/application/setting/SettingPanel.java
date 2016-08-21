package com.netbrasoft.gnuob.application.setting;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.SETTING_DATA_PROVIDER_NAME;
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

import com.netbrasoft.gnuob.api.Setting;
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
 * Panel for viewing, selecting and editing {@link Setting} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
public class SettingPanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
  class SettingPanelContainer extends WebMarkupContainer {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
    class SettingTableContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
      class AddAjaxLink extends BootstrapAjaxLink<Setting> {

        private static final long serialVersionUID = -8317730269644885290L;

        public AddAjaxLink(final String id, final IModel<Setting> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.plus);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          final Setting setting = new Setting();
          setting.setActive(true);
          AddAjaxLink.this.setDefaultModelObject(setting);
          target.add(settingViewOrEditPanel.setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
      class SettingDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
        class RemoveAjaxLink extends BootstrapAjaxLink<Setting> {

          private static final long serialVersionUID = -8317730269644885290L;

          public RemoveAjaxLink(final String id, final IModel<Setting> model, final Buttons.Type type,
              final IModel<String> labelModel) {
            super(id, model, type, labelModel);
            setIconType(GlyphIconType.remove);
            setSize(Buttons.Size.Mini);
          }

          @Override
          public void onClick(final AjaxRequestTarget target) {
            try {
              settingDataProvider.remove((Setting) RemoveAjaxLink.this.getDefaultModelObject());
            } catch (final RuntimeException e) {
              LOGGER.warn(e.getMessage(), e);
              settingTableContainer.warn(e.getLocalizedMessage());
            } finally {
              target.add(settingTableContainer.setOutputMarkupId(true));
            }
          }
        }

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
        class SettingDataview extends DataView<Setting> {

          private static final String PROPERTY_ID = PROPERTY_PROPERTY;

          private static final String DESCRIPTION_ID = DESCRIPTION_PROPERTY;

          private static final long serialVersionUID = -5039874949058607907L;

          private static final String VALUE_ID = "value";

          private int index = 0;

          protected SettingDataview(final String id, final IDataProvider<Setting> dataProvider,
              final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<Setting> newItem(final String id, final int index, final IModel<Setting> model) {
            final Item<Setting> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void populateItem(final Item<Setting> item) {
            item.setModel(new CompoundPropertyModel<Setting>(item.getModelObject()));
            item.add(new Label(PROPERTY_ID));
            item.add(new Label(VALUE_ID));
            item.add(new Label(DESCRIPTION_ID));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                target
                    .add(settingDataviewContainer.setDefaultModelObject(item.getModelObject()).setOutputMarkupId(true));
                target.add(settingViewOrEditPanel.setOutputMarkupId(true));
              }
            });
            item.add(new RemoveAjaxLink(REMOVE_ID, item.getModel(), Buttons.Type.Default,
                Model.of(SettingPanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY)))
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

        private static final String SETTING_DATAVIEW_ID = "settingDataview";

        private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

        private static final String REMOVE_ID = "remove";

        private static final String CLICK_EVENT = "click";

        private static final String INFO_VALUE = "info";

        private static final String CLASS_ATTRIBUTE = "class";

        private static final long serialVersionUID = 1658088620417029170L;

        private final SettingDataview settingDataview;

        public SettingDataviewContainer(final String id, final IModel<Setting> model) {
          super(id, model);
          settingDataview = new SettingDataview(SETTING_DATAVIEW_ID, settingDataProvider, 5);
        }

        @Override
        protected void onInitialize() {
          add(settingDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String VALUE_PROPERTY = "value";

      private static final String ORDER_BY_VALUE_ID = "orderByValue";

      private static final String SETTING_PAGING_NAVIGATOR_MARKUP_ID = "settingPagingNavigator";

      private static final String SETTING_DATAVIEW_CONTAINER_ID = "settingDataviewContainer";

      private static final String PROPERTY_PROPERTY = "property";

      private static final String ORDER_BY_PROPERTY_ID = "orderByProperty";

      private static final String DESCRIPTION_PROPERTY = "description";

      private static final String ORDER_BY_DESCRIPTION_ID = "orderByDescription";

      private static final String ADD_ID = "add";

      private static final String FEEDBACK_ID = "feedback";

      private static final long serialVersionUID = 6323041555610152460L;

      private final NotificationPanel feedbackPanel;

      private final AddAjaxLink addAjaxLink;

      private final OrderByBorder<String> orderByProperty;

      private final OrderByBorder<String> orderByValue;

      private final OrderByBorder<String> orderByDescription;

      private final SettingDataviewContainer settingDataviewContainer;

      private final BootstrapPagingNavigator settingPagingNavigator;

      public SettingTableContainer(final String id, final IModel<Setting> model) {
        super(id, model);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
        addAjaxLink = new AddAjaxLink(ADD_ID, (IModel<Setting>) SettingTableContainer.this.getDefaultModel(),
            Buttons.Type.Primary,
            Model.of(SettingPanel.this.getString(NetbrasoftApplicationConstants.ADD_MESSAGE_KEY)));
        orderByProperty = new OrderByBorder<String>(ORDER_BY_PROPERTY_ID, PROPERTY_PROPERTY, settingDataProvider);
        orderByValue = new OrderByBorder<String>(ORDER_BY_VALUE_ID, VALUE_PROPERTY, settingDataProvider);
        orderByDescription =
            new OrderByBorder<String>(ORDER_BY_DESCRIPTION_ID, DESCRIPTION_PROPERTY, settingDataProvider);
        settingDataviewContainer = new SettingDataviewContainer(SETTING_DATAVIEW_CONTAINER_ID,
            (IModel<Setting>) SettingTableContainer.this.getDefaultModel());
        settingPagingNavigator =
            new BootstrapPagingNavigator(SETTING_PAGING_NAVIGATOR_MARKUP_ID, settingDataviewContainer.settingDataview);
      }

      @Override
      protected void onInitialize() {
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(addAjaxLink.setOutputMarkupId(true));
        add(orderByProperty.setOutputMarkupId(true));
        add(orderByValue.setOutputMarkupId(true));
        add(orderByDescription.setOutputMarkupId(true));
        add(settingDataviewContainer.setOutputMarkupId(true));
        add(settingPagingNavigator.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SETTING_TABLE_CONTAINER_ID = "settingTableContainer";

    private static final String SETTING_VIEW_OR_EDIT_PANEL_ID = "settingViewOrEditPanel";

    private static final long serialVersionUID = 1147490546680500759L;

    private final SettingViewOrEditPanel settingViewOrEditPanel;

    private final SettingTableContainer settingTableContainer;

    public SettingPanelContainer(final String id, final IModel<Setting> model) {
      super(id, model);
      settingTableContainer = new SettingTableContainer(SETTING_TABLE_CONTAINER_ID,
          (IModel<Setting>) SettingPanelContainer.this.getDefaultModel());
      settingViewOrEditPanel = new SettingViewOrEditPanel(SETTING_VIEW_OR_EDIT_PANEL_ID,
          (IModel<Setting>) SettingPanelContainer.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(settingTableContainer.add(new TableBehavior().hover()).setOutputMarkupId(true));
      add(settingViewOrEditPanel.add(settingViewOrEditPanel.new SettingViewFragment()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final String SETTING_PANEL_CONTAINER_ID = "settingPanelContainer";

  private static final Logger LOGGER = LoggerFactory.getLogger(SettingPanel.class);

  private static final long serialVersionUID = 3703226064705246155L;

  @SpringBean(name = SETTING_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Setting> settingDataProvider;

  private final SettingPanelContainer settingPanelContainer;

  public SettingPanel(final String id, final IModel<Setting> model) {
    super(id, model);
    settingPanelContainer =
        new SettingPanelContainer(SETTING_PANEL_CONTAINER_ID, (IModel<Setting>) SettingPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    settingDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    settingDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    settingDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    settingDataProvider.setType(new Setting());
    settingDataProvider.getType().setActive(true);
    if (settingDataProvider.size() > 0) {
      SettingPanel.this.setDefaultModelObject(settingDataProvider.iterator(0, 1).next());
    }
    add(settingPanelContainer.add(new BootstrapBaseBehavior() {

      private static final long serialVersionUID = -4903722864597601489L;

      @Override
      public void onComponentTag(final Component component, final ComponentTag tag) {
        Attributes.addClass(tag, MediumSpanType.SPAN10);
      }
    }).setOutputMarkupId(true));
    super.onInitialize();
  }
}
