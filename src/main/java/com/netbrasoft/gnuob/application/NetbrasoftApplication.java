package com.netbrasoft.gnuob.application;

import static com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants.CDNJS_CLOUDFLARE_COM_80;
import static com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants.FALSE;
import static com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants.GNUOB_SITE_CDN_ENABLED;
import static com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants.GNUOB_SITE_CDN_URL;
import static com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants.GNUOB_SITE_ENCRYPTION_KEY;
import static com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants.INSPECTOR_PAGE_HTML;
import static com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants.NETBRASOFT_APPLICATION_JAVASCRIPT_CONTAINER;
import static com.netbrasoft.gnuob.application.NetbrasoftApplicationConstants.WICKET_APPLICATION_NAME;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.bean.validation.BeanValidationConfiguration;
import org.apache.wicket.devutils.inspector.InspectorPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.settings.SecuritySettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.crypt.CachingSunJceCryptFactory;
import org.springframework.stereotype.Service;
import org.wicketstuff.wicket.servlet3.auth.ServletContainerAuthenticatedWebApplication;
import org.wicketstuff.wicket.servlet3.auth.ServletContainerAuthenticatedWebSession;

import com.netbrasoft.gnuob.api.generic.converter.XmlGregorianCalendarConverter;
import com.netbrasoft.gnuob.application.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.application.page.MainPage;
import com.netbrasoft.gnuob.application.page.SignInPage;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.RenderJavaScriptToFooterHeaderResponseDecorator;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.webjars.WicketWebjars;
import de.agilecoders.wicket.webjars.settings.WebjarsSettings;
import net.ftlines.wicketsource.WicketSource;

@Service(WICKET_APPLICATION_NAME)
public class NetbrasoftApplication extends ServletContainerAuthenticatedWebApplication {

  private static final BootstrapSettings BOOTSTRAP_SETTINGS = new BootstrapSettings();
  private static final WebjarsSettings WEBJARS_SETTINGS = new WebjarsSettings();

  static {
    BOOTSTRAP_SETTINGS.useCdnResources(Boolean.valueOf(System.getProperty(GNUOB_SITE_CDN_ENABLED, FALSE)));
    BOOTSTRAP_SETTINGS.setJsResourceFilterName(NETBRASOFT_APPLICATION_JAVASCRIPT_CONTAINER);
    WEBJARS_SETTINGS.cdnUrl(System.getProperty(GNUOB_SITE_CDN_URL, CDNJS_CLOUDFLARE_COM_80));
    WEBJARS_SETTINGS.useCdnResources(Boolean.valueOf(System.getProperty(GNUOB_SITE_CDN_ENABLED, FALSE)));
  }

  @Override
  protected void init() {
    super.init();
    initDeploymentSettings();
  }

  private void initDeploymentSettings() {
    installBootstrapSettings();
    installWebjarsSettings();
    setupApplicationSettings();
    setupBeanValidationSettings();
    setupSecurityCryptoFactorySettings();
    setupJavaScriptToFooterHeaderResponseDecorator();
    setupSpringCompInjectorForCompInstantListeners();
    setupDevelopmentModeSettings();
  }

  private void installBootstrapSettings() {
    Bootstrap.install(this, BOOTSTRAP_SETTINGS);
  }

  private void installWebjarsSettings() {
    WicketWebjars.install(this, WEBJARS_SETTINGS);
  }

  private void setupApplicationSettings() {
    getApplicationSettings().setUploadProgressUpdatesEnabled(true);
    getApplicationSettings().setAccessDeniedPage(SignInPage.class);
  }

  private void setupBeanValidationSettings() {
    new BeanValidationConfiguration().configure(this);
  }

  private void setupSecurityCryptoFactorySettings() {
    getSecuritySettings().setCryptFactory(new CachingSunJceCryptFactory(
        System.getProperty(GNUOB_SITE_ENCRYPTION_KEY, SecuritySettings.DEFAULT_ENCRYPTION_KEY)));
  }

  private void setupJavaScriptToFooterHeaderResponseDecorator() {
    setHeaderResponseDecorator(new RenderJavaScriptToFooterHeaderResponseDecorator());
  }

  private void setupSpringCompInjectorForCompInstantListeners() {
    getComponentInstantiationListeners().add(new SpringComponentInjector(this));
  }

  private void setupDevelopmentModeSettings() {
    if (isDevelopmentModeEnabled()) {
      enableDevelopmentSettings();
    }
  }

  private boolean isDevelopmentModeEnabled() {
    return RuntimeConfigurationType.DEVELOPMENT == getConfigurationType();
  }

  private void enableDevelopmentSettings() {
    mountInspectorPage();
    enableDevelopmentUtilsAndAjaxDebugMode();
    configureWicketSource();
  }

  private void mountInspectorPage() {
    mountPage(INSPECTOR_PAGE_HTML, InspectorPage.class);
  }

  private void enableDevelopmentUtilsAndAjaxDebugMode() {
    getDebugSettings().setDevelopmentUtilitiesEnabled(true);
    getDebugSettings().setAjaxDebugModeEnabled(true);
  }

  private void configureWicketSource() {
    WicketSource.configure(this);
  }

  @Override
  protected Class<? extends ServletContainerAuthenticatedWebSession> getContainerManagedWebSessionClass() {
    return AppServletContainerAuthenticatedWebSession.class;
  }

  @Override
  public Class<? extends Page> getHomePage() {
    return MainPage.class;
  }

  @Override
  protected Class<? extends WebPage> getSignInPageClass() {
    return SignInPage.class;
  }

  @Override
  protected IConverterLocator newConverterLocator() {
    return newXMLGregorianCalanderLocator();
  }

  private ConverterLocator newXMLGregorianCalanderLocator() {
    final ConverterLocator locator = (ConverterLocator) super.newConverterLocator();
    locator.set(XMLGregorianCalendar.class, new XmlGregorianCalendarConverter());
    return locator;
  }
}
