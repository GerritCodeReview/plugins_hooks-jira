package com.gerritforge.hooks.filters;

import java.io.IOException;

import com.gerritforge.its.ItsFacade;
import com.google.gerrit.server.events.AccountAttribute;
import com.google.gerrit.server.events.ApprovalAttribute;
import com.google.gerrit.server.events.ChangeAbandonedEvent;
import com.google.gerrit.server.events.ChangeAttribute;
import com.google.gerrit.server.events.ChangeEvent;
import com.google.gerrit.server.events.ChangeMergedEvent;
import com.google.gerrit.server.events.ChangeRestoredEvent;
import com.google.gerrit.server.events.CommentAddedEvent;
import com.google.inject.Inject;

public class GerritHookFilterAddComment extends GerritHookItsFilter implements
    GerritHookFilter {

  @Inject
  private ItsFacade its;

  @Override
  public void doFilter(CommentAddedEvent hook) throws IOException {
    String comment = getComment(hook);
    addComment(hook.change, comment);
  }

  @Override
  public void doFilter(ChangeMergedEvent hook) throws IOException {
    String comment = getComment(hook);
    addComment(hook.change, comment);
  }

  @Override
  public void doFilter(ChangeAbandonedEvent hook) throws IOException {
    String comment = getComment(hook);
    addComment(hook.change, comment);
  }

  @Override
  public void doFilter(ChangeRestoredEvent hook) throws IOException {
    String comment = getComment(hook);
    addComment(hook.change, comment);
  }

  private String getCommentPrefix(ChangeAttribute change) {
    return getChangeIdUrl(change) + " | ";
  }

  private String getComment(ChangeAttribute change, ChangeEvent hook, AccountAttribute who, String what) {
    return getCommentPrefix(change) + "change " + what + " [by " + who + "]";
  }

  private String getComment(ChangeRestoredEvent hook) {
    return getComment(hook.change, hook, hook.restorer, "RESTORED");
  }

  private String getComment(ChangeAbandonedEvent hook) {
    return getComment(hook.change, hook, hook.abandoner, "ABANDONED");
  }

  private String getComment(ChangeMergedEvent hook) {
    return getComment(hook.change, hook, hook.submitter, "APPROVED and MERGED");
  }

  private String getChangeIdUrl(ChangeAttribute change) {
    final String url = change.url;
    String changeId = change.id;
    return its.createLinkForWebui(url, "Gerrit Change " + changeId);
  }

  private String getComment(CommentAddedEvent commentAdded) {
    StringBuilder comment = new StringBuilder(getCommentPrefix(commentAdded.change));

    if (commentAdded.approvals.length > 0) {
      comment.append("Code-Review: ");
      for (ApprovalAttribute approval : commentAdded.approvals) {
        String value = getApprovalValue(approval);
        if (value != null) {
          comment.append(getApprovalType(approval) + ":" + value + " ");
        }
      }
    }

    comment.append(commentAdded.comment + " ");
    comment.append("[by " + commentAdded.author + "]");
    return comment.toString();
  }

  private String getApprovalValue(ApprovalAttribute approval) {
    if (approval.value.equals("0")) {
      return null;
    }

    if (approval.value.charAt(0) != '-') {
      return "+" + approval.value;
    } else {
      return approval.value;
    }
  }

  private String getApprovalType(ApprovalAttribute approval) {
    if (approval.type.equalsIgnoreCase("CRVW")) {
      return "Reviewed";
    } else if (approval.type.equalsIgnoreCase("VRIF")) {
      return "Verified";
    } else
      return approval.type;
  }

  private void addComment(ChangeAttribute change, String comment)
      throws IOException {
    String gitComment = change.subject;;
    String[] issues = getIssueIds(gitComment);

    for (String issue : issues) {
      its.addComment(issue, comment);
    }
  }

}
