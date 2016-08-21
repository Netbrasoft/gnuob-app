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
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;

import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.Payment;
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
public class OrderInvoicePaymentViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class OrderInvoicePaymentEditFragement extends Fragment {

    private static final String PAYMENT_EDIT_TABLE_ID = "paymentEditTable";

    private static final String ORDER_INVOICE_PAYMENT_EDIT_FRAGMENT_MARKUP_ID = "orderInvoicePaymentEditFragment";

    private static final String ORDER_INVOICE_PAYMENT_VIEW_OR_EDIT_FRAGMENT_ID = "orderInvoicePaymentViewOrEditFragment";

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class PaymentEditTable extends WebMarkupContainer {

      private static final String TAX_AMOUNT_ID = "taxAmount";

      private static final String FEE_AMOUNT_ID = "feeAmount";

      private static final String SETTLE_AMOUNT_ID = "settleAmount";

      private static final String GROSS_AMOUNT_ID = "grossAmount";

      private static final String INSTALLMENT_COUNT_ID = "installmentCount";

      private static final String EXCHANGE_RATE_ID = "exchangeRate";

      private static final String DD_MM_YYYY_FORMAT = "dd-MM-YYYY";

      private static final String PAYMENT_DATE_ID = "paymentDate";

      private static final String REASON_CODE_ID = "reasonCode";

      private static final String HOLD_DECISION_ID = "holdDecision";

      private static final String PENDING_REASON_ID = "pendingReason";

      private static final String PAYMENT_STATUS_ID = "paymentStatus";

      private static final String PAYMENT_TYPE_ID = "paymentType";

      private static final String PROTECTION_ELIGIBILITY_TYPE_ID = "protectionEligibilityType";

      private static final String TRANSACTION_TYPE_ID = "transactionType";

      private static final String TRANSACTION_ID_ID = "transactionId";

      private static final String TERMINAL_ID_ID = "terminalId";

      private static final String STORE_ID_ID = "storeId";

      private static final String PAYMENT_REQUEST_ID_ID = "paymentRequestId";

      private static final String SAVE_ID = "save";

      private static final String PAYMENT_EDIT_FORM_COMPONENT_ID = "paymentEditForm";

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class SaveAjaxButton extends BootstrapAjaxButton {

        private static final long serialVersionUID = 2695394292963384938L;

        public SaveAjaxButton(final String id, final IModel<String> model, final Form<Payment> form, final Buttons.Type type) {
          super(id, model, form, type);
          setSize(Buttons.Size.Small);
          add(new LoadingBehavior(Model.of(OrderInvoicePaymentViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY))));
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          target.add(form.add(new TooltipValidation()));
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(OrderInvoicePaymentViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          if (((Payment) form.getDefaultModelObject()).getId() == 0) {
            ((Order) OrderInvoicePaymentViewOrEditPanel.this.getDefaultModelObject()).getInvoice().getPayments().add((Payment) form.getDefaultModelObject());
          }
          target.add(form.setOutputMarkupId(true));
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(OrderInvoicePaymentViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY)))));
          target.add(OrderInvoicePaymentViewOrEditPanel.this.getParent().setOutputMarkupId(true));
        }
      }

      private static final long serialVersionUID = -317942480731012722L;

      private final BootstrapForm<Payment> paymentEditForm;

      private final SaveAjaxButton saveAjaxButton;

      public PaymentEditTable(final String id, final IModel<Order> model) {
        super(id, model);
        paymentEditForm = new BootstrapForm<Payment>(PAYMENT_EDIT_FORM_COMPONENT_ID, new CompoundPropertyModel<Payment>(OrderInvoicePaymentViewOrEditPanel.this.selectedModel));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID, Model.of(OrderInvoicePaymentViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_MESSAGE_KEY)), paymentEditForm,
            Buttons.Type.Primary);
      }

      @Override
      protected void onInitialize() {
        paymentEditForm.add(new TextField<String>(PAYMENT_REQUEST_ID_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        paymentEditForm.add(new TextField<String>(STORE_ID_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        paymentEditForm.add(new TextField<String>(TERMINAL_ID_ID).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        paymentEditForm.add(new RequiredTextField<String>(TRANSACTION_ID_ID).add(StringValidator.maximumLength(64)).setOutputMarkupId(true));
        paymentEditForm.add(new RequiredTextField<String>(TRANSACTION_TYPE_ID).add(StringValidator.maximumLength(62)).setOutputMarkupId(true));
        paymentEditForm.add(new TextField<String>(PROTECTION_ELIGIBILITY_TYPE_ID).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        paymentEditForm.add(new RequiredTextField<String>(PAYMENT_TYPE_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        paymentEditForm.add(new RequiredTextField<String>(PAYMENT_STATUS_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        paymentEditForm.add(new TextField<String>(PENDING_REASON_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        paymentEditForm.add(new TextField<String>(HOLD_DECISION_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        paymentEditForm.add(new TextField<String>(REASON_CODE_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        paymentEditForm.add(new DatetimePicker(PAYMENT_DATE_ID, new DatetimePickerConfig().useLocale(Locale.getDefault().toString()).withFormat(DD_MM_YYYY_FORMAT)) {

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
        paymentEditForm.add(new TextField<String>(EXCHANGE_RATE_ID).add(StringValidator.maximumLength(17)).setOutputMarkupId(true));
        paymentEditForm.add(new NumberTextField<BigInteger>(INSTALLMENT_COUNT_ID).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        paymentEditForm.add(new NumberTextField<BigDecimal>(GROSS_AMOUNT_ID).setRequired(true).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        paymentEditForm.add(new NumberTextField<BigDecimal>(SETTLE_AMOUNT_ID).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        paymentEditForm.add(new NumberTextField<BigDecimal>(FEE_AMOUNT_ID).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        paymentEditForm.add(new NumberTextField<BigDecimal>(TAX_AMOUNT_ID).add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        paymentEditForm.add(saveAjaxButton.setOutputMarkupId(true));
        add(paymentEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final long serialVersionUID = 3709791409078428685L;

    private final WebMarkupContainer paymentEditTable;

    public OrderInvoicePaymentEditFragement() {
      super(ORDER_INVOICE_PAYMENT_VIEW_OR_EDIT_FRAGMENT_ID, ORDER_INVOICE_PAYMENT_EDIT_FRAGMENT_MARKUP_ID, OrderInvoicePaymentViewOrEditPanel.this,
          OrderInvoicePaymentViewOrEditPanel.this.getDefaultModel());
      paymentEditTable = new PaymentEditTable(PAYMENT_EDIT_TABLE_ID, (IModel<Order>) OrderInvoicePaymentEditFragement.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(paymentEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class OrderInvoicePaymentViewFragement extends Fragment {

    private static final String PAYMENT_VIEW_TABLE_ID = "paymentViewTable";

    private static final String ORDER_INVOICE_PAYMENT_VIEW_FRAGMENT_MARKUP_ID = "orderInvoicePaymentViewFragment";

    private static final String ORDER_INVOICE_PAYMENT_VIEW_OR_EDIT_FRAGMENT_ID = "orderInvoicePaymentViewOrEditFragment";

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class PaymentViewTable extends WebMarkupContainer {

      private static final String TAX_AMOUNT_ID = "taxAmount";

      private static final String FEE_AMOUNT_ID = "feeAmount";

      private static final String SETTLE_AMOUNT_ID = "settleAmount";

      private static final String GROSS_AMOUNT_ID = "grossAmount";

      private static final String INSTALLMENT_COUNT_ID = "installmentCount";

      private static final String EXCHANGE_RATE_ID = "exchangeRate";

      private static final String DD_MM_YYYY_FORMAT = "dd-MM-YYYY";

      private static final String PAYMENT_DATE_ID = "paymentDate";

      private static final String REASON_CODE_ID = "reasonCode";

      private static final String HOLD_DECISION_ID = "holdDecision";

      private static final String PENDING_REASON_ID = "pendingReason";

      private static final String PAYMENT_STATUS_ID = "paymentStatus";

      private static final String PAYMENT_TYPE_ID = "paymentType";

      private static final String PROTECTION_ELIGIBILITY_TYPE_ID = "protectionEligibilityType";

      private static final String TRANSACTION_TYPE_ID = "transactionType";

      private static final String TRANSACTION_ID_ID = "transactionId";

      private static final String TERMINAL_ID_ID = "terminalId";

      private static final String STORE_ID_ID = "storeId";

      private static final String PAYMENT_REQUEST_ID_ID = "paymentRequestId";

      private static final String PAYMENT_VIEW_FORM_COMPONENT_ID = "paymentViewForm";

      private static final long serialVersionUID = 3485437486331806341L;

      private final BootstrapForm<Payment> paymentViewForm;

      public PaymentViewTable(final String id, final IModel<Order> model) {
        super(id, model);
        paymentViewForm = new BootstrapForm<Payment>(PAYMENT_VIEW_FORM_COMPONENT_ID, new CompoundPropertyModel<Payment>(OrderInvoicePaymentViewOrEditPanel.this.selectedModel));
      }

      @Override
      protected void onInitialize() {
        paymentViewForm.add(new TextField<String>(PAYMENT_REQUEST_ID_ID).setOutputMarkupId(true));
        paymentViewForm.add(new TextField<String>(STORE_ID_ID).setOutputMarkupId(true));
        paymentViewForm.add(new TextField<String>(TERMINAL_ID_ID).setOutputMarkupId(true));
        paymentViewForm.add(new RequiredTextField<String>(TRANSACTION_ID_ID).setOutputMarkupId(true));
        paymentViewForm.add(new RequiredTextField<String>(TRANSACTION_TYPE_ID).setOutputMarkupId(true));
        paymentViewForm.add(new TextField<String>(PROTECTION_ELIGIBILITY_TYPE_ID).setOutputMarkupId(true));
        paymentViewForm.add(new RequiredTextField<String>(PAYMENT_TYPE_ID).setOutputMarkupId(true));
        paymentViewForm.add(new RequiredTextField<String>(PAYMENT_STATUS_ID).setOutputMarkupId(true));
        paymentViewForm.add(new TextField<String>(PENDING_REASON_ID).setOutputMarkupId(true));
        paymentViewForm.add(new TextField<String>(HOLD_DECISION_ID).setOutputMarkupId(true));
        paymentViewForm.add(new TextField<String>(REASON_CODE_ID).setOutputMarkupId(true));
        paymentViewForm.add(new DatetimePicker(PAYMENT_DATE_ID, new DatetimePickerConfig().useLocale(Locale.getDefault().toString()).withFormat(DD_MM_YYYY_FORMAT)) {

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
        paymentViewForm.add(new TextField<String>(EXCHANGE_RATE_ID).setOutputMarkupId(true));
        paymentViewForm.add(new NumberTextField<BigInteger>(INSTALLMENT_COUNT_ID).setOutputMarkupId(true));
        paymentViewForm.add(new NumberTextField<BigDecimal>(GROSS_AMOUNT_ID).setOutputMarkupId(true));
        paymentViewForm.add(new NumberTextField<BigDecimal>(SETTLE_AMOUNT_ID).setOutputMarkupId(true));
        paymentViewForm.add(new NumberTextField<BigDecimal>(FEE_AMOUNT_ID).setOutputMarkupId(true));
        paymentViewForm.add(new NumberTextField<BigDecimal>(TAX_AMOUNT_ID).setOutputMarkupId(true));
        add(paymentViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final long serialVersionUID = 6927997909191615786L;

    private final WebMarkupContainer paymentViewTable;

    public OrderInvoicePaymentViewFragement() {
      super(ORDER_INVOICE_PAYMENT_VIEW_OR_EDIT_FRAGMENT_ID, ORDER_INVOICE_PAYMENT_VIEW_FRAGMENT_MARKUP_ID, OrderInvoicePaymentViewOrEditPanel.this,
          OrderInvoicePaymentViewOrEditPanel.this.getDefaultModel());
      paymentViewTable = new PaymentViewTable(PAYMENT_VIEW_TABLE_ID, (IModel<Order>) OrderInvoicePaymentViewFragement.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(paymentViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = -7002701340914975498L;

  private IModel<Payment> selectedModel;

  public OrderInvoicePaymentViewOrEditPanel(final String id, final IModel<Order> model) {
    super(id, model);
    selectedModel = Model.of(new Payment());
  }

  public void setSelectedModel(final IModel<Payment> selectedModel) {
    this.selectedModel = selectedModel;
  }
}
