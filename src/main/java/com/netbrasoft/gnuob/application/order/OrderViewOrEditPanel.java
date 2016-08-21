package com.netbrasoft.gnuob.application.order;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.ORDER_DATA_PROVIDER_NAME;

import java.math.BigDecimal;
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
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
import com.netbrasoft.gnuob.api.generic.converter.XmlGregorianCalendarConverter;
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
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePicker;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class OrderViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class OrderEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class OrderEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class CancelAjaxLink extends BootstrapAjaxLink<Order> {

        private static final long serialVersionUID = 4267535261864907719L;

        public CancelAjaxLink(final String id, final IModel<Order> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          OrderViewOrEditPanel.this.removeAll();
          if (((Order) CancelAjaxLink.this.getDefaultModelObject()).getId() > 0) {
            OrderViewOrEditPanel.this
                .setDefaultModelObject(orderDataProvider.findById((Order) CancelAjaxLink.this.getDefaultModelObject()));
          }
          target.add(OrderViewOrEditPanel.this.add(OrderViewOrEditPanel.this.new OrderViewFragment())
              .setOutputMarkupPlaceholderTag(true));
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class SaveAjaxButton extends BootstrapAjaxButton {

        private static final long serialVersionUID = 2695394292963384938L;

        public SaveAjaxButton(final String id, final IModel<String> model, final Form<?> form,
            final Buttons.Type type) {
          super(id, model, form, type);
          setSize(Buttons.Size.Small);
          add(new LoadingBehavior(Model
              .of(OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY))));
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          form.add(new TooltipValidation());
          target.add(form);
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
              .of(OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          try {
            if (((Order) form.getDefaultModelObject()).getId() == 0) {
              OrderEditTable.this.setDefaultModelObject(
                  orderDataProvider.findById(orderDataProvider.persist((Order) form.getDefaultModelObject())));
            } else {
              OrderEditTable.this.setDefaultModelObject(
                  orderDataProvider.findById(orderDataProvider.merge((Order) form.getDefaultModelObject())));
            }
          } catch (final RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
            feedbackPanel.warn(e.getLocalizedMessage());
            target.add(feedbackPanel.setOutputMarkupId(true));
            target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
                .of(OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
            OrderViewOrEditPanel.this.removeAll();
            target.add(OrderViewOrEditPanel.this.add(OrderViewOrEditPanel.this.new OrderViewFragment())
                .setOutputMarkupId(true));
          }
        }
      }

      private static final String INVOICE_ADDRESS_PHONE_ID = "invoice.address.phone";

      private static final String INVOICE_ADDRESS_INTERNATIONAL_STATE_AND_CITY_ID =
          "invoice.address.internationalStateAndCity";

      private static final String INVOICE_ADDRESS_INTERNATIONAL_STREET_ID = "invoice.address.internationalStreet";

      private static final String INVOICE_ADDRESS_COUNTRY_NAME_ID = "invoice.address.countryName";

      private static final String INVOICE_ADDRESS_STATE_OR_PROVINCE_ID = "invoice.address.stateOrProvince";

      private static final String INVOICE_ADDRESS_CITY_NAME_ID = "invoice.address.cityName";

      private static final String INVOICE_ADDRESS_DISTRICT_ID = "invoice.address.district";

      private static final String INVOICE_ADDRESS_COMPLEMENT_ID = "invoice.address.complement";

      private static final String INVOICE_ADDRESS_STREET2_ID = "invoice.address.street2";

      private static final String INVOICE_ADDRESS_STREET1_ID = "invoice.address.street1";

      private static final String INVOICE_ADDRESS_COUNTRY_ID = "invoice.address.country";

      private static final String INVOICE_ADDRESS_NUMBER_ID = "invoice.address.number";

      private static final String INVOICE_ADDRESS_POSTAL_CODE_ID = "invoice.address.postalCode";

      private static final String INVOICE_INVOICE_ID_ID = "invoice.invoiceId";

      private static final String SHIPMENT_ADDRESS_PHONE_ID = "shipment.address.phone";

      private static final String SHIPMENT_ADDRESS_INTERNATIONAL_STATE_AND_CITY_ID =
          "shipment.address.internationalStateAndCity";

      private static final String SHIPMENT_ADDRESS_INTERNATIONAL_STREET_ID = "shipment.address.internationalStreet";

      private static final String SHIPMENT_ADDRESS_COUNTRY_NAME_ID = "shipment.address.countryName";

      private static final String SHIPMENT_ADDRESS_STATE_OR_PROVINCE_ID = "shipment.address.stateOrProvince";

      private static final String SHIPMENT_ADDRESS_CITY_NAME_ID = "shipment.address.cityName";

      private static final String SHIPMENT_ADDRESS_DISTRICT_ID = "shipment.address.district";

      private static final String SHIPMENT_ADDRESS_COMPLEMENT_ID = "shipment.address.complement";

      private static final String SHIPMENT_ADDRESS_STREET2_ID = "shipment.address.street2";

      private static final String SHIPMENT_ADDRESS_STREET1_ID = "shipment.address.street1";

      private static final String SHIPMENT_ADDRESS_COUNTRY_ID = "shipment.address.country";

      private static final String SHIPMENT_ADDRESS_NUMBER_ID = "shipment.address.number";

      private static final String _0_9_5_0_9_3_PATTERN = "([0-9]){5}([-])([0-9]){3}";

      private static final String SHIPMENT_ADDRESS_POSTAL_CODE_ID = "shipment.address.postalCode";

      private static final String SHIPMENT_SHIPMENT_TYPE_ID = "shipment.shipmentType";

      private static final String GIFT_MESSAGE_ID = "giftMessage";

      private static final String GIFT_WRAP_NAME_ID = "giftWrapName";

      private static final String GIFT_WRAP_ENABLE_ID = "giftWrapEnable";

      private static final String GIFT_RECEIPT_ENABLE_ID = "giftReceiptEnable";

      private static final String GIFT_MESSAGE_ENABLE_ID = "giftMessageEnable";

      private static final String ORDER_TOTAL_ID = "orderTotal";

      private static final String TAX_TOTAL_ID = "taxTotal";

      private static final String SHIPPING_DISCOUNT_ID = "shippingDiscount";

      private static final String INSURANCE_TOTAL_ID = "insuranceTotal";

      private static final String SHIPPING_TOTAL_ID = "shippingTotal";

      private static final String HANDLING_TOTAL_ID = "handlingTotal";

      private static final String DISCOUNT_TOTAL_ID = "discountTotal";

      private static final String EXTRA_AMOUNT_ID = "extraAmount";

      private static final String GIFT_WRAP_AMOUNT_ID = "giftWrapAmount";

      private static final String INSURANCE_OPTION_OFFERED_ID = "insuranceOptionOffered";

      private static final String NOTE_TEXT_ID = "noteText";

      private static final String NOTE_ID = "note";

      private static final String CUSTOM_ID = "custom";

      private static final String ORDER_DESCRIPTION_ID = "orderDescription";

      private static final String ITEM_TOTAL_ID = "itemTotal";

      private static final String CHECKOUT_STATUS_ID = "checkoutStatus";

      private static final String BILLING_AGREEMENT_ID_ID = "billingAgreementId";

      private static final String TRANSACTION_ID_ID = "transactionId";

      private static final String TOKEN_ID = "token";

      private static final String DD_MM_YYYY_FORMAT = "dd-MM-YYYY";

      private static final String ORDER_DATE_ID = "orderDate";

      private static final String CONTRACT_CONTRACT_ID_ID = "contract.contractId";

      private static final String ORDER_ID_ID = "orderId";

      private static final String ORDER_INVOICE_PAYMENT_PANEL_ID = "orderInvoicePaymentPanel";

      private static final String ORDER_RECORD_PANEL_ID = "orderRecordPanel";

      private static final String FEEDBACK_ID = "feedback";

      private static final String SAVE_ID = "save";

      private static final String CANCEL_ID = "cancel";

      private static final String ORDER_EDIT_FORM_COMPONENT_ID = "orderEditForm";

      private static final long serialVersionUID = 6328203994858830738L;

      private final BootstrapForm<Order> orderEditForm;

      private final CancelAjaxLink cancelAjaxLink;

      private final SaveAjaxButton saveAjaxButton;

      private final NotificationPanel feedbackPanel;

      private final OrderRecordPanel orderRecordPanel;

      private final OrderInvoicePaymentPanel orderInvoicePaymentPanel;

      public OrderEditTable(final String id, final IModel<Order> model) {
        super(id, model);
        orderEditForm = new BootstrapForm<Order>(ORDER_EDIT_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Order>((IModel<Order>) OrderEditTable.this.getDefaultModel()));
        cancelAjaxLink = new CancelAjaxLink(CANCEL_ID, model, Buttons.Type.Default,
            Model.of(OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID,
            Model.of(OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)),
            orderEditForm, Buttons.Type.Primary);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
        orderRecordPanel =
            new OrderRecordPanel(ORDER_RECORD_PANEL_ID, (IModel<Order>) OrderEditTable.this.getDefaultModel());
        orderInvoicePaymentPanel = new OrderInvoicePaymentPanel(ORDER_INVOICE_PAYMENT_PANEL_ID,
            (IModel<Order>) OrderEditTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        orderEditForm.add(
            new RequiredTextField<String>(ORDER_ID_ID).add(StringValidator.maximumLength(64)).setOutputMarkupId(true));
        orderEditForm.add(new RequiredTextField<String>(CONTRACT_CONTRACT_ID_ID).add(StringValidator.maximumLength(127))
            .setOutputMarkupId(true));
        orderEditForm.add(new DatetimePicker(ORDER_DATE_ID,
            new DatetimePickerConfig().useLocale(Locale.getDefault().toString()).withFormat(DD_MM_YYYY_FORMAT)) {

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
        orderEditForm
            .add(new TextField<String>(TOKEN_ID).add(StringValidator.maximumLength(40)).setOutputMarkupId(true));
        orderEditForm.add(
            new TextField<String>(TRANSACTION_ID_ID).add(StringValidator.maximumLength(64)).setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(BILLING_AGREEMENT_ID_ID).add(StringValidator.maximumLength(20))
            .setOutputMarkupId(true));
        orderEditForm.add(
            new TextField<String>(CHECKOUT_STATUS_ID).add(StringValidator.maximumLength(255)).setOutputMarkupId(true));
        orderEditForm.add(new NumberTextField<BigDecimal>(ITEM_TOTAL_ID).add(RangeValidator.minimum(BigDecimal.ZERO))
            .setOutputMarkupId(true));
        orderEditForm.add(
            new TextArea<String>(ORDER_DESCRIPTION_ID).add(StringValidator.maximumLength(127)).setOutputMarkupId(true));
        orderEditForm
            .add(new TextField<String>(CUSTOM_ID).add(StringValidator.maximumLength(255)).setOutputMarkupId(true));
        orderEditForm
            .add(new TextField<String>(NOTE_ID).add(StringValidator.maximumLength(165)).setOutputMarkupId(true));
        orderEditForm
            .add(new TextArea<String>(NOTE_TEXT_ID).add(StringValidator.maximumLength(255)).setOutputMarkupId(true));
        orderEditForm.add(new BootstrapCheckbox(INSURANCE_OPTION_OFFERED_ID).setOutputMarkupId(true));
        orderEditForm.add(new NumberTextField<BigDecimal>(GIFT_WRAP_AMOUNT_ID)
            .add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        orderEditForm.add(new NumberTextField<BigDecimal>(EXTRA_AMOUNT_ID).setRequired(true)
            .add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        orderEditForm.add(new NumberTextField<BigDecimal>(DISCOUNT_TOTAL_ID)
            .add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        orderEditForm.add(new NumberTextField<BigDecimal>(HANDLING_TOTAL_ID).setRequired(true)
            .add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        orderEditForm.add(new NumberTextField<BigDecimal>(SHIPPING_TOTAL_ID)
            .add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        orderEditForm.add(new NumberTextField<BigDecimal>(INSURANCE_TOTAL_ID).setRequired(true)
            .add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        orderEditForm.add(new NumberTextField<BigDecimal>(SHIPPING_DISCOUNT_ID).setRequired(true)
            .add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        orderEditForm.add(new NumberTextField<BigDecimal>(TAX_TOTAL_ID).add(RangeValidator.minimum(BigDecimal.ZERO))
            .setOutputMarkupId(true));
        orderEditForm.add(new NumberTextField<BigDecimal>(ORDER_TOTAL_ID).add(RangeValidator.minimum(BigDecimal.ZERO))
            .setOutputMarkupId(true));
        orderEditForm.add(new BootstrapCheckbox(GIFT_MESSAGE_ENABLE_ID).setOutputMarkupId(true));
        orderEditForm.add(new BootstrapCheckbox(GIFT_RECEIPT_ENABLE_ID).setOutputMarkupId(true));
        orderEditForm.add(new BootstrapCheckbox(GIFT_WRAP_ENABLE_ID).setOutputMarkupId(true));
        orderEditForm.add(
            new TextField<String>(GIFT_WRAP_NAME_ID).add(StringValidator.maximumLength(25)).setOutputMarkupId(true));
        orderEditForm
            .add(new TextArea<String>(GIFT_MESSAGE_ID).add(StringValidator.maximumLength(150)).setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(SHIPMENT_SHIPMENT_TYPE_ID).add(StringValidator.maximumLength(128))
            .setOutputMarkupId(true));
        orderEditForm
            .add(new RequiredTextField<String>(SHIPMENT_ADDRESS_POSTAL_CODE_ID)
                .setLabel(Model
                    .of(OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.POSTAL_CODE_MESSAGE_KEY)))
                .add(new PatternValidator(_0_9_5_0_9_3_PATTERN)).add(StringValidator.maximumLength(20))
                .setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(SHIPMENT_ADDRESS_NUMBER_ID).add(StringValidator.maximumLength(10))
            .setOutputMarkupId(true));
        orderEditForm
            .add(new RequiredTextField<String>(SHIPMENT_ADDRESS_COUNTRY_ID, Model.of("Brasil"))
                .setLabel(Model
                    .of(OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.COUNTRY_NAME_MESSAGE_KEY)))
                .add(StringValidator.maximumLength(40)).setEnabled(false).setOutputMarkupId(true));
        orderEditForm
            .add(
                new RequiredTextField<String>(SHIPMENT_ADDRESS_STREET1_ID)
                    .setLabel(Model
                        .of(OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.STREET1_MESSAGE_KEY)))
                    .add(StringValidator.maximumLength(100)).setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(SHIPMENT_ADDRESS_STREET2_ID).add(StringValidator.maximumLength(100))
            .setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(SHIPMENT_ADDRESS_COMPLEMENT_ID).add(StringValidator.maximumLength(40))
            .setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(SHIPMENT_ADDRESS_DISTRICT_ID).add(StringValidator.maximumLength(40))
            .setOutputMarkupId(true));
        orderEditForm.add(new RequiredTextField<String>(SHIPMENT_ADDRESS_CITY_NAME_ID)
            .setLabel(
                Model.of(OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.CITY_NAME_MESSAGE_KEY)))
            .add(StringValidator.maximumLength(40)).setOutputMarkupId(true));
        orderEditForm.add(
            new RequiredTextField<String>(SHIPMENT_ADDRESS_STATE_OR_PROVINCE_ID).add(StringValidator.maximumLength(2))
                .setLabel(Model.of(
                    OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.STATE_OR_PROVINCE_MESSAGE_KEY)))
                .setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(SHIPMENT_ADDRESS_COUNTRY_NAME_ID).add(StringValidator.maximumLength(40))
            .setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(SHIPMENT_ADDRESS_INTERNATIONAL_STREET_ID)
            .add(StringValidator.maximumLength(40)).setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(SHIPMENT_ADDRESS_INTERNATIONAL_STATE_AND_CITY_ID)
            .add(StringValidator.maximumLength(80)).setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(SHIPMENT_ADDRESS_PHONE_ID).add(StringValidator.maximumLength(20))
            .setOutputMarkupId(true));
        orderEditForm.add(orderRecordPanel.add(orderRecordPanel.new OrderRecordEditFragment()).setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(INVOICE_INVOICE_ID_ID));
        orderEditForm
            .add(new RequiredTextField<String>(INVOICE_ADDRESS_POSTAL_CODE_ID)
                .setLabel(Model
                    .of(OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.POSTAL_CODE_MESSAGE_KEY)))
                .add(new PatternValidator(_0_9_5_0_9_3_PATTERN)).add(StringValidator.maximumLength(20))
                .setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(INVOICE_ADDRESS_NUMBER_ID).add(StringValidator.maximumLength(10))
            .setOutputMarkupId(true));
        orderEditForm
            .add(new TextField<String>(INVOICE_ADDRESS_COUNTRY_ID, Model.of("Brasil"))
                .setLabel(Model
                    .of(OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.COUNTRY_NAME_MESSAGE_KEY)))
                .add(StringValidator.maximumLength(40)).setEnabled(false).setOutputMarkupId(true));
        orderEditForm
            .add(
                new RequiredTextField<String>(INVOICE_ADDRESS_STREET1_ID)
                    .setLabel(Model
                        .of(OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.STREET1_MESSAGE_KEY)))
                    .add(StringValidator.maximumLength(100)).setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(INVOICE_ADDRESS_STREET2_ID).add(StringValidator.maximumLength(100))
            .setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(INVOICE_ADDRESS_COMPLEMENT_ID).add(StringValidator.maximumLength(40))
            .setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(INVOICE_ADDRESS_DISTRICT_ID).add(StringValidator.maximumLength(40))
            .setOutputMarkupId(true));
        orderEditForm.add(new RequiredTextField<String>(INVOICE_ADDRESS_CITY_NAME_ID)
            .setLabel(
                Model.of(OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.CITY_NAME_MESSAGE_KEY)))
            .add(StringValidator.maximumLength(40)).setOutputMarkupId(true));
        orderEditForm.add(new RequiredTextField<String>(INVOICE_ADDRESS_STATE_OR_PROVINCE_ID)
            .setLabel(Model
                .of(OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.STATE_OR_PROVINCE_MESSAGE_KEY)))
            .setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(INVOICE_ADDRESS_COUNTRY_NAME_ID).add(StringValidator.maximumLength(40))
            .setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(INVOICE_ADDRESS_INTERNATIONAL_STREET_ID)
            .add(StringValidator.maximumLength(40)).setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(INVOICE_ADDRESS_INTERNATIONAL_STATE_AND_CITY_ID)
            .add(StringValidator.maximumLength(80)).setOutputMarkupId(true));
        orderEditForm.add(new TextField<String>(INVOICE_ADDRESS_PHONE_ID).add(StringValidator.maximumLength(20))
            .setOutputMarkupId(true));
        orderEditForm.add(orderInvoicePaymentPanel.add(orderInvoicePaymentPanel.new OrderInvoicePaymentEditFragment())
            .setOutputMarkupId(true));
        add(orderEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(cancelAjaxLink.setOutputMarkupId(true));
        add(saveAjaxButton.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String ORDER_EDIT_TABLE_ID = "orderEditTable";

    private static final String ORDER_EDIT_FRAGMENT_MARKUP_ID = "orderEditFragment";

    private static final String ORDER_VIEW_OR_EDIT_FRAGMENT_ID = "orderViewOrEditFragment";

    private static final long serialVersionUID = -5645656866901827543L;

    private final WebMarkupContainer orderEditTable;

    public OrderEditFragment() {
      super(ORDER_VIEW_OR_EDIT_FRAGMENT_ID, ORDER_EDIT_FRAGMENT_MARKUP_ID, OrderViewOrEditPanel.this,
          OrderViewOrEditPanel.this.getDefaultModel());
      orderEditTable =
          new OrderEditTable(ORDER_EDIT_TABLE_ID, (IModel<Order>) OrderEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(orderEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class OrderViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class OrderViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class EditAjaxLink extends BootstrapAjaxLink<Order> {

        private static final long serialVersionUID = 4267535261864907719L;

        public EditAjaxLink(final String id, final IModel<Order> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.edit);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          OrderViewOrEditPanel.this.removeAll();
          target.add(
              OrderViewOrEditPanel.this.add(OrderViewOrEditPanel.this.new OrderEditFragment().setOutputMarkupId(true)));
        }
      }

      private static final String INVOICE_ADDRESS_PHONE_ID = "invoice.address.phone";

      private static final String INVOICE_ADDRESS_INTERNATIONAL_STATE_AND_CITY_ID =
          "invoice.address.internationalStateAndCity";

      private static final String INVOICE_ADDRESS_INTERNATIONAL_STREET_ID = "invoice.address.internationalStreet";

      private static final String INVOICE_ADDRESS_COUNTRY_NAME_ID = "invoice.address.countryName";

      private static final String INVOICE_ADDRESS_STATE_OR_PROVINCE_ID = "invoice.address.stateOrProvince";

      private static final String INVOICE_ADDRESS_CITY_NAME_ID = "invoice.address.cityName";

      private static final String INVOICE_ADDRESS_DISTRICT_ID = "invoice.address.district";

      private static final String INVOICE_ADDRESS_COMPLEMENT_ID = "invoice.address.complement";

      private static final String INVOICE_ADDRESS_STREET2_ID = "invoice.address.street2";

      private static final String INVOICE_ADDRESS_STREET1_ID = "invoice.address.street1";

      private static final String INVOICE_ADDRESS_COUNTRY_ID = "invoice.address.country";

      private static final String INVOICE_ADDRESS_NUMBER_ID = "invoice.address.number";

      private static final String INVOICE_ADDRESS_POSTAL_CODE_ID = "invoice.address.postalCode";

      private static final String INVOICE_INVOICE_ID_ID = "invoice.invoiceId";

      private static final String SHIPMENT_ADDRESS_PHONE_ID = "shipment.address.phone";

      private static final String SHIPMENT_ADDRESS_INTERNATIONAL_STATE_AND_CITY_ID =
          "shipment.address.internationalStateAndCity";

      private static final String SHIPMENT_ADDRESS_INTERNATIONAL_STREET_ID = "shipment.address.internationalStreet";

      private static final String SHIPMENT_ADDRESS_COUNTRY_NAME_ID = "shipment.address.countryName";

      private static final String SHIPMENT_ADDRESS_STATE_OR_PROVINCE_ID = "shipment.address.stateOrProvince";

      private static final String SHIPMENT_ADDRESS_CITY_NAME_ID = "shipment.address.cityName";

      private static final String SHIPMENT_ADDRESS_DISTRICT_ID = "shipment.address.district";

      private static final String SHIPMENT_ADDRESS_COMPLEMENT_ID = "shipment.address.complement";

      private static final String SHIPMENT_ADDRESS_STREET2_ID = "shipment.address.street2";

      private static final String SHIPMENT_ADDRESS_STREET1_ID = "shipment.address.street1";

      private static final String SHIPMENT_ADDRESS_COUNTRY_ID = "shipment.address.country";

      private static final String SHIPMENT_ADDRESS_NUMBER_ID = "shipment.address.number";

      private static final String SHIPMENT_ADDRESS_POSTAL_CODE_ID = "shipment.address.postalCode";

      private static final String SHIPMENT_SHIPMENT_TYPE_ID = "shipment.shipmentType";

      private static final String GIFT_MESSAGE_ID = "giftMessage";

      private static final String GIFT_WRAP_NAME_ID = "giftWrapName";

      private static final String GIFT_WRAP_ENABLE_ID = "giftWrapEnable";

      private static final String GIFT_RECEIPT_ENABLE_ID = "giftReceiptEnable";

      private static final String GIFT_MESSAGE_ENABLE_ID = "giftMessageEnable";

      private static final String ORDER_TOTAL_ID = "orderTotal";

      private static final String TAX_TOTAL_ID = "taxTotal";

      private static final String SHIPPING_DISCOUNT_ID = "shippingDiscount";

      private static final String INSURANCE_TOTAL_ID = "insuranceTotal";

      private static final String SHIPPING_TOTAL_ID = "shippingTotal";

      private static final String HANDLING_TOTAL_ID = "handlingTotal";

      private static final String DISCOUNT_TOTAL_ID = "discountTotal";

      private static final String EXTRA_AMOUNT_ID = "extraAmount";

      private static final String GIFT_WRAP_AMOUNT_ID = "giftWrapAmount";

      private static final String INSURANCE_OPTION_OFFERED_ID = "insuranceOptionOffered";

      private static final String NOTE_TEXT_ID = "noteText";

      private static final String NOTE_ID = "note";

      private static final String CUSTOM_ID = "custom";

      private static final String ORDER_DESCRIPTION_ID = "orderDescription";

      private static final String ITEM_TOTAL_ID = "itemTotal";

      private static final String CHECKOUT_STATUS_ID = "checkoutStatus";

      private static final String BILLING_AGREEMENT_ID_ID = "billingAgreementId";

      private static final String TRANSACTION_ID_ID = "transactionId";

      private static final String TOKEN_ID = "token";

      private static final String DD_MM_YYYY_FORMAT = "dd-MM-YYYY";

      private static final String ORDER_DATE_ID = "orderDate";

      private static final String CONTRACT_CONTRACT_ID_ID = "contract.contractId";

      private static final String ORDER_ID_ID = "orderId";

      private static final String ORDER_INVOICE_PAYMENT_PANEL_ID = "orderInvoicePaymentPanel";

      private static final String ORDER_RECORD_PANEL_ID = "orderRecordPanel";

      private static final String EDIT_ID = "edit";

      private static final String ORDER_VIEW_FORM_COMPONENT_ID = "orderViewForm";

      private static final long serialVersionUID = 8819766347985798200L;

      private final EditAjaxLink editAjaxLink;

      private final BootstrapForm<Order> orderViewForm;

      private final OrderRecordPanel orderRecordPanel;

      private final OrderInvoicePaymentPanel orderInvoicePaymentPanel;

      public OrderViewTable(final String id, final IModel<Order> model) {
        super(id, model);
        orderViewForm = new BootstrapForm<Order>(ORDER_VIEW_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Order>((IModel<Order>) OrderViewTable.this.getDefaultModel()));
        editAjaxLink = new EditAjaxLink(EDIT_ID, model, Buttons.Type.Primary,
            Model.of(OrderViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.EDIT_MESSAGE_KEY)));
        orderRecordPanel =
            new OrderRecordPanel(ORDER_RECORD_PANEL_ID, (IModel<Order>) OrderViewTable.this.getDefaultModel());
        orderInvoicePaymentPanel = new OrderInvoicePaymentPanel(ORDER_INVOICE_PAYMENT_PANEL_ID,
            (IModel<Order>) OrderViewTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        orderViewForm.add(new RequiredTextField<String>(ORDER_ID_ID).setOutputMarkupId(true));
        orderViewForm.add(new RequiredTextField<String>(CONTRACT_CONTRACT_ID_ID).setOutputMarkupId(true));
        orderViewForm.add(new DatetimePicker(ORDER_DATE_ID,
            new DatetimePickerConfig().useLocale(Locale.getDefault().toString()).withFormat(DD_MM_YYYY_FORMAT)) {

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
        orderViewForm.add(new TextField<String>(TOKEN_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(TRANSACTION_ID_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(BILLING_AGREEMENT_ID_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(CHECKOUT_STATUS_ID).setOutputMarkupId(true));
        orderViewForm.add(new NumberTextField<BigDecimal>(ITEM_TOTAL_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextArea<String>(ORDER_DESCRIPTION_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(CUSTOM_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(NOTE_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextArea<String>(NOTE_TEXT_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(INSURANCE_OPTION_OFFERED_ID).setOutputMarkupId(true));
        orderViewForm.add(new NumberTextField<BigDecimal>(GIFT_WRAP_AMOUNT_ID).setOutputMarkupId(true));
        orderViewForm.add(new NumberTextField<BigDecimal>(EXTRA_AMOUNT_ID).setOutputMarkupId(true));
        orderViewForm.add(new NumberTextField<BigDecimal>(DISCOUNT_TOTAL_ID).setOutputMarkupId(true));
        orderViewForm.add(new NumberTextField<BigDecimal>(HANDLING_TOTAL_ID).setOutputMarkupId(true));
        orderViewForm.add(new NumberTextField<BigDecimal>(SHIPPING_TOTAL_ID).setOutputMarkupId(true));
        orderViewForm.add(new NumberTextField<BigDecimal>(INSURANCE_TOTAL_ID).setOutputMarkupId(true));
        orderViewForm.add(new NumberTextField<BigDecimal>(SHIPPING_DISCOUNT_ID).setOutputMarkupId(true));
        orderViewForm.add(new NumberTextField<BigDecimal>(TAX_TOTAL_ID).setOutputMarkupId(true));
        orderViewForm.add(new NumberTextField<BigDecimal>(ORDER_TOTAL_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(GIFT_MESSAGE_ENABLE_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(GIFT_RECEIPT_ENABLE_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(GIFT_WRAP_ENABLE_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(GIFT_WRAP_NAME_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextArea<String>(GIFT_MESSAGE_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(SHIPMENT_SHIPMENT_TYPE_ID).setOutputMarkupId(true));
        orderViewForm.add(new RequiredTextField<String>(SHIPMENT_ADDRESS_POSTAL_CODE_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(SHIPMENT_ADDRESS_NUMBER_ID).setOutputMarkupId(true));
        orderViewForm.add(
            new RequiredTextField<String>(SHIPMENT_ADDRESS_COUNTRY_ID, Model.of("Brasil")).setOutputMarkupId(true));
        orderViewForm.add(new RequiredTextField<String>(SHIPMENT_ADDRESS_STREET1_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(SHIPMENT_ADDRESS_STREET2_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(SHIPMENT_ADDRESS_COMPLEMENT_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(SHIPMENT_ADDRESS_DISTRICT_ID).setOutputMarkupId(true));
        orderViewForm.add(new RequiredTextField<String>(SHIPMENT_ADDRESS_CITY_NAME_ID).setOutputMarkupId(true));
        orderViewForm.add(new RequiredTextField<String>(SHIPMENT_ADDRESS_STATE_OR_PROVINCE_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(SHIPMENT_ADDRESS_COUNTRY_NAME_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(SHIPMENT_ADDRESS_INTERNATIONAL_STREET_ID).setOutputMarkupId(true));
        orderViewForm
            .add(new TextField<String>(SHIPMENT_ADDRESS_INTERNATIONAL_STATE_AND_CITY_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(SHIPMENT_ADDRESS_PHONE_ID).setOutputMarkupId(true));
        orderViewForm.add(orderRecordPanel.add(orderRecordPanel.new OrderRecordViewFragment()).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(INVOICE_INVOICE_ID_ID));
        orderViewForm.add(new RequiredTextField<String>(INVOICE_ADDRESS_POSTAL_CODE_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(INVOICE_ADDRESS_NUMBER_ID).setOutputMarkupId(true));
        orderViewForm
            .add(new TextField<String>(INVOICE_ADDRESS_COUNTRY_ID, Model.of("Brasil")).setOutputMarkupId(true));
        orderViewForm.add(new RequiredTextField<String>(INVOICE_ADDRESS_STREET1_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(INVOICE_ADDRESS_STREET2_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(INVOICE_ADDRESS_COMPLEMENT_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(INVOICE_ADDRESS_DISTRICT_ID).setOutputMarkupId(true));
        orderViewForm.add(new RequiredTextField<String>(INVOICE_ADDRESS_CITY_NAME_ID).setOutputMarkupId(true));
        orderViewForm.add(new RequiredTextField<String>(INVOICE_ADDRESS_STATE_OR_PROVINCE_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(INVOICE_ADDRESS_COUNTRY_NAME_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(INVOICE_ADDRESS_INTERNATIONAL_STREET_ID).setOutputMarkupId(true));
        orderViewForm
            .add(new TextField<String>(INVOICE_ADDRESS_INTERNATIONAL_STATE_AND_CITY_ID).setOutputMarkupId(true));
        orderViewForm.add(new TextField<String>(INVOICE_ADDRESS_PHONE_ID).setOutputMarkupId(true));
        orderViewForm.add(orderInvoicePaymentPanel.add(orderInvoicePaymentPanel.new OrderInvoicePaymentViewFragment())
            .setOutputMarkupId(true));
        add(orderViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(editAjaxLink.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String ORDER_VIEW_TABLE_ID = "orderViewTable";

    private static final String ORDER_VIEW_FRAGMENT_MARKUP_ID = "orderViewFragment";

    private static final String ORDER_VIEW_OR_EDIT_FRAGMENT_ID = "orderViewOrEditFragment";

    private static final long serialVersionUID = 2134263849806147209L;

    private final OrderViewTable orderViewTable;

    public OrderViewFragment() {
      super(ORDER_VIEW_OR_EDIT_FRAGMENT_ID, ORDER_VIEW_FRAGMENT_MARKUP_ID, OrderViewOrEditPanel.this,
          OrderViewOrEditPanel.this.getDefaultModel());
      orderViewTable =
          new OrderViewTable(ORDER_VIEW_TABLE_ID, (IModel<Order>) OrderViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(orderViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = 4702200954395165271L;

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderViewOrEditPanel.class);

  @SpringBean(name = ORDER_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Order> orderDataProvider;

  public OrderViewOrEditPanel(final String id, final IModel<Order> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    orderDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    orderDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    orderDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    orderDataProvider.setType(new Order());
    orderDataProvider.getType().setActive(true);
    super.onInitialize();
  }
}
