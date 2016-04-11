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
package com.spacepirates.jua;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spacepirates.jua.util.TaskManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The base class for all commands.
 * @author Scott Douglass <scott@swdouglass.com>
 */
public abstract class OperatingSystem {

  /** Username. */
  public static final String SYS_USER = System.getProperty("user.name");
  /** Working directory. */
  public static final String SYS_DIR = System.getProperty("user.dir");
  /** Property for setting base package: -Djua.package=value. */
  public static final String P_JUA_PKG = "jua.package";
  /** Default value for base package: com.spacepirates.jua. */
  public static final String D_JUA_PKG = "com.spacepirates.jua";
  /** Property for setting the optional scripts direcotry: -Djua.package=value. */
  public static final String P_JUA_SCRIPTS = "jua.scripts";
  /** Default value for base package: com.spacepirates.jua. */
  public static final String D_JUA_SCRIPTS = SYS_DIR + "/scripts";
  /** The script directory. */
  public String scriptsDir =
    System.getProperty(P_JUA_SCRIPTS, D_JUA_SCRIPTS);
  /** Property to disable pretty printing: -Djua.pretty=false.*/
  public static final String P_JUA_PRETTY = "jua.pretty";
  /** Defaulve value of the jua.pretty property: true. */
  public static final String D_JUA_PRETTY = "true";
  /** Set the SYS_PRETTY property. */
  public String prettyPrint =
    System.getProperty(P_JUA_PRETTY, D_JUA_PRETTY);
  /** A StringBuilder to hold the command output. */
  private final StringBuilder output = new StringBuilder();
  /** The list of Strings that make up the command and its arguments. */
  private List<String> commandLine = new ArrayList<String>();
  /** ProcessBuilder used to create the Process. */
  private final ProcessBuilder processBuilder = new ProcessBuilder();
  /** The operating system name. */
  protected static final String SYS_OS_NAME =
    System.getProperty("os.name").replaceAll("\\s", "");

  /** The base package name. */
  private static final String JUA_PKG =
    System.getProperty(P_JUA_PKG, D_JUA_PKG);
  /** A Logger for logging errors. */
  private static final Logger LOGGER =
    Logger.getLogger(OperatingSystem.class.getName());
  /** Timestamp format. */
  private static final String TS_FORMAT = "yyyy-MM-dd hh:mm:ss.SSS z";
  /** Date format. */
  private static final String DATE_FORMAT = "yyyy-MM-dd";
  /** Time format. */
  private static final String TIME_FORMAT = "hh:mm:ss";
  /** Set of line numbers to ignore when generating output. */
  private Set<Integer> skipLines = new HashSet<Integer>();
  /** Hold the command output in this Map. */
  private Map<String, String> outputMap = new HashMap<String, String>();

  /** Property for setting caller ID: -Djua.callerid=.*/
  public static final String P_JUA_CALLERID = "jua.callerid";
  /** Default value for caller ID setting: JUA. */
  public static final String D_JUA_CALLERID = "JUA";
  /** The unique ID of the caller (e.g. an agent.) */
  private String callerID =
    System.getProperty(P_JUA_CALLERID, D_JUA_CALLERID);
  /**
   * Task manager to prevent long running scripts/commands from blocking us
   * forever.
   */
  public TaskManager taskManager = TaskManager.TASK_MANAGER;

  /**
   * The most generic way to run a command. Override if necessary.
   * @return the text output from the command
   * @throws JUAException if there is a problem running the command
   */
  public final String run() throws JUAException {
    setDefaultInfo();
    getProcessBuilder().command(getCommandLine());
    try {
      final Process process = getProcessBuilder().start();

      final BufferedReader inputBuffer =
        new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line = "";
      while ((line = inputBuffer.readLine()) != null) {
        getOutput().append(line);
        getOutput().append("\n");
      }
      inputBuffer.close();
      //FIXME: This will block the calling Thread until the OS command
      //       exits. Probably I should not call this in a constructor.
      try {
        process.waitFor();
      } catch (InterruptedException ie) {
        LOGGER.log(Level.FINE, "Interrupted", ie);
      }
      final int result = process.exitValue();
      if (result != 0) {
        throw new IOException("Non-zero exit status: " + result);
      }
    } catch (IOException ex) {
      throw new JUAException("Whoa nelly!", ex);
    }
    parseOutput(getOutput().toString());
    return this.toString();
  }

  /**
   * Create a new command object based on the interface name.
   * @param inInterface the class name of the desired Interface
   * @return the desired interface
   */
  public static Object getCommand(final String inInterface) {
    final StringBuilder className = new StringBuilder();
    className.append(JUA_PKG);
    className.append(".");
    className.append(SYS_OS_NAME.toLowerCase(Locale.US));
    className.append(".");
    className.append(inInterface);
    className.append(SYS_OS_NAME);
    Object result = null;
    try {
      try {
        result = Class.forName(className.toString()).newInstance();
      } catch (InstantiationException ex) {
        LOGGER.log(Level.SEVERE,
          "Failed to instantiate class: " + className.toString(), ex);
      } catch (IllegalAccessException ex) {
        LOGGER.log(Level.SEVERE,
          "Could not access class: " + className.toString(), ex);
      }
    } catch (ClassNotFoundException ex) {
      LOGGER.log(Level.SEVERE, "Class not found: " + className.toString(), ex);
    }
    return result;
  }

  /**
   * Method for getting the hostname.
   * @return the hostname
   */
  public final String getHostname() {
    String hostname = "unknown";
    try {
       hostname = java.net.InetAddress.getLocalHost().getHostName();
     } catch (UnknownHostException e) {
       LOGGER.log(Level.WARNING, "Couldn't determine hostname!", e);
     }
     return hostname;
   }

   /**
    * Add the default information common to all outputs.
    */
   public final void setDefaultInfo() {
     final SimpleDateFormat tsFormat =
       new SimpleDateFormat(TS_FORMAT, Locale.US);
     final SimpleDateFormat dateFormat =
       new SimpleDateFormat(DATE_FORMAT, Locale.US);
     final SimpleDateFormat timeFormat =
       new SimpleDateFormat(TIME_FORMAT, Locale.US);
     final Date now = new Date();
     getOutputMap().put("os", formatValue(SYS_OS_NAME));
     getOutputMap().put("user", formatValue(SYS_USER));
     getOutputMap().put("host", formatValue(getHostname()));
     getOutputMap().put("directory", formatValue(SYS_DIR));
     getOutputMap().put("timestamp", tsFormat.format(now));
     getOutputMap().put("seconds", Long.toString(now.getTime()));
     getOutputMap().put("date", dateFormat.format(now));
     getOutputMap().put("time", timeFormat.format(now));
     getOutputMap().put("callerID", getCallerID());
     getOutputMap().put("UUID", UUID.randomUUID().toString());
     getOutputMap().put("command", getCommandString());
   }

   /**
    * Get the command and arguments as the command line would be in the shell.
    * @return the command line
    */
   public final String getCommandString() {
     final StringBuilder result = new StringBuilder();
     final Iterator clIterator = getCommandLine().listIterator();
     while (clIterator.hasNext()) {
       result.append(clIterator.next());
       result.append(" ");
     }
     result.deleteCharAt(result.length() - 1);
     return result.toString();
   }

  /**
   * Parse the command output into JSON.
   * @param inOutput the command output
   */
  public abstract void parseOutput(String inOutput);

  /**
   * Turn "vmstat" into "  \"vmstat\": ".
   * @param inString to be quoted
   * @return quoted string with trailing ": "
   */
  public final String formatName(final String inString) {
    String result = "";
    if (inString != null) {
      result =
        inString.replaceAll("\\s", "_")
        .toLowerCase()
        .trim()
        .replaceAll("\"", "");
    }
    return result;
  }

  /**
   *
   * @param inString the value of a JSON name:value pair
   * @return the quoted string terminated by a comma and new line
   */
  public final String formatValue(final String inString) {
    String result = "";
    if (!(inString == null || inString.equals(""))) {
      result = inString.trim().replaceAll("\"", "");
    }
    return result;
  }

  /**
   * Use the Jackson API and convert the encapsulated Map to
   * JSON String.
   * @return JSON String
   */
  @Override
  public final String toString() {
    final StringWriter outputString = new StringWriter();
    final ObjectMapper mapper = new ObjectMapper();
    try {
      if (Boolean.parseBoolean(prettyPrint)) {
        mapper.writerWithDefaultPrettyPrinter().writeValue(
          outputString, getOutputMap());
      } else {
        mapper.writeValue(
          outputString, getOutputMap());
      }
    } catch (JsonGenerationException ex) {
      LOGGER.log(Level.SEVERE, "Failed to generate JSON", ex);
    } catch (JsonMappingException ex) {
      LOGGER.log(Level.SEVERE, "Could not map object to JSON", ex);
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "Could not write to StringWriter", ex);
    }
    return outputString.toString();
  }

  /**
   * Creates a new VirtualMemory interface, hiding the platform
   * dependent implementation.
   * @return a VirtualMemory interface
   */
  public static VirtualMemory getVirtualMemory() {
    return (VirtualMemory) getCommand(VirtualMemory.class.getSimpleName());
  }

  /**
   * @return the output
   */
  public final StringBuilder getOutput() {
    return output;
  }

  /**
   * @return the commandLine
   */
  public final List<String> getCommandLine() {
    return commandLine;
  }

  /**
   * @param inCommandLine the commandLine to set
   */
  public final void setCommandLine(final List<String> inCommandLine) {
    this.commandLine = inCommandLine;
  }

  /**
   * @return the processBuilder
   */
  public final ProcessBuilder getProcessBuilder() {
    return processBuilder;
  }

  /**
   * @return the skipLines
   */
  public final Set<Integer> getSkipLines() {
    return skipLines;
  }

  /**
   * @param inLines the skipLines to set
   */
  public final void setSkipLines(final Integer[] inLines) {
    this.skipLines = new HashSet<Integer>(Arrays.asList(inLines));
  }

  /**
   * @return the outputMap
   */
  public final Map<String, String> getOutputMap() {
    return outputMap;
  }

  /**
   * @param inOutputMap the outputMap to set
   */
  public final void setOutputMap(final Map<String, String> inOutputMap) {
    this.outputMap = inOutputMap;
  }

  /**
   * @return the callerID
   */
  public final String getCallerID() {
    return callerID;
  }

  /**
   * @param inCallerID the callerID to set
   */
  public final void setCallerID(final String inCallerID) {
    this.callerID = inCallerID;
  }

  /**
   * Set the pretty printing on (true) or off (false).
   * @param inPrettyPrint the boolean
   */
  public final void setPrettyPrint(String inPrettyPrint) {
    this.prettyPrint = inPrettyPrint;
  }
}
