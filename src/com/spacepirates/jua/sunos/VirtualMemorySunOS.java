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
package com.spacepirates.jua.sunos;

import com.spacepirates.jua.OperatingSystem;
import com.spacepirates.jua.JUAException;
import com.spacepirates.jua.VirtualMemory;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of VirtualMemory for SunOS.
 * <pre>
 * </pre>
 *
 * @author Scott Douglass <scott@swdouglass.com>
 */
public class VirtualMemorySunOS extends OperatingSystem implements VirtualMemory {
  /** Logger for this class. */
  private static final Logger LOGGER =
    Logger.getLogger(VirtualMemorySunOS.class.getName());
  /** Array of fields. */
  private static final String[] FIELDS = {"running", "blocked", "waiting",
      "memory_swap", "memory_free",
      "re", "mf", "pi", "po", "fr", "de", "sr",
      "m0", "m1", "m2", "m3",
      "in", "sy",
      "cs", "us", "sy", "id"};

  /**
   * Constructor for the SunOS flavor of vmstat.
   */
  public VirtualMemorySunOS() {
    super();
    getCommandLine().add("vmstat");
    getCommandLine().add("1");
    getCommandLine().add("2");
    setSkipLines(new Integer[] {1, 2, 3});
    try {
      run();
    } catch (JUAException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public final Integer getFreeVirtualMemory() {
    final Double result = Double.valueOf(getOutputMap().get("memory_free"));
    return result.intValue();
  }

  @Override
  public final void parseOutput(final String inOutput) {
    Integer lineCount  = 0;
    Integer fieldCount = 0;

    final Scanner lineScanner = new Scanner(inOutput);
    while (lineScanner.hasNextLine()) {
      final Scanner fieldScanner = new Scanner(lineScanner.nextLine());
      lineCount++;
      if (!getSkipLines().contains(lineCount)) {
        while (fieldScanner.hasNext()) {
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
