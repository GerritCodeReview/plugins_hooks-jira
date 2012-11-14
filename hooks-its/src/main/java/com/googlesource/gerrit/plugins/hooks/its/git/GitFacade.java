// Copyright (C) 2013 The Android Open Source Project
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

package com.googlesource.gerrit.plugins.hooks.its.git;

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
