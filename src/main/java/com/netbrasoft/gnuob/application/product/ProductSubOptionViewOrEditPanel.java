package com.netbrasoft.gnuob.application.product;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.StringValidator;

import com.netbrasoft.gnuob.api.Option;
import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.api.SubOption;
import com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants;
import com.netbrasoft.gnuob.application.security.AppRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.LoadingBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapCheckbox;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;
import wicket.contrib.tinymce4.ajax.TinyMceAjaxSubmitModifier;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class ProductSubOptionViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER})
  class ProductSubOptionEditFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER})
    class SubOptionEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class SaveAjaxButton extends BootstrapAjaxButton {

        private static final long serialVersionUID = 2695394292963384938L;

        public SaveAjaxButton(final String id, final IModel<String> model, final Form<SubOption> form, final Buttons.Type type) {
          super(id, model, form, type);
          setSize(Buttons.Size.Small);
          add(new LoadingBehavior(Model.of(ProductSubOptionViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY))), new TinyMceAjaxSubmitModifier());
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          form.add(new TooltipValidation());
          target.add(form);
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(ProductSubOptionViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          if (((SubOption) form.getDefaultModelObject()).getId() == 0) {
            ProductSubOptionViewOrEditPanel.this.selectedParentModel.getObject().getSubOptions().add((SubOption) form.getDefaultModelObject());
          }
          target.add(form.setOutputMarkupId(true));
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(ProductSubOptionViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY)))));
          target.add(ProductSubOptionViewOrEditPanel.this.getParent().setOutputMarkupId(true));
        }
      }

      private static final String DISABLED_ID = "disabled";

      private static final String DESCRIPTION_ID = "description";

      private static final String VALUE_ID = "value";

      private static final String SAVE_ID = "save";

      private static final String SUB_OPTION_EDIT_FORM_COMPONENT_ID = "subOptionEditForm";

      private static final long serialVersionUID = -7519943626345095089L;

      private final BootstrapForm<SubOption> subOptionEditForm;

      private final SaveAjaxButton saveAjaxButton;

      public SubOptionEditTable(final String id, final IModel<Product> model) {
        super(id, model);
        subOptionEditForm =
            new BootstrapForm<SubOption>(SUB_OPTION_EDIT_FORM_COMPONENT_ID, new CompoundPropertyModel<SubOption>(ProductSubOptionViewOrEditPanel.this.selectedModel));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID, Model.of(ProductSubOptionViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY)), subOptionEditForm,
            Buttons.Type.Primary);
      }

      @Override
      protected void onInitialize() {
        subOptionEditForm.add(new TextArea<String>(VALUE_ID).add(StringValidator.maximumLength(128)).setRequired(true).setOutputMarkupId(true));
        subOptionEditForm.add(new TextArea<String>(DESCRIPTION_ID).add(StringValidator.maximumLength(128)).setRequired(true).setOutputMarkupId(true));
        subOptionEditForm.add(new BootstrapCheckbox(DISABLED_ID).setOutputMarkupId(true));
        subOptionEditForm.add(saveAjaxButton.setOutputMarkupId(true));
        add(subOptionEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SUB_OPTION_EDIT_TABLE_ID = "subOptionEditTable";

    private static final String PRODUCT_SUB_OPTION_EDIT_FRAGMENT_MARKUP_ID = "productSubOptionEditFragment";

    private static final String PRODUCT_SUB_OPTION_VIEW_OR_EDIT_FRAGMENT_ID = "productSubOptionViewOrEditFragment";

    private static final long serialVersionUID = -4032029235917033204L;

    private final SubOptionEditTable subOptionEditTable;

    public ProductSubOptionEditFragment() {
      super(PRODUCT_SUB_OPTION_VIEW_OR_EDIT_FRAGMENT_ID, PRODUCT_SUB_OPTION_EDIT_FRAGMENT_MARKUP_ID, ProductSubOptionViewOrEditPanel.this,
          ProductSubOptionViewOrEditPanel.this.getDefaultModel());
      subOptionEditTable = new SubOptionEditTable(SUB_OPTION_EDIT_TABLE_ID, (IModel<Product>) ProductSubOptionViewOrEditPanel.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(subOptionEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class ProductSubOptionViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class SubOptionViewTable extends WebMarkupContainer {

      private static final String DISABLED_ID = "disabled";

      private static final String DESCRIPTION_ID = "description";

      private static final String VALUE_ID = "value";

      private static final String SUB_OPTION_VIEW_FORM_COMPONENT_ID = "subOptionViewForm";

      private static final long serialVersionUID = -7519943626345095089L;

      private final BootstrapForm<SubOption> subOptionViewForm;

      public SubOptionViewTable(final String id, final IModel<Product> model) {
        super(id, model);
        subOptionViewForm =
            new BootstrapForm<SubOption>(SUB_OPTION_VIEW_FORM_COMPONENT_ID, new CompoundPropertyModel<SubOption>(ProductSubOptionViewOrEditPanel.this.selectedModel));
      }

      @Override
      protected void onInitialize() {
        subOptionViewForm.add(new TextArea<String>(VALUE_ID).setOutputMarkupId(true));
        subOptionViewForm.add(new TextArea<String>(DESCRIPTION_ID).setOutputMarkupId(true));
        subOptionViewForm.add(new TextField<String>(DISABLED_ID).setOutputMarkupId(true));
        add(subOptionViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SUB_OPTION_VIEW_TABLE_ID = "subOptionViewTable";

    private static final String PRODUCT_SUB_OPTION_VIEW_FRAGMENT_MARKUP_ID = "productSubOptionViewFragment";

    private static final String PRODUCT_SUB_OPTION_VIEW_OR_EDIT_FRAGMENT_ID = "productSubOptionViewOrEditFragment";

    private static final long serialVersionUID = -4032029235917033204L;

    private final SubOptionViewTable subOptionViewTable;

    public ProductSubOptionViewFragment() {
      super(PRODUCT_SUB_OPTION_VIEW_OR_EDIT_FRAGMENT_ID, PRODUCT_SUB_OPTION_VIEW_FRAGMENT_MARKUP_ID, ProductSubOptionViewOrEditPanel.this,
          ProductSubOptionViewOrEditPanel.this.getDefaultModel());
      subOptionViewTable = new SubOptionViewTable(SUB_OPTION_VIEW_TABLE_ID, (IModel<Product>) ProductSubOptionViewOrEditPanel.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(subOptionViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = 8609291357690450348L;

  private IModel<SubOption> selectedModel;

  private IModel<Option> selectedParentModel;

  public ProductSubOptionViewOrEditPanel(final String id, final IModel<Product> model) {
    super(id, model);
    selectedModel = Model.of(new SubOption());
    selectedParentModel = Model.of(new Option());
  }

  public void setSelectedModel(final IModel<SubOption> selectedModel, final IModel<Option> selectedParentModel) {
    this.selectedModel = selectedModel;
    this.selectedParentModel = selectedParentModel;
  }
}
