package com.gerritforge.hooks;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
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
import com.google.gerrit.server.git.validators.CommitValidationException;
import com.google.gerrit.server.git.validators.CommitValidationListener;
import com.google.gerrit.server.git.validators.CommitValidationMessage;
import com.google.inject.Inject;

public class ItsValidateComment implements CommitValidationListener {

  private static final Logger log = LoggerFactory
      .getLogger(ItsValidateComment.class);

  @Inject
  private ItsFacade client;

  @Inject
  @GerritServerConfig
  private Config gerritConfig;

  public List<CommitValidationMessage> validCommit(ReceiveCommand cmd, RevCommit commit) throws CommitValidationException {

    HashMap<Pattern, ItsAssociationPolicy> regexes = getCommentRegexMap();
    if (regexes.size() == 0) {
      return Collections.emptyList();
    }

    String comment = commit.getFullMessage();
    log.debug("Searching comment " + comment.trim() + " for patterns "
        + regexes);

    String issueId = null;
    ItsAssociationPolicy associationPolicy = ItsAssociationPolicy.OPTIONAL;
    for ( Entry<Pattern, ItsAssociationPolicy>  entry : regexes.entrySet()) {
      Matcher matcher = entry.getKey().matcher(comment);
      associationPolicy = entry.getValue();
      if (matcher.find()) {
        issueId = extractMatchedWorkItems(matcher);
        log.debug("Pattern matched on comment '{}' with issue id '{}'",
            comment.trim(), issueId);
        break;
      }
    }

    String validationMessage = null;
    if (issueId == null) {
      validationMessage = "Commit is not linked to any issue";
    } else if (!isWorkitemPresent(issueId, comment)) {
      validationMessage = "Issue " + issueId + " could not be found";
    } 

    switch (associationPolicy) {
      case MANDATORY:
        throw new CommitValidationException(validationMessage);

      case SUGGESTED:
        return Collections.singletonList(new CommitValidationMessage(validationMessage, true));

      default:
        return Collections.emptyList();
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

  private HashMap<Pattern, ItsAssociationPolicy> getCommentRegexMap() {
    HashMap<Pattern, ItsAssociationPolicy> regexMap = new HashMap<Pattern, ItsAssociationPolicy>();

    Set<String> linkSubsections = gerritConfig.getSubsections("commentLink");
    for (String string : linkSubsections) {
      String match = gerritConfig.getString("commentLink", string, "match");
      if (match != null) {
        regexMap
            .put(Pattern.compile(match), gerritConfig.getEnum("commentLink",
                string, "association", ItsAssociationPolicy.OPTIONAL));
      }
    }

    return regexMap;
  }

  private String extractMatchedWorkItems(Matcher matcher) {
    int groupCount = matcher.groupCount();
    if (groupCount >= 1)
      return matcher.group(1);
    else
      return null;
  }


  @Override
  public List<CommitValidationMessage> onCommitReceived(
      CommitReceivedEvent receiveEvent) throws CommitValidationException {
    return validCommit(receiveEvent.command, receiveEvent.commit);
  }
}
