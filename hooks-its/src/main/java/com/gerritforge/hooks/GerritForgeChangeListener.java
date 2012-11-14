package com.gerritforge.hooks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gerritforge.hooks.filters.GerritHookFilter;
import com.google.gerrit.common.ChangeListener;
import com.google.gerrit.server.events.ChangeEvent;
import com.google.inject.Inject;

public class GerritForgeChangeListener implements ChangeListener {
  private static final Logger log = LoggerFactory.getLogger(GerritForgeChangeListener.class);
  private List<GerritHookFilter> hookFilters;

  @Inject
  public GerritForgeChangeListener(java.util.List<GerritHookFilter> hookFilters) {
    this.hookFilters = hookFilters;
  }
  
  @Override
  public void onChangeEvent(ChangeEvent event) {
    for (GerritHookFilter hookFilter : hookFilters) {
      hookFilter.onChangeEvent(event);
    }
  }

}
