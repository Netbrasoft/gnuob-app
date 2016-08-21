package com.netbrasoft.gnuob.application.category.table;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.HumanTheme;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.api.SubCategory;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;

public abstract class SubCategoryTableTree extends TableTree<SubCategory, String> {

  static class SubCategoryExpansion implements Set<SubCategory>, Serializable {

    private static final long serialVersionUID = -115456451423752276L;

    private final MetaDataKey<SubCategoryExpansion> KEY = new MetaDataKey<SubCategoryExpansion>() {

      private static final long serialVersionUID = 689335047835010940L;
    };

    private transient Set<Long> ids = new HashSet<Long>();

    private boolean inverse;

    @Override
    public boolean add(final SubCategory subCategory) {
      if (inverse) {
        return ids.remove(subCategory.getId());
      } else {
        return ids.add(subCategory.getId());
      }
    }

    @Override
    public boolean addAll(final Collection<? extends SubCategory> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException();
    }

    public void collapseAll() {
      ids.clear();
      inverse = false;
    }

    @Override
    public boolean contains(final Object object) {
      final SubCategory subCategory = (SubCategory) object;
      if (inverse) {
        return !ids.contains(subCategory.getId());
      } else {
        return ids.contains(subCategory.getId());
      }
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
      throw new UnsupportedOperationException();
    }

    public void expandAll() {
      ids.clear();
      inverse = true;
    }

    public SubCategoryExpansion get() {
      SubCategoryExpansion expansion = Session.get().getMetaData(KEY);
      if (expansion == null) {
        expansion = new SubCategoryExpansion();
        Session.get().setMetaData(KEY, expansion);
      }
      return expansion;
    }

    @Override
    public boolean isEmpty() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<SubCategory> iterator() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(final Object object) {
      final SubCategory subCategory = (SubCategory) object;
      if (inverse) {
        return ids.add(subCategory.getId());
      } else {
        return ids.remove(subCategory.getId());
      }
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
      throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
      throw new UnsupportedOperationException();
    }
  }

  class SubCategoryToolBar extends AbstractToolbar {

    class ExpandAllAjaxButton extends BootstrapAjaxLink<Void> {

      private static final long serialVersionUID = -4365742269070797904L;

      public ExpandAllAjaxButton() {
        super("expandAll", Buttons.Type.Link);
        setSize(Buttons.Size.Small);
        setLabel(Model.of("Expand all"));
      }

      @Override
      public void onClick(final AjaxRequestTarget target) {
        new SubCategoryExpansion().get().expandAll();
        target.add(SubCategoryTableTree.this.setOutputMarkupId(true));
      }
    }

    private static final long serialVersionUID = 297616411999549064L;

    public <T> SubCategoryToolBar(final DataTable<T, String> table) {
      super(table);
    }

    @Override
    protected void onInitialize() {
      add(new ExpandAllAjaxButton().setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = 7537885853180365347L;

  public SubCategoryTableTree(final String id, final List<? extends IColumn<SubCategory, String>> columns, final ITreeProvider<SubCategory> provider, final long rowsPerPage) {
    super(id, columns, provider, rowsPerPage, new AbstractReadOnlyModel<Set<SubCategory>>() {

      private static final long serialVersionUID = 950443447495060811L;

      @Override
      public Set<SubCategory> getObject() {
        return new SubCategoryExpansion().get();
      }
    });
  }

  @Override
  protected Component newContentComponent(final String id, final IModel<SubCategory> model) {
    return new Label(id, model.getObject().getName());
  }

  @Override
  protected void onInitialize() {
    setItemReuseStrategy(new ReuseIfModelsEqualStrategy());
    getTable().add(new HumanTheme());
    getTable().add(new wicketdnd.theme.HumanTheme());
    getTable().add(new TableBehavior().hover());
    getTable().addTopToolbar(new HeadersToolbar<String>(getTable(), null));
    getTable().addTopToolbar(new SubCategoryToolBar(getTable()));
    // TODO: Remove / move this to child class.
    // add(new
    // DragSource(Operation.MOVE).drag("tr").initiate("span.tree-content").clone("span.tree-content"));
    // add(new DropTarget().dropCenter("tbody tr"));
    super.onInitialize();
  }
}
