package com.gerritforge.hooks;

import java.io.IOException;
import java.util.List;

import com.gerritforge.hooks.filters.GerritHookFilter;

public abstract class GerritHook {

  protected List<GerritHookFilter> filters;
  
  public abstract void execute() throws IOException;

}
