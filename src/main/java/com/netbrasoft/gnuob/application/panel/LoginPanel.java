package com.netbrasoft.gnuob.application.panel;

import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.util.time.Duration;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;

public class LoginPanel extends SignInPanel {

  private static final String FEEDBACK_ID = "feedback";

  private static final String SIGN_IN_FORM_ID = "signInForm";

  private static final long serialVersionUID = -438674592477878425L;

  private final NotificationPanel feedback;

  private final SignInForm signInForm;

  public LoginPanel(final String id) {
    this(id, true);
  }

  public LoginPanel(final String id, final boolean includeRememberMe) {
    super(id, includeRememberMe);
    feedback = new NotificationPanel(FEEDBACK_ID);
    signInForm = new SignInForm(SIGN_IN_FORM_ID);
  }

  @Override
  protected void onInitialize() {
    removeAll();
    add(feedback.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
    add(signInForm.setOutputMarkupId(true));
    super.onInitialize();
  }
}
