package com.netbrasoft.gnuob.application.panel;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebApplication;

import com.netbrasoft.gnuob.api.generic.GNUOpenBusinessApplicationException;
import com.netbrasoft.gnuob.application.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.application.page.SignInPage;
import com.netbrasoft.gnuob.application.security.AppRoles;

@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class HeaderPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER, AppRoles.EMPLOYEE})
  class LogoutAjaxLink extends AjaxLink<Void> {

    private static final long serialVersionUID = -6950515027229520882L;

    public LogoutAjaxLink(final String id) {
      super(id);
    }

    @Override
    public void onClick(final AjaxRequestTarget target) {
      AppServletContainerAuthenticatedWebSession.get().signOut();
      AppServletContainerAuthenticatedWebSession.get().invalidate();
      AppServletContainerAuthenticatedWebSession.get().invalidateNow();
      setResponsePage(SignInPage.class);
    }
  }

  private static final String META_INF_MANIFEST_MF_RESOURCE = "/META-INF/MANIFEST.MF";

  private static final String IMPLEMENTATION_VERSION_NAME = "Implementation-Version";

  private static final String IMPLEMENTATION_TITLE_NAME = "Implementation-Title";

  private static final String IMPLEMENTATION_VENDOR_NAME = "Implementation-Vendor";

  private static final String IMPLEMENTATION_VERSION_ID = "implementationVersion";

  private static final String IMPLEMENTATION_TITLE_ID = "implementationTitle";

  private static final String IMPLEMENTATION_VENDOR_ID = "implementationVendor";

  private static final String LOGOUT_ID = "logout";

  private static final long serialVersionUID = 3137234732197409313L;

  public HeaderPanel(final String id) {
    super(id);
  }

  @Override
  protected void onInitialize() {
    try {
      final ServletContext application = WebApplication.get().getServletContext();
      final InputStream inputStream = application.getResourceAsStream(META_INF_MANIFEST_MF_RESOURCE);
      final Attributes attributes = new Manifest(inputStream).getMainAttributes();

      add(new Label(IMPLEMENTATION_VENDOR_ID, attributes.getValue(IMPLEMENTATION_VENDOR_NAME)).setOutputMarkupId(true));
      add(new Label(IMPLEMENTATION_TITLE_ID, attributes.getValue(IMPLEMENTATION_TITLE_NAME)).setOutputMarkupId(true));
      add(new Label(IMPLEMENTATION_VERSION_ID, attributes.getValue(IMPLEMENTATION_VERSION_NAME)).setOutputMarkupId(true));
      add(new LogoutAjaxLink(LOGOUT_ID));

      super.onInitialize();
    } catch (final IOException e) {
      throw new GNUOpenBusinessApplicationException(e.getLocalizedMessage(), e);
    }
  }
}
