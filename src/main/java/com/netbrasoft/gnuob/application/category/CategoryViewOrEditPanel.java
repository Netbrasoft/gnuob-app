package com.netbrasoft.gnuob.application.category;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.CATEGORY_DATA_PROVIDER_NAME;

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

import com.netbrasoft.gnuob.api.Category;
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
@AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class CategoryViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
  class CategoryEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
    class CategoryEditTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class CancelAjaxLink extends BootstrapAjaxLink<Category> {

        private static final long serialVersionUID = 4267535261864907719L;

        public CancelAjaxLink(final String id, final IModel<Category> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          CategoryViewOrEditPanel.this.removeAll();
          if (((Category) CancelAjaxLink.this.getDefaultModelObject()).getId() > 0) {
            CancelAjaxLink.this.setDefaultModelObject(
                categoryDataProvider.findById((Category) CancelAjaxLink.this.getDefaultModelObject()));
          }
          target.add(CategoryViewOrEditPanel.this.add(CategoryViewOrEditPanel.this.new CategoryViewFragment())
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
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          form.add(new TooltipValidation());
          target.add(form);
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model
              .of(CategoryViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          try {
            if (((Category) form.getDefaultModelObject()).getId() == 0) {
              CategoryViewOrEditPanel.this.setDefaultModel(Model.of(categoryDataProvider
                  .findById(categoryDataProvider.persist((Category) form.getDefaultModelObject()))));;
            } else {
              CategoryViewOrEditPanel.this.setDefaultModel(Model.of(
                  categoryDataProvider.findById(categoryDataProvider.merge((Category) form.getDefaultModelObject()))));
            }
            CategoryViewOrEditPanel.this.removeAll();
            target.add(CategoryViewOrEditPanel.this.add(CategoryViewOrEditPanel.this.new CategoryViewFragment())
                .setOutputMarkupId(true));
          } catch (final RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
            feedbackPanel.warn(e.getLocalizedMessage());
            target.add(feedbackPanel.setOutputMarkupId(true));
            target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(
                CategoryViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)))));
          }
        }
      }

      private static final String DESCRIPTION_ID = "description";

      private static final String NAME_ID = "name";

      private static final String POSITION_ID = "position";

      private static final String CONTENT_VIEW_OR_EDIT_PANEL_ID = "contentViewOrEditPanel";

      private static final String SUB_CATEGORY_VIEW_OR_EDIT_PANEL_ID = "subCategoryViewOrEditPanel";

      private static final String CATEGORY_EDIT_FORM_COMPONENT_ID = "categoryEditForm";

      private static final long serialVersionUID = -3918606131239617970L;

      private static final String FEEDBACK_MARKUP_ID = "feedback";

      private static final String SAVE_ID = "save";

      private static final String CANCEL_ID = "cancel";

      private final CategoryContentPanel contentViewOrEditPanel;

      private final SubCategoryPanel subCategoryViewOrEditPanel;

      private final BootstrapForm<Category> categoryEditForm;

      private final CancelAjaxLink cancelAjaxLink;

      private final SaveAjaxButton saveAjaxButton;

      private final NotificationPanel feedbackPanel;

      public CategoryEditTable(final String id, final IModel<Category> model) {
        super(id, model);
        categoryEditForm = new BootstrapForm<Category>(CATEGORY_EDIT_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Category>((IModel<Category>) CategoryEditTable.this.getDefaultModel()));
        contentViewOrEditPanel = new CategoryContentPanel(CONTENT_VIEW_OR_EDIT_PANEL_ID,
            (IModel<Category>) CategoryEditTable.this.getDefaultModel());
        subCategoryViewOrEditPanel = new SubCategoryPanel(SUB_CATEGORY_VIEW_OR_EDIT_PANEL_ID,
            (IModel<Category>) CategoryEditTable.this.getDefaultModel());
        cancelAjaxLink = new CancelAjaxLink(CANCEL_ID, model, Buttons.Type.Default,
            Model.of(CategoryViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.CANCEL_MESSAGE_KEY)));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID,
            Model.of(CategoryViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.SAVE_AND_CLOSE_MESSAGE_KEY)),
            categoryEditForm, Buttons.Type.Primary);
        feedbackPanel = new NotificationPanel(FEEDBACK_MARKUP_ID);
      }

      @Override
      protected void onInitialize() {
        categoryEditForm
            .add(new NumberTextField<Integer>(POSITION_ID).add(RangeValidator.minimum(0)).setOutputMarkupId(true));
        categoryEditForm.add(
            new RequiredTextField<String>(NAME_ID).add(StringValidator.maximumLength(128)).setOutputMarkupId(true));
        categoryEditForm.add(new TextArea<String>(DESCRIPTION_ID).add(StringValidator.maximumLength(128))
            .setRequired(true).setOutputMarkupId(true));
        categoryEditForm.add(contentViewOrEditPanel.add(contentViewOrEditPanel.new CategoryContentEditFragment())
            .setOutputMarkupId(true));
        categoryEditForm.add(subCategoryViewOrEditPanel.add(subCategoryViewOrEditPanel.new SubCategoryEditFragement())
            .setOutputMarkupId(true));
        add(categoryEditForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        add(saveAjaxButton.setOutputMarkupId(true));
        add(cancelAjaxLink.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CATEGORY_EDIT_TABLE_ID = "categoryEditTable";

    private static final String CATEGORY_EDIT_FRAGMENT_MARKUP_ID = "categoryEditFragment";

    private static final String CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID = "categoryViewOrEditFragment";

    private static final long serialVersionUID = 5133082553128798473L;

    private final CategoryEditTable categoryEditTable;

    public CategoryEditFragment() {
      super(CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID, CATEGORY_EDIT_FRAGMENT_MARKUP_ID, CategoryViewOrEditPanel.this,
          CategoryViewOrEditPanel.this.getDefaultModel());
      categoryEditTable =
          new CategoryEditTable(CATEGORY_EDIT_TABLE_ID, (IModel<Category>) CategoryEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(categoryEditTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class CategoryViewFragment extends Fragment {

    @AuthorizeAction(action = Action.ENABLE, roles = {AppRoles.MANAGER, AppRoles.EMPLOYEE})
    class CategoryViewTable extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.MANAGER})
      class EditAjaxLink extends BootstrapAjaxLink<Category> {

        private static final long serialVersionUID = 4267535261864907719L;

        public EditAjaxLink(final String id, final IModel<Category> model, final Buttons.Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setIconType(GlyphIconType.edit);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          CategoryViewOrEditPanel.this.removeAll();
          target.add(CategoryViewOrEditPanel.this
              .add(CategoryViewOrEditPanel.this.new CategoryEditFragment().setOutputMarkupId(true)));
        }
      }

      private static final String DESCRIPTION_ID = "description";

      private static final String NAME_ID = "name";

      private static final String POSITION_ID = "position";

      private static final String SUB_CATEGORY_VIEW_OR_EDIT_PANEL_ID = "subCategoryViewOrEditPanel";

      private static final String CONTENT_VIEW_OR_EDIT_PANEL_ID = "contentViewOrEditPanel";

      private static final String CATEGORY_VIEW_FORM_COMPONENT_ID = "categoryViewForm";

      private static final long serialVersionUID = 8000374832364819124L;

      private static final String EDIT_ID = "edit";

      private final EditAjaxLink editAjaxLink;

      private final BootstrapForm<Category> categoryViewForm;

      private final CategoryContentPanel contentViewOrEditPanel;

      private final SubCategoryPanel subCategoryViewOrEditPanel;

      public CategoryViewTable(final String id, final IModel<Category> model) {
        super(id, model);
        categoryViewForm = new BootstrapForm<Category>(CATEGORY_VIEW_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Category>((IModel<Category>) CategoryViewTable.this.getDefaultModel()));
        editAjaxLink =
            new EditAjaxLink(EDIT_ID, (IModel<Category>) CategoryViewTable.this.getDefaultModel(), Buttons.Type.Primary,
                Model.of(CategoryViewOrEditPanel.this.getString(NetbrasoftApplicationConstants.EDIT_MESSAGE_KEY)));
        contentViewOrEditPanel = new CategoryContentPanel(CONTENT_VIEW_OR_EDIT_PANEL_ID,
            (IModel<Category>) CategoryViewTable.this.getDefaultModel());
        subCategoryViewOrEditPanel = new SubCategoryPanel(SUB_CATEGORY_VIEW_OR_EDIT_PANEL_ID,
            (IModel<Category>) CategoryViewTable.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        categoryViewForm.add(new NumberTextField<Integer>(POSITION_ID).setOutputMarkupId(true));
        categoryViewForm.add(new RequiredTextField<String>(NAME_ID).setOutputMarkupId(true));
        categoryViewForm.add(new TextArea<String>(DESCRIPTION_ID).setOutputMarkupId(true));
        categoryViewForm.add(contentViewOrEditPanel.add(contentViewOrEditPanel.new CategoryContentViewFragment())
            .setOutputMarkupId(true));
        categoryViewForm.add(subCategoryViewOrEditPanel.add(subCategoryViewOrEditPanel.new SubCategoryViewFragement())
            .setOutputMarkupId(true));
        add(categoryViewForm.add(new FormBehavior(FormType.Horizontal)).setOutputMarkupId(true));
        add(editAjaxLink.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CATEGORY_VIEW_TABLE_ID = "categoryViewTable";

    private static final String CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID = "categoryViewOrEditFragment";

    private static final String CATEGORY_VIEW_FRAGMENT_MARKUP_ID = "categoryViewFragment";

    private static final long serialVersionUID = 5863708936560086113L;

    private final CategoryViewTable categoryViewTable;

    public CategoryViewFragment() {
      super(CATEGORY_VIEW_OR_EDIT_FRAGMENT_ID, CATEGORY_VIEW_FRAGMENT_MARKUP_ID, CategoryViewOrEditPanel.this,
          CategoryViewOrEditPanel.this.getDefaultModel());
      categoryViewTable =
          new CategoryViewTable(CATEGORY_VIEW_TABLE_ID, (IModel<Category>) CategoryViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(categoryViewTable.add(new TableBehavior()).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(CategoryViewOrEditPanel.class);

  private static final long serialVersionUID = 3968615764565588442L;

  @SpringBean(name = CATEGORY_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Category> categoryDataProvider;

  public CategoryViewOrEditPanel(final String id, final IModel<Category> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    categoryDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    categoryDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    categoryDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    categoryDataProvider.setType(new Category());
    categoryDataProvider.getType().setActive(true);
    super.onInitialize();
  }
}
