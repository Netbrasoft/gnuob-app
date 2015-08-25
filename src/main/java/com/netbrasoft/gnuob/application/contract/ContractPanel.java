package com.netbrasoft.gnuob.application.contract;

import static de.agilecoders.wicket.jquery.JQuery.$;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Contract;
import com.netbrasoft.gnuob.api.Customer;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.application.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.application.security.AppRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationConfig;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = { AppRoles.MANAGER, AppRoles.EMPLOYEE })
public class ContractPanel extends Panel {

   @AuthorizeAction(action = Action.RENDER, roles = { AppRoles.MANAGER })
   class AddAjaxLink extends BootstrapAjaxLink<String> {

      private static final long serialVersionUID = -8317730269644885290L;

      public AddAjaxLink() {
         super("add", Model.of(ContractPanel.this.getString("addMessage")), Buttons.Type.Primary, Model.of(ContractPanel.this.getString("addMessage")));
         setIconType(GlyphIconType.plus);
         setSize(Buttons.Size.Small);
      }

      @Override
      public void onClick(AjaxRequestTarget target) {
         contractViewOrEditPanel.setDefaultModelObject(new Customer());
         target.add(contractViewOrEditPanel.setOutputMarkupId(true));
      }
   }

   @AuthorizeAction(action = Action.ENABLE, roles = { AppRoles.MANAGER, AppRoles.EMPLOYEE })
   class ContractDataview extends DataView<Contract> {

      private static final long serialVersionUID = -7876356935046054019L;

      protected ContractDataview() {
         super("contractDataview", contractDataProvider, ITEMS_PER_PAGE);
      }

      @Override
      protected Item<Contract> newItem(String id, int index, IModel<Contract> model) {
         final Item<Contract> item = super.newItem(id, index, model);

         if (model.getObject().getId() == ((Contract) contractViewOrEditPanel.getDefaultModelObject()).getId()) {
            // FIXME BD: use wicket bootstrap for this attribute / table.
            item.add(new AttributeModifier("class", "info"));
         }

         return item;
      }

      @Override
      protected void populateItem(Item<Contract> item) {
         item.setModel(new CompoundPropertyModel<Contract>(item.getModelObject()));
         item.add(new Label("contractId"));
         item.add(new Label("customer.firstName"));
         item.add(new Label("customer.lastName"));
         item.add(new AjaxEventBehavior("click") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onEvent(AjaxRequestTarget target) {
               contractViewOrEditPanel.setDefaultModelObject(item.getModelObject());
               target.add(getPage());
            }
         });
         item.add(new RemoveAjaxLink(item.getModel()).add(new ConfirmationBehavior() {

            private static final long serialVersionUID = 7744720444161839031L;

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
               response.render($(component)
                     .chain("confirmation", new ConfirmationConfig().withTitle(getString("confirmationTitleMessage")).withSingleton(true).withPopout(true).withBtnOkLabel(getString("confirmMessage")).withBtnCancelLabel(getString("cancelMessage")))
                     .asDomReadyScript());
            }
         }));
      }
   }

   @AuthorizeAction(action = Action.RENDER, roles = { AppRoles.MANAGER })
   class RemoveAjaxLink extends BootstrapAjaxLink<Contract> {

      private static final long serialVersionUID = -8317730269644885290L;

      public RemoveAjaxLink(final IModel<Contract> model) {
         super("remove", model, Buttons.Type.Default, Model.of(ContractPanel.this.getString("removeMessage")));
         setIconType(GlyphIconType.remove);
         setSize(Buttons.Size.Mini);
      }

      @Override
      public void onClick(AjaxRequestTarget target) {
         try {
            getModelObject().setActive(false);
            contractDataProvider.merge(getModelObject());
            contractViewOrEditPanel.setDefaultModelObject(new Contract());
         } catch (final RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
            warn(e.getLocalizedMessage());
         } finally {
            target.add(getPage());
         }
      }
   }

   private static final long serialVersionUID = 3703226064705246155L;

   private static final int ITEMS_PER_PAGE = 10;

   private static final Logger LOGGER = LoggerFactory.getLogger(ContractPanel.class);

   @SpringBean(name = "ContractDataProvider", required = true)
   private GenericTypeDataProvider<Contract> contractDataProvider;

   private final OrderByBorder<String> orderByFirstName = new OrderByBorder<String>("orderByFirstName", "customer.firstName", contractDataProvider);

   private final OrderByBorder<String> orderByLastName = new OrderByBorder<String>("orderByLastName", "customer.lastName", contractDataProvider);

   private final OrderByBorder<String> orderByContractId = new OrderByBorder<String>("orderByContractId", "contractId", contractDataProvider);

   private final DataView<Contract> contractDataview = new ContractDataview();

   private final WebMarkupContainer contractDataviewContainer = new WebMarkupContainer("contractDataviewContainer") {

      private static final long serialVersionUID = -497527332092449028L;

      @Override
      protected void onInitialize() {
         add(contractDataview.setOutputMarkupId(true));
         super.onInitialize();
      }
   };

   private final BootstrapPagingNavigator contractPagingNavigator = new BootstrapPagingNavigator("contractPagingNavigator", contractDataview);

   private final ContractViewOrEditPanel contractViewOrEditPanel = new ContractViewOrEditPanel("contractViewOrEditPanel", (IModel<Contract>) getDefaultModel());

   public ContractPanel(final String id, final IModel<Contract> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      contractDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      contractDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      contractDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      contractDataProvider.setType(new Contract());
      contractDataProvider.getType().setActive(true);

      add(new AddAjaxLink());
      add(orderByFirstName.setOutputMarkupId(true));
      add(orderByLastName.setOutputMarkupId(true));
      add(orderByContractId.setOutputMarkupId(true));
      add(contractDataviewContainer.setOutputMarkupId(true));
      add(contractPagingNavigator.setOutputMarkupId(true));
      add(contractViewOrEditPanel.setOutputMarkupId(true));

      add(new NotificationPanel("feedback").hideAfter(Duration.seconds(5)).setOutputMarkupId(true));

      super.onInitialize();
   }
}
