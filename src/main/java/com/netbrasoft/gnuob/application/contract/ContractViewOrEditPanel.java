package com.netbrasoft.gnuob.application.contract;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.CONTRACT_DATA_PROVIDER_NAME;

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
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Contract;
import com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
import com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants;
import com.netbrasoft.gnuob.application.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.application.content.ContentViewOrEditPanel;
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
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;
import wicket.contrib.tinymce4.ajax.TinyMceAjaxSubmitModifier;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class ContractViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class ContractEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class ContractEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class CancelAjaxLink extends BootstrapAjaxLink<Contract> {

        private static final long serialVersionUID = 4267535261864907719L;

        public CancelAjaxLink(final String id, final IModel<Contract> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          ContractViewOrEditPanel.this.removeAll();
          if (((Contract) CancelAjaxLink.this.getDefaultModelObject()).getId() > 0) {
            CancelAjaxLink.this.setDefaultModelObject(
                contractDataProvider.findById((Contract) CancelAjaxLink.this.getDefaultModelObject()));
          }
          target.add(ContractViewOrEditPanel.this.add(ContractViewOrEditPanel.this.new ContractViewFragment())
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
              .of(ContractViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY))),
              new TinyMceAjaxSubmitModifier());
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          form.add(new TooltipValidation());
          target.add(form);
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
              .of(ContractViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          try {
            if (((Contract) form.getDefaultModelObject()).getId() == 0) {
              ContractEditTable.this.setDefaultModelObject(
                  contractDataProvider.findById(contractDataProvider.persist((Contract) form.getDefaultModelObject())));
            } else {
              ContractEditTable.this.setDefaultModelObject(
                  contractDataProvider.findById(contractDataProvider.merge((Contract) form.getDefaultModelObject())));
            }
            ContractViewOrEditPanel.this.removeAll();
            target.add(ContractViewOrEditPanel.this.add(ContractViewOrEditPanel.this.new ContractViewFragment())
                .setOutputMarkupId(true));
          } catch (final RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
            feedbackPanel.warn(e.getLocalizedMessage());
            target.add(feedbackPanel.setOutputMarkupId(true));
            target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(
                ContractViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
          }
        }
      }

      private static final String CUSTOMER_TAX_ID_TYPE_ID = "customer.taxIdType";

      private static final String CUSTOMER_TAX_ID_ID = "customer.taxId";

      private static final String CUSTOMER_PAYER_STATUS_ID = "customer.payerStatus";

      private static final String CUSTOMER_PAYER_ID_ID = "customer.payerId";

      private static final String CUSTOMER_PAYER_BUSINESS_ID = "customer.payerBusiness";

      private static final String CUSTOMER_PAYER_ID = "customer.payer";

      private static final String CUSTOMER_CONTACT_PHONE_ID = "customer.contactPhone";

      private static final String CUSTOMER_BUYER_MARKETING_EMAIL_ID = "customer.buyerMarketingEmail";

      private static final String CUSTOMER_FRIENDLY_NAME_ID = "customer.friendlyName";

      private static final String CONTRACT_ID_ID = "contractId";

      private static final String FEEDBACK_MARKUP_ID = "feedback";

      private static final String SAVE_ID = "save";

      private static final String CANCEL_ID = "cancel";

      private static final String CONTRACT_EDIT_FORM_COMPONENT_ID = "contractEditForm";

      private static final long serialVersionUID = -1708404592209286996L;

      private final BootstrapForm<Contract> contractEditForm;

      private final CancelAjaxLink cancelAjaxLink;

      private final SaveAjaxButton saveAjaxButton;

      private final NotificationPanel feedbackPanel;

      public ContractEditTable(final String id, final IModel<Contract> model) {
        super(id, model);
        contractEditForm = new BootstrapForm<Contract>(CONTRACT_EDIT_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Contract>((IModel<Contract>) ContractEditTable.this.getDefaultModel()));
        cancelAjaxLink = new CancelAjaxLink(CANCEL_ID, model, Buttons.Type.Default,
            Model.of(ContractViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID,
            Model.of(ContractViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)),
            contractEditForm, Buttons.Type.Primary);
        feedbackPanel = new NotificationPanel(FEEDBACK_MARKUP_ID);
      }

      @Override
      protected void onInitialize() {
        contractEditForm.add(new RequiredTextField<String>(CONTRACT_ID_ID).add(StringValidator.maximumLength(127))
            .setOutputMarkupId(true));
        contractEditForm.add(new TextField<String>(CUSTOMER_FRIENDLY_NAME_ID).add(StringValidator.maximumLength(128))
            .setOutputMarkupId(true));
        contractEditForm
            .add(new TextField<String>(CUSTOMER_BUYER_MARKETING_EMAIL_ID)
                .setLabel(Model.of(ContractViewOrEditPanel.this
                    .getString(NetbrasoftApplicationConstants.BUYER_MARKETING_EMAIL_MESSAGE)))
                .add(EmailAddressValidator.getInstance()).add(StringValidator.maximumLength(127))
                .setOutputMarkupId(true));
        contractEditForm.add(new TextField<String>(CUSTOMER_CONTACT_PHONE_ID).add(StringValidator.maximumLength(20))
            .setOutputMarkupId(true));
        contractEditForm
            .add(
                new TextField<String>(CUSTOMER_PAYER_ID)
                    .setLabel(Model
                        .of(ContractViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.PAYER_MESSAGE_KEY)))
                    .add(EmailAddressValidator.getInstance()).add(StringValidator.maximumLength(127))
                    .setOutputMarkupId(true));
        contractEditForm
            .add(
                new TextField<String>(CUSTOMER_PAYER_BUSINESS_ID)
                    .setLabel(Model.of(ContractViewOrEditPanel.this
                        .getString(NetbrasoftApplicationConstants.PAYER_BUSINESS_MESSAGE_KEY)))
                    .add(EmailAddressValidator.getInstance()).add(StringValidator.maximumLength(127))
                    .setOutputMarkupId(true));
        contractEditForm.add(
            new TextField<String>(CUSTOMER_PAYER_ID_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        contractEditForm.add(new TextField<String>(CUSTOMER_PAYER_STATUS_ID).add(StringValidator.maximumLength(20))
            .setOutputMarkupId(true));
        contractEditForm.add(
            new TextField<String>(CUSTOMER_TAX_ID_ID).add(StringValidator.maximumLength(20)).setOutputMarkupId(true));
        contractEditForm.add(new TextField<String>(CUSTOMER_TAX_ID_TYPE_ID).add(StringValidator.maximumLength(20))
            .setOutputMarkupId(true));
        add(contractEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(saveAjaxButton.setOutputMarkupId(true));
        add(cancelAjaxLink.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CONTRACT_EDIT_TABLE_ID = "contractEditTable";

    private static final String CONTRACT_EDIT_FRAGMENT_MARKUP_ID = "contractEditFragment";

    private static final String CONTRACT_VIEW_OR_EDIT_FRAGMENT_ID = "contractViewOrEditFragment";

    private static final long serialVersionUID = 4702333788976660894L;

    private final WebMarkupContainer contractEditTable;

    public ContractEditFragment() {
      super(CONTRACT_VIEW_OR_EDIT_FRAGMENT_ID, CONTRACT_EDIT_FRAGMENT_MARKUP_ID, ContractViewOrEditPanel.this,
          ContractViewOrEditPanel.this.getDefaultModel());
      contractEditTable =
          new ContractEditTable(CONTRACT_EDIT_TABLE_ID, (IModel<Contract>) ContractEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(contractEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class ContractViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class ContractViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class EditAjaxLink extends BootstrapAjaxLink<Contract> {

        private static final long serialVersionUID = 4267535261864907719L;

        public EditAjaxLink(final String id, final IModel<Contract> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.edit);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          ContractViewOrEditPanel.this.removeAll();
          target.add(ContractViewOrEditPanel.this
              .add(ContractViewOrEditPanel.this.new ContractEditFragment().setOutputMarkupId(true)));
        }
      }

      private static final String CUSTOMER_TAX_ID_TYPE_ID = "customer.taxIdType";

      private static final String CUSTOMER_TAX_ID_ID = "customer.taxId";

      private static final String CUSTOMER_PAYER_STATUS_ID = "customer.payerStatus";

      private static final String CUSTOMER_PAYER_ID_ID = "customer.payerId";

      private static final String CUSTOMER_PAYER_BUSINESS_ID = "customer.payerBusiness";

      private static final String CUSTOMER_PAYER_ID = "customer.payer";

      private static final String CUSTOMER_CONTACT_PHONE_ID = "customer.contactPhone";

      private static final String CUSTOMER_BUYER_MARKETING_EMAIL_ID = "customer.buyerMarketingEmail";

      private static final String CUSTOMER_FRIENDLY_NAME_ID = "customer.friendlyName";

      private static final String CONTRACT_ID_ID = "contractId";

      private static final String EDIT_ID = "edit";

      private static final String CONTRACT_VIEW_FORM_COMPONENT_ID = "contractViewForm";

      private static final long serialVersionUID = 7285844181873490052L;

      private final Form<Contract> contractViewForm;

      private final EditAjaxLink editAjaxLink;

      public ContractViewTable(final String id, final IModel<Contract> model) {
        super(id, model);
        contractViewForm = new Form<Contract>(CONTRACT_VIEW_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Contract>((IModel<Contract>) ContractViewTable.this.getDefaultModel()));
        editAjaxLink = new EditAjaxLink(EDIT_ID, model, Buttons.Type.Primary,
            Model.of(ContractViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.EDIT_MESSAGE_KEY)));
      }

      @Override
      protected void onInitialize() {
        contractViewForm.add(new RequiredTextField<String>(CONTRACT_ID_ID).setOutputMarkupId(true));
        contractViewForm.add(new TextField<String>(CUSTOMER_FRIENDLY_NAME_ID).setOutputMarkupId(true));
        contractViewForm.add(new TextField<String>(CUSTOMER_BUYER_MARKETING_EMAIL_ID).setOutputMarkupId(true));
        contractViewForm.add(new TextField<String>(CUSTOMER_CONTACT_PHONE_ID).setOutputMarkupId(true));
        contractViewForm.add(new TextField<String>(CUSTOMER_PAYER_ID).setOutputMarkupId(true));
        contractViewForm.add(new TextField<String>(CUSTOMER_PAYER_BUSINESS_ID).setOutputMarkupId(true));
        contractViewForm.add(new TextField<String>(CUSTOMER_PAYER_ID_ID).setOutputMarkupId(true));
        contractViewForm.add(new TextField<String>(CUSTOMER_PAYER_STATUS_ID).setOutputMarkupId(true));
        contractViewForm.add(new TextField<String>(CUSTOMER_TAX_ID_ID).setOutputMarkupId(true));
        contractViewForm.add(new TextField<String>(CUSTOMER_TAX_ID_TYPE_ID).setOutputMarkupId(true));
        add(contractViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(editAjaxLink.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CONTRACT_VIEW_TABLE_ID = "contractViewTable";

    private static final String CONTRACT_VIEW_FRAGMENT_MARKUP_ID = "contractViewFragment";

    private static final String CONTRACT_VIEW_OR_EDIT_FRAGMENT_ID = "contractViewOrEditFragment";

    private static final long serialVersionUID = 4702333788976660894L;

    private final ContractViewTable contractViewTable;

    public ContractViewFragment() {
      super(CONTRACT_VIEW_OR_EDIT_FRAGMENT_ID, CONTRACT_VIEW_FRAGMENT_MARKUP_ID, ContractViewOrEditPanel.this,
          ContractViewOrEditPanel.this.getDefaultModel());
      contractViewTable =
          new ContractViewTable(CONTRACT_VIEW_TABLE_ID, (IModel<Contract>) ContractViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(contractViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = 5269452849219756769L;

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentViewOrEditPanel.class);

  @SpringBean(name = CONTRACT_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Contract> contractDataProvider;

  public ContractViewOrEditPanel(final String id, final IModel<Contract> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    contractDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    contractDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    contractDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    contractDataProvider.setType(new Contract());
    contractDataProvider.getType().setActive(true);
    super.onInitialize();
  }
}
