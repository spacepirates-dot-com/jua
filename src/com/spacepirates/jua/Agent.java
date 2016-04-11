/*
 * Copyright 2012, Scott Douglass <scott@swdouglass.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * on the World Wide Web for more details:
 * http://www.fsf.org/licensing/licenses/gpl.txt
 */

package com.spacepirates.jua;

import com.spacepirates.jua.channel.JMSChannel;
import com.spacepirates.jua.channel.NoSQLChannel;
import com.spacepirates.jua.util.TaskManager;
import java.util.*;
import java.util.logging.Logger;

/**
 * This is the process which will run the various command objects, publish
 * data via channels and so on.
 * @author Scott Douglass <scott@swdouglass.com>
 */
public class Agent {
  private static final Logger LOGGER = Logger.getLogger(Agent.class.getName());
  public static final String P_JUA_AGENT_ID = "jua.agent.id";
  public static final String D_JUA_AGENT_ID = UUID.randomUUID().toString();
  public Channel controlChannel = new JMSChannel();
  public List<Channel> outputChannel =
    Arrays.asList((Channel)new NoSQLChannel(), (Channel) new JMSChannel());
  public Map osMap = new HashMap();
  public String id =
    System.getProperty(P_JUA_AGENT_ID, D_JUA_AGENT_ID);
  public Date dateStarted = new Date();
  public TaskManager taskManager = TaskManager.TASK_MANAGER;

  public static void main(String[] args) {
    Agent agentZero = new Agent();
  }
}
