package com.gerritforge.hooks.filters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gerritforge.git.GitFacade;
import com.gerritforge.its.ItsFacade;
import com.google.gerrit.common.data.GitWebType;
import com.google.gerrit.common.data.ParameterizedString;
import com.google.gerrit.httpd.GitWebConfig;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.events.RefUpdatedEvent;
import com.google.inject.Inject;

public class GerritHookFilterAddRelatedLinkToGitWeb extends GerritHookItsFilter
    implements GerritHookFilter {

  Logger log = LoggerFactory
      .getLogger(GerritHookFilterAddRelatedLinkToGitWeb.class);

  @Inject
  private GitFacade git;

  @Inject
  @GerritServerConfig
  private Config gerritConfig;

  @Inject
  private ItsFacade its;
  
  @Inject
  private GitWebConfig gitWebConfig;


  @Override
  public void doFilter(RefUpdatedEvent hook) throws IOException {

    String gitComment = git.getComment(hook.refUpdate.project,  hook.refUpdate.newRev);
    log.debug("Git commit " + hook.refUpdate.newRev + ": " + gitComment);
    
    URL gitUrl = getGitUrl(hook);
    String[] issues = getIssueIds(gitComment);

    for (String issue : issues) {
      log.debug("Adding GitWeb URL " + gitUrl + " to issue " + issue);
      
      its.addRelatedLink(issue, gitUrl, "Git: "
          + hook.refUpdate.newRev);
    }
  }


  private URL getGitUrl(RefUpdatedEvent hook) throws MalformedURLException,
      UnsupportedEncodingException {
    String gerritCanonicalUrl =
        gerritConfig.getString("gerrit", null, "canonicalWebUrl");
    if(!gerritCanonicalUrl.endsWith("/")) {
      gerritCanonicalUrl += "/";
    }

    String gitWebUrl = gitWebConfig.getUrl();
    if (!gitWebUrl.startsWith("http")) {
      gitWebUrl = gerritCanonicalUrl + gitWebUrl;
    }

    GitWebType gitWebType = gitWebConfig.getGitWebType();
    String revUrl = gitWebType.getRevision();

    ParameterizedString pattern = new ParameterizedString(revUrl);
    final Map<String, String> p = new HashMap<String, String>();
    p.put("project", URLEncoder.encode(
        gitWebType.replacePathSeparator(hook.refUpdate.project), "US-ASCII"));
    p.put("commit", hook.refUpdate.newRev);
    return new URL(gitWebUrl + pattern.replace(p));
  }
}
