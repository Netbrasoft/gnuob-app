package com.netbrasoft.gnuob.application.product;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
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

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class ProductOptionViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class ProductOptionEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class OptionEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class SaveAjaxButton extends BootstrapAjaxButton {

        private static final long serialVersionUID = 2695394292963384938L;

        public SaveAjaxButton(final String id, final IModel<String> model, final Form<Option> form, final Buttons.Type type) {
          super(id, model, form, type);
          setSize(Buttons.Size.Small);
          add(new LoadingBehavior(Model.of(ProductOptionViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY))));
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          target.add(form.add(new TooltipValidation()));
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(ProductOptionViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          if (((Option) form.getDefaultModelObject()).getId() == 0) {
            ((Product) ProductOptionViewOrEditPanel.this.getDefaultModelObject()).getOptions().add((Option) form.getDefaultModelObject());
          }
          target.add(form.setOutputMarkupId(true));
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(ProductOptionViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY)))));
          target.add(ProductOptionViewOrEditPanel.this.getParent().setOutputMarkupId(true));
        }
      }

      private static final String DISABLED_ID = "disabled";

      private static final String DESCRIPTION_ID = "description";

      private static final String VALUE_ID = "value";

      private static final String POSITION_ID = "position";

      private static final String SAVE_ID = "save";

      private static final String PRODUCT_SUB_OPTION_PANEL_ID = "productSubOptionPanel";

      private static final String OPTION_EDIT_FORM_COMPONENT_ID = "optionEditForm";

      private static final long serialVersionUID = -7519943626345095089L;

      private final BootstrapForm<Option> optionEditForm;

      private final ProductSubOptionPanel productSubOptionPanel;

      private final SaveAjaxButton saveAjaxButton;

      public OptionEditTable(final String id, final IModel<Product> model) {
        super(id, model);
        optionEditForm = new BootstrapForm<Option>(OPTION_EDIT_FORM_COMPONENT_ID, new CompoundPropertyModel<Option>(ProductOptionViewOrEditPanel.this.selectedModel));
        productSubOptionPanel = new ProductSubOptionPanel(PRODUCT_SUB_OPTION_PANEL_ID, (IModel<Product>) OptionEditTable.this.getDefaultModel());
        productSubOptionPanel.setSelectedModel(ProductOptionViewOrEditPanel.this.selectedModel);
        saveAjaxButton = new SaveAjaxButton(SAVE_ID, Model.of(ProductOptionViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY)), optionEditForm,
            Buttons.Type.Primary);
      }

      @Override
      protected void onInitialize() {
        optionEditForm.add(new NumberTextField<Integer>(POSITION_ID).setMinimum(0).setOutputMarkupId(true));
        optionEditForm.add(new TextArea<String>(VALUE_ID).add(StringValidator.maximumLength(128)).setRequired(true).setOutputMarkupId(true));
        optionEditForm.add(new TextArea<String>(DESCRIPTION_ID).add(StringValidator.maximumLength(128)).setRequired(true).setOutputMarkupId(true));
        optionEditForm.add(new BootstrapCheckbox(DISABLED_ID).setOutputMarkupId(true));
        optionEditForm.add(saveAjaxButton.setOutputMarkupId(true));
        optionEditForm.add(productSubOptionPanel.add(productSubOptionPanel.new ProductSubOptionEditFragment()).setOutputMarkupId(true));
        add(optionEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String OPTION_EDIT_TABLE_ID = "optionEditTable";

    private static final String PRODUCT_OPTION_EDIT_FRAGMENT_MARKUP_ID = "productOptionEditFragment";

    private static final String PRODUCT_OPTION_VIEW_OR_EDIT_FRAGMENT_ID = "productOptionViewOrEditFragment";

    private static final long serialVersionUID = -4032029235917033204L;

    private final OptionEditTable optionEditTable;

    public ProductOptionEditFragment() {
      super(PRODUCT_OPTION_VIEW_OR_EDIT_FRAGMENT_ID, PRODUCT_OPTION_EDIT_FRAGMENT_MARKUP_ID, ProductOptionViewOrEditPanel.this,
          ProductOptionViewOrEditPanel.this.getDefaultModel());
      optionEditTable = new OptionEditTable(OPTION_EDIT_TABLE_ID, (IModel<Product>) ProductOptionViewOrEditPanel.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(optionEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class ProductOptionViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class OptionViewTable extends WebMarkupContainer {

      private static final String DISABLED_ID = "disabled";

      private static final String DESCRIPTION_ID = "description";

      private static final String VALUE_ID = "value";

      private static final String POSITION_ID = "position";

      private static final String PRODUCT_SUB_OPTION_PANEL_ID = "productSubOptionPanel";

      private static final String OPTION_VIEW_FORM_COMPONENT_ID = "optionViewForm";

      private static final long serialVersionUID = -7519943626345095089L;

      private final BootstrapForm<Option> optionViewForm;

      private final ProductSubOptionPanel productSubOptionPanel;

      public OptionViewTable(final String id, final IModel<Product> model) {
        super(id, model);
        optionViewForm = new BootstrapForm<Option>(OPTION_VIEW_FORM_COMPONENT_ID, new CompoundPropertyModel<Option>(ProductOptionViewOrEditPanel.this.selectedModel));
        productSubOptionPanel = new ProductSubOptionPanel(PRODUCT_SUB_OPTION_PANEL_ID, (IModel<Product>) OptionViewTable.this.getDefaultModel());
        productSubOptionPanel.setSelectedModel(ProductOptionViewOrEditPanel.this.selectedModel);
      }

      @Override
      protected void onInitialize() {
        optionViewForm.add(new NumberTextField<Integer>(POSITION_ID).setOutputMarkupId(true));
        optionViewForm.add(new TextArea<String>(VALUE_ID).setOutputMarkupId(true));
        optionViewForm.add(new TextArea<String>(DESCRIPTION_ID).setOutputMarkupId(true));
        optionViewForm.add(new TextField<String>(DISABLED_ID).setOutputMarkupId(true));
        optionViewForm.add(productSubOptionPanel.add(productSubOptionPanel.new ProductSubOptionViewFragment()).setOutputMarkupId(true));
        optionViewForm.add(new FormBehavior(FormType.Horizontal));
        add(optionViewForm.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String OPTION_VIEW_TABLE_ID = "optionViewTable";

    private static final String PRODUCT_OPTION_VIEW_FRAGMENT_MARKUP_ID = "productOptionViewFragment";

    private static final String PRODUCT_OPTION_VIEW_OR_EDIT_FRAGMENT_ID = "productOptionViewOrEditFragment";

    private static final long serialVersionUID = -4032029235917033204L;

    private final OptionViewTable optionViewTable;

    public ProductOptionViewFragment() {
      super(PRODUCT_OPTION_VIEW_OR_EDIT_FRAGMENT_ID, PRODUCT_OPTION_VIEW_FRAGMENT_MARKUP_ID, ProductOptionViewOrEditPanel.this,
          ProductOptionViewOrEditPanel.this.getDefaultModel());
      optionViewTable = new OptionViewTable(OPTION_VIEW_TABLE_ID, (IModel<Product>) ProductOptionViewOrEditPanel.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(optionViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = 8609291357690450348L;

  private IModel<Option> selectedModel;

  public ProductOptionViewOrEditPanel(final String id, final IModel<Product> model) {
    super(id, model);
    selectedModel = Model.of(new Option());
  }

  public void setSelectedModel(final IModel<Option> selectedModel) {
    this.selectedModel = selectedModel;
  }
}
