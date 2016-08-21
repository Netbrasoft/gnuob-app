package com.netbrasoft.gnuob.application.content;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.CONTENT_DATA_PROVIDER_NAME;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Content;
import com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
import com.netbrasoft.gnuob.api.generic.converter.ByteArrayConverter;
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
import wicket.contrib.tinymce4.TinyMceBehavior;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class ContentViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class ContentEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class ContentEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class CancelAjaxLink extends BootstrapAjaxLink<Content> {

        private static final long serialVersionUID = 4267535261864907719L;

        public CancelAjaxLink(final String id, final IModel<Content> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          ContentViewOrEditPanel.this.removeAll();
          if (((Content) CancelAjaxLink.this.getDefaultModelObject()).getId() > 0) {
            CancelAjaxLink.this.setDefaultModelObject(
                contentDataProvider.findById((Content) CancelAjaxLink.this.getDefaultModelObject()));
          }
          target.add(ContentViewOrEditPanel.this.add(ContentViewOrEditPanel.this.new ContentViewFragment())
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
              .of(ContentViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY))));
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          form.add(new TooltipValidation());
          target.add(form);
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
              .of(ContentViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          try {
            if (((Content) form.getDefaultModelObject()).getId() == 0) {
              ContentEditTable.this.setDefaultModelObject(
                  contentDataProvider.findById(contentDataProvider.persist((Content) form.getDefaultModelObject())));
            } else {
              ContentEditTable.this.setDefaultModelObject(
                  contentDataProvider.findById(contentDataProvider.merge((Content) form.getDefaultModelObject())));
            }
            ContentViewOrEditPanel.this.removeAll();
            target.add(ContentViewOrEditPanel.this.add(ContentViewOrEditPanel.this.new ContentViewFragment())
                .setOutputMarkupId(true));
          } catch (final RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
            feedbackPanel.warn(e.getLocalizedMessage());
            target.add(feedbackPanel.setOutputMarkupId(true));
            target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(
                ContentViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
          }
        }
      }

      private static final String CONTENT_ID = "content";

      private static final String FORMAT_ID = "format";

      private static final String NAME_ID = "name";

      private static final String FEEDBACK_MARKUP_ID = "feedback";

      private static final String SAVE_ID = "save";

      private static final String CANCEL_ID = "cancel";

      private static final String CONTENT_EDIT_FORM_COMPONENT_ID = "contentEditForm";

      private static final long serialVersionUID = -1112121331425285822L;

      private final BootstrapForm<Content> contentEditForm;

      private final CancelAjaxLink cancelAjaxLink;

      private final SaveAjaxButton saveAjaxButton;

      private final NotificationPanel feedbackPanel;

      public ContentEditTable(final String id, final IModel<Content> model) {
        super(id, model);
        contentEditForm = new BootstrapForm<Content>(CONTENT_EDIT_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Content>((IModel<Content>) ContentEditTable.this.getDefaultModel()));
        cancelAjaxLink = new CancelAjaxLink(CANCEL_ID, model, Buttons.Type.Default,
            Model.of(ContentViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID,
            Model.of(ContentViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)),
            contentEditForm, Buttons.Type.Primary);
        feedbackPanel = new NotificationPanel(FEEDBACK_MARKUP_ID);
      }

      @Override
      protected void onInitialize() {
        contentEditForm.add(
            new RequiredTextField<String>(NAME_ID).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        contentEditForm.add(
            new RequiredTextField<String>(FORMAT_ID).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        contentEditForm.add(new TextArea<byte[]>(CONTENT_ID) {

          private static final long serialVersionUID = -7341359315847579440L;

          @Override
          public <C> IConverter<C> getConverter(final Class<C> type) {
            if (byte[].class.isAssignableFrom(type)) {
              return (IConverter<C>) new ByteArrayConverter();
            } else {
              return super.getConverter(type);
            }
          }
        }.setRequired(true).add(new TinyMceBehavior()).setOutputMarkupId(true));
        add(contentEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(saveAjaxButton.setOutputMarkupId(true));
        add(cancelAjaxLink.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CONTENT_EDIT_TABLE_ID = "contentEditTable";

    private static final String CONTENT_EDIT_FRAGMENT_ID = "contentEditFragment";

    private static final String CONTENT_VIEW_OR_EDIT_FRAGMENT_ID = "contentViewOrEditFragment";

    private static final long serialVersionUID = -8862288407883660764L;

    private final ContentEditTable contentEditTable;

    public ContentEditFragment() {
      super(CONTENT_VIEW_OR_EDIT_FRAGMENT_ID, CONTENT_EDIT_FRAGMENT_ID, ContentViewOrEditPanel.this,
          ContentViewOrEditPanel.this.getDefaultModel());
      contentEditTable =
          new ContentEditTable(CONTENT_EDIT_TABLE_ID, (IModel<Content>) ContentEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(contentEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class ContentViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class ContentViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class EditAjaxLink extends BootstrapAjaxLink<Content> {

        private static final long serialVersionUID = 4267535261864907719L;

        public EditAjaxLink(final String id, final IModel<Content> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.edit);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          ContentViewOrEditPanel.this.removeAll();
          target.add(ContentViewOrEditPanel.this
              .add(ContentViewOrEditPanel.this.new ContentEditFragment().setOutputMarkupId(true)));
        }
      }

      private static final String CONTENT_ID = "content";

      private static final String FORMAT_ID = "format";

      private static final String NAME_ID = "name";

      private static final String EDIT_ID = "edit";

      private static final String CONTENT_VIEW_FORM_COMPONENT_ID = "contentViewForm";

      private static final long serialVersionUID = 3338005815718092161L;

      private final EditAjaxLink editAjaxLink;

      private final BootstrapForm<Content> contentViewForm;

      public ContentViewTable(final String id, final IModel<Content> model) {
        super(id, model);
        contentViewForm = new BootstrapForm<Content>(CONTENT_VIEW_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Content>((IModel<Content>) ContentViewTable.this.getDefaultModel()));
        editAjaxLink =
            new EditAjaxLink(EDIT_ID, (IModel<Content>) ContentViewTable.this.getDefaultModel(), Buttons.Type.Primary,
                Model.of(ContentViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.EDIT_MESSAGE_KEY)));
      }

      @Override
      protected void onInitialize() {
        contentViewForm.add(new RequiredTextField<String>(NAME_ID).setOutputMarkupId(true));
        contentViewForm.add(new RequiredTextField<String>(FORMAT_ID).setOutputMarkupId(true));
        contentViewForm.add(new Label(CONTENT_ID) {

          private static final long serialVersionUID = 721587245052671908L;

          @Override
          public <C> IConverter<C> getConverter(final Class<C> type) {
            if (byte[].class.isAssignableFrom(type)) {
              return (IConverter<C>) new ByteArrayConverter();
            } else {
              return super.getConverter(type);
            }
          }
        }.setEscapeModelStrings(false).setOutputMarkupId(true));
        add(contentViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(editAjaxLink.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class EditAjaxLink extends BootstrapAjaxLink<Content> {

      private static final long serialVersionUID = 4267535261864907719L;

      public EditAjaxLink(final String id, final IModel<Content> model, final Buttons.Type type,
          final IModel<String> labelModel) {
        super(id, model, type, labelModel);
        setIconType(GlyphIconType.edit);
        setSize(Buttons.Size.Small);
      }

      @Override
      public void onClick(final AjaxRequestTarget target) {
        ContentViewOrEditPanel.this.removeAll();
        target.add(ContentViewOrEditPanel.this
            .add(ContentViewOrEditPanel.this.new ContentEditFragment().setOutputMarkupId(true)));
      }
    }

    private static final String CONTENT_VIEW_TABLE_ID = "contentViewTable";

    private static final String CONTENT_VIEW_FRAGMENT_MARKUP_ID = "contentViewFragment";

    private static final String CONTENT_VIEW_OR_EDIT_FRAGMENT_ID = "contentViewOrEditFragment";

    private static final long serialVersionUID = -8862288407883660764L;

    private final ContentViewTable contentViewTable;

    public ContentViewFragment() {
      super(CONTENT_VIEW_OR_EDIT_FRAGMENT_ID, CONTENT_VIEW_FRAGMENT_MARKUP_ID, ContentViewOrEditPanel.this,
          ContentViewOrEditPanel.this.getDefaultModel());
      contentViewTable =
          new ContentViewTable(CONTENT_VIEW_TABLE_ID, (IModel<Content>) ContentViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(contentViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentViewOrEditPanel.class);

  private static final long serialVersionUID = -3061472875418422947L;

  @SpringBean(name = CONTENT_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Content> contentDataProvider;

  public ContentViewOrEditPanel(final String id, final IModel<Content> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    contentDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    contentDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    contentDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    contentDataProvider.setType(new Content());
    contentDataProvider.getType().setActive(true);
    super.onInitialize();
  }
}
