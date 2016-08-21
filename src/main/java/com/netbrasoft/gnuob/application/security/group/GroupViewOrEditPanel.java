package com.netbrasoft.gnuob.application.security.group;

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

import com.netbrasoft.gnuob.api.Group;
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
public class GroupViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
  class GroupEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
    class GroupEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
      class CancelAjaxLink extends BootstrapAjaxLink<Group> {

        private static final long serialVersionUID = 4267535261864907719L;

        public CancelAjaxLink(final String id, final IModel<Group> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          GroupViewOrEditPanel.this.removeAll();
          if (((Group) CancelAjaxLink.this.getDefaultModelObject()).getId() > 0) {
            CancelAjaxLink.this
                .setDefaultModelObject(groupDataProvider.findById((Group) CancelAjaxLink.this.getDefaultModelObject()));
          }
          target.add(
              GroupViewOrEditPanel.this.add(GroupViewOrEditPanel.this.new GroupViewFragment()).setOutputMarkupId(true));
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
              .of(GroupViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY))));
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          form.add(new TooltipValidation());
          target.add(form);
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
              .of(GroupViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          try {
            if (((Group) form.getDefaultModelObject()).getId() == 0) {
              GroupEditTable.this.setDefaultModelObject(
                  groupDataProvider.findById(groupDataProvider.persist((Group) form.getDefaultModelObject())));
            } else {
              GroupEditTable.this.setDefaultModelObject(
                  groupDataProvider.findById(groupDataProvider.merge((Group) form.getDefaultModelObject())));
            }
            GroupViewOrEditPanel.this.removeAll();
            target.add(GroupViewOrEditPanel.this.getParent().setOutputMarkupId(true));
            target.add(GroupViewOrEditPanel.this.add(GroupViewOrEditPanel.this.new GroupViewFragment())
                .setOutputMarkupId(true));
          } catch (final RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
            feedbackPanel.warn(e.getLocalizedMessage());
            target.add(feedbackPanel.setOutputMarkupId(true));
            target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
                .of(GroupViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
          }
        }
      }

      private static final String DESCRIPTION_ID = "description";

      private static final String NAME_ID = "name";

      private static final String GROUP_EDIT_FORM_COMPONENT_ID = "groupEditForm";

      private static final long serialVersionUID = 3535754607916237212L;

      private static final String CANCEL_ID = "cancel";

      private static final String SAVE_ID = "save";

      private static final String FEEDBACK_ID = "feedback";

      private final CancelAjaxLink cancelAjaxLink;

      private final SaveAjaxButton saveAjaxButton;

      private final NotificationPanel feedbackPanel;

      private final BootstrapForm<Group> groupEditForm;

      public GroupEditTable(final String id, final IModel<Group> model) {
        super(id, model);
        groupEditForm = new BootstrapForm<Group>(GROUP_EDIT_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Group>((IModel<Group>) GroupEditTable.this.getDefaultModel()));
        cancelAjaxLink = new CancelAjaxLink(CANCEL_ID, model, Buttons.Type.Default,
            Model.of(GroupViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID,
            Model.of(GroupViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)),
            groupEditForm, Buttons.Type.Primary);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
      }

      @Override
      protected void onInitialize() {
        groupEditForm.add(new RequiredTextField<String>(NAME_ID)
            .setLabel(Model.of(GroupEditTable.this.getString(NetbrasoftApplicationConstants.VALUE_MESSAGE_KEY)))
            .add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        groupEditForm
            .add(new TextArea<String>(DESCRIPTION_ID)
                .setLabel(
                    Model.of(GroupEditTable.this.getString(NetbrasoftApplicationConstants.DESCRIPTION_MESSAGE_KEY)))
                .setRequired(true).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        add(groupEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(saveAjaxButton.setOutputMarkupId(true));
        add(cancelAjaxLink.setOutputMarkupId(true));
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String GROUP_EDIT_TABLE_ID = "groupEditTable";

    private static final String GROUP_EDIT_FRAGMENT_MARKUP_ID = "groupEditFragment";

    private static final String GROUP_VIEW_OR_EDIT_FRAGMENT_ID = "groupViewOrEditFragment";

    private static final long serialVersionUID = 8971798392355786447L;

    private final GroupEditTable groupEditTable;

    public GroupEditFragment() {
      super(GROUP_VIEW_OR_EDIT_FRAGMENT_ID, GROUP_EDIT_FRAGMENT_MARKUP_ID, GroupViewOrEditPanel.this,
          GroupViewOrEditPanel.this.getDefaultModel());
      groupEditTable =
          new GroupEditTable(GROUP_EDIT_TABLE_ID, (IModel<Group>) GroupEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(groupEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
  class GroupViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
    class GroupViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
      class EditAjaxLink extends BootstrapAjaxLink<Group> {

        private static final long serialVersionUID = 4267535261864907719L;

        public EditAjaxLink(final String id, final IModel<Group> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.edit);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          GroupViewOrEditPanel.this.removeAll();
          target.add(GroupViewOrEditPanel.this.add(new GroupEditFragment().setOutputMarkupId(true)));
        }
      }

      private static final String GROUP_VIEW_FORM_COMPONENT_ID = "groupViewForm";

      private static final String NAME_ID = "name";

      private static final String DESCRIPTION_ID = "description";

      private static final long serialVersionUID = 5946293942432844492L;

      private static final String EDIT_ID = "edit";

      private final EditAjaxLink editAjaxLink;

      private final BootstrapForm<Group> groupViewForm;

      public GroupViewTable(final String id, final IModel<Group> model) {
        super(id, model);
        groupViewForm = new BootstrapForm<Group>(GROUP_VIEW_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Group>((IModel<Group>) GroupViewTable.this.getDefaultModel()));
        editAjaxLink =
            new EditAjaxLink(EDIT_ID, (IModel<Group>) GroupViewTable.this.getDefaultModel(), Buttons.Type.Primary,
                Model.of(GroupViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.EDIT_MESSAGE_KEY)));
      }

      @Override
      protected void onInitialize() {
        groupViewForm.add(new RequiredTextField<String>(NAME_ID).setOutputMarkupId(true));
        groupViewForm.add(new TextArea<String>(DESCRIPTION_ID).setOutputMarkupId(true));
        add(groupViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(editAjaxLink.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String GROUP_VIEW_OR_EDIT_FRAGMENT_ID = "groupViewOrEditFragment";

    private static final String GROUP_VIEW_FRAGMENT_MARKUP_ID = "groupViewFragment";

    private static final String GROUP_VIEW_TABLE_ID = "groupViewTable";

    private static final long serialVersionUID = 498703216819003839L;

    private final GroupViewTable groupViewTable;

    public GroupViewFragment() {
      super(GROUP_VIEW_OR_EDIT_FRAGMENT_ID, GROUP_VIEW_FRAGMENT_MARKUP_ID, GroupViewOrEditPanel.this,
          GroupViewOrEditPanel.this.getDefaultModel());
      groupViewTable =
          new GroupViewTable(GROUP_VIEW_TABLE_ID, (IModel<Group>) GroupViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(groupViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = -8401960249843479048L;

  private static final Logger LOGGER = LoggerFactory.getLogger(GroupViewOrEditPanel.class);

  @SpringBean(name = GROUP_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Group> groupDataProvider;

  public GroupViewOrEditPanel(final String id, final IModel<Group> model) {
    super(id, model);
    groupDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    groupDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    groupDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    groupDataProvider.setType(new Group());
    groupDataProvider.getType().setActive(true);
    super.onInitialize();
  }
}
