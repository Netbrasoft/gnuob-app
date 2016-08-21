package com.netbrasoft.gnuob.application.security;

public class AppRoles extends org.apache.wicket.authroles.authorization.strategies.role.Roles {

  private static final long serialVersionUID = -1865955815082169990L;

  public static final String ADMINISTRATOR = "ADMINISTRATOR";

  public static final String MANAGER = "MANAGER";

  public static final String EMPLOYEE = "EMPLOYEE";

  public static final String GUEST = "GUEST";

  public AppRoles() {
    super(new String[] {ADMINISTRATOR, MANAGER, EMPLOYEE, GUEST});
  }
}
