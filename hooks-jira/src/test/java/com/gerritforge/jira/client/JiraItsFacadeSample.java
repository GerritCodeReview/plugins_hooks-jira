package com.gerritforge.jira.client;

import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Config;

import com.gerritforge.jira.client.JiraItsFacade;

public class JiraItsFacadeSample extends JiraItsFacade {

  private static final String JIRA_URL = "http://127.0.0.1:8080";
  private static final String JIRA_USER = "bbossola";
  private static final String JIRA_PASS = "jugtorino";

  public JiraItsFacadeSample() {
    super(newConfig());
  }

  private static Config newConfig() {
    final Config cfg = new Config();
    try {
      final String text = "[jira]\nurl="+JIRA_URL+"\nadminUsername = "+JIRA_USER+"\nadminPassword = "+JIRA_PASS+"\n";
      cfg.fromText(text);
    } catch (ConfigInvalidException e) {
      throw new RuntimeException(e);
    }
    return cfg;
  }

  public static void main(String[] args)  {
    final JiraItsFacadeSample facade = new JiraItsFacadeSample();
    for (int i = 1; i < 10; i++) {
      try {
        System.out.println(i+": "+facade.getIssue("SP-1"));
        // explicit logout
        facade.logout();
      }
      catch (Exception ex) {
        System.err.println("Unexpected exception: "+ex.getMessage());
        ex.printStackTrace();
      }
    }
  }
}
