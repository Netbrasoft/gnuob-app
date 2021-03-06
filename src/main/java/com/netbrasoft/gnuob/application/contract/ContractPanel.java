package com.netbrasoft.gnuob.application.contract;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Contract;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.application.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.application.security.AppRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = { AppRoles.MANAGER, AppRoles.EMPLOYEE })
public class ContractPanel extends Panel {

   @AuthorizeAction(action = Action.RENDER, roles = { AppRoles.MANAGER })
   class AddAjaxLink extends AjaxLink<Void> {

      private static final long serialVersionUID = -8317730269644885290L;

      public AddAjaxLink() {
         super("add");
      }

      @Override
      public void onClick(AjaxRequestTarget target) {
         // TODO Auto-generated method stub
      }
   }

   class ContractDataview extends DataView<Contract> {

      private static final long serialVersionUID = -7876356935046054019L;

      protected ContractDataview() {
         super("contractDataview", contractDataProvider, ITEMS_PER_PAGE);
      }

      @Override
      protected Item<Contract> newItem(String id, int index, IModel<Contract> model) {
         Item<Contract> item = super.newItem(id, index, model);

         if (model.getObject().getId() == ((Contract) contractViewOrEditPanel.getDefaultModelObject()).getId()) {
            item.add(new AttributeModifier("class", "info"));
         }

         return item;
      }

      @Override
      protected void populateItem(Item<Contract> paramItem) {
         paramItem.setModel(new CompoundPropertyModel<Contract>(paramItem.getModelObject()));
         paramItem.add(new Label("contractId"));
         paramItem.add(new Label("customer.firstName"));
         paramItem.add(new Label("customer.lastName"));
         paramItem.add(new AjaxEventBehavior("click") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onEvent(AjaxRequestTarget target) {
               contractViewOrEditPanel.setDefaultModelObject(paramItem.getModelObject());
               target.add(getPage());
            }
         });
      }
   }

   private static final long serialVersionUID = 3703226064705246155L;

   private static final int ITEMS_PER_PAGE = 10;

   @SpringBean(name = "ContractDataProvider", required = true)
   private GenericTypeDataProvider<Contract> contractDataProvider;

   private OrderByBorder<String> orderByFirstName = new OrderByBorder<String>("orderByFirstName", "firstName", contractDataProvider);

   private OrderByBorder<String> orderByLastName = new OrderByBorder<String>("orderByLastName", "lastName", contractDataProvider);

   private OrderByBorder<String> orderByContractId = new OrderByBorder<String>("orderByContractId", "contractId", contractDataProvider);

   private DataView<Contract> contractDataview = new ContractDataview();

   private WebMarkupContainer contractDataviewContainer = new WebMarkupContainer("contractDataviewContainer") {

      private static final long serialVersionUID = -497527332092449028L;

      @Override
      protected void onInitialize() {
         add(contractDataview);
         super.onInitialize();
      }
   };

   private BootstrapPagingNavigator contractPagingNavigator = new BootstrapPagingNavigator("contractPagingNavigator", contractDataview);

   private ContractViewOrEditPanel contractViewOrEditPanel = new ContractViewOrEditPanel("contractViewOrEditPanel", (IModel<Contract>) getDefaultModel());

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
      add(orderByFirstName);
      add(orderByLastName);
      add(orderByContractId);
      add(contractDataviewContainer.setOutputMarkupId(true));
      add(contractPagingNavigator);
      add(contractViewOrEditPanel.setOutputMarkupId(true));

      super.onInitialize();
   }
}
