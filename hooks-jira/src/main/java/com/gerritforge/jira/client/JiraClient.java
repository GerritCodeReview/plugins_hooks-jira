package com.gerritforge.jira.client;

import java.net.URL;
import java.rmi.RemoteException;

import com.atlassian.jira.rpc.soap.client.JiraSoapService;
import com.atlassian.jira.rpc.soap.client.JiraSoapServiceServiceLocator;
import com.atlassian.jira.rpc.soap.client.RemoteComment;
import com.atlassian.jira.rpc.soap.client.RemoteFieldValue;
import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteNamedObject;
import com.atlassian.jira.rpc.soap.client.RemoteServerInfo;

public class JiraClient {

  private final JiraSoapService service;

  public JiraClient(final String baseUrl) throws RemoteException {
    this(baseUrl, "/rpc/soap/jirasoapservice-v2");
  }
  
  public JiraClient(final String baseUrl, final String rpcPath) throws RemoteException {
    try {
      JiraSoapServiceServiceLocator locator = new JiraSoapServiceServiceLocator();
      service = locator.getJirasoapserviceV2(new URL(baseUrl+rpcPath));
    }
    catch (Exception e) {
        throw new RemoteException("ServiceException during SOAPClient contruction", e);
    }
  }

  public JiraToken login(final String username, final String password) throws RemoteException {
    String token = service.login(username, password);
    return new JiraToken(username, token);
  }
  
  public void logout(JiraToken token) throws RemoteException {
    service.logout(getToken(token));
  }

  public RemoteIssue getIssue(JiraToken token, String issueKey) throws RemoteException {
    return service.getIssue(getToken(token), issueKey);
  }

  public RemoteNamedObject[] getAvailableActions(JiraToken token, String issueKey) throws RemoteException {
    return service.getAvailableActions(getToken(token), issueKey);
  }

  public RemoteIssue performAction(JiraToken token, String issueKey, String actionId, RemoteFieldValue... params) throws RemoteException {
    return service.progressWorkflowAction(getToken(token), issueKey, actionId, params);
  }

  public void addComment(JiraToken token, String issueKey, RemoteComment comment) throws RemoteException {
    service.addComment(getToken(token), issueKey, comment);
  }

  public RemoteServerInfo getServerInfo(JiraToken token) throws RemoteException {
    return service.getServerInfo(getToken(token));
  }

  private String getToken(JiraToken token) {
    return token == null ? null : token.getToken();
  }

}
