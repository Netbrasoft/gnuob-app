package com.netbrasoft.gnuob.application.page;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.filter.FilteredHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import com.google.common.collect.Lists;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeCDNCSSReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.references.JQueryCookieJsReference;

public abstract class BasePage extends WebPage implements IAjaxIndicatorAware {

  class NetbrasoftApplicationJavaScript extends JavaScriptResourceReference {

    private static final long serialVersionUID = 62421909883685410L;

    private NetbrasoftApplicationJavaScript(final Class<?> scope, final String name) {
      super(scope, name);
    }

    @Override
    public List<HeaderItem> getDependencies() {
      final List<HeaderItem> dependencies = Lists.newArrayList(super.getDependencies());

      dependencies.add(JavaScriptHeaderItem.forReference(JQueryCookieJsReference.INSTANCE));
      dependencies.add(JavaScriptHeaderItem.forReference(WebApplication.get().getJavaScriptLibrarySettings().getJQueryReference()));
      dependencies.add(JavaScriptHeaderItem.forReference(Bootstrap.getSettings().getJsResourceReference()));
      dependencies.add(CssHeaderItem.forReference(FontAwesomeCDNCSSReference.instance()));

      return dependencies;
    }
  }

  private static final String NETBRASOFT_APPLICATION_JAVASCRIPT_CONTAINER_FILTER_NAME = "netbrasoft-application-javascript-container";

  private static final String BOOTSTRAP_CONFIRMATION_JS_NAME = "bootstrap-confirmation.js";

  private static final long serialVersionUID = 8192334293970678397L;

  private static final String GNUOB_SITE_TITLE_PROPERTY = "gnuob.site.title";

  private static final String VEIL_HEX_LOADING = "veil-hex-loading";

  @Override
  public String getAjaxIndicatorMarkupId() {
    return VEIL_HEX_LOADING;
  }

  @Override
  protected void onInitialize() {
    final String site = getRequest().getClientUrl().getHost();
    final String title = site.replaceFirst("www.", "").split("\\.")[0];

    add(new Label(GNUOB_SITE_TITLE_PROPERTY, System.getProperty(GNUOB_SITE_TITLE_PROPERTY, WordUtils.capitalize(title))).setOutputMarkupId(true));
    add(new HeaderResponseContainer(NETBRASOFT_APPLICATION_JAVASCRIPT_CONTAINER_FILTER_NAME, NETBRASOFT_APPLICATION_JAVASCRIPT_CONTAINER_FILTER_NAME).setOutputMarkupId(true));

    super.onInitialize();
  }

  @Override
  public void renderHead(final IHeaderResponse response) {
    final NetbrasoftApplicationJavaScript netbrasoftApplicatijaonJavaScript = new NetbrasoftApplicationJavaScript(ConfirmationBehavior.class, BOOTSTRAP_CONFIRMATION_JS_NAME);
    final FilteredHeaderItem filteredHeaderItem =
        new FilteredHeaderItem(JavaScriptHeaderItem.forReference(netbrasoftApplicatijaonJavaScript), NETBRASOFT_APPLICATION_JAVASCRIPT_CONTAINER_FILTER_NAME);
    response.render(filteredHeaderItem);
    super.renderHead(response);
  }
}
