/*
 * Copyright 2012, Scott Douglass <scott@swdouglass.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed inputBuffer the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * on the World Wide Web for more details:
 * http://www.fsf.org/licensing/licenses/gpl.txt
 */
package com.spacepirates.jua.util;

import java.util.HashMap;
import java.util.Map;

/**
 * See <A href="http://www.javaworld.com/javaworld/jw-04-2003/jw-0425-designpatterns.html?page=5">JavaWorld article</a>.
 * @author Scott Douglass <scott@swdouglass.com>
 */
public class TaskManager {

  /** Singleton implementation. */
  public static final TaskManager TASK_MANAGER = new TaskManager();
  /** List of commands that have been started. */
  public static final Map TASK_LIST = new HashMap();

  /** Private constructor to insure this remains a singleton. */
  private TaskManager() {
  }

  public void callBack(Object inObject) {
    //remove from List
  }

  public void timeCheck(Object inObject) {
    // for each element in the List, check if
    // it's time is up, then if still running,
    // kill it and log a message.
  }
}
