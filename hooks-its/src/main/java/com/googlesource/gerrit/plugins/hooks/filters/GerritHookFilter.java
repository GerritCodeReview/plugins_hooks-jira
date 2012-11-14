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

package com.googlesource.gerrit.plugins.hooks.filters;

import java.io.IOException;

import com.google.gerrit.common.ChangeListener;
import com.google.gerrit.server.events.ChangeAbandonedEvent;
import com.google.gerrit.server.events.ChangeEvent;
import com.google.gerrit.server.events.ChangeMergedEvent;
import com.google.gerrit.server.events.ChangeRestoredEvent;
import com.google.gerrit.server.events.CommentAddedEvent;
import com.google.gerrit.server.events.PatchSetCreatedEvent;
import com.google.gerrit.server.events.RefUpdatedEvent;

public interface GerritHookFilter extends ChangeListener {

  public void doFilter(PatchSetCreatedEvent hook) throws IOException;

  public void doFilter(CommentAddedEvent hook) throws IOException;

  public void doFilter(ChangeMergedEvent hook) throws IOException;

  public void doFilter(ChangeAbandonedEvent changeAbandonedHook)
      throws IOException;

  public void doFilter(ChangeRestoredEvent changeRestoredHook) throws IOException;

  public void doFilter(RefUpdatedEvent refUpdatedHook) throws IOException;

  public void onChangeEvent(ChangeEvent event);
}
