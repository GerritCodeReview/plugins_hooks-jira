package com.gerritforge.jira.client;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gerritforge.init.Section;
import com.gerritforge.its.InitIts;
import com.google.gerrit.pgm.init.InitStep;
import com.google.gerrit.pgm.util.ConsoleUI;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/** Initialize the GitRepositoryManager configuration section. */
@Singleton
class InitJira extends InitIts implements InitStep {
  private static final Logger log = LoggerFactory.getLogger(InitJira.class);
  private static final String JIRA_SECTION = "jira";
  private static final String COMMENT_LINK_SECTION = "commentLink";
  private final ConsoleUI ui;
  private Section jira;
  private Section jiraComment;

  public static enum MandatoryJira {
    TRUE, FALSE;
  }

  @Inject
  InitJira(final ConsoleUI ui, final Injector injector) {
    super(injector);
    this.ui = ui;
  }

  public void run() {
    Section.Factory sections = getSectionFactory();
    this.jira = sections.get(JIRA_SECTION, null);
    this.jiraComment = sections.get(COMMENT_LINK_SECTION, JIRA_SECTION);

    ui.message("\n");
    ui.header("Jira connectivity");

    String jiraUrl = null;
    do {
      jiraUrl = enterJiraConnectivity();
    } while (jiraUrl != null
        && (isConnectivityRequested(ui, jiraUrl) && !isJiraConnectSuccessful()));

    if (jiraUrl == null) {
      return;
    }

    ui.header("Jira issue-tracking association");
    jiraComment.string("Jira issue-Id regex", "match", "([A-Z]+-[0-9]+)");
    jiraComment.set("html",
        String.format("<a href=\"%s/browse/$1\">$1</a>", jiraUrl));
    jiraComment.select("Reject commits without Jira-Id", "mandatory",
        MandatoryJira.FALSE);
  }



  public String enterJiraConnectivity() {
    String jiraUrl = jira.string("Jira URL (empty to skip)", "url", null);
    if (jiraUrl != null) {
      jira.string("Jira username", "username", "");
      jira.password("username", "password");
    }
    return jiraUrl;
  }

  private boolean isJiraConnectSuccessful() {
    ui.message("Checking Jira connectivity ... ");
    try {
      JiraClient jiraClient = new JiraClient(jira.get("url"));
      JiraToken jiraToken =
          jiraClient.login(jira.get("username"), jira.get("password"));
      jiraClient.logout(jiraToken);
      ui.message("[OK]\n");
      return true;
    } catch (RemoteException e) {
      ui.message("*FAILED* (%s)\n", e.toString());
      return false;
    }
  }
}
