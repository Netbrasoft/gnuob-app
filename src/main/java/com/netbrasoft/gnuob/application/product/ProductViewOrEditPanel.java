package com.netbrasoft.gnuob.application.product;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.PRODUCT_DATA_PROVIDER_NAME;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.UrlTextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
import com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants;
import com.netbrasoft.gnuob.application.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.application.security.AppRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.LoadingBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapCheckbox;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class ProductViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER})
  class ProductEditFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER})
    class ProductEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER})
      class CancelAjaxLink extends BootstrapAjaxLink<Product> {

        private static final long serialVersionUID = 4267535261864907719L;

        public CancelAjaxLink(final String id, final IModel<Product> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          ProductViewOrEditPanel.this.removeAll();
          if (((Product) CancelAjaxLink.this.getDefaultModelObject()).getId() > 0) {
            CancelAjaxLink.this.setDefaultModelObject(
                productDataProvider.findById((Product) CancelAjaxLink.this.getDefaultModelObject()));
          }
          target.add(ProductViewOrEditPanel.this.add(ProductViewOrEditPanel.this.new ProductViewFragment())
              .setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class SaveAjaxButton extends BootstrapAjaxButton {

        private static final long serialVersionUID = 2695394292963384938L;

        public SaveAjaxButton(final String id, final IModel<String> model, final Form<Product> form,
            final Buttons.Type type) {
          super(id, model, form, type);
          setSize(Buttons.Size.Small);
          add(new LoadingBehavior(Model
              .of(ProductViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY))));
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          form.add(new TooltipValidation());
          target.add(form);
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
              .of(ProductViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          try {
            if (((Product) form.getDefaultModelObject()).getId() == 0) {
              ProductEditTable.this.setDefaultModelObject(
                  productDataProvider.findById(productDataProvider.persist((Product) form.getDefaultModelObject())));
            } else {
              ProductEditTable.this.setDefaultModelObject(
                  productDataProvider.findById(productDataProvider.merge((Product) form.getDefaultModelObject())));
            }
            ProductViewOrEditPanel.this.removeAll();
            target.add(ProductViewOrEditPanel.this.add(ProductViewOrEditPanel.this.new ProductViewFragment())
                .setOutputMarkupId(true));
          } catch (final RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
            feedbackPanel.warn(e.getLocalizedMessage());
            target.add(feedbackPanel.setOutputMarkupId(true));
            target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(
                ProductViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
          }
        }
      }

      private static final String PRODUCT_EDIT_FORM_COMPONENT_ID = "productEditForm";

      private static final String OPTION_VIEW_OR_EDIT_PANEL_ID = "optionViewOrEditPanel";

      private static final String CONTENT_VIEW_OR_EDIT_PANEL_ID = "contentViewOrEditPanel";

      private static final String SUB_CATEGORY_VIEW_OR_EDIT_PANEL_ID = "subCategoryViewOrEditPanel";

      private static final String CANCEL_ID = "cancel";

      private static final String SAVE_ID = "save";

      private static final String FEEDBACK_ID = "feedback";

      private static final String STOCK_QUANTITY_ID = "stock.quantity";

      private static final String STOCK_MIN_QUANTITY_ID = "stock.minQuantity";

      private static final String STOCK_MAX_QUANTITY_ID = "stock.maxQuantity";

      private static final String RECOMMENDED_ID = "recommended";

      private static final String RATING_ID = "rating";

      private static final String LATEST_COLLECTION_ID = "latestCollection";

      private static final String BESTSELLERS_ID = "bestsellers";

      private static final String ITEM_WIDTH_UNIT_ID = "itemWidthUnit";

      private static final String ITEM_WIDTH_ID = "itemWidth";

      private static final String ITEM_WEIGHT_UNIT_ID = "itemWeightUnit";

      private static final String ITEM_WEIGHT_ID = "itemWeight";

      private static final String ITEM_LENGTH_UNIT_ID = "itemLengthUnit";

      private static final String ITEM_LENGTH_ID = "itemLength";

      private static final String ITEM_HEIGHT_UNIT_ID = "itemHeightUnit";

      private static final String ITEM_HEIGHT_ID = "itemHeight";

      private static final String TAX_ID = "tax";

      private static final String SHIPPING_COST_ID = "shippingCost";

      private static final String DISCOUNT_ID = "discount";

      private static final String AMOUNT_ID = "amount";

      private static final String ITEM_URL_ID = "itemUrl";

      private static final String DESCRIPTION_ID = "description";

      private static final String NAME_ID = "name";

      private static final String NUMBER_ID = "number";

      private static final long serialVersionUID = 4144078478707460879L;

      private final BootstrapForm<Product> productEditForm;

      private final ProductOptionPanel productOptionPanel;

      private final ProductContentPanel contentViewOrEditPanel;

      private final ProductSubCategoryPanel subCategoryViewOrEditPanel;

      private final CancelAjaxLink cancelAjaxLink;

      private final SaveAjaxButton saveAjaxButton;

      private final NotificationPanel feedbackPanel;

      public ProductEditTable(final String id, final IModel<Product> model) {
        super(id, model);
        productEditForm = new BootstrapForm<Product>(PRODUCT_EDIT_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Product>((IModel<Product>) ProductEditTable.this.getDefaultModel()));
        productOptionPanel = new ProductOptionPanel(OPTION_VIEW_OR_EDIT_PANEL_ID,
            (IModel<Product>) ProductEditTable.this.getDefaultModel());
        contentViewOrEditPanel = new ProductContentPanel(CONTENT_VIEW_OR_EDIT_PANEL_ID,
            (IModel<Product>) ProductEditTable.this.getDefaultModel());
        subCategoryViewOrEditPanel = new ProductSubCategoryPanel(SUB_CATEGORY_VIEW_OR_EDIT_PANEL_ID,
            (IModel<Product>) ProductEditTable.this.getDefaultModel());
        cancelAjaxLink = new CancelAjaxLink(CANCEL_ID, model, Buttons.Type.Default,
            Model.of(ProductViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID,
            Model.of(ProductViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)),
            productEditForm, Buttons.Type.Primary);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
      }

      @Override
      protected void onInitialize() {
        productEditForm.add(
            new RequiredTextField<String>(NUMBER_ID).add(StringValidator.maximumLength(64)).setOutputMarkupId(true));
        productEditForm.add(
            new RequiredTextField<String>(NAME_ID).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        productEditForm.add(new TextArea<String>(DESCRIPTION_ID).add(StringValidator.maximumLength(128))
            .setRequired(true).setOutputMarkupId(true));
        productEditForm
            .add(new UrlTextField(ITEM_URL_ID, new PropertyModel<String>(getDefaultModelObject(), ITEM_URL_ID))
                .setOutputMarkupId(true));
        productEditForm.add(new NumberTextField<BigDecimal>(AMOUNT_ID).setMinimum(BigDecimal.ZERO).setRequired(true)
            .setOutputMarkupId(true));
        productEditForm.add(new NumberTextField<BigDecimal>(DISCOUNT_ID).setMinimum(BigDecimal.ZERO).setRequired(true)
            .setOutputMarkupId(true));
        productEditForm.add(new NumberTextField<BigDecimal>(SHIPPING_COST_ID).setMinimum(BigDecimal.ZERO)
            .setRequired(true).setOutputMarkupId(true));
        productEditForm.add(new NumberTextField<BigDecimal>(TAX_ID).setMinimum(BigDecimal.ZERO).setRequired(true)
            .setOutputMarkupId(true));
        productEditForm
            .add(new NumberTextField<BigDecimal>(ITEM_HEIGHT_ID).setMinimum(BigDecimal.ZERO).setOutputMarkupId(true));
        productEditForm.add(
            new TextField<String>(ITEM_HEIGHT_UNIT_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        productEditForm
            .add(new NumberTextField<BigDecimal>(ITEM_LENGTH_ID).setMinimum(BigDecimal.ZERO).setOutputMarkupId(true));
        productEditForm.add(
            new TextField<String>(ITEM_LENGTH_UNIT_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        productEditForm.add(new NumberTextField<BigDecimal>(ITEM_WEIGHT_ID).setMinimum(BigDecimal.ZERO)
            .setRequired(true).setOutputMarkupId(true));
        productEditForm.add(
            new TextField<String>(ITEM_WEIGHT_UNIT_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        productEditForm
            .add(new NumberTextField<BigDecimal>(ITEM_WIDTH_ID).setMinimum(BigDecimal.ZERO).setOutputMarkupId(true));
        productEditForm.add(
            new TextField<String>(ITEM_WIDTH_UNIT_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        productEditForm.add(new BootstrapCheckbox(BESTSELLERS_ID).setOutputMarkupId(true));
        productEditForm.add(new BootstrapCheckbox(LATEST_COLLECTION_ID).setOutputMarkupId(true));
        productEditForm.add(
            new NumberTextField<Integer>(RATING_ID).setMinimum(0).setMinimum(0).setMaximum(5).setOutputMarkupId(true));
        productEditForm.add(new BootstrapCheckbox(RECOMMENDED_ID).setOutputMarkupId(true));
        productEditForm.add(new NumberTextField<BigInteger>(STOCK_MAX_QUANTITY_ID).setMinimum(BigInteger.ZERO)
            .setRequired(true).setOutputMarkupId(true));
        productEditForm.add(new NumberTextField<BigInteger>(STOCK_MIN_QUANTITY_ID).setMinimum(BigInteger.ZERO)
            .setRequired(true).setOutputMarkupId(true));
        productEditForm.add(new NumberTextField<BigInteger>(STOCK_QUANTITY_ID).setMinimum(BigInteger.ZERO)
            .setRequired(true).setOutputMarkupId(true));
        productEditForm
            .add(productOptionPanel.add(productOptionPanel.new ProductOptionEditFragment()).setOutputMarkupId(true));
        productEditForm.add(contentViewOrEditPanel.add(contentViewOrEditPanel.new ProductContentEditFragment())
            .setOutputMarkupId(true));
        productEditForm.add(subCategoryViewOrEditPanel
            .add(subCategoryViewOrEditPanel.new ProductSubCategoryEditFragment()).setOutputMarkupId(true));
        add(productEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(cancelAjaxLink.setOutputMarkupId(true));
        add(saveAjaxButton.setOutputMarkupId(true));
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String PRODUCT_EDIT_TABLE_ID = "productEditTable";

    private static final String PRODUCT_EDIT_FRAGMENT_MARKUP_ID = "productEditFragment";

    private static final String PRODUCT_VIEW_OR_EDIT_FRAGMENT_ID = "productViewOrEditFragment";

    private static final long serialVersionUID = 4745878764173533323L;

    private final ProductEditTable productEditTable;

    public ProductEditFragment() {
      super(PRODUCT_VIEW_OR_EDIT_FRAGMENT_ID, PRODUCT_EDIT_FRAGMENT_MARKUP_ID, ProductViewOrEditPanel.this,
          ProductViewOrEditPanel.this.getDefaultModel());
      productEditTable =
          new ProductEditTable(PRODUCT_EDIT_TABLE_ID, (IModel<Product>) ProductEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(productEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class ProductViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class ProductViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class EditAjaxLink extends BootstrapAjaxLink<Product> {

        private static final long serialVersionUID = 4267535261864907719L;

        public EditAjaxLink(final String id, final IModel<Product> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.edit);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          ProductViewOrEditPanel.this.removeAll();
          target.add(ProductViewOrEditPanel.this
              .add(ProductViewOrEditPanel.this.new ProductEditFragment().setOutputMarkupId(true)));
        }
      }

      private static final String STOCK_QUANTITY_ID = "stock.quantity";

      private static final String STOCK_MIN_QUANTITY_ID = "stock.minQuantity";

      private static final String STOCK_MAX_QUANTITY_ID = "stock.maxQuantity";

      private static final String RECOMMENDED_ID = "recommended";

      private static final String RATING_ID = "rating";

      private static final String LATEST_COLLECTION_ID = "latestCollection";

      private static final String BESTSELLERS_ID = "bestsellers";

      private static final String ITEM_WIDTH_UNIT_ID = "itemWidthUnit";

      private static final String ITEM_WIDTH_ID = "itemWidth";

      private static final String ITEM_WEIGHT_UNIT_ID = "itemWeightUnit";

      private static final String ITEM_WEIGHT_ID = "itemWeight";

      private static final String ITEM_LENGTH_UNIT_ID = "itemLengthUnit";

      private static final String ITEM_LENGTH_ID = "itemLength";

      private static final String ITEM_HEIGHT_UNIT_ID = "itemHeightUnit";

      private static final String ITEM_HEIGHT_ID = "itemHeight";

      private static final String TAX_ID = "tax";

      private static final String SHIPPING_COST_ID = "shippingCost";

      private static final String DISCOUNT_ID = "discount";

      private static final String AMOUNT_ID = "amount";

      private static final String ITEM_URL_ID = "itemUrl";

      private static final String DESCRIPTION_ID = "description";

      private static final String NAME_ID = "name";

      private static final String NUMBER_ID = "number";

      private static final String SUB_CATEGORY_VIEW_OR_EDIT_PANEL_ID = "subCategoryViewOrEditPanel";

      private static final String CONTENT_VIEW_OR_EDIT_PANEL_ID = "contentViewOrEditPanel";

      private static final String OPTION_VIEW_OR_EDIT_PANEL_ID = "optionViewOrEditPanel";

      private static final String EDIT_ID = "edit";

      private static final String PRODUCT_VIEW_FORM_COMPONENT_ID = "productViewForm";

      private static final long serialVersionUID = -6470226164583172027L;

      private final EditAjaxLink editAjaxLink;

      private final BootstrapForm<Product> productViewForm;

      private final ProductOptionPanel productOptionPanel;

      private final ProductContentPanel contentViewOrEditPanel;

      private final ProductSubCategoryPanel subCategoryViewOrEditPanel;

      public ProductViewTable(final String id, final IModel<Product> model) {
        super(id, model);
        productViewForm = new BootstrapForm<Product>(PRODUCT_VIEW_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Product>((IModel<Product>) ProductViewTable.this.getDefaultModel()));
        editAjaxLink =
            new EditAjaxLink(EDIT_ID, (IModel<Product>) ProductViewTable.this.getDefaultModel(), Buttons.Type.Primary,
                Model.of(ProductViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.EDIT_MESSAGE_KEY)));
        productOptionPanel = new ProductOptionPanel(OPTION_VIEW_OR_EDIT_PANEL_ID,
            (IModel<Product>) ProductViewTable.this.getDefaultModel());
        contentViewOrEditPanel = new ProductContentPanel(CONTENT_VIEW_OR_EDIT_PANEL_ID,
            (IModel<Product>) ProductViewTable.this.getDefaultModel());
        subCategoryViewOrEditPanel = new ProductSubCategoryPanel(SUB_CATEGORY_VIEW_OR_EDIT_PANEL_ID,
            (IModel<Product>) ProductViewTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        productViewForm.add(new RequiredTextField<String>(NUMBER_ID).setOutputMarkupId(true));
        productViewForm.add(new RequiredTextField<String>(NAME_ID).setOutputMarkupId(true));
        productViewForm.add(new TextArea<String>(DESCRIPTION_ID).setOutputMarkupId(true));
        productViewForm
            .add(new UrlTextField(ITEM_URL_ID, new PropertyModel<String>(getDefaultModelObject(), ITEM_URL_ID))
                .setOutputMarkupId(true));
        productViewForm.add(new NumberTextField<Integer>(AMOUNT_ID).setOutputMarkupId(true));
        productViewForm.add(new NumberTextField<Integer>(DISCOUNT_ID).setOutputMarkupId(true));
        productViewForm.add(new NumberTextField<Integer>(SHIPPING_COST_ID).setOutputMarkupId(true));
        productViewForm.add(new NumberTextField<Integer>(TAX_ID).setOutputMarkupId(true));
        productViewForm.add(new NumberTextField<Integer>(ITEM_HEIGHT_ID).setOutputMarkupId(true));
        productViewForm.add(new TextField<String>(ITEM_HEIGHT_UNIT_ID).setOutputMarkupId(true));
        productViewForm.add(new NumberTextField<Integer>(ITEM_LENGTH_ID).setOutputMarkupId(true));
        productViewForm.add(new TextField<String>(ITEM_LENGTH_UNIT_ID).setOutputMarkupId(true));
        productViewForm.add(new NumberTextField<Integer>(ITEM_WEIGHT_ID).setOutputMarkupId(true));
        productViewForm.add(new TextField<String>(ITEM_WEIGHT_UNIT_ID).setOutputMarkupId(true));
        productViewForm.add(new NumberTextField<Integer>(ITEM_WIDTH_ID).setOutputMarkupId(true));
        productViewForm.add(new TextField<String>(ITEM_WIDTH_UNIT_ID).setOutputMarkupId(true));
        productViewForm.add(new TextField<String>(BESTSELLERS_ID).setOutputMarkupId(true));
        productViewForm.add(new TextField<String>(LATEST_COLLECTION_ID).setOutputMarkupId(true));
        productViewForm.add(new NumberTextField<Integer>(RATING_ID).setOutputMarkupId(true));
        productViewForm.add(new TextField<String>(RECOMMENDED_ID).setOutputMarkupId(true));
        productViewForm.add(new NumberTextField<Integer>(STOCK_MAX_QUANTITY_ID).setOutputMarkupId(true));
        productViewForm.add(new NumberTextField<Integer>(STOCK_MIN_QUANTITY_ID).setOutputMarkupId(true));
        productViewForm.add(new NumberTextField<Integer>(STOCK_QUANTITY_ID).setOutputMarkupId(true));
        productViewForm
            .add(productOptionPanel.add(productOptionPanel.new ProductOptionViewFragment()).setOutputMarkupId(true));
        productViewForm.add(contentViewOrEditPanel.add(contentViewOrEditPanel.new ProductContentViewFragment())
            .setOutputMarkupId(true));
        productViewForm.add(subCategoryViewOrEditPanel
            .add(subCategoryViewOrEditPanel.new ProductSubCategoryViewFragment()).setOutputMarkupId(true));
        add(productViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(editAjaxLink.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String PRODUCT_VIEW_TABLE_ID = "productViewTable";

    private static final String PRODUCT_VIEW_FRAGMENT_MARKUP_ID = "productViewFragment";

    private static final String PRODUCT_VIEW_OR_EDIT_FRAGMENT_ID = "productViewOrEditFragment";

    private static final long serialVersionUID = 4745878764173533323L;

    private final ProductViewTable productViewTable;

    public ProductViewFragment() {
      super(PRODUCT_VIEW_OR_EDIT_FRAGMENT_ID, PRODUCT_VIEW_FRAGMENT_MARKUP_ID, ProductViewOrEditPanel.this,
          ProductViewOrEditPanel.this.getDefaultModel());
      productViewTable =
          new ProductViewTable(PRODUCT_VIEW_TABLE_ID, (IModel<Product>) ProductViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(productViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductViewOrEditPanel.class);

  private static final long serialVersionUID = -4234081101243453856L;

  @SpringBean(name = PRODUCT_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Product> productDataProvider;

  public ProductViewOrEditPanel(final String id, final IModel<Product> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    productDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    productDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    productDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    productDataProvider.setType(new Product());
    productDataProvider.getType().setActive(true);
    super.onInitialize();
  }
}
