package com.gerritforge.jira.client;

import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gerritforge.hooks.GerritHookModule;
import com.gerritforge.hooks.ItsValidateComment;
import com.gerritforge.its.ItsFacade;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.git.validators.CommitValidationListener;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;

public class JiraModule extends AbstractModule {

  private static final Logger LOG = LoggerFactory.getLogger(JiraModule.class);

  private final Config gerritConfig;

  @Inject
  public JiraModule(@GerritServerConfig final Config config) {
    this.gerritConfig = config;
  }

  @Override
  protected void configure() {    
    if (gerritConfig
        .getString(JiraItsFacade.ITS_NAME_JIRA, null, "url") != null) {
      LOG.info("JIRA is configured as ITS");
      bind(ItsFacade.class).toInstance(new JiraItsFacade(gerritConfig));
      DynamicSet.bind(binder(), CommitValidationListener.class).to(ItsValidateComment.class);
      
      install(new GerritHookModule(JiraItsFacade.ITS_NAME_JIRA));
    }
  }
}
