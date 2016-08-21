package com.netbrasoft.gnuob.application.offer;

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
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;

import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.OfferRecord;
import com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants;
import com.netbrasoft.gnuob.application.security.AppRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.LoadingBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;

/**
 * Panel for viewing, selecting and editing {@link OfferRecord} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class OfferRecordViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class OfferRecordEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class OfferRecordEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class SaveAjaxButton extends BootstrapAjaxButton {

        private static final long serialVersionUID = 2695394292963384938L;

        public SaveAjaxButton(final String id, final IModel<String> model, final Form<OfferRecord> form, final Buttons.Type type) {
          super(id, model, form, type);
          setSize(Buttons.Size.Small);
          add(new LoadingBehavior(Model.of(OfferRecordViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY))));
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          form.add(new TooltipValidation());
          target.add(form);
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(OfferRecordViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          if (((OfferRecord) form.getDefaultModelObject()).getId() == 0) {
            ((Offer) OfferRecordViewOrEditPanel.this.getDefaultModelObject()).getRecords().add((OfferRecord) form.getDefaultModelObject());
          }
          target.add(form.setOutputMarkupId(true));
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(OfferRecordViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY)))));
          target.add(OfferRecordViewOrEditPanel.this.getParent().setOutputMarkupId(true));
        }
      }

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

      private static final String QUANTITY_ID = "quantity";

      private static final String ITEM_URL_ID = "itemUrl";

      private static final String DESCRIPTION_ID = "description";

      private static final String OPTION_ID = "option";

      private static final String NAME_ID = "name";

      private static final String PRODUCT_NUMBER_ID = "productNumber";

      private static final String OFFER_RECORD_ID_ID = "offerRecordId";

      private static final String SAVE_ID = "save";

      private static final String OFFER_RECORD_EDIT_FORM_COMPONENT_ID = "offerRecordEditForm";

      private static final long serialVersionUID = 101774853102549233L;

      private final BootstrapForm<OfferRecord> offerRecordEditForm;

      private final SaveAjaxButton saveAjaxButton;

      public OfferRecordEditTable(final String id, final IModel<Offer> model) {
        super(id, model);
        offerRecordEditForm =
            new BootstrapForm<OfferRecord>(OFFER_RECORD_EDIT_FORM_COMPONENT_ID, new CompoundPropertyModel<OfferRecord>(OfferRecordViewOrEditPanel.this.selectedModel));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID, Model.of(OfferRecordViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY)), offerRecordEditForm,
            Buttons.Type.Primary);
      }

      @Override
      protected void onInitialize() {
        offerRecordEditForm.add(new RequiredTextField<String>(OFFER_RECORD_ID_ID).add(StringValidator.maximumLength(64)).setOutputMarkupId(true));
        offerRecordEditForm.add(new RequiredTextField<String>(PRODUCT_NUMBER_ID).add(StringValidator.maximumLength(64)).setOutputMarkupId(true));
        offerRecordEditForm.add(new RequiredTextField<String>(NAME_ID).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        offerRecordEditForm.add(new TextField<String>(OPTION_ID).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        offerRecordEditForm.add(new TextArea<String>(DESCRIPTION_ID).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        offerRecordEditForm.add(new UrlTextField(ITEM_URL_ID, new PropertyModel<String>(offerRecordEditForm.getDefaultModelObject(), ITEM_URL_ID))
            .add(StringValidator.maximumLength(255)).setOutputMarkupId(true));
        offerRecordEditForm.add(new NumberTextField<BigInteger>(QUANTITY_ID).setRequired(true).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        offerRecordEditForm.add(new NumberTextField<BigDecimal>(AMOUNT_ID).setRequired(true).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        offerRecordEditForm.add(new NumberTextField<BigDecimal>(DISCOUNT_ID).setRequired(true).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        offerRecordEditForm.add(new NumberTextField<BigDecimal>(SHIPPING_COST_ID).setRequired(true).add(RangeValidator.minimum(BigDecimal.ZERO))
            .add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        offerRecordEditForm.add(new NumberTextField<BigDecimal>(TAX_ID).setRequired(true).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        offerRecordEditForm.add(new NumberTextField<BigDecimal>(ITEM_HEIGHT_ID).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        offerRecordEditForm.add(new TextField<String>(ITEM_HEIGHT_UNIT_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        offerRecordEditForm.add(new NumberTextField<BigDecimal>(ITEM_LENGTH_ID).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        offerRecordEditForm.add(new TextField<String>(ITEM_LENGTH_UNIT_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        offerRecordEditForm.add(new NumberTextField<BigDecimal>(ITEM_WEIGHT_ID).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        offerRecordEditForm.add(new TextField<String>(ITEM_WEIGHT_UNIT_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        offerRecordEditForm.add(new NumberTextField<BigDecimal>(ITEM_WIDTH_ID).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        offerRecordEditForm.add(new TextField<String>(ITEM_WIDTH_UNIT_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        offerRecordEditForm.add(saveAjaxButton.setOutputMarkupId(true));
        add(offerRecordEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String OFFER_RECORD_EDIT_TABLE_ID = "offerRecordEditTable";

    private static final String OFFER_RECORD_EDIT_FRAGMENT_MARKUP_ID = "offerRecordEditFragment";

    private static final String OFFER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID = "offerRecordViewOrEditFragment";

    private static final long serialVersionUID = 3709791409078428685L;

    private final OfferRecordEditTable offerRecordEditTable;

    public OfferRecordEditFragment() {
      super(OFFER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID, OFFER_RECORD_EDIT_FRAGMENT_MARKUP_ID, OfferRecordViewOrEditPanel.this, OfferRecordViewOrEditPanel.this.getDefaultModel());
      offerRecordEditTable = new OfferRecordEditTable(OFFER_RECORD_EDIT_TABLE_ID, (IModel<Offer>) OfferRecordEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(offerRecordEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class OfferRecordViewFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class OfferRecordViewTable extends WebMarkupContainer {

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

      private static final String QUANTITY_ID = "quantity";

      private static final String ITEM_URL_ID = "itemUrl";

      private static final String DESCRIPTION_ID = "description";

      private static final String OPTION_ID = "option";

      private static final String NAME_ID = "name";

      private static final String PRODUCT_NUMBER_ID = "productNumber";

      private static final String OFFER_RECORD_ID_ID = "offerRecordId";

      private static final String OFFER_RECORD_VIEW_FORM_COMPONENT_ID = "offerRecordViewForm";

      private static final long serialVersionUID = 101774853102549233L;

      private final BootstrapForm<OfferRecord> offerRecordViewForm;

      public OfferRecordViewTable(final String id, final IModel<Offer> model) {
        super(id, model);
        offerRecordViewForm =
            new BootstrapForm<OfferRecord>(OFFER_RECORD_VIEW_FORM_COMPONENT_ID, new CompoundPropertyModel<OfferRecord>(OfferRecordViewOrEditPanel.this.selectedModel));
      }

      @Override
      protected void onInitialize() {
        offerRecordViewForm.add(new RequiredTextField<String>(OFFER_RECORD_ID_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new RequiredTextField<String>(PRODUCT_NUMBER_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new RequiredTextField<String>(NAME_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new TextField<String>(OPTION_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new TextArea<String>(DESCRIPTION_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new UrlTextField(ITEM_URL_ID, new PropertyModel<String>(offerRecordViewForm.getDefaultModelObject(), ITEM_URL_ID)).setOutputMarkupId(true));
        offerRecordViewForm.add(new NumberTextField<Integer>(QUANTITY_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new NumberTextField<Integer>(AMOUNT_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new NumberTextField<Integer>(DISCOUNT_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new NumberTextField<Integer>(SHIPPING_COST_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new NumberTextField<Integer>(TAX_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new NumberTextField<Integer>(ITEM_HEIGHT_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new TextField<String>(ITEM_HEIGHT_UNIT_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new NumberTextField<Integer>(ITEM_LENGTH_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new TextField<String>(ITEM_LENGTH_UNIT_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new NumberTextField<Integer>(ITEM_WEIGHT_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new TextField<String>(ITEM_WEIGHT_UNIT_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new NumberTextField<Integer>(ITEM_WIDTH_ID).setOutputMarkupId(true));
        offerRecordViewForm.add(new TextField<String>(ITEM_WIDTH_UNIT_ID).setOutputMarkupId(true));
        add(offerRecordViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String OFFER_RECORD_VIEW_TABLE_ID = "offerRecordViewTable";

    private static final String OFFER_RECORD_VIEW_FRAGMENT_MARKUP_ID = "offerRecordViewFragment";

    private static final String OFFER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID = "offerRecordViewOrEditFragment";

    private static final long serialVersionUID = 3709791409078428685L;

    private final OfferRecordViewTable offerRecordViewTable;

    public OfferRecordViewFragment() {
      super(OFFER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID, OFFER_RECORD_VIEW_FRAGMENT_MARKUP_ID, OfferRecordViewOrEditPanel.this, OfferRecordViewOrEditPanel.this.getDefaultModel());
      offerRecordViewTable = new OfferRecordViewTable(OFFER_RECORD_VIEW_TABLE_ID, (IModel<Offer>) OfferRecordViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(offerRecordViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = -7002701340914975498L;

  private IModel<OfferRecord> selectedModel;

  public OfferRecordViewOrEditPanel(final String id, final IModel<Offer> model) {
    super(id, model);
    selectedModel = Model.of(new OfferRecord());
  }

  public void setSelectedModel(final IModel<OfferRecord> selectedModel) {
    this.selectedModel = selectedModel;
  }
}
