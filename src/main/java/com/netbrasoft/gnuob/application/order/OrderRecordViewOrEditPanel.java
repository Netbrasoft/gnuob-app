package com.netbrasoft.gnuob.application.order;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import javax.xml.datatype.XMLGregorianCalendar;

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
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;

import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.OrderRecord;
import com.netbrasoft.gnuob.api.generic.converter.XmlGregorianCalendarConverter;
import com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants;
import com.netbrasoft.gnuob.application.security.AppRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.LoadingBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePicker;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class OrderRecordViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class OrderRecordEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class OrderRecordEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class SaveAjaxButton extends BootstrapAjaxButton {

        private static final long serialVersionUID = 2695394292963384938L;

        public SaveAjaxButton(final String id, final IModel<String> model, final Form<OrderRecord> form, final Buttons.Type type) {
          super(id, model, form, type);
          setSize(Buttons.Size.Small);
          add(new LoadingBehavior(Model.of(OrderRecordViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY))));
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          target.add(form.add(new TooltipValidation()));
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(OrderRecordViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          if (((OrderRecord) form.getDefaultModelObject()).getId() == 0) {
            ((Order) OrderRecordViewOrEditPanel.this.getDefaultModelObject()).getRecords().add((OrderRecord) form.getDefaultModelObject());
          }
          target.add(form.setOutputMarkupId(true));
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(OrderRecordViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY)))));
          target.add(OrderRecordViewOrEditPanel.this.getParent().setOutputMarkupId(true));
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

      private static final String DD_MM_YYYY_FORMAT = "dd-MM-YYYY";

      private static final String DELIVERY_DATE_ID = "deliveryDate";

      private static final String ORDER_RECORD_ID_ID = "orderRecordId";

      private static final String SAVE_ID = "save";

      private static final String ORDER_RECORD_EDIT_FORM_COMPONENT_ID = "orderRecordEditForm";

      private static final long serialVersionUID = 8716967873428915351L;

      private final BootstrapForm<OrderRecord> orderRecordEditForm;

      private final SaveAjaxButton saveAjaxButton;

      public OrderRecordEditTable(final String id, final IModel<Order> model) {
        super(id, model);
        orderRecordEditForm =
            new BootstrapForm<OrderRecord>(ORDER_RECORD_EDIT_FORM_COMPONENT_ID, new CompoundPropertyModel<OrderRecord>(OrderRecordViewOrEditPanel.this.selectedModel));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID, Model.of(OrderRecordViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY)), orderRecordEditForm,
            Buttons.Type.Primary);
      }

      @Override
      protected void onInitialize() {
        orderRecordEditForm.add(new RequiredTextField<String>(ORDER_RECORD_ID_ID).add(StringValidator.maximumLength(64)).setOutputMarkupId(true));
        orderRecordEditForm.add(new DatetimePicker(DELIVERY_DATE_ID, new DatetimePickerConfig().useLocale(Locale.getDefault().toString()).withFormat(DD_MM_YYYY_FORMAT)) {

          private static final long serialVersionUID = 1209354725150726556L;

          @Override
          public <C> IConverter<C> getConverter(final Class<C> type) {
            if (XMLGregorianCalendar.class.isAssignableFrom(type)) {
              return (IConverter<C>) new XmlGregorianCalendarConverter();
            } else {
              return super.getConverter(type);
            }
          }
        }.setOutputMarkupId(true));
        orderRecordEditForm.add(new RequiredTextField<String>(PRODUCT_NUMBER_ID).add(StringValidator.maximumLength(64)).setOutputMarkupId(true));
        orderRecordEditForm.add(new RequiredTextField<String>(NAME_ID).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        orderRecordEditForm.add(new TextField<String>(OPTION_ID).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        orderRecordEditForm.add(new TextArea<String>(DESCRIPTION_ID).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        orderRecordEditForm.add(new UrlTextField(ITEM_URL_ID, new PropertyModel<String>(orderRecordEditForm.getDefaultModelObject(), ITEM_URL_ID)).setOutputMarkupId(true));
        orderRecordEditForm
            .add(new NumberTextField<BigInteger>(QUANTITY_ID).setRequired(true).add(RangeValidator.range(BigDecimal.ZERO, BigDecimal.valueOf(50))).setOutputMarkupId(true));
        orderRecordEditForm.add(new NumberTextField<BigDecimal>(AMOUNT_ID).setRequired(true).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        orderRecordEditForm.add(new NumberTextField<BigDecimal>(DISCOUNT_ID).setRequired(true).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        orderRecordEditForm.add(new NumberTextField<BigDecimal>(SHIPPING_COST_ID).setRequired(true).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        orderRecordEditForm.add(new NumberTextField<BigDecimal>(TAX_ID).setRequired(true).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        orderRecordEditForm.add(new NumberTextField<BigDecimal>(ITEM_HEIGHT_ID).setOutputMarkupId(true));
        orderRecordEditForm.add(new TextField<String>(ITEM_HEIGHT_UNIT_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        orderRecordEditForm.add(new NumberTextField<BigDecimal>(ITEM_LENGTH_ID).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        orderRecordEditForm.add(new TextField<String>(ITEM_LENGTH_UNIT_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        orderRecordEditForm.add(new NumberTextField<BigDecimal>(ITEM_WEIGHT_ID).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        orderRecordEditForm.add(new TextField<String>(ITEM_WEIGHT_UNIT_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        orderRecordEditForm.add(new NumberTextField<BigDecimal>(ITEM_WIDTH_ID).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        orderRecordEditForm.add(new TextField<String>(ITEM_WIDTH_UNIT_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        orderRecordEditForm.add(saveAjaxButton.setOutputMarkupId(true));
        add(orderRecordEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }


    private static final long serialVersionUID = 3709791409078428685L;

    private final OrderRecordEditTable orderRecordEditTable;

    public OrderRecordEditFragment() {
      super("orderRecordViewOrEditFragment", "orderRecordEditFragment", OrderRecordViewOrEditPanel.this, OrderRecordViewOrEditPanel.this.getDefaultModel());
      orderRecordEditTable = new OrderRecordEditTable("orderRecordEditTable", (IModel<Order>) OrderRecordEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(orderRecordEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class OrderRecordViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class OrderRecordViewTable extends WebMarkupContainer {

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

      private static final String DD_MM_YYYY_FORMAT = "dd-MM-YYYY";

      private static final String DELIVERY_DATE_ID = "deliveryDate";

      private static final String ORDER_RECORD_ID_ID = "orderRecordId";

      private static final String ORDER_RECORD_VIEW_FORM_COMPONENT_ID = "orderRecordViewForm";

      private static final long serialVersionUID = 5781878310530984048L;

      private final BootstrapForm<OrderRecord> orderRecorViewForm;

      public OrderRecordViewTable(final String id, final IModel<Order> model) {
        super(id, model);
        orderRecorViewForm =
            new BootstrapForm<OrderRecord>(ORDER_RECORD_VIEW_FORM_COMPONENT_ID, new CompoundPropertyModel<OrderRecord>(OrderRecordViewOrEditPanel.this.selectedModel));
      }

      @Override
      protected void onInitialize() {
        orderRecorViewForm.add(new RequiredTextField<String>(ORDER_RECORD_ID_ID).setOutputMarkupId(true));
        orderRecorViewForm.add(new DatetimePicker(DELIVERY_DATE_ID, new DatetimePickerConfig().useLocale(Locale.getDefault().toString()).withFormat(DD_MM_YYYY_FORMAT)) {

          private static final long serialVersionUID = 1209354725150726556L;

          @Override
          public <C> IConverter<C> getConverter(final Class<C> type) {
            if (XMLGregorianCalendar.class.isAssignableFrom(type)) {
              return (IConverter<C>) new XmlGregorianCalendarConverter();
            } else {
              return super.getConverter(type);
            }
          }
        }.setOutputMarkupId(true));
        orderRecorViewForm.add(new RequiredTextField<String>(PRODUCT_NUMBER_ID).setOutputMarkupId(true));
        orderRecorViewForm.add(new RequiredTextField<String>(NAME_ID).setOutputMarkupId(true));
        orderRecorViewForm.add(new TextField<String>(OPTION_ID).setOutputMarkupId(true));
        orderRecorViewForm.add(new TextArea<String>(DESCRIPTION_ID).setOutputMarkupId(true));
        orderRecorViewForm.add(new UrlTextField(ITEM_URL_ID, new PropertyModel<String>(orderRecorViewForm.getDefaultModelObject(), ITEM_URL_ID)).setOutputMarkupId(true));
        orderRecorViewForm.add(new NumberTextField<Integer>(QUANTITY_ID).setRequired(true).setOutputMarkupId(true));
        orderRecorViewForm.add(new NumberTextField<Integer>(AMOUNT_ID).setRequired(true).setOutputMarkupId(true));
        orderRecorViewForm.add(new NumberTextField<Integer>(DISCOUNT_ID).setRequired(true).setOutputMarkupId(true));
        orderRecorViewForm.add(new NumberTextField<Integer>(SHIPPING_COST_ID).setRequired(true).setOutputMarkupId(true));
        orderRecorViewForm.add(new NumberTextField<Integer>(TAX_ID).setRequired(true).setOutputMarkupId(true));
        orderRecorViewForm.add(new NumberTextField<Integer>(ITEM_HEIGHT_ID).setOutputMarkupId(true));
        orderRecorViewForm.add(new TextField<String>(ITEM_HEIGHT_UNIT_ID).setOutputMarkupId(true));
        orderRecorViewForm.add(new NumberTextField<Integer>(ITEM_LENGTH_ID).setOutputMarkupId(true));
        orderRecorViewForm.add(new TextField<String>(ITEM_LENGTH_UNIT_ID).setOutputMarkupId(true));
        orderRecorViewForm.add(new NumberTextField<Integer>(ITEM_WEIGHT_ID).setOutputMarkupId(true));
        orderRecorViewForm.add(new TextField<String>(ITEM_WEIGHT_UNIT_ID).setOutputMarkupId(true));
        orderRecorViewForm.add(new NumberTextField<Integer>(ITEM_WIDTH_ID).setOutputMarkupId(true));
        orderRecorViewForm.add(new TextField<String>(ITEM_WIDTH_UNIT_ID).setOutputMarkupId(true));
        add(orderRecorViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String ORDER_RECORD_VIEW_TABLE_ID = "orderRecordViewTable";

    private static final String ORDER_RECORD_VIEW_FRAGMENT_MARKUP_ID = "orderRecordViewFragment";

    private static final String ORDER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID = "orderRecordViewOrEditFragment";

    private static final long serialVersionUID = 6927997909191615786L;

    private final OrderRecordViewTable orderRecordViewTable;

    public OrderRecordViewFragment() {
      super(ORDER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID, ORDER_RECORD_VIEW_FRAGMENT_MARKUP_ID, OrderRecordViewOrEditPanel.this, OrderRecordViewOrEditPanel.this.getDefaultModel());
      orderRecordViewTable = new OrderRecordViewTable(ORDER_RECORD_VIEW_TABLE_ID, (IModel<Order>) OrderRecordViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(orderRecordViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = -7002701340914975498L;

  private IModel<OrderRecord> selectedModel;

  public OrderRecordViewOrEditPanel(final String id, final IModel<Order> model) {
    super(id, model);
    selectedModel = Model.of(new OrderRecord());
  }

  public void setSelectedModel(final IModel<OrderRecord> selectedModel) {
    this.selectedModel = selectedModel;
  }
}
