package com.gerritforge.its;

import java.io.IOException;
import java.net.URL;

/**
 * A simple facade to an issue tracking system (its)
 */
public interface ItsFacade {

  public enum Check {
    SYSINFO,
    ACCESS
  }
  
  public String name();

  public String healthCheck(Check check)
    throws IOException;

  public void addRelatedLink(String issueId, URL relatedUrl, String description)
      throws IOException;

  public void addComment(String issueId, String comment) 
      throws IOException;

  public void performAction(String issueId, String actionName)
      throws IOException;

  public ItsIssue getIssue(String issueId) 
      throws IOException;
  
  public String createLinkForWebui(String url, String text);


}
