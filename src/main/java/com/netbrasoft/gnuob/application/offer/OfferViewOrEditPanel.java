package com.netbrasoft.gnuob.application.offer;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.OFFER_DATA_PROVIDER_NAME;

import java.math.BigDecimal;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
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
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class OfferViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER})
  class OfferEditFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER})
    class OfferEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class CancelAjaxLink extends BootstrapAjaxLink<Offer> {

        private static final long serialVersionUID = 4267535261864907719L;

        public CancelAjaxLink(final String id, final IModel<Offer> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          OfferViewOrEditPanel.this.removeAll();
          if (((Offer) CancelAjaxLink.this.getDefaultModelObject()).getId() > 0) {
            OfferViewOrEditPanel.this
                .setDefaultModelObject(offerDataProvider.findById((Offer) CancelAjaxLink.this.getDefaultModelObject()));
          }
          target.add(OfferViewOrEditPanel.this.add(OfferViewOrEditPanel.this.new OfferViewFragment())
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
              .of(OfferViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY))));
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          form.add(new TooltipValidation());
          target.add(form);
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
              .of(OfferViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          try {
            if (((Offer) form.getDefaultModelObject()).getId() == 0) {
              OfferEditTable.this.setDefaultModelObject(
                  offerDataProvider.findById(offerDataProvider.persist((Offer) form.getDefaultModelObject())));
            } else {
              OfferEditTable.this.setDefaultModelObject(
                  offerDataProvider.findById(offerDataProvider.merge((Offer) form.getDefaultModelObject())));
            }
          } catch (final RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
            feedbackPanel.warn(e.getLocalizedMessage());
            target.add(feedbackPanel.setOutputMarkupId(true));
            target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
                .of(OfferViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
            OfferViewOrEditPanel.this.removeAll();
            target.add(OfferViewOrEditPanel.this.add(OfferViewOrEditPanel.this.new OfferViewFragment())
                .setOutputMarkupId(true));
          }
        }
      }

      private static final String OFFER_TOTAL_ID = "offerTotal";

      private static final String TAX_TOTAL_ID = "taxTotal";

      private static final String SHIPPING_DISCOUNT_ID = "shippingDiscount";

      private static final String INSURANCE_TOTAL_ID = "insuranceTotal";

      private static final String SHIPPING_TOTAL_ID = "shippingTotal";

      private static final String HANDLING_TOTAL_ID = "handlingTotal";

      private static final String DISCOUNT_TOTAL_ID = "discountTotal";

      private static final String EXTRA_AMOUNT_ID = "extraAmount";

      private static final String OFFER_DESCRIPTION_ID = "offerDescription";

      private static final String ITEM_TOTAL_ID = "itemTotal";

      private static final String CONTRACT_CONTRACT_ID_ID = "contract.contractId";

      private static final String OFFER_ID_ID = "offerId";

      private static final String OFFER_RECORD_PANEL_ID = "offerRecordPanel";

      private static final String OFFER_EDIT_FORM_COMPONENT_ID = "offerEditForm";

      private static final long serialVersionUID = -4754331099163469949L;

      private static final String FEEDBACK_ID = "feedback";

      private static final String SAVE_ID = "save";

      private static final String CANCEL_ID = "cancel";

      private final BootstrapForm<Offer> offerEditForm;

      private final OfferRecordPanel offerRecordPanel;

      private final CancelAjaxLink cancelAjaxLink;

      private final SaveAjaxButton saveAjaxButton;

      private final NotificationPanel feedbackPanel;

      public OfferEditTable(final String id, final IModel<Offer> model) {
        super(id, model);
        offerEditForm = new BootstrapForm<Offer>(OFFER_EDIT_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Offer>((IModel<Offer>) OfferEditTable.this.getDefaultModel()));
        cancelAjaxLink = new CancelAjaxLink(CANCEL_ID, model, Buttons.Type.Default,
            Model.of(OfferEditTable.this.getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID,
            Model.of(OfferEditTable.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)),
            offerEditForm, Buttons.Type.Primary);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
        offerRecordPanel =
            new OfferRecordPanel(OFFER_RECORD_PANEL_ID, (IModel<Offer>) OfferEditTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        offerEditForm.add(
            new RequiredTextField<String>(OFFER_ID_ID).add(StringValidator.maximumLength(64)).setOutputMarkupId(true));
        offerEditForm.add(new RequiredTextField<String>(CONTRACT_CONTRACT_ID_ID).add(StringValidator.maximumLength(127))
            .setOutputMarkupId(true));
        offerEditForm.add(new NumberTextField<BigDecimal>(ITEM_TOTAL_ID).add(RangeValidator.minimum(BigDecimal.ZERO))
            .setOutputMarkupId(true));
        offerEditForm.add(
            new TextArea<String>(OFFER_DESCRIPTION_ID).add(StringValidator.maximumLength(127)).setOutputMarkupId(true));
        offerEditForm.add(new NumberTextField<BigDecimal>(EXTRA_AMOUNT_ID).setRequired(true)
            .add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        offerEditForm.add(new NumberTextField<BigDecimal>(DISCOUNT_TOTAL_ID)
            .add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        offerEditForm.add(new NumberTextField<BigDecimal>(HANDLING_TOTAL_ID).setRequired(true)
            .add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        offerEditForm.add(new NumberTextField<BigDecimal>(SHIPPING_TOTAL_ID)
            .add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        offerEditForm.add(new NumberTextField<BigDecimal>(INSURANCE_TOTAL_ID).setRequired(true)
            .add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        offerEditForm.add(new NumberTextField<BigDecimal>(SHIPPING_DISCOUNT_ID).setRequired(true)
            .add(RangeValidator.minimum(BigDecimal.ZERO)).setOutputMarkupId(true));
        offerEditForm.add(new NumberTextField<BigDecimal>(TAX_TOTAL_ID).add(RangeValidator.minimum(BigDecimal.ZERO))
            .setOutputMarkupId(true));
        offerEditForm.add(new NumberTextField<BigDecimal>(OFFER_TOTAL_ID).add(RangeValidator.minimum(BigDecimal.ZERO))
            .setOutputMarkupId(true));
        offerEditForm.add(offerRecordPanel.add(offerRecordPanel.new OfferRecordEditFragment()).setOutputMarkupId(true));
        add(offerEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(cancelAjaxLink.setOutputMarkupId(true));
        add(saveAjaxButton.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String OFFER_EDIT_TABLE_ID = "offerEditTable";

    private static final String OFFER_EDIT_FRAGMENT_MARKUP_ID = "offerEditFragment";

    private static final String OFFER_VIEW_OR_EDIT_FRAGMENT_ID = "offerViewOrEditFragment";

    private static final long serialVersionUID = -5645656866901827543L;

    private final OfferEditTable offerEditTable;

    public OfferEditFragment() {
      super(OFFER_VIEW_OR_EDIT_FRAGMENT_ID, OFFER_EDIT_FRAGMENT_MARKUP_ID, OfferViewOrEditPanel.this,
          OfferViewOrEditPanel.this.getDefaultModel());
      offerEditTable =
          new OfferEditTable(OFFER_EDIT_TABLE_ID, (IModel<Offer>) OfferEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(offerEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class OfferViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class OfferViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class EditAjaxLink extends BootstrapAjaxLink<Offer> {

        private static final long serialVersionUID = 4267535261864907719L;

        public EditAjaxLink(final String id, final IModel<Offer> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.edit);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          OfferViewOrEditPanel.this.removeAll();
          target.add(
              OfferViewOrEditPanel.this.add(OfferViewOrEditPanel.this.new OfferEditFragment().setOutputMarkupId(true)));
        }
      }

      private static final String OFFER_TOTAL_ID = "offerTotal";

      private static final String TAX_TOTAL_ID = "taxTotal";

      private static final String SHIPPING_DISCOUNT_ID = "shippingDiscount";

      private static final String INSURANCE_TOTAL_ID = "insuranceTotal";

      private static final String SHIPPING_TOTAL_ID = "shippingTotal";

      private static final String HANDLING_TOTAL_ID = "handlingTotal";

      private static final String DISCOUNT_TOTAL_ID = "discountTotal";

      private static final String EXTRA_AMOUNT_ID = "extraAmount";

      private static final String OFFER_DESCRIPTION_ID = "offerDescription";

      private static final String ITEM_TOTAL_ID = "itemTotal";

      private static final String CONTRACT_CONTRACT_ID_ID = "contract.contractId";

      private static final String OFFER_ID_ID = "offerId";

      private static final String OFFER_RECORD_PANEL_ID = "offerRecordPanel";

      private static final String OFFER_VIEW_FORM_ID = "offerViewForm";

      private static final long serialVersionUID = -7208630721706186595L;

      private static final String EDIT_ID = "edit";

      private final EditAjaxLink editAjaxLink;

      private final BootstrapForm<Offer> offerViewForm;

      private final OfferRecordPanel offerRecordPanel;

      public OfferViewTable(final String id, final IModel<Offer> model) {
        super(id, model);
        offerViewForm = new BootstrapForm<Offer>(OFFER_VIEW_FORM_ID,
            new CompoundPropertyModel<Offer>((IModel<Offer>) OfferViewTable.this.getDefaultModel()));
        editAjaxLink = new EditAjaxLink(EDIT_ID, model, Buttons.Type.Primary,
            Model.of(OfferViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.EDIT_MESSAGE_KEY)));
        offerRecordPanel =
            new OfferRecordPanel(OFFER_RECORD_PANEL_ID, (IModel<Offer>) OfferViewTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        offerViewForm.add(new RequiredTextField<String>(OFFER_ID_ID).setOutputMarkupId(true));
        offerViewForm.add(new RequiredTextField<String>(CONTRACT_CONTRACT_ID_ID).setOutputMarkupId(true));
        offerViewForm.add(new NumberTextField<BigDecimal>(ITEM_TOTAL_ID).setOutputMarkupId(true));
        offerViewForm.add(new TextArea<String>(OFFER_DESCRIPTION_ID).setOutputMarkupId(true));
        offerViewForm.add(new NumberTextField<BigDecimal>(EXTRA_AMOUNT_ID).setOutputMarkupId(true));
        offerViewForm.add(new NumberTextField<BigDecimal>(DISCOUNT_TOTAL_ID).setOutputMarkupId(true));
        offerViewForm.add(new NumberTextField<BigDecimal>(HANDLING_TOTAL_ID).setOutputMarkupId(true));
        offerViewForm.add(new NumberTextField<BigDecimal>(SHIPPING_TOTAL_ID).setOutputMarkupId(true));
        offerViewForm.add(new NumberTextField<BigDecimal>(INSURANCE_TOTAL_ID).setOutputMarkupId(true));
        offerViewForm.add(new NumberTextField<BigDecimal>(SHIPPING_DISCOUNT_ID).setOutputMarkupId(true));
        offerViewForm.add(new NumberTextField<BigDecimal>(TAX_TOTAL_ID).setOutputMarkupId(true));
        offerViewForm.add(new NumberTextField<BigDecimal>(OFFER_TOTAL_ID).setOutputMarkupId(true));
        offerViewForm.add(offerRecordPanel.add(offerRecordPanel.new OfferRecordViewFragment()).setOutputMarkupId(true));
        add(editAjaxLink.setOutputMarkupId(true));
        add(offerViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String OFFER_VIEW_TABLE_ID = "offerViewTable";

    private static final String OFFER_VIEW_FRAGMENT_MARKUP_ID = "offerViewFragment";

    private static final String OFFER_VIEW_OR_EDIT_FRAGMENT_ID = "offerViewOrEditFragment";

    private static final long serialVersionUID = 2134263849806147209L;

    private final OfferViewTable offerViewTable;

    public OfferViewFragment() {
      super(OFFER_VIEW_OR_EDIT_FRAGMENT_ID, OFFER_VIEW_FRAGMENT_MARKUP_ID, OfferViewOrEditPanel.this,
          OfferViewOrEditPanel.this.getDefaultModel());
      offerViewTable =
          new OfferViewTable(OFFER_VIEW_TABLE_ID, (IModel<Offer>) OfferViewOrEditPanel.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(offerViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(OfferViewOrEditPanel.class);

  private static final long serialVersionUID = 4702200954395165271L;

  @SpringBean(name = OFFER_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Offer> offerDataProvider;

  public OfferViewOrEditPanel(final String id, final IModel<Offer> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    offerDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    offerDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    offerDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    offerDataProvider.setType(new Offer());
    offerDataProvider.getType().setActive(true);
    super.onInitialize();
  }
}
