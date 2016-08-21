package com.netbrasoft.gnuob.application.border;

import org.apache.wicket.markup.html.border.Border;

import com.netbrasoft.gnuob.application.panel.FooterPanel;
import com.netbrasoft.gnuob.application.panel.HeaderPanel;

public class ContentBorder extends Border {

  private static final String FOOTER_PANEL_ID = "footerPanel";

  private static final String HEADER_PANEL_ID = "headerPanel";

  private static final long serialVersionUID = 6569587142042286311L;

  private final HeaderPanel headerPanel;

  private final FooterPanel footerPanel;

  public ContentBorder(final String id) {
    super(id);
    headerPanel = new HeaderPanel(HEADER_PANEL_ID);
    footerPanel = new FooterPanel(FOOTER_PANEL_ID);
  }

  @Override
  protected void onInitialize() {
    addToBorder(headerPanel.setOutputMarkupId(true));
    addToBorder(footerPanel.setOutputMarkupId(true));
    super.onInitialize();
  }
}
