package com.netbrasoft.gnuob.application.security.site;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.GROUP_DATA_PROVIDER_NAME;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Site;
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
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
public class SiteViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
  class SiteEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
    class SiteEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
      class CancelAjaxLink extends BootstrapAjaxLink<Site> {

        private static final long serialVersionUID = 4267535261864907719L;

        public CancelAjaxLink(final String id, final IModel<Site> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          SiteViewOrEditPanel.this.removeAll();
          if (((Site) CancelAjaxLink.this.getDefaultModelObject()).getId() > 0) {
            CancelAjaxLink.this
                .setDefaultModelObject(siteDataProvider.findById((Site) CancelAjaxLink.this.getDefaultModelObject()));
          }
          target.add(
              SiteViewOrEditPanel.this.add(SiteViewOrEditPanel.this.new SiteViewFragment()).setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
      class SaveAjaxButton extends BootstrapAjaxButton {

        private static final long serialVersionUID = 2695394292963384938L;

        public SaveAjaxButton(final String id, final IModel<String> model, final Form<?> form,
            final Buttons.Type type) {
          super(id, model, form, type);
          setSize(Buttons.Size.Small);
          add(new LoadingBehavior(
              Model.of(SiteViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY))));
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          form.add(new TooltipValidation());
          target.add(form);
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
              .of(SiteViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          try {
            if (((Site) form.getDefaultModelObject()).getId() == 0) {
              SiteEditTable.this.setDefaultModelObject(
                  siteDataProvider.findById(siteDataProvider.persist((Site) form.getDefaultModelObject())));
            } else {
              SiteEditTable.this.setDefaultModelObject(
                  siteDataProvider.findById(siteDataProvider.merge((Site) form.getDefaultModelObject())));
            }
            SiteViewOrEditPanel.this.removeAll();
            target.add(SiteViewOrEditPanel.this.getParent().setOutputMarkupId(true));
            target.add(
                SiteViewOrEditPanel.this.add(SiteViewOrEditPanel.this.new SiteViewFragment()).setOutputMarkupId(true));
          } catch (final RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
            feedbackPanel.warn(e.getLocalizedMessage());
            target.add(feedbackPanel.setOutputMarkupId(true));
            target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
                .of(SiteViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
          }
        }
      }

      private static final String DESCRIPTION_ID = "description";

      private static final String NAME_ID = "name";

      private static final String SITE_EDIT_FORM_COMPONENT_ID = "siteEditForm";

      private static final long serialVersionUID = 3535754607916237212L;

      private static final String CANCEL_ID = "cancel";

      private static final String SAVE_ID = "save";

      private static final String FEEDBACK_ID = "feedback";

      private final CancelAjaxLink cancelAjaxLink;

      private final SaveAjaxButton saveAjaxButton;

      private final NotificationPanel feedbackPanel;

      private final BootstrapForm<Site> siteEditForm;

      public SiteEditTable(final String id, final IModel<Site> model) {
        super(id, model);
        siteEditForm = new BootstrapForm<Site>(SITE_EDIT_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Site>((IModel<Site>) SiteEditTable.this.getDefaultModel()));
        cancelAjaxLink = new CancelAjaxLink(CANCEL_ID, model, Buttons.Type.Default,
            Model.of(SiteViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID,
            Model.of(SiteViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)),
            siteEditForm, Buttons.Type.Primary);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
      }

      @Override
      protected void onInitialize() {
        siteEditForm.add(new RequiredTextField<String>(NAME_ID)
            .setLabel(Model.of(SiteEditTable.this.getString(NetbrasoftApplicationConstants.VALUE_MESSAGE_KEY)))
            .add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        siteEditForm
            .add(new TextArea<String>(DESCRIPTION_ID)
                .setLabel(
                    Model.of(SiteEditTable.this.getString(NetbrasoftApplicationConstants.DESCRIPTION_MESSAGE_KEY)))
                .setRequired(true).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        add(siteEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(saveAjaxButton.setOutputMarkupId(true));
        add(cancelAjaxLink.setOutputMarkupId(true));
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SITE_EDIT_TABLE_ID = "siteEditTable";

    private static final String SITE_EDIT_FRAGMENT_MARKUP_ID = "siteEditFragment";

    private static final String SITE_VIEW_OR_EDIT_FRAGMENT_ID = "siteViewOrEditFragment";

    private static final long serialVersionUID = 8971798392355786447L;

    private final SiteEditTable siteEditTable;

    public SiteEditFragment() {
      super(SITE_VIEW_OR_EDIT_FRAGMENT_ID, SITE_EDIT_FRAGMENT_MARKUP_ID, SiteViewOrEditPanel.this,
          SiteViewOrEditPanel.this.getDefaultModel());
      siteEditTable = new SiteEditTable(SITE_EDIT_TABLE_ID, (IModel<Site>) SiteEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(siteEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
  class SiteViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
    class SiteViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
      class EditAjaxLink extends BootstrapAjaxLink<Site> {

        private static final long serialVersionUID = 4267535261864907719L;

        public EditAjaxLink(final String id, final IModel<Site> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.edit);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          SiteViewOrEditPanel.this.removeAll();
          target.add(SiteViewOrEditPanel.this.add(new SiteEditFragment().setOutputMarkupId(true)));
        }
      }

      private static final String SITE_VIEW_FORM_COMPONENT_ID = "siteViewForm";

      private static final String NAME_ID = "name";

      private static final String DESCRIPTION_ID = "description";

      private static final long serialVersionUID = 5946293942432844492L;

      private static final String EDIT_ID = "edit";

      private final EditAjaxLink editAjaxLink;

      private final BootstrapForm<Site> siteViewForm;

      public SiteViewTable(final String id, final IModel<Site> model) {
        super(id, model);
        siteViewForm = new BootstrapForm<Site>(SITE_VIEW_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Site>((IModel<Site>) SiteViewTable.this.getDefaultModel()));
        editAjaxLink =
            new EditAjaxLink(EDIT_ID, (IModel<Site>) SiteViewTable.this.getDefaultModel(), Buttons.Type.Primary,
                Model.of(SiteViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.EDIT_MESSAGE_KEY)));
      }

      @Override
      protected void onInitialize() {
        siteViewForm.add(new RequiredTextField<String>(NAME_ID).setOutputMarkupId(true));
        siteViewForm.add(new TextArea<String>(DESCRIPTION_ID).setOutputMarkupId(true));
        add(siteViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(editAjaxLink.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SITE_VIEW_OR_EDIT_FRAGMENT_ID = "siteViewOrEditFragment";

    private static final String SITE_VIEW_FRAGMENT_MARKUP_ID = "siteViewFragment";

    private static final String SITE_VIEW_TABLE_ID = "siteViewTable";

    private static final long serialVersionUID = 498703216819003839L;

    private final SiteViewTable siteViewTable;

    public SiteViewFragment() {
      super(SITE_VIEW_OR_EDIT_FRAGMENT_ID, SITE_VIEW_FRAGMENT_MARKUP_ID, SiteViewOrEditPanel.this,
          SiteViewOrEditPanel.this.getDefaultModel());
      siteViewTable = new SiteViewTable(SITE_VIEW_TABLE_ID, (IModel<Site>) SiteViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(siteViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = -8401960249843479048L;

  private static final Logger LOGGER = LoggerFactory.getLogger(SiteViewOrEditPanel.class);

  @SpringBean(name = GROUP_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Site> siteDataProvider;

  public SiteViewOrEditPanel(final String id, final IModel<Site> model) {
    super(id, model);
    siteDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    siteDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    siteDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    siteDataProvider.setType(new Site());
    siteDataProvider.getType().setActive(true);
    super.onInitialize();
  }
}
