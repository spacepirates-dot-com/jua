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
package com.spacepirates.jua.macosx;

import com.spacepirates.jua.OperatingSystem;
import com.spacepirates.jua.JUAException;
import com.spacepirates.jua.VirtualMemory;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of VirtualMemory for Mac OS X.
 * <pre>
 * scott@MusicStation.local(ttys005):~ > vm_stat
 * Mach Virtual Memory Statistics: (page size of 4096 bytes)
 * Pages free:                          12936.
 * Pages active:                       602013.
 * Pages inactive:                     107725.
 * Pages speculative:                  207765.
 * Pages wired down:                   118085.
 * "Translation faults":           1068322253.
 * Pages copy-on-write:             120438716.
 *  * Pages zero filled:               397439885.
 * Pages reactivated:                   11207.
 * Pageins:                           2846777.
 * Pageouts:                                0.
 * Object cache: 170 hits of 7316883 lookups (0% hit rate)
 * </pre>
 *
 * @author Scott Douglass <scott@swdouglass.com>
 */
public class VirtualMemoryMacOSX extends OperatingSystem implements VirtualMemory {

  /** Logger for this class. */
  private static final Logger LOGGER =
    Logger.getLogger(VirtualMemoryMacOSX.class.getName());

  /** Default memory page size. */
  private static final Integer PAGE_SIZE = 4; //4KB

  /**
   * Constructor for the Mac OS X flavor of vmstat (vm_stat).
   */
  public VirtualMemoryMacOSX() {
    super();
    getCommandLine().add("vm_stat");
    setSkipLines(new Integer[] {1, 13});
    try {
      run();
    } catch (JUAException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public final Integer getFreeVirtualMemory() {
    final Double result =
      Double.valueOf(getOutputMap().get("pages_free")) * PAGE_SIZE;
    return result.intValue();
    //return Integer.valueOf(outputMap.get("pages_free"));
  }

  /**
   * Method to parse the output from the vm_stat command into name/value pairs.
   * @param inOutput is the output of the vm_stat command
   */
  @Override
  public final void parseOutput(final String inOutput) {
    Integer lineCount = 0;

    final Scanner lineScanner = new Scanner(inOutput);
    while (lineScanner.hasNextLine()) {
      final Scanner fieldScanner = new Scanner(lineScanner.nextLine());
      lineCount++;
      if (!getSkipLines().contains(lineCount)) {
        fieldScanner.useDelimiter(":");
        while (fieldScanner.hasNext()) {
          final String name = formatName(fieldScanner.next());
          final String value = formatValue(fieldScanner.next());
          getOutputMap().put(name, value.substring(0, value.length() - 1));
        }
      }
      fieldScanner.close();
    }
    lineScanner.close();
  }
}
