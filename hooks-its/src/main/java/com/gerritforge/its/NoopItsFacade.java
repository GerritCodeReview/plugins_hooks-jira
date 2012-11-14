package com.gerritforge.its;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An ITS facade doing nothing, it's configured when no ITS 
 * are referenced in config

 */
public class NoopItsFacade implements ItsFacade {
  
  private Logger log = LoggerFactory.getLogger(NoopItsFacade.class);

  @Override
  public void addComment(String issueId, String comment) throws IOException {
    if (log.isDebugEnabled()) log.debug("addComment({},{})", issueId, comment);
  }

  @Override
  public void addRelatedLink(String issueId, URL relatedUrl, String description)
      throws IOException {
    if (log.isDebugEnabled()) log.debug("addRelatedLink({},{},{})", new Object[]{issueId, relatedUrl, description});
  }

  @Override
  public ItsIssue getIssue(String issueId) throws IOException {
    if (log.isDebugEnabled()) log.debug("getIssue({})", issueId);
    return null;
  }

  @Override
  public void performAction(String issueId, String actionName)
      throws IOException {
    if (log.isDebugEnabled()) log.debug("performAction({},{})", issueId, actionName);
  }

  @Override
  public String healthCheck(Check check) throws IOException {
    if (log.isDebugEnabled()) log.debug("healthCheck()");
    return "{\"status\"=\"ok\",\"system\"=\"not configured\",}";
  }

  @Override
  public String createLinkForWebui(String url, String text) {
    if (log.isDebugEnabled()) log.debug("createLinkForWebui({},{})", url, text);
    return "";
  }

  @Override
  public String name() {
    return "not configured";
  }
}
