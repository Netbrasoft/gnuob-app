package com.netbrasoft.gnuob.application.setting;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.SETTING_DATA_PROVIDER_NAME;

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

import com.netbrasoft.gnuob.api.Setting;
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
public class SettingViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
  class SettingEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
    class SettingEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
      class CancelAjaxLink extends BootstrapAjaxLink<Setting> {

        private static final long serialVersionUID = 4267535261864907719L;

        public CancelAjaxLink(final String id, final IModel<Setting> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          SettingViewOrEditPanel.this.removeAll();
          if (((Setting) CancelAjaxLink.this.getDefaultModelObject()).getId() > 0) {
            CancelAjaxLink.this.setDefaultModelObject(
                settingDataProvider.findById((Setting) CancelAjaxLink.this.getDefaultModelObject()));
          }
          target.add(SettingViewOrEditPanel.this.add(SettingViewOrEditPanel.this.new SettingViewFragment())
              .setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
      class SaveAjaxButton extends BootstrapAjaxButton {

        private static final long serialVersionUID = 2695394292963384938L;

        public SaveAjaxButton(final String id, final IModel<String> model, final Form<?> form,
            final Buttons.Type type) {
          super(id, model, form, type);
          setSize(Buttons.Size.Small);
          add(new LoadingBehavior(Model
              .of(SettingViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY))));
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          form.add(new TooltipValidation());
          target.add(form);
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
              .of(SettingViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          try {
            if (((Setting) form.getDefaultModelObject()).getId() == 0) {
              SettingEditTable.this.setDefaultModelObject(
                  settingDataProvider.findById(settingDataProvider.persist((Setting) form.getDefaultModelObject())));
            } else {
              SettingEditTable.this.setDefaultModelObject(
                  settingDataProvider.findById(settingDataProvider.merge((Setting) form.getDefaultModelObject())));
            }
            SettingViewOrEditPanel.this.removeAll();
            target.add(SettingViewOrEditPanel.this.getParent().setOutputMarkupId(true));
            target.add(SettingViewOrEditPanel.this.add(SettingViewOrEditPanel.this.new SettingViewFragment())
                .setOutputMarkupId(true));
          } catch (final RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
            feedbackPanel.warn(e.getLocalizedMessage());
            target.add(feedbackPanel.setOutputMarkupId(true));
            target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(
                SettingViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
          }
        }
      }

      private static final String DESCRIPTION_ID = "description";

      private static final String VALUE_ID = "value";

      private static final String SETTING_EDIT_FORM_COMPONENT_ID = "settingEditForm";

      private static final long serialVersionUID = 3535754607916237212L;

      private static final String CANCEL_ID = "cancel";

      private static final String SAVE_ID = "save";

      private static final String FEEDBACK_ID = "feedback";

      private static final String PROPERTY_ID = "property";

      private final CancelAjaxLink cancelAjaxLink;

      private final SaveAjaxButton saveAjaxButton;

      private final NotificationPanel feedbackPanel;

      private final BootstrapForm<Setting> settingEditForm;

      public SettingEditTable(final String id, final IModel<Setting> model) {
        super(id, model);
        settingEditForm = new BootstrapForm<Setting>(SETTING_EDIT_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Setting>((IModel<Setting>) SettingEditTable.this.getDefaultModel()));
        cancelAjaxLink = new CancelAjaxLink(CANCEL_ID, model, Buttons.Type.Default,
            Model.of(SettingViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID,
            Model.of(SettingViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)),
            settingEditForm, Buttons.Type.Primary);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
      }

      @Override
      protected void onInitialize() {
        settingEditForm
            .add(new RequiredTextField<String>(PROPERTY_ID)
                .setLabel(
                    Model.of(SettingEditTable.this.getString(NetbrasoftApplicationConstants.PROPERTY_MESSAGE_KEY)))
                .add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        settingEditForm.add(new RequiredTextField<String>(VALUE_ID)
            .setLabel(Model.of(SettingEditTable.this.getString(NetbrasoftApplicationConstants.VALUE_MESSAGE_KEY)))
            .add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        settingEditForm
            .add(
                new TextArea<String>(DESCRIPTION_ID)
                    .setLabel(Model
                        .of(SettingEditTable.this.getString(NetbrasoftApplicationConstants.DESCRIPTION_MESSAGE_KEY)))
                    .setRequired(true).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        add(settingEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(saveAjaxButton.setOutputMarkupId(true));
        add(cancelAjaxLink.setOutputMarkupId(true));
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SETTING_EDIT_TABLE_ID = "settingEditTable";

    private static final String SETTING_EDIT_FRAGMENT_MARKUP_ID = "settingEditFragment";

    private static final String SETTING_VIEW_OR_EDIT_FRAGMENT_ID = "settingViewOrEditFragment";

    private static final long serialVersionUID = 8971798392355786447L;

    private final SettingEditTable settingEditTable;

    public SettingEditFragment() {
      super(SETTING_VIEW_OR_EDIT_FRAGMENT_ID, SETTING_EDIT_FRAGMENT_MARKUP_ID, SettingViewOrEditPanel.this,
          SettingViewOrEditPanel.this.getDefaultModel());
      settingEditTable =
          new SettingEditTable(SETTING_EDIT_TABLE_ID, (IModel<Setting>) SettingEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(settingEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
  class SettingViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
    class SettingViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
      class EditAjaxLink extends BootstrapAjaxLink<Setting> {

        private static final long serialVersionUID = 4267535261864907719L;

        public EditAjaxLink(final String id, final IModel<Setting> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.edit);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          SettingViewOrEditPanel.this.removeAll();
          target.add(SettingViewOrEditPanel.this.add(new SettingEditFragment().setOutputMarkupId(true)));
        }
      }

      private static final String SETTING_VIEW_FORM_COMPONENT_ID = "settingViewForm";

      private static final String PROPERTY_ID = "property";

      private static final String DESCRIPTION_ID = "description";

      private static final long serialVersionUID = 5946293942432844492L;

      private static final String EDIT_ID = "edit";

      private static final String VALUE_ID = "value";

      private final EditAjaxLink editAjaxLink;

      private final BootstrapForm<Setting> settingViewForm;

      public SettingViewTable(final String id, final IModel<Setting> model) {
        super(id, model);
        settingViewForm = new BootstrapForm<Setting>(SETTING_VIEW_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Setting>((IModel<Setting>) SettingViewTable.this.getDefaultModel()));
        editAjaxLink =
            new EditAjaxLink(EDIT_ID, (IModel<Setting>) SettingViewTable.this.getDefaultModel(), Buttons.Type.Primary,
                Model.of(SettingViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.EDIT_MESSAGE_KEY)));
      }

      @Override
      protected void onInitialize() {
        settingViewForm.add(new RequiredTextField<String>(PROPERTY_ID).setOutputMarkupId(true));
        settingViewForm.add(new RequiredTextField<String>(VALUE_ID).setOutputMarkupId(true));
        settingViewForm.add(new TextArea<String>(DESCRIPTION_ID).setOutputMarkupId(true));
        add(settingViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(editAjaxLink.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SETTING_VIEW_OR_EDIT_FRAGMENT_ID = "settingViewOrEditFragment";

    private static final String SETTING_VIEW_FRAGMENT_MARKUP_ID = "settingViewFragment";

    private static final String SETTING_VIEW_TABLE_ID = "settingViewTable";

    private static final long serialVersionUID = 498703216819003839L;

    private final SettingViewTable settingViewTable;

    public SettingViewFragment() {
      super(SETTING_VIEW_OR_EDIT_FRAGMENT_ID, SETTING_VIEW_FRAGMENT_MARKUP_ID, SettingViewOrEditPanel.this,
          SettingViewOrEditPanel.this.getDefaultModel());
      settingViewTable =
          new SettingViewTable(SETTING_VIEW_TABLE_ID, (IModel<Setting>) SettingViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(settingViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = -8401960249843479048L;

  private static final Logger LOGGER = LoggerFactory.getLogger(SettingViewOrEditPanel.class);

  @SpringBean(name = SETTING_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Setting> settingDataProvider;

  public SettingViewOrEditPanel(final String id, final IModel<Setting> model) {
    super(id, model);
    settingDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    settingDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    settingDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    settingDataProvider.setType(new Setting());
    settingDataProvider.getType().setActive(true);
    super.onInitialize();
  }
}
