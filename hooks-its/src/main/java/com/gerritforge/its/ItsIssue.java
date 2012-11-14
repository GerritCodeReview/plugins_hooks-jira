package com.gerritforge.its;

public class ItsIssue {

  private final String id;
  private Object issue;

  public ItsIssue(String id, Object issue) {
    super();
    this.id = id;
    this.issue = issue;
  }
  
  public String getId() {
    return id;
  }

  public Object getIssue() {
    return issue;
  }
  
  @Override
  public String toString() {
    return "id="+id+",issue="+issue;
  }

}
