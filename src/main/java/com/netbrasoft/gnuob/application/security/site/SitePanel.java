package com.netbrasoft.gnuob.application.security.site;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.SITE_DATA_PROVIDER_NAME;
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

import com.netbrasoft.gnuob.api.Site;
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
 * Panel for viewing, selecting and editing {@link Site} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
public class SitePanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
  class SitePanelContainer extends WebMarkupContainer {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
    class SiteTableContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
      class AddAjaxLink extends BootstrapAjaxLink<Site> {

        private static final long serialVersionUID = -8317730269644885290L;

        public AddAjaxLink(final String id, final IModel<Site> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.plus);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          final Site site = new Site();
          site.setActive(true);
          AddAjaxLink.this.setDefaultModelObject(site);
          target.add(siteViewOrEditPanel.setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
      class SiteDataviewContainer extends WebMarkupContainer {

        @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
        class RemoveAjaxLink extends BootstrapAjaxLink<Site> {

          private static final long serialVersionUID = -8317730269644885290L;

          public RemoveAjaxLink(final String id, final IModel<Site> model, final Buttons.Type type,
              final IModel<String> labelModel) {
            super(id, model, type, labelModel);
            setIconType(GlyphIconType.remove);
            setSize(Buttons.Size.Mini);
          }

          @Override
          public void onClick(final AjaxRequestTarget target) {
            try {
              siteDataProvider.remove((Site) RemoveAjaxLink.this.getDefaultModelObject());
            } catch (final RuntimeException e) {
              LOGGER.warn(e.getMessage(), e);
              siteTableContainer.warn(e.getLocalizedMessage());
            } finally {
              target.add(siteTableContainer.setOutputMarkupId(true));
            }
          }
        }

        @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
        class SiteDataview extends DataView<Site> {

          private static final String NAME_ID = NAME_PROPERTY;

          private static final String DESCRIPTION_ID = DESCRIPTION_PROPERTY;

          private static final long serialVersionUID = -5039874949058607907L;

          private int index = 0;

          protected SiteDataview(final String id, final IDataProvider<Site> dataProvider, final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected Item<Site> newItem(final String id, final int index, final IModel<Site> model) {
            final Item<Site> item = super.newItem(id, index, model);
            if (this.index == index) {
              item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
            }
            return item;
          }

          @Override
          protected void populateItem(final Item<Site> item) {
            item.setModel(new CompoundPropertyModel<Site>(item.getModelObject()));
            item.add(new Label(NAME_ID));
            item.add(new Label(DESCRIPTION_ID));
            item.add(new AjaxEventBehavior(CLICK_EVENT) {

              private static final long serialVersionUID = 1L;

              @Override
              public void onEvent(final AjaxRequestTarget target) {
                index = item.getIndex();
                target.add(siteDataviewContainer.setDefaultModelObject(item.getModelObject()).setOutputMarkupId(true));
                target.add(siteViewOrEditPanel.setOutputMarkupId(true));
              }
            });
            item.add(new RemoveAjaxLink(REMOVE_ID, item.getModel(), Buttons.Type.Default,
                Model.of(SitePanel.this.getString(NetbrasoftApplicationConstants.REMOVE_MESSAGE_KEY)))
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

        private static final String SITE_DATAVIEW_ID = "siteDataview";

        private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

        private static final String REMOVE_ID = "remove";

        private static final String CLICK_EVENT = "click";

        private static final String INFO_VALUE = "info";

        private static final String CLASS_ATTRIBUTE = "class";

        private static final long serialVersionUID = 1658088620417029170L;

        private final SiteDataview siteDataview;

        public SiteDataviewContainer(final String id, final IModel<Site> model) {
          super(id, model);
          siteDataview = new SiteDataview(SITE_DATAVIEW_ID, siteDataProvider, 5);
        }

        @Override
        protected void onInitialize() {
          add(siteDataview.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String SITE_PAGING_NAVIGATOR_MARKUP_ID = "sitePagingNavigator";

      private static final String SITE_DATAVIEW_CONTAINER_ID = "siteDataviewContainer";

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

      private final SiteDataviewContainer siteDataviewContainer;

      private final BootstrapPagingNavigator sitePagingNavigator;

      public SiteTableContainer(final String id, final IModel<Site> model) {
        super(id, model);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
        addAjaxLink = new AddAjaxLink(ADD_ID, (IModel<Site>) SiteTableContainer.this.getDefaultModel(),
            Buttons.Type.Primary, Model.of(SitePanel.this.getString(NetbrasoftApplicationConstants.ADD_MESSAGE_KEY)));
        orderByName = new OrderByBorder<String>(ORDER_BY_NAME_ID, NAME_PROPERTY, siteDataProvider);
        orderByDescription = new OrderByBorder<String>(ORDER_BY_DESCRIPTION_ID, DESCRIPTION_PROPERTY, siteDataProvider);
        siteDataviewContainer = new SiteDataviewContainer(SITE_DATAVIEW_CONTAINER_ID,
            (IModel<Site>) SiteTableContainer.this.getDefaultModel());
        sitePagingNavigator =
            new BootstrapPagingNavigator(SITE_PAGING_NAVIGATOR_MARKUP_ID, siteDataviewContainer.siteDataview);
      }

      @Override
      protected void onInitialize() {
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(addAjaxLink.setOutputMarkupId(true));
        add(orderByName.setOutputMarkupId(true));
        add(orderByDescription.setOutputMarkupId(true));
        add(siteDataviewContainer.setOutputMarkupId(true));
        add(sitePagingNavigator.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SITE_TABLE_CONTAINER_ID = "siteTableContainer";

    private static final String SITE_VIEW_OR_EDIT_PANEL_ID = "siteViewOrEditPanel";

    private static final long serialVersionUID = 1147490546680500759L;

    private final SiteViewOrEditPanel siteViewOrEditPanel;

    private final SiteTableContainer siteTableContainer;

    public SitePanelContainer(final String id, final IModel<Site> model) {
      super(id, model);
      siteTableContainer =
          new SiteTableContainer(SITE_TABLE_CONTAINER_ID, (IModel<Site>) SitePanelContainer.this.getDefaultModel());
      siteViewOrEditPanel =
          new SiteViewOrEditPanel(SITE_VIEW_OR_EDIT_PANEL_ID, (IModel<Site>) SitePanelContainer.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(siteTableContainer.add(new TableBehavior().hover()).setOutputMarkupId(true));
      add(siteViewOrEditPanel.add(siteViewOrEditPanel.new SiteViewFragment()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final String SITE_PANEL_CONTAINER_ID = "sitePanelContainer";

  private static final Logger LOGGER = LoggerFactory.getLogger(SitePanel.class);

  private static final long serialVersionUID = 3703226064705246155L;

  @SpringBean(name = SITE_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Site> siteDataProvider;

  private final SitePanelContainer sitePanelContainer;

  public SitePanel(final String id, final IModel<Site> model) {
    super(id, model);
    sitePanelContainer =
        new SitePanelContainer(SITE_PANEL_CONTAINER_ID, (IModel<Site>) SitePanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    siteDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    siteDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    siteDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    siteDataProvider.setType(new Site());
    siteDataProvider.getType().setActive(true);
    if (siteDataProvider.size() > 0) {
      SitePanel.this.setDefaultModelObject(siteDataProvider.iterator(0, 1).next());
    }
    add(sitePanelContainer.add(new BootstrapBaseBehavior() {

      private static final long serialVersionUID = -4903722864597601489L;

      @Override
      public void onComponentTag(final Component component, final ComponentTag tag) {
        Attributes.addClass(tag, MediumSpanType.SPAN10);
      }
    }).setOutputMarkupId(true));
    super.onInitialize();
  }
}
