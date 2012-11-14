package com.gerritforge.hooks.filters;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gerritforge.git.GitFacade;
import com.gerritforge.its.ItsFacade;
import com.google.gerrit.server.events.PatchSetCreatedEvent;
import com.google.inject.Inject;

public class GerritHookFilterAddRelatedLinkToChangeId extends
    GerritHookItsFilter implements GerritHookFilter {

  Logger log = LoggerFactory
      .getLogger(GerritHookFilterAddRelatedLinkToChangeId.class);

  @Inject
  private GitFacade git;

  @Inject
  private ItsFacade its;

  @Override
  public void doFilter(PatchSetCreatedEvent patchsetCreated) throws IOException {

    String gitComment =
        git.getComment(patchsetCreated.change.project,
            patchsetCreated.patchSet.revision);
    String[] issues = getIssueIds(gitComment);

    for (String issue : issues) {
      its.addRelatedLink(issue, new URL(patchsetCreated.change.url),
          "Gerrit Patch-Set: " + patchsetCreated.change.id + "/"
              + patchsetCreated.patchSet.number);
    }
  }
}
