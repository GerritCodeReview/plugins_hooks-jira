package com.gerritforge.hooks.filters;

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
