package com.netbrasoft.gnuob.application.security;

public class AppRoles extends org.apache.wicket.authroles.authorization.strategies.role.Roles {

   private static final long serialVersionUID = -1865955815082169990L;

   public static final String ADMINISTRATOR = "Administrator";

   public static final String MANAGER = "Manager";

   public static final String EMPLOYEE = "Employee";

   public static final String GUEST = "Guest";

   public AppRoles() {
      super(new String[] { ADMINISTRATOR, MANAGER, EMPLOYEE, GUEST });
   }
}
