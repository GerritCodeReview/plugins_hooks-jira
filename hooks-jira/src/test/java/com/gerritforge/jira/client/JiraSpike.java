package com.gerritforge.jira.client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.xmlrpc.XmlRpcException;

import com.atlassian.jira.rpc.soap.client.RemoteAuthenticationException;
import com.atlassian.jira.rpc.soap.client.RemoteComment;
import com.atlassian.jira.rpc.soap.client.RemoteCustomFieldValue;
import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteNamedObject;

public class JiraSpike {
  public static final String JIRA_URI = "http://jira.lmitsoftware.com";
  public static final String USERNAME = "admin";
  public static final String PASSWORD = "bf333ma1";

  public static void main(String[] args) throws Exception {
    JiraClient client = new JiraClient(JIRA_URI);

    try {
      JiraToken token = client.login(USERNAME, PASSWORD);
      System.out.println("Token: " + token);
      try {
        run(client, token);
      } finally {
        client.logout(token);
        System.out.println("\nLogout of " + token + " successful");
      }
    } catch (RemoteAuthenticationException e) {
      System.err.println("Login *FAILED*: " + e.toString());
    }
  }


  private static void run(JiraClient client, JiraToken token)
      throws IOException, XmlRpcException {

    RemoteIssue issue = client.getIssue(token, "SP-1");
    System.out.println("\nSP-1 (cur): " + toString(issue, true));

    RemoteNamedObject[] actions = client.getAvailableActions(token, "SP-1");
    System.out.println("\nAvailable actions:" + toString(actions));

    RemoteNamedObject action = actions[(int) (Math.random() * actions.length)];
    System.out.println("\nAction performed: " + toString(action));
    issue = client.performAction(token, "SP-1", action.getId());
    System.out.println("\nSP-1 (new): " + toString(issue));

    final String text =
        "This is a new comment, added on "
            + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date());
    RemoteComment comment = new RemoteComment();
    comment.setBody(text);
    client.addComment(token, "SP-1", comment);
    System.out.println("\nAdded comment " + text);
  }


  private static String toString(RemoteIssue issue) {
    return toString(issue, false);
  }


  private static String toString(RemoteNamedObject[] datas) {
    StringBuilder sb = new StringBuilder();
    for (RemoteNamedObject data : datas) {
      sb.append('\n');
      sb.append(toString(data));
    }
    return sb.toString();
  }

  private static String toString(RemoteNamedObject data) {
    StringBuilder sb = new StringBuilder();
    sb.append("id=");
    sb.append(data.getId());
    sb.append(",name=");
    sb.append(data.getName());
    return sb.toString();
  }


  private static String toString(RemoteIssue issue, boolean extended) {
    StringBuilder sb = new StringBuilder();
    sb.append("key=");
    sb.append(issue.getKey());
    sb.append(",id=");
    sb.append(issue.getId());
    sb.append(",summary=");
    sb.append(issue.getSummary());
    sb.append(",status=");
    sb.append(issue.getStatus());
    sb.append(",project=");
    sb.append(issue.getProject());

    if (extended) {
      RemoteCustomFieldValue[] values = issue.getCustomFieldValues();
      if (values.length > 0) {
        sb.append(",custom-fields=");
        for (RemoteCustomFieldValue value : values) {
          sb.append(value.getKey());
          sb.append("=");
          sb.append(Arrays.asList(value.getValues()));
        }
      }
    }
    return sb.toString();
  }
}
