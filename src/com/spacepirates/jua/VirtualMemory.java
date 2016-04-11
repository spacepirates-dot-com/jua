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

/**
 *
 * @author scott
 */
public interface VirtualMemory {

  /**
   * Return the number of bytes of free virtual memory.
   * @return the number of byte of free virtual memory
   */
  Integer getFreeVirtualMemory();

  /**
   * Execute the command and return the output.
   * @return the output from the command
   * @throws JUAException if there is a problem running the command
   */
  String run() throws JUAException;

  /**
   * Provide access to the actual command stdout.
   * @return the stdout as a string
   */
  StringBuilder getOutput();

  /**
   * Provide access to the pretty print boolean.
   * @param inPrettyPrint boolean
   */
  void setPrettyPrint(String inPrettyPrint);

  /**
   * Allow implementations so set a calling ID string.
   * @param inCallerID the ID string
   */
  void setCallerID(String inCallerID);

}
