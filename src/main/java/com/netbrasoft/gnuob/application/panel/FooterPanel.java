package com.netbrasoft.gnuob.application.panel;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;

import com.netbrasoft.gnuob.application.security.AppRoles;

@AuthorizeAction(action = Action.RENDER, roles = {AppRoles.ADMINISTRATOR, AppRoles.MANAGER, AppRoles.EMPLOYEE})
public class FooterPanel extends Panel {

  private static final long serialVersionUID = 5022744674799716569L;

  public FooterPanel(final String id) {
    super(id);
  }
}
