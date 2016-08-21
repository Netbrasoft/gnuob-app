package com.netbrasoft.gnuob.application.customer;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.CUSTOMER_DATA_PROVIDER_NAME;

import java.util.Locale;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Customer;
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
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePicker;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;
import wicket.contrib.tinymce4.ajax.TinyMceAjaxSubmitModifier;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class CustomerViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class CustomerEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class CustomerEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class CancelAjaxLink extends BootstrapAjaxLink<Customer> {

        private static final long serialVersionUID = 4267535261864907719L;

        public CancelAjaxLink(final String id, final IModel<Customer> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          CustomerViewOrEditPanel.this.removeAll();
          if (((Customer) CancelAjaxLink.this.getDefaultModelObject()).getId() > 0) {
            CancelAjaxLink.this.setDefaultModelObject(
                customerDataProvider.findById((Customer) CancelAjaxLink.this.getDefaultModelObject()));
          }
          target.add(CustomerViewOrEditPanel.this.add(CustomerViewOrEditPanel.this.new CustomerViewFragment())
              .setOutputMarkupId(true));
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
              .of(CustomerViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY))),
              new TinyMceAjaxSubmitModifier());
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          form.add(new TooltipValidation());
          target.add(form);
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
              .of(CustomerViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          try {
            if (((Customer) form.getDefaultModelObject()).getId() == 0) {
              CustomerEditTable.this.setDefaultModelObject(
                  customerDataProvider.findById(customerDataProvider.persist((Customer) form.getDefaultModelObject())));
            } else {
              CustomerEditTable.this.setDefaultModelObject(
                  customerDataProvider.findById(customerDataProvider.merge((Customer) form.getDefaultModelObject())));
            }
            CustomerViewOrEditPanel.this.removeAll();
            target.add(CustomerViewOrEditPanel.this.add(CustomerViewOrEditPanel.this.new CustomerViewFragment())
                .setOutputMarkupId(true));
          } catch (final RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
            feedbackPanel.warn(e.getLocalizedMessage());
            target.add(feedbackPanel.setOutputMarkupId(true));
            target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(
                CustomerViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
          }
        }
      }

      private static final String _0_9_5_0_9_3_PATTERN = "([0-9]){5}([-])([0-9]){3}";

      private static final String DD_MM_YYYY_FORMAT = "dd-MM-YYYY";

      private static final String ADDRESS_PHONE_ID = "address.phone";

      private static final String ADDRESS_INTERNATIONAL_STATE_AND_CITY_ID = "address.internationalStateAndCity";

      private static final String ADDRESS_INTERNATIONAL_STREET_ID = "address.internationalStreet";

      private static final String ADDRESS_COUNTRY_NAME_ID = "address.countryName";

      private static final String ADDRESS_STATE_OR_PROVINCE_ID = "address.stateOrProvince";

      private static final String ADDRESS_CITY_NAME_ID = "address.cityName";

      private static final String ADDRESS_DISTRICT_ID = "address.district";

      private static final String ADDRESS_COMPLEMENT_ID = "address.complement";

      private static final String ADDRESS_STREET2_ID = "address.street2";

      private static final String ADDRESS_STREET1_ID = "address.street1";

      private static final String ADDRESS_COUNTRY_ID = "address.country";

      private static final String ADDRESS_NUMBER_ID = "address.number";

      private static final String ADDRESS_POSTAL_CODE_ID = "address.postalCode";

      private static final String BUYER_EMAIL_ID = "buyerEmail";

      private static final String DATE_OF_BIRTH_ID = "dateOfBirth";

      private static final String LAST_NAME_ID = "lastName";

      private static final String MIDDLE_NAME_ID = "middleName";

      private static final String FIRST_NAME_ID = "firstName";

      private static final String SUFFIX_ID = "suffix";

      private static final String SALUTATION_ID = "salutation";

      private static final String FEEDBACK_ID = "feedback";

      private static final String SAVE_ID = "save";

      private static final String CANCEL_ID = "cancel";

      private static final String CUSTOMER_EDIT_FORM_COMPONENT_ID = "customerEditForm";

      private static final long serialVersionUID = -906207026151446160L;

      private final BootstrapForm<Customer> customerEditForm;

      private final CancelAjaxLink cancelAjaxLink;

      private final SaveAjaxButton saveAjaxButton;

      private final NotificationPanel feedbackPanel;

      public CustomerEditTable(final String id, final IModel<Customer> model) {
        super(id, model);
        customerEditForm = new BootstrapForm<Customer>(CUSTOMER_EDIT_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Customer>((IModel<Customer>) CustomerEditTable.this.getDefaultModel()));
        cancelAjaxLink = new CancelAjaxLink(CANCEL_ID, model, Buttons.Type.Default,
            Model.of(CustomerViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID,
            Model.of(CustomerViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)),
            customerEditForm, Buttons.Type.Primary);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
      }

      @Override
      protected void onInitialize() {
        customerEditForm
            .add(new TextField<String>(SALUTATION_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        customerEditForm
            .add(new TextField<String>(SUFFIX_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        customerEditForm
            .add(new RequiredTextField<String>(FIRST_NAME_ID)
                .setLabel(Model
                    .of(CustomerViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.FIRST_NAME_MESSAGE_KEY)))
                .add(StringValidator.maximumLength(64)).setOutputMarkupId(true));
        customerEditForm
            .add(new TextField<String>(MIDDLE_NAME_ID).add(StringValidator.maximumLength(64)).setOutputMarkupId(true));
        customerEditForm
            .add(new RequiredTextField<String>(LAST_NAME_ID)
                .setLabel(Model
                    .of(CustomerViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.LAST_NAME_MESSAGE_KEY)))
                .add(StringValidator.maximumLength(64)).setOutputMarkupId(true));
        customerEditForm.add(new DatetimePicker(DATE_OF_BIRTH_ID,
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
        customerEditForm.add(new RequiredTextField<String>(BUYER_EMAIL_ID)
            .setLabel(Model
                .of(CustomerViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.BUYER_EMAIL_MESSAGE_KEY)))
            .add(EmailAddressValidator.getInstance()).add(StringValidator.maximumLength(127)).setOutputMarkupId(true));
        customerEditForm.add(new RequiredTextField<String>(ADDRESS_POSTAL_CODE_ID)
            .setLabel(Model
                .of(CustomerViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.POSTAL_CODE_MESSAGE_KEY)))
            .add(new PatternValidator(_0_9_5_0_9_3_PATTERN)).add(StringValidator.maximumLength(20))
            .setOutputMarkupId(true));
        customerEditForm.add(
            new TextField<String>(ADDRESS_NUMBER_ID).add(StringValidator.maximumLength(10)).setOutputMarkupId(true));
        customerEditForm
            .add(
                new TextField<String>(ADDRESS_COUNTRY_ID, Model.of("Brasil"))
                    .setLabel(Model.of(CustomerViewOrEditPanel.this
                        .getString(NetbrasoftApplicationConstants.COUNTRY_NAME_MESSAGE_KEY)))
                    .add(StringValidator.maximumLength(40)).setEnabled(false).setOutputMarkupId(true));
        customerEditForm
            .add(new RequiredTextField<String>(ADDRESS_STREET1_ID)
                .setLabel(Model
                    .of(CustomerViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.STREET1_MESSAGE_KEY)))
                .add(StringValidator.maximumLength(100)).setOutputMarkupId(true));
        customerEditForm.add(
            new TextField<String>(ADDRESS_STREET2_ID).add(StringValidator.maximumLength(100)).setOutputMarkupId(true));
        customerEditForm.add(new TextField<String>(ADDRESS_COMPLEMENT_ID).add(StringValidator.maximumLength(40))
            .setOutputMarkupId(true));
        customerEditForm.add(
            new TextField<String>(ADDRESS_DISTRICT_ID).add(StringValidator.maximumLength(40)).setOutputMarkupId(true));
        customerEditForm
            .add(new RequiredTextField<String>(ADDRESS_CITY_NAME_ID)
                .setLabel(Model
                    .of(CustomerViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.CITY_NAME_MESSAGE_KEY)))
                .add(StringValidator.maximumLength(40)).setOutputMarkupId(true));
        customerEditForm.add(new RequiredTextField<String>(ADDRESS_STATE_OR_PROVINCE_ID)
            .setLabel(Model.of(
                CustomerViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.STATE_OR_PROVINCE_MESSAGE_KEY)))
            .setOutputMarkupId(true));
        customerEditForm.add(new TextField<String>(ADDRESS_COUNTRY_NAME_ID).add(StringValidator.maximumLength(40))
            .setOutputMarkupId(true));
        customerEditForm.add(new TextField<String>(ADDRESS_INTERNATIONAL_STREET_ID)
            .add(StringValidator.maximumLength(40)).setOutputMarkupId(true));
        customerEditForm.add(new TextField<String>(ADDRESS_INTERNATIONAL_STATE_AND_CITY_ID)
            .add(StringValidator.maximumLength(80)).setOutputMarkupId(true));
        customerEditForm.add(
            new TextField<String>(ADDRESS_PHONE_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        add(customerEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(saveAjaxButton.setOutputMarkupId(true));
        add(cancelAjaxLink.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CUSTOMER_EDIT_TABLE_ID = "customerEditTable";

    private static final String CUSTOMER_EDIT_FRAGMENT_MARKUP_ID = "customerEditFragment";

    private static final String CUSTOMER_VIEW_OR_EDIT_FRAGMENT_ID = "customerViewOrEditFragment";

    private static final long serialVersionUID = 4702333788976660894L;

    private final CustomerEditTable customerEditTable;

    public CustomerEditFragment() {
      super(CUSTOMER_VIEW_OR_EDIT_FRAGMENT_ID, CUSTOMER_EDIT_FRAGMENT_MARKUP_ID, CustomerViewOrEditPanel.this,
          CustomerViewOrEditPanel.this.getDefaultModel());
      customerEditTable =
          new CustomerEditTable(CUSTOMER_EDIT_TABLE_ID, (IModel<Customer>) CustomerEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(customerEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }


  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class CustomerViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class CustomerViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class EditAjaxLink extends BootstrapAjaxLink<Customer> {

        private static final long serialVersionUID = 4267535261864907719L;

        public EditAjaxLink(final String id, final IModel<Customer> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.edit);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          CustomerViewOrEditPanel.this.removeAll();
          target.add(CustomerViewOrEditPanel.this
              .add(CustomerViewOrEditPanel.this.new CustomerEditFragment().setOutputMarkupId(true)));
        }
      }

      private static final String ADDRESS_PHONE_ID = "address.phone";

      private static final String ADDRESS_INTERNATIONAL_STATE_AND_CITY_ID = "address.internationalStateAndCity";

      private static final String ADDRESS_INTERNATIONAL_STREET_ID = "address.internationalStreet";

      private static final String ADDRESS_COUNTRY_NAME_ID = "address.countryName";

      private static final String ADDRESS_STATE_OR_PROVINCE_ID = "address.stateOrProvince";

      private static final String ADDRESS_CITY_NAME_ID = "address.cityName";

      private static final String ADDRESS_DISTRICT_ID = "address.district";

      private static final String ADDRESS_COMPLEMENT_ID = "address.complement";

      private static final String ADDRESS_STREET2_ID = "address.street2";

      private static final String ADDRESS_STREET1_ID = "address.street1";

      private static final String ADDRESS_COUNTRY_ID = "address.country";

      private static final String ADDRESS_NUMBER_ID = "address.number";

      private static final String ADDRESS_POSTAL_CODE_ID = "address.postalCode";

      private static final String BUYER_EMAIL_ID = "buyerEmail";

      private static final String DATE_OF_BIRTH_ID = "dateOfBirth";

      private static final String LAST_NAME_ID = "lastName";

      private static final String MIDDLE_NAME_ID = "middleName";

      private static final String FIRST_NAME_ID = "firstName";

      private static final String SUFFIX_ID = "suffix";

      private static final String SALUTATION_ID = "salutation";

      private static final String EDIT_ID = "edit";

      private static final String CUSTOMER_VIEW_FORM_COMPONENT_ID = "customerViewForm";

      private static final long serialVersionUID = 989923448088758813L;

      private final BootstrapForm<Customer> customerViewForm;

      private final EditAjaxLink editAjaxLink;

      public CustomerViewTable(final String id, final IModel<Customer> model) {
        super(id, model);
        customerViewForm = new BootstrapForm<Customer>(CUSTOMER_VIEW_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Customer>((IModel<Customer>) CustomerViewTable.this.getDefaultModel()));
        editAjaxLink = new EditAjaxLink(EDIT_ID, model, Buttons.Type.Primary,
            Model.of(CustomerViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.EDIT_MESSAGE_KEY)));
      }

      @Override
      protected void onInitialize() {
        customerViewForm.add(new TextField<String>(SALUTATION_ID).setOutputMarkupId(true));
        customerViewForm.add(new TextField<String>(SUFFIX_ID).setOutputMarkupId(true));
        customerViewForm.add(new RequiredTextField<String>(FIRST_NAME_ID).setOutputMarkupId(true));
        customerViewForm.add(new TextField<String>(MIDDLE_NAME_ID).setOutputMarkupId(true));
        customerViewForm.add(new RequiredTextField<String>(LAST_NAME_ID).setOutputMarkupId(true));
        customerViewForm.add(new DatetimePicker(DATE_OF_BIRTH_ID,
            new DatetimePickerConfig().useLocale(Locale.getDefault().toString()).withFormat("dd-MM-YYYY")) {

          private static final long serialVersionUID = 1209354725150726556L;

          @Override
          public <C> IConverter<C> getConverter(final Class<C> type) {
            if (XMLGregorianCalendar.class.isAssignableFrom(type)) {
              return (IConverter<C>) new XmlGregorianCalendarConverter();
            } else {
              return super.getConverter(type);
            }
          }
        });
        customerViewForm.add(new RequiredTextField<String>(BUYER_EMAIL_ID).setOutputMarkupId(true));
        customerViewForm.add(new RequiredTextField<String>(ADDRESS_POSTAL_CODE_ID).setOutputMarkupId(true));
        customerViewForm.add(new TextField<String>(ADDRESS_NUMBER_ID).setOutputMarkupId(true));
        customerViewForm.add(new TextField<String>(ADDRESS_COUNTRY_ID, Model.of("Brasil")).setOutputMarkupId(true));
        customerViewForm.add(new RequiredTextField<String>(ADDRESS_STREET1_ID).setOutputMarkupId(true));
        customerViewForm.add(new TextField<String>(ADDRESS_STREET2_ID).setOutputMarkupId(true));
        customerViewForm.add(new TextField<String>(ADDRESS_COMPLEMENT_ID).setOutputMarkupId(true));
        customerViewForm.add(new TextField<String>(ADDRESS_DISTRICT_ID).setOutputMarkupId(true));
        customerViewForm.add(new RequiredTextField<String>(ADDRESS_CITY_NAME_ID).setOutputMarkupId(true));
        customerViewForm.add(new RequiredTextField<String>(ADDRESS_STATE_OR_PROVINCE_ID).setOutputMarkupId(true));
        customerViewForm.add(new TextField<String>(ADDRESS_COUNTRY_NAME_ID).setOutputMarkupId(true));
        customerViewForm.add(new TextField<String>(ADDRESS_INTERNATIONAL_STREET_ID).setOutputMarkupId(true));
        customerViewForm.add(new TextField<String>(ADDRESS_INTERNATIONAL_STATE_AND_CITY_ID).setOutputMarkupId(true));
        customerViewForm.add(new TextField<String>(ADDRESS_PHONE_ID).setOutputMarkupId(true));
        add(customerViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(editAjaxLink.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CUSTOMER_VIEW_TABLE_ID = "customerViewTable";

    private static final String CUSTOMER_VIEW_FRAGMENT_MARKUP_ID = "customerViewFragment";

    private static final String CUSTOMER_VIEW_OR_EDIT_FRAGMENT_ID = "customerViewOrEditFragment";

    private static final long serialVersionUID = 4702333788976660894L;

    private final CustomerViewTable customerViewTable;

    public CustomerViewFragment() {
      super(CUSTOMER_VIEW_OR_EDIT_FRAGMENT_ID, CUSTOMER_VIEW_FRAGMENT_MARKUP_ID, CustomerViewOrEditPanel.this,
          CustomerViewOrEditPanel.this.getDefaultModel());
      customerViewTable =
          new CustomerViewTable(CUSTOMER_VIEW_TABLE_ID, (IModel<Customer>) CustomerViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(customerViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerViewOrEditPanel.class);

  private static final long serialVersionUID = 5273022766621299743L;

  @SpringBean(name = CUSTOMER_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Customer> customerDataProvider;

  public CustomerViewOrEditPanel(final String id, final IModel<Customer> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    customerDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    customerDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    customerDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    customerDataProvider.setType(new Customer());
    customerDataProvider.getType().setActive(true);
    super.onInitialize();
  }
}
