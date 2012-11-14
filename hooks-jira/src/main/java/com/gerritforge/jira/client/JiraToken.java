package com.gerritforge.jira.client;

public class JiraToken {

  private final String username;
  private final String token;

  public JiraToken(final String username, final String loginToken) {
    super();
    this.username = username;
    this.token = loginToken;
  }

  public String getUsername() {
    return username;
  }

  public String getToken() {
    return token;
  }
  
  public String toString() {
    return "username="+username+", token="+token;
  }
}
