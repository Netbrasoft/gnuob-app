package com.netbrasoft.gnuob.application.setting;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.api.Setting;

public class SettingTab extends AbstractTab {

  private static final long serialVersionUID = 4835579949680085443L;

  public SettingTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    final Setting setting = new Setting();
    setting.setActive(true);
    return new SettingPanel(panelId, Model.of(setting));
  }
}
