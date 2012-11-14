package com.gerritforge.hooks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gerritforge.its.ItsFacade;
import com.gerritforge.its.ItsIssue;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.events.CommitReceivedEvent;
import com.google.gerrit.server.git.validators.CommitValidationListener;
import com.google.gerrit.server.git.validators.CommitValidationResult;
import com.google.inject.Inject;

public class ItsValidateComment implements CommitValidationListener {

  private static final Logger log = LoggerFactory
      .getLogger(ItsValidateComment.class);

  @Inject
  private ItsFacade client;

  @Inject
  @GerritServerConfig
  private Config gerritConfig;

  public CommitValidationResult validCommit(ReceiveCommand cmd, RevCommit commit) {

    List<Pattern> regexes = getCommentRegexList();
    if (regexes.size() == 0) {
      return CommitValidationResult.SUCCESS;
    }

    String comment = commit.getFullMessage();
    log.debug("Searching comment " + comment.trim() + " for patterns "
        + regexes);

    String issueId = null;
    for (Pattern pattern : regexes) {
      Matcher matcher = pattern.matcher(comment);
      if (matcher.find()) {
        issueId = extractMatchedWorkItems(matcher);
        log.debug("Pattern matched on comment '{}' with issue id '{}'",
            comment.trim(), issueId);
        break;
      }
    }

    if (issueId == null) {
      log.debug("Refusing commit: no issue id specified in comment");
      return CommitValidationResult
          .newFailure("Commit is not linked to any issue tracking artifact");
    } else if (!isWorkitemPresent(issueId, comment)) {
      log.debug("Refusing commit: issue '{}' not found", issueId);
      return CommitValidationResult
          .newFailure("Commit is linked to non-existent issue " + issueId);
    } else {
      log.debug("Commit validated, issue '{}' found", issueId);
      return CommitValidationResult.SUCCESS;
    }
  }

  private boolean isWorkitemPresent(String issueId, String comment) {
    boolean exist = false;
    if (issueId != null) {
      try {
        ItsIssue issue = client.getIssue(issueId);

        if (issue == null) {
          log.warn("Workitem " + issueId + " declared in the comment "
              + comment + " but not found on ITS");
        } else {
          exist = true;
          log.warn("Workitem found: " + issue);
        }
      } catch (IOException ex) {
        log.warn("Unexpected error accessint ITS", ex);
      }
    } else {
      log.debug("Rejecting commit: no pattern matched on comment " + comment);
    }
    return exist;
  }

  private List<Pattern> getCommentRegexList() {
    ArrayList<Pattern> regexList = new ArrayList<Pattern>();

    Set<String> linkSubsections = gerritConfig.getSubsections("commentLink");
    for (String string : linkSubsections) {
      boolean mandatory =
          gerritConfig.getBoolean("commentLink", string, "mandatory", false);
      if (mandatory) {
        String match = gerritConfig.getString("commentLink", string, "match");
        if (match != null) regexList.add(Pattern.compile(match));
      }
    }

    return regexList;
  }

  private String extractMatchedWorkItems(Matcher matcher) {
    int groupCount = matcher.groupCount();
    if (groupCount >= 1)
      return matcher.group(1);
    else
      return null;
  }


  @Override
  public CommitValidationResult onCommitReceived(
      CommitReceivedEvent receiveEvent) {
    return validCommit(receiveEvent.command, receiveEvent.commit);
  }
}
