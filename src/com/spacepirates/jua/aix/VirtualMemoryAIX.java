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
package com.spacepirates.jua.aix;

import com.spacepirates.jua.OperatingSystem;
import com.spacepirates.jua.JUAException;
import com.spacepirates.jua.VirtualMemory;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of VirtualMemory for AIX.
 *
 * <pre>
 *
 * System Configuration: lcpu=16 mem=95488MB
 *
 * kthr    memory              page              faults        cpu
 * ----- ----------- ------------------------ ------------ -----------
 *   r  b   avm   fre  re  pi  po  fr   sr  cy  in   sy  cs us sy id wa
 *   4  9 12982249 4242626   0   0   1  10   19   0 390 11482 4759  9  1 90  0
 * </pre>
 *
 * @author Scott Douglass <scott@spacepirates.com>
 */
public class VirtualMemoryAIX extends OperatingSystem implements VirtualMemory {

  /** Logger for this class. */
  private static final Logger LOGGER =
    Logger.getLogger(VirtualMemoryAIX.class.getName());
  /** Array of field names. */
  private static String[] FIELDS = { "running", "blocked", "p",
      "available_memory", "free_memory", "fi",
      "page_in", "page out", "fr", "sr", "fo", "in", "sy", "cs",
      "cpu_user",  "cpu_system",
      "cpu_idle", "cpu_waiting" };
  /**
   * Constructor for the VirtualMemory command on AIX.
   */
  public VirtualMemoryAIX() {
    super();
    getCommandLine().add("vmstat");
    getCommandLine().add("-I");
    getCommandLine().add("-w");
    setSkipLines(new Integer[] { 1, 2, 3, 4, 5, 6 });
    try {
      run();
    } catch (JUAException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public final Integer getFreeVirtualMemory() {
    return Integer.parseInt(getOutputMap().get("free_memory"));
  }

  @Override
  public void parseOutput(final String inOutput) {
    Integer lineCount  = 0;
    Integer fieldCount = 0;

    final Scanner lineScanner = new Scanner(inOutput);
    while (lineScanner.hasNextLine()) {
      final Scanner fieldScanner = new Scanner(lineScanner.nextLine());
      lineCount++;
      if (!getSkipLines().contains(lineCount)) {
        while (fieldScanner.hasNext()) {
          getOutputMap().put(formatName(FIELDS[fieldCount]),
            formatValue(fieldScanner.next()));
          fieldCount++;
        }
      }
      fieldScanner.close();
    }
    lineScanner.close();
  }
}
