package com.gerritforge.hooks;

import com.gerritforge.hooks.filters.GerritHookFilterAddComment;
import com.gerritforge.hooks.filters.GerritHookFilterAddRelatedLinkToChangeId;
import com.gerritforge.hooks.filters.GerritHookFilterAddRelatedLinkToGitWeb;
import com.gerritforge.hooks.filters.GerritHookFilterChangeState;
import com.google.gerrit.common.ChangeListener;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.inject.AbstractModule;

public class GerritHookModule extends AbstractModule {
  
  private String itsName;
  
  public GerritHookModule(String itsName) {
    this.itsName = itsName;
  }

  @Override
  protected void configure() {
    bind(String.class).annotatedWith(ItsName.class).toInstance(itsName);
    DynamicSet.bind(binder(), ChangeListener.class).to(GerritHookFilterAddRelatedLinkToChangeId.class);
    DynamicSet.bind(binder(), ChangeListener.class).to(GerritHookFilterAddComment.class);
    DynamicSet.bind(binder(), ChangeListener.class).to(GerritHookFilterChangeState.class);
    DynamicSet.bind(binder(), ChangeListener.class).to(GerritHookFilterAddRelatedLinkToGitWeb.class);
  }

}
