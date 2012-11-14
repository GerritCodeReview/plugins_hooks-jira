package com.gerritforge.hooks.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gerritforge.hooks.ItsName;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.events.ChangeAbandonedEvent;
import com.google.gerrit.server.events.ChangeEvent;
import com.google.gerrit.server.events.ChangeMergedEvent;
import com.google.gerrit.server.events.ChangeRestoredEvent;
import com.google.gerrit.server.events.CommentAddedEvent;
import com.google.gerrit.server.events.PatchSetCreatedEvent;
import com.google.gerrit.server.events.RefUpdatedEvent;
import com.google.inject.Inject;



public class GerritHookItsFilter implements GerritHookFilter {

  Logger log = LoggerFactory.getLogger(GerritHookItsFilter.class);

  @Inject
  @GerritServerConfig
  private Config gerritConfig;
  
  @Inject
  @ItsName
  private String itsName;

  public GerritHookItsFilter() {
    super();
  }

  protected String[] getIssueIds(String gitComment) {
    List<Pattern> commentRegexList = getCommentRegexList();
    if (commentRegexList == null) return new String[] {};

    log.debug("Matching '" + gitComment + "' against " + commentRegexList);

    ArrayList<String> issues = new ArrayList<String>();
    for (Pattern pattern : commentRegexList) {
      Matcher matcher = pattern.matcher(gitComment);

      while (matcher.find()) {
        int groupCount = matcher.groupCount();
        for (int i = 1; i <= groupCount; i++) {
          String group = matcher.group(i);
          issues.add(group);
        }
      }
    }

    return issues.toArray(new String[issues.size()]);
  }

  protected Long[] getWorkItems(String gitComment) {
    List<Pattern> commentRegexList = getCommentRegexList();
    if (commentRegexList == null) return new Long[] {};

    log.debug("Matching '" + gitComment + "' against " + commentRegexList);

    ArrayList<Long> workItems = new ArrayList<Long>();

    for (Pattern pattern : commentRegexList) {
      Matcher matcher = pattern.matcher(gitComment);

      while (matcher.find()) {
        addMatchedWorkItems(workItems, matcher);
      }
    }

    return workItems.toArray(new Long[workItems.size()]);
  }

  private void addMatchedWorkItems(ArrayList<Long> workItems, Matcher matcher) {
    int groupCount = matcher.groupCount();
    for (int i = 1; i <= groupCount; i++) {

      String group = matcher.group(i);
      try {
        Long workItem = new Long(group);
        workItems.add(workItem);
      } catch (NumberFormatException e) {
        log.debug("matched string '" + group
            + "' is not a work item > skipping");
      }
    }
  }

  private List<Pattern> getCommentRegexList() {
    ArrayList<Pattern> regexList = new ArrayList<Pattern>();

    String match = gerritConfig.getString("commentLink", itsName, "match");
    if (match != null) {
      regexList.add(Pattern.compile(match));
    }

    return regexList;
  }

  @Override
  public void doFilter(PatchSetCreatedEvent hook) throws IOException {
  }

  @Override
  public void doFilter(CommentAddedEvent hook) throws IOException {
  }

  @Override
  public void doFilter(ChangeMergedEvent hook) throws IOException {
  }

  @Override
  public void doFilter(ChangeAbandonedEvent changeAbandonedHook)
      throws IOException {
  }

  @Override
  public void doFilter(ChangeRestoredEvent changeRestoredHook)
      throws IOException {
  }

  @Override
  public void doFilter(RefUpdatedEvent refUpdatedHook) throws IOException {
  }

  @Override
  public void onChangeEvent(ChangeEvent event) {
    try {
      if (event instanceof PatchSetCreatedEvent) {
        doFilter((PatchSetCreatedEvent) event);
      } else if (event instanceof CommentAddedEvent) {
        doFilter((CommentAddedEvent) event);
      } else if (event instanceof ChangeMergedEvent) {
        doFilter((ChangeMergedEvent) event);
      } else if (event instanceof ChangeAbandonedEvent) {
        doFilter((ChangeAbandonedEvent) event);
      } else if (event instanceof ChangeRestoredEvent) {
        doFilter((ChangeRestoredEvent) event);
      } else if (event instanceof RefUpdatedEvent) {
        doFilter((RefUpdatedEvent) event);
      } else {
        log.info("Event " + event + " not recognised and ignored");
      }
    } catch (Throwable e) {
      log.error("Event " + e + " processing failed", e);
    }
  }

  public String getUrl(PatchSetCreatedEvent hook) {
    return null;
  }

}
