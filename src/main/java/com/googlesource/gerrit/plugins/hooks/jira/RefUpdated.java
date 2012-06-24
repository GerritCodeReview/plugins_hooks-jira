// Copyright (C) 2012 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.hooks.jira;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.extensions.events.GitReferenceUpdatedListener;
import com.google.inject.Inject;

@Listen
class RefUpdated implements GitReferenceUpdatedListener {
  private static final Logger log = LoggerFactory
      .getLogger(GitReferenceUpdatedListener.class);

  private final JiraClient jiraRpc;
  private final File gitDir;
  private Pattern commentPattern;
  private Pattern issuePattern = Pattern.compile("[A-Z]*-[0-9]*");
  private String gitwebUrl;

  @Inject
  RefUpdated(final JiraClient jiraRpc, JiraPluginConfig config) {
    this.jiraRpc = jiraRpc;
    this.gitDir = config.gitBasePath;
    this.commentPattern = Pattern.compile(config.issueRegex);
    this.gitwebUrl = config.gitwebUrl;
  }

  public void onGitReferenceUpdated(Event event) {

    try {
      final Repository repo =
          new RepositoryBuilder()
              .setGitDir(new File(gitDir, event.getProjectName() + ".git"))
              .setBare().build();
      RevWalk revWalk = new RevWalk(repo);
      JiraClientSession jira = jiraRpc.newSession();

      for (Update u : event.getUpdates()) {
        String newObjId = u.getNewObjectId();
        if (newObjId == null) continue;

        RevCommit commit = revWalk.parseCommit(ObjectId.fromString(newObjId));
        process(jira, u.getRefName(), commit);
      }

    } catch (Exception e) {
      log.error("Error processing event " + event, e);
    }


  }

  private void process(JiraClientSession jira, String refName, RevCommit commit)
      throws XmlRpcException {

    String commitMsg = commit.getFullMessage();
    Matcher matcher = commentPattern.matcher(commitMsg);
    while (matcher.find()) {
      String matched = matcher.group();

      try {
        addComment(jira, matched, refName, commit);
      } catch (XmlRpcException e) {
        log.warn("Jira issue " + matched + " was not found: comment not added");
      }
    }
  }

  private void addComment(JiraClientSession jira, String commentMatch, String refName, 
      RevCommit commit) throws XmlRpcException {
    Matcher matcher = issuePattern.matcher(commentMatch);
    if (!matcher.find()) return;

    jira.addComment(matcher.group(), getComment(refName, commit));
  }

  private String getComment(String refName, RevCommit commit) {
    String commitId = commit.getName();
    String comment = String.format(
        "Git commit: %s\n" + 
        "Branch: %s\n" +
        "Author: %s\n" + 
        "Committer: %s\n" +
        "%s", commitId,
        refName,
        getIdentity(commit.getAuthorIdent()),
        getIdentity(commit.getCommitterIdent()),
        commit.getFullMessage());
    if (gitwebUrl != null) {
      comment =
          comment + "\n" + gitwebUrl + "?p=dev-test.git;a=commit;h=" + commitId;
    }
    return comment;
  }

  private String getIdentity(PersonIdent ident) {
    return String.format("%s <%s>", ident.getName(), ident.getEmailAddress());
  }
}
