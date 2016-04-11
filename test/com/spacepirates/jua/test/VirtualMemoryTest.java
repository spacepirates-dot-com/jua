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
package com.spacepirates.jua.test;

import com.spacepirates.jua.JUAException;
import com.spacepirates.jua.OperatingSystem;
import com.spacepirates.jua.VirtualMemory;
import org.junit.Test;

/**
 *
 * @author scott
 */
public class VirtualMemoryTest {

  /** The VirtualMemory test class. */
  private static final VirtualMemory VMSTAT =
    OperatingSystem.getVirtualMemory();

  /**
   * Test that the free memory function is working.
   */
  @Test
  public final void freeMem() {
    final Integer freeMem = VMSTAT.getFreeVirtualMemory();
    System.out.println("Free memory: " + freeMem + " kB\n\n");
    assert (freeMem > 0);
  }

  /**
   * Test that the default output is working.
   */
  @Test
  public final void run() {
    final String output = VMSTAT.toString();
    System.out.println(output);
    System.out.println(VMSTAT.getOutput().toString());
    assert (output != null);
  }

  @Test
  public final void ugly() {
    VMSTAT.setPrettyPrint("false");
    final String output = VMSTAT.toString();
    System.out.println(output);
    int lineCount = output.split(System.getProperty("line.separator")).length;
    assert (lineCount == 1);
  }

  @Test
  public final void agentZero() throws JUAException {
    System.out.println("#################################################################");
    VMSTAT.setCallerID("agentZero");
    VMSTAT.setPrettyPrint("true");
    VMSTAT.run();
    final String output = VMSTAT.toString();
    System.out.println(output);
    assert (output != null);
  }

}
