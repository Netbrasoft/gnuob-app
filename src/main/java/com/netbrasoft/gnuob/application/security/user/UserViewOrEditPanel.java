package com.netbrasoft.gnuob.application.security.user;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.GROUP_DATA_PROVIDER_NAME;
import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.SITE_DATA_PROVIDER_NAME;
import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.USER_DATA_PROVIDER_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Group;
import com.netbrasoft.gnuob.api.Role;
import com.netbrasoft.gnuob.api.Rule;
import com.netbrasoft.gnuob.api.Site;
import com.netbrasoft.gnuob.api.User;
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
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapMultiSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
public class UserViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
  class UserEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
    class UserEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
      class CancelAjaxLink extends BootstrapAjaxLink<User> {

        private static final long serialVersionUID = 4267535261864907719L;

        public CancelAjaxLink(final String id, final IModel<User> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          UserViewOrEditPanel.this.removeAll();
          if (((User) CancelAjaxLink.this.getDefaultModelObject()).getId() > 0) {
            CancelAjaxLink.this
                .setDefaultModelObject(userDataProvider.findById((User) CancelAjaxLink.this.getDefaultModelObject()));
          }
          target.add(
              UserViewOrEditPanel.this.add(UserViewOrEditPanel.this.new UserViewFragment()).setOutputMarkupId(true));
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
              Model.of(UserViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY))));
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          form.add(new TooltipValidation());
          target.add(form);
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
              .of(UserViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          try {
            if (((User) form.getDefaultModelObject()).getId() == 0) {
              UserEditTable.this.setDefaultModelObject(
                  userDataProvider.findById(userDataProvider.persist((User) form.getDefaultModelObject())));
            } else {
              UserEditTable.this.setDefaultModelObject(
                  userDataProvider.findById(userDataProvider.merge((User) form.getDefaultModelObject())));
            }
            UserViewOrEditPanel.this.removeAll();
            target.add(UserViewOrEditPanel.this.getParent().setOutputMarkupId(true));
            target.add(
                UserViewOrEditPanel.this.add(UserViewOrEditPanel.this.new UserViewFragment()).setOutputMarkupId(true));
          } catch (final RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
            feedbackPanel.warn(e.getLocalizedMessage());
            target.add(feedbackPanel.setOutputMarkupId(true));
            target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
                .of(UserViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
          }
        }
      }

      private static final String ROLES_ID = "roles";

      private static final String CPASSWORD_ID = "cpassword";

      private static final String PASSWORD_ID = "password";

      private static final String ACCESS_ID = "access";

      private static final String DESCRIPTION_ID = "description";

      private static final String NAME_ID = "name";

      private static final String USER_EDIT_FORM_COMPONENT_ID = "userEditForm";

      private static final long serialVersionUID = 3535754607916237212L;

      private static final String CANCEL_ID = "cancel";

      private static final String SAVE_ID = "save";

      private static final String FEEDBACK_ID = "feedback";

      private static final String SITES_ID = "sites";

      private static final String GROUPS_ID = "groups";

      private final CancelAjaxLink cancelAjaxLink;

      private final SaveAjaxButton saveAjaxButton;

      private final NotificationPanel feedbackPanel;

      private final BootstrapForm<User> userEditForm;

      public UserEditTable(final String id, final IModel<User> model) {
        super(id, model);
        userEditForm = new BootstrapForm<User>(USER_EDIT_FORM_COMPONENT_ID,
            new CompoundPropertyModel<User>((IModel<User>) UserEditTable.this.getDefaultModel()));
        cancelAjaxLink = new CancelAjaxLink(CANCEL_ID, model, Buttons.Type.Default,
            Model.of(UserViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID,
            Model.of(UserViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)),
            userEditForm, Buttons.Type.Primary);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
      }

      @Override
      protected void onInitialize() {
        userEditForm
            .add(new BootstrapSelect<Rule>(ACCESS_ID, Arrays.asList(Rule.values()), new IChoiceRenderer<Rule>() {

              private static final long serialVersionUID = -1398366720610788897L;

              @Override
              public Object getDisplayValue(final Rule object) {
                return UserEditTable.this.getString(object.name()).toUpperCase();
              }

              @Override
              public String getIdValue(final Rule object, final int index) {
                return object.value();
              }

              @Override
              public Rule getObject(final String id, final IModel<? extends List<? extends Rule>> choices) {
                for (final Rule rule : choices.getObject()) {
                  if (id.equals(rule.value())) {
                    return rule;
                  }
                }
                return null;
              }

            }).setOutputMarkupId(true));
        userEditForm.add(new RequiredTextField<String>(NAME_ID));
        userEditForm.add(new PasswordTextField(PASSWORD_ID).setRequired(true));
        userEditForm.add(new PasswordTextField(CPASSWORD_ID, Model.of("")).setRequired(true));
        userEditForm
            .add(new BootstrapMultiSelect<Role>(ROLES_ID, Arrays.asList(Role.values()), new IChoiceRenderer<Role>() {

              private static final long serialVersionUID = 1L;

              @Override
              public Object getDisplayValue(final Role object) {
                return UserEditTable.this.getString(object.name()).toUpperCase();
              }

              @Override
              public String getIdValue(final Role object, final int index) {
                return object.value();
              }

              @Override
              public Role getObject(final String id, final IModel<? extends List<? extends Role>> choices) {
                for (final Role role : choices.getObject()) {
                  if (id.equals(role.value())) {
                    return role;
                  }
                }
                return null;
              }
            }));
        userEditForm.add(new TextArea<String>(DESCRIPTION_ID));
        userEditForm.add(new BootstrapMultiSelect<Group>(GROUPS_ID,
            Model.of(((IModel<User>) UserEditTable.this.getDefaultModel()).getObject().getGroups()),
            getAllAvailableGroups(), new IChoiceRenderer<Group>() {

              private static final long serialVersionUID = 1L;

              @Override
              public Object getDisplayValue(final Group object) {
                return object.getName().toUpperCase();
              }

              @Override
              public String getIdValue(final Group object, final int index) {
                return String.valueOf(object.getId());
              }

              @Override
              public Group getObject(final String id, final IModel<? extends List<? extends Group>> choices) {
                for (final Group group : choices.getObject()) {
                  if (id.equals(String.valueOf(group.getId()))) {
                    return group;
                  }
                }
                return null;
              }
            }).setRequired(true));
        userEditForm.add(new BootstrapMultiSelect<Site>(SITES_ID,
            Model.of(((IModel<User>) UserEditTable.this.getDefaultModel()).getObject().getSites()),
            getAllAvailableSites(), new IChoiceRenderer<Site>() {

              private static final long serialVersionUID = 1L;

              @Override
              public Object getDisplayValue(final Site object) {
                return object.getName().toUpperCase();
              }

              @Override
              public String getIdValue(final Site object, final int index) {
                return String.valueOf(object.getId());
              }

              @Override
              public Site getObject(final String id, final IModel<? extends List<? extends Site>> choices) {
                for (final Site site : choices.getObject()) {
                  if (id.equals(String.valueOf(site.getId()))) {
                    return site;
                  }
                }
                return null;
              }
            }).setRequired(true));
        add(userEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(saveAjaxButton.setOutputMarkupId(true));
        add(cancelAjaxLink.setOutputMarkupId(true));
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String USER_EDIT_TABLE_ID = "userEditTable";

    private static final String USER_EDIT_FRAGMENT_MARKUP_ID = "userEditFragment";

    private static final String USER_VIEW_OR_EDIT_FRAGMENT_ID = "userViewOrEditFragment";

    private static final long serialVersionUID = 8971798392355786447L;

    private final UserEditTable userEditTable;

    public UserEditFragment() {
      super(USER_VIEW_OR_EDIT_FRAGMENT_ID, USER_EDIT_FRAGMENT_MARKUP_ID, UserViewOrEditPanel.this,
          UserViewOrEditPanel.this.getDefaultModel());
      userEditTable = new UserEditTable(USER_EDIT_TABLE_ID, (IModel<User>) UserEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(userEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
  class UserViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER})
    class UserViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR})
      class EditAjaxLink extends BootstrapAjaxLink<User> {

        private static final long serialVersionUID = 4267535261864907719L;

        public EditAjaxLink(final String id, final IModel<User> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.edit);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          UserViewOrEditPanel.this.removeAll();
          target.add(UserViewOrEditPanel.this.add(new UserEditFragment().setOutputMarkupId(true)));
        }
      }

      private static final String SITES_ID = "sites";

      private static final String GROUPS_ID = "groups";

      private static final String ROLES_ID = "roles";

      private static final String USER_VIEW_FORM_COMPONENT_ID = "userViewForm";

      private static final String ACCESS_ID = "access";

      private static final String DESCRIPTION_ID = "description";

      private static final String NAME_ID = "name";

      private static final long serialVersionUID = 5946293942432844492L;

      private static final String EDIT_ID = "edit";

      private final EditAjaxLink editAjaxLink;

      private final BootstrapForm<User> userViewForm;

      public UserViewTable(final String id, final IModel<User> model) {
        super(id, model);
        userViewForm = new BootstrapForm<User>(USER_VIEW_FORM_COMPONENT_ID,
            new CompoundPropertyModel<User>((IModel<User>) UserViewTable.this.getDefaultModel()));
        editAjaxLink =
            new EditAjaxLink(EDIT_ID, (IModel<User>) UserViewTable.this.getDefaultModel(), Buttons.Type.Primary,
                Model.of(UserViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.EDIT_MESSAGE_KEY)));
      }

      @Override
      protected void onInitialize() {
        userViewForm
            .add(new BootstrapSelect<Rule>(ACCESS_ID, Arrays.asList(Rule.values()), new IChoiceRenderer<Rule>() {

              private static final long serialVersionUID = -1398366720610788897L;

              @Override
              public Object getDisplayValue(final Rule object) {
                return UserViewTable.this.getString(object.name()).toUpperCase();
              }

              @Override
              public String getIdValue(final Rule object, final int index) {
                return object.value();
              }

              @Override
              public Rule getObject(final String id, final IModel<? extends List<? extends Rule>> choices) {
                for (final Rule rule : choices.getObject()) {
                  if (id.equals(rule.value())) {
                    return rule;
                  }
                }
                return null;
              }

            }).setOutputMarkupId(true));
        userViewForm.add(new RequiredTextField<String>(NAME_ID).setOutputMarkupId(true));
        userViewForm
            .add(new BootstrapMultiSelect<Role>(ROLES_ID, Arrays.asList(Role.values()), new IChoiceRenderer<Role>() {

              private static final long serialVersionUID = 1L;

              @Override
              public Object getDisplayValue(final Role object) {
                return UserViewTable.this.getString(object.name()).toUpperCase();
              }

              @Override
              public String getIdValue(final Role object, final int index) {
                return object.value();
              }

              @Override
              public Role getObject(final String id, final IModel<? extends List<? extends Role>> choices) {
                for (final Role role : choices.getObject()) {
                  if (id.equals(role.value())) {
                    return role;
                  }
                }
                return null;
              }
            }));
        userViewForm.add(new TextArea<String>(DESCRIPTION_ID).setOutputMarkupId(true));
        userViewForm
            .add(new BootstrapMultiSelect<Group>(GROUPS_ID, getAllAvailableGroups(), new IChoiceRenderer<Group>() {

              private static final long serialVersionUID = 1L;

              @Override
              public Object getDisplayValue(final Group object) {
                return object.getName().toUpperCase();
              }

              @Override
              public String getIdValue(final Group object, final int index) {
                return String.valueOf(object.getId());
              }

              @Override
              public Group getObject(final String id, final IModel<? extends List<? extends Group>> choices) {
                for (final Group group : choices.getObject()) {
                  if (id.equals(String.valueOf(group.getId()))) {
                    return group;
                  }
                }
                return null;
              }
            }));
        userViewForm.add(new BootstrapMultiSelect<Site>(SITES_ID, getAllAvailableSites(), new IChoiceRenderer<Site>() {

          private static final long serialVersionUID = 1L;

          @Override
          public Object getDisplayValue(final Site object) {
            return object.getName().toUpperCase();
          }

          @Override
          public String getIdValue(final Site object, final int index) {
            return String.valueOf(object.getId());
          }

          @Override
          public Site getObject(final String id, final IModel<? extends List<? extends Site>> choices) {
            for (final Site site : choices.getObject()) {
              if (id.equals(String.valueOf(site.getId()))) {
                return site;
              }
            }
            return null;
          }
        }));
        add(userViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(editAjaxLink.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String USER_VIEW_OR_EDIT_FRAGMENT_ID = "userViewOrEditFragment";

    private static final String USER_VIEW_FRAGMENT_MARKUP_ID = "userViewFragment";

    private static final String USER_VIEW_TABLE_ID = "userViewTable";

    private static final long serialVersionUID = 498703216819003839L;

    private final UserViewTable userViewTable;

    public UserViewFragment() {
      super(USER_VIEW_OR_EDIT_FRAGMENT_ID, USER_VIEW_FRAGMENT_MARKUP_ID, UserViewOrEditPanel.this,
          UserViewOrEditPanel.this.getDefaultModel());
      userViewTable = new UserViewTable(USER_VIEW_TABLE_ID, (IModel<User>) UserViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(userViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = -8401960249843479048L;

  private static final Logger LOGGER = LoggerFactory.getLogger(UserViewOrEditPanel.class);

  @SpringBean(name = USER_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<User> userDataProvider;

  @SpringBean(name = GROUP_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Group> groupDataProvider;

  @SpringBean(name = SITE_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Site> siteDataProvider;

  public UserViewOrEditPanel(final String id, final IModel<User> model) {
    super(id, model);
    userDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    userDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    userDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getSite());
    userDataProvider.setType(new User());
    userDataProvider.getType().setActive(true);

    groupDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    groupDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    groupDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    groupDataProvider.setType(new Group());
    groupDataProvider.getType().setActive(true);

    siteDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    siteDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    siteDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    siteDataProvider.setType(new Site());
    siteDataProvider.getType().setActive(true);
    super.onInitialize();
  }

  private List<Group> getAllAvailableGroups() {
    final List<Group> sites = new ArrayList<Group>();
    final Iterator<? extends Group> siteIterator = groupDataProvider.iterator(-1, -1);

    while (siteIterator.hasNext()) {
      sites.add(siteIterator.next());
    }
    return sites;
  }

  private List<Site> getAllAvailableSites() {
    final List<Site> groups = new ArrayList<Site>();
    final Iterator<? extends Site> groupIterator = siteDataProvider.iterator(-1, -1);

    while (groupIterator.hasNext()) {
      groups.add(groupIterator.next());
    }
    return groups;
  }
}
