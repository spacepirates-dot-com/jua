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
package com.spacepirates.jua.linux;

import com.spacepirates.jua.OperatingSystem;
import com.spacepirates.jua.JUAException;
import com.spacepirates.jua.VirtualMemory;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of VirtualMemory for Linux.
 *
 * <pre>
 * vmstat |tail -1
 * 0  1  612 867628 9084 7074844  0  0  99  68  0  0  5  1 93  2  0
 * </pre>
 *
 * @author Scott Douglass <scott@spacepirates.com>
 */
public class VirtualMemoryLinux extends OperatingSystem implements VirtualMemory {

  /** Logger for this class. */
  private static final Logger LOGGER =
    Logger.getLogger(VirtualMemoryLinux.class.getName());
  /** OperatingSystem to be used. */
  private static final String COMMAND = "vmstat";
  /** Array of field names. */
  private static final String[] FIELDS = {"running_procs", "blocked_procs",
    "memory_swapped",
    "memory_free", "memory_buffer", "memory_cache", "swap_in", "swap_out",
    "io_in", "io_out", "system_in", "system_cs", "cpu_user", "cpu_system",
    "cpu_idle", "cpu_waiting", "cpu_st"};
  /** Default memory page size. */
  //private static final Integer PAGE_SIZE = 4; //4KB

  /**
   * Constructor for the VirtualMemory command on Linux.
   */
  public VirtualMemoryLinux() {
    super();
    getCommandLine().add(COMMAND);
    //getCommandLine().add("-w"); // Removed by RedHat in 3.2.8?
    getCommandLine().add("-S");
    getCommandLine().add("k");
    getCommandLine().add("1");
    getCommandLine().add("2");
    setSkipLines(new Integer[] {1, 2, 3});
    try {
      run();
    } catch (JUAException ex) {
      LOGGER.log(Level.SEVERE, "Command failed.", ex);
    }
  }

  /**
   * Return the amount of free virtual memory in kB.
   * @return the amount of free memory
   */
  @Override
  public final Integer getFreeVirtualMemory() {
    final Double result = Double.valueOf(getOutputMap().get("memory_free"));
    return result.intValue();
  }

  /**
   * Build the output map from the stdout of the command.
   */
  @Override
  public final void parseOutput(final String inOutput) {
    Integer lineCount  = 0;
    Integer fieldCount = 0;
    System.out.println("DEBUG: " + inOutput);

    final Scanner lineScanner = new Scanner(inOutput);
    while (lineScanner.hasNextLine()) {
      final Scanner fieldScanner = new Scanner(lineScanner.nextLine());
      lineCount++;
      if (!getSkipLines().contains(lineCount)) {
        while (fieldScanner.hasNext() && fieldCount < FIELDS.length) {
          getOutputMap().put(FIELDS[fieldCount],
            formatValue(fieldScanner.next()));
          fieldCount++;
        }
      }
      fieldScanner.close();
    }
    lineScanner.close();
  }
}
