package com.gerritforge.git;

import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import com.google.gerrit.reviewdb.client.Project.NameKey;
import com.google.gerrit.server.git.LocalDiskRepositoryManager;
import com.google.inject.Inject;

public class GitFacade {

  @Inject
  LocalDiskRepositoryManager repoManager;

  public String getComment(String projectName, String commitId)
      throws IOException {

    final Repository repo =
        repoManager.openRepository(new NameKey(projectName));
    try {
      RevWalk revWalk = new RevWalk(repo);
      RevCommit commit = revWalk.parseCommit(ObjectId.fromString(commitId));

      return commit.getFullMessage();
    } finally {
      repo.close();
    }
  }
}
