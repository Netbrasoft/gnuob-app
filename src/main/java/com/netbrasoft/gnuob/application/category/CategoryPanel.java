package com.netbrasoft.gnuob.application.category;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.application.authorization.RolesSession;
import com.netbrasoft.gnuob.application.paging.ItemsPerPagePagingNavigator;

public class CategoryPanel extends Panel {

   private static final long serialVersionUID = 3703226064705246155L;
   private static final int ITEMS_PER_PAGE = 5;

   @SpringBean(name = "CategoryDataProvider", required = true)
   private GenericTypeDataProvider<Category> categoryDataProvider;

   private AjaxLink<Void> add = new AjaxLink<Void>("add") {

      private static final long serialVersionUID = 9191172039973638020L;

      @Override
      public void onClick(AjaxRequestTarget paramAjaxRequestTarget) {
      }
   };
   private OrderByBorder<String> orderByposition = new OrderByBorder<String>("orderByPosition", "position", categoryDataProvider);
   private OrderByBorder<String> orderByName = new OrderByBorder<String>("orderByName", "name", categoryDataProvider);
   private OrderByBorder<String> orderByDescription = new OrderByBorder<String>("orderByDescription", "description", categoryDataProvider);
   private DataView<Category> categoryDataview = new DataView<Category>("categoryDataview", categoryDataProvider, ITEMS_PER_PAGE) {

      private static final long serialVersionUID = -5039874949058607907L;

      @Override
      protected void populateItem(Item<Category> paramItem) {
         paramItem.setModel(new CompoundPropertyModel<Category>(paramItem.getModelObject()));
         paramItem.add(new Label("position"));
         paramItem.add(new Label("name"));
         paramItem.add(new Label("description"));
         paramItem.add(new AjaxEventBehavior("onclick") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onEvent(AjaxRequestTarget paramAjaxRequestTarget) {
               categoryViewOrEditPanel.setDefaultModelObject(paramItem.getModelObject());
               paramAjaxRequestTarget.add(categoryViewOrEditPanel);
            }
         });
      }
   };
   private WebMarkupContainer categoryDataviewContainer = new WebMarkupContainer("categoryDataviewContainer") {

      private static final long serialVersionUID = -497527332092449028L;

      @Override
      protected void onInitialize() {
         add(categoryDataview);

         setOutputMarkupId(true);
         super.onInitialize();
      };
   };
   private ItemsPerPagePagingNavigator categoryPagingNavigator = new ItemsPerPagePagingNavigator("categoryPagingNavigator", categoryDataview);
   private CategoryViewOrEditPanel categoryViewOrEditPanel = new CategoryViewOrEditPanel("categoryViewOrEditPanel", new Model<Category>(new Category()));

   public CategoryPanel(String id, IModel<Category> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      RolesSession roleSession = (RolesSession) Session.get();

      categoryDataProvider.setUser(roleSession.getUsername());
      categoryDataProvider.setPassword(roleSession.getPassword());
      categoryDataProvider.setSite(roleSession.getSite());
      categoryDataProvider.setType((Category) getDefaultModelObject());

      add(add);
      add(orderByposition);
      add(orderByName);
      add(orderByDescription);
      add(categoryDataviewContainer);
      add(categoryPagingNavigator);
      add(categoryViewOrEditPanel);

      super.onInitialize();
   }
}