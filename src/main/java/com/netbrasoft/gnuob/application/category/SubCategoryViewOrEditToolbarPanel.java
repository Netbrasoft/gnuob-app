package com.netbrasoft.gnuob.application.category;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.StringValidator;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.SubCategory;
import com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants;
import com.netbrasoft.gnuob.application.security.AppRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.LoadingBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class SubCategoryViewOrEditToolbarPanel extends AbstractToolbar {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class SubCategoryEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class SubCategoryEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class SaveAjaxButton extends BootstrapAjaxButton {

        private static final long serialVersionUID = 2695394292963384938L;

        public SaveAjaxButton(final String id, final IModel<String> model, final Form<?> form, final Buttons.Type type) {
          super(id, model, form, type);
          setSize(Buttons.Size.Small);
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          form.add(new TooltipValidation());
          target.add(form);
          target.add(
              SaveAjaxButton.this.add(new LoadingBehavior(Model.of(SubCategoryViewOrEditToolbarPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          if (((SubCategory) form.getDefaultModelObject()).getId() == 0) {
            SubCategoryViewOrEditToolbarPanel.this.selectedModel.getObject().getSubCategories().add(((SubCategory) form.getDefaultModelObject()));
          }
          target.add(form.setOutputMarkupId(true));
          target.add(
              SaveAjaxButton.this.add(new LoadingBehavior(Model.of(SubCategoryViewOrEditToolbarPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
        }
      }

      private static final String DESCRIPTION_ID = "description";

      private static final String NAME_ID = "name";

      private static final String CONTENT_VIEW_OR_EDIT_PANEL_ID = "contentViewOrEditPanel";

      private static final String SUB_CATEGORY_EDIT_FORM_COMPONENT_ID = "subCategoryEditForm";

      private static final long serialVersionUID = -693089465363429203L;

      private static final String SAVE_ID = "save";

      private final BootstrapForm<SubCategory> subCategoryEditForm;

      private final SubCategoryContentPanel contentViewOrEditPanel;

      private final SaveAjaxButton saveAjaxButton;

      public SubCategoryEditTable(final String id, final IModel<Category> model) {
        super(id, model);
        subCategoryEditForm =
            new BootstrapForm<SubCategory>(SUB_CATEGORY_EDIT_FORM_COMPONENT_ID, new CompoundPropertyModel<SubCategory>(SubCategoryViewOrEditToolbarPanel.this.selectedModel));
        contentViewOrEditPanel = new SubCategoryContentPanel(CONTENT_VIEW_OR_EDIT_PANEL_ID, (IModel<Category>) SubCategoryEditTable.this.getDefaultModel());
        saveAjaxButton = new SaveAjaxButton(SAVE_ID, Model.of(SubCategoryViewOrEditToolbarPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)),
            subCategoryEditForm, Buttons.Type.Primary);
        contentViewOrEditPanel.setSelectedModel(SubCategoryViewOrEditToolbarPanel.this.selectedModel);
      }

      @Override
      protected void onInitialize() {
        subCategoryEditForm.add(new RequiredTextField<String>(NAME_ID).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        subCategoryEditForm.add(new TextArea<String>(DESCRIPTION_ID).add(StringValidator.maximumLength(128)).setRequired(true).setOutputMarkupId(true));
        subCategoryEditForm.add(contentViewOrEditPanel.add(contentViewOrEditPanel.new SubCategoryContentEditFragment()).setOutputMarkupId(true));
        subCategoryEditForm.add(saveAjaxButton.setOutputMarkupId(true));
        add(subCategoryEditForm.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SUB_CATEGORY_EDIT_TABLE_ID = "subCategoryEditTable";

    private static final String SUB_CATEGORY_EDIT_FRAGMENT_MARKUP_ID = "subCategoryEditFragment";

    private static final String SUB_CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID = "subCategoryViewOrEditFragment";

    private static final long serialVersionUID = 5133082553128798473L;

    private final SubCategoryEditTable subCategoryEditTable;

    public SubCategoryEditFragment() {
      super(SUB_CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID, SUB_CATEGORY_EDIT_FRAGMENT_MARKUP_ID, SubCategoryViewOrEditToolbarPanel.this,
          SubCategoryViewOrEditToolbarPanel.this.getDefaultModel());
      subCategoryEditTable = new SubCategoryEditTable(SUB_CATEGORY_EDIT_TABLE_ID, (IModel<Category>) SubCategoryEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(subCategoryEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class SubCategoryViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class SubCategoryViewTable extends WebMarkupContainer {

      private static final String SUB_CATEGORY_VIEW_FORM_COMPONENT_ID = "subCategoryViewForm";

      private static final String CONTENT_VIEW_OR_EDIT_PANEL_ID = "contentViewOrEditPanel";

      private static final long serialVersionUID = 8826384526238664948L;

      private static final String DESCRIPTION_ID = "description";

      private static final String NAME_ID = "name";

      private final BootstrapForm<SubCategory> subCategoryViewForm;

      private final SubCategoryContentPanel contentViewOrEditPanel;

      public SubCategoryViewTable(final String id, final IModel<Category> model) {
        super(id, model);
        subCategoryViewForm =
            new BootstrapForm<SubCategory>(SUB_CATEGORY_VIEW_FORM_COMPONENT_ID, new CompoundPropertyModel<SubCategory>(SubCategoryViewOrEditToolbarPanel.this.selectedModel));
        contentViewOrEditPanel = new SubCategoryContentPanel(CONTENT_VIEW_OR_EDIT_PANEL_ID, (IModel<Category>) SubCategoryViewTable.this.getDefaultModel());
        contentViewOrEditPanel.setSelectedModel(SubCategoryViewOrEditToolbarPanel.this.selectedModel);
      }

      @Override
      protected void onInitialize() {
        subCategoryViewForm.add(new RequiredTextField<String>(NAME_ID));
        subCategoryViewForm.add(new TextArea<String>(DESCRIPTION_ID));
        subCategoryViewForm.add(contentViewOrEditPanel.add(contentViewOrEditPanel.new SubCategoryContentViewFragment()).setOutputMarkupId(true));
        add(subCategoryViewForm.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SUB_CATEGORY_VIEW_TABLE_ID = "subCategoryViewTable";

    private static final String SUB_CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID = "subCategoryViewOrEditFragment";

    private static final String SUB_CATEGORY_VIEW_FRAGMENT_MARKUP_ID = "subCategoryViewFragment";

    private static final long serialVersionUID = 5863708936560086113L;

    private final SubCategoryViewTable subCategoryViewTable;

    public SubCategoryViewFragment() {
      super(SUB_CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID, SUB_CATEGORY_VIEW_FRAGMENT_MARKUP_ID, SubCategoryViewOrEditToolbarPanel.this,
          SubCategoryViewOrEditToolbarPanel.this.getDefaultModel());
      subCategoryViewTable = new SubCategoryViewTable(SUB_CATEGORY_VIEW_TABLE_ID, (IModel<Category>) SubCategoryViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(subCategoryViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = 3968615764565588442L;

  private IModel<SubCategory> selectedModel;

  public <T> SubCategoryViewOrEditToolbarPanel(final IModel<Category> model, final DataTable<T, String> table) {
    super(model, table);
    selectedModel = Model.of(new SubCategory());
  }

  public void setSelectedModel(final IModel<SubCategory> selectedModel) {
    this.selectedModel = selectedModel;
  }
}
