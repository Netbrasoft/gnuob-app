package com.netbrasoft.gnuob.application.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.netbrasoft.gnuob.application.panel.LoginPanel;

public class SignInPage extends BasePage {

  private static final String SIGN_IN_PANEL_ID = "signInPanel";

  private static final long serialVersionUID = -8080808937122686622L;

  private final LoginPanel loginPanel;

  public SignInPage() {
    this(null);
  }

  public SignInPage(final PageParameters parameters) {
    loginPanel = new LoginPanel(SIGN_IN_PANEL_ID);
  }

  @Override
  protected void onInitialize() {
    add(loginPanel.setOutputMarkupId(true));
    super.onInitialize();
  }
}
