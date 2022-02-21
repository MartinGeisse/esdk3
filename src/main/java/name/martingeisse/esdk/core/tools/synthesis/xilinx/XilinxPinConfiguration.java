/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.tools.synthesis.xilinx;

import name.martingeisse.esdk.core.library.pin.Pin;
import name.martingeisse.esdk.core.library.pin.PinConfiguration;

import java.io.PrintWriter;

/**
 *
 */
public final class XilinxPinConfiguration extends PinConfiguration {

	private String iostandard;
	private Integer drive;
	private Slew slew;
	private String[] additionalInfo;

	public String getIostandard() {
		return iostandard;
	}

	public void setIostandard(String iostandard) {
		this.iostandard = iostandard;
	}

	public Integer getDrive() {
		return drive;
	}

	public void setDrive(Integer drive) {
		this.drive = drive;
	}

	public Slew getSlew() {
		return slew;
	}

	public void setSlew(Slew slew) {
		this.slew = slew;
	}

	public String[] getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String... additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public void writeUcf(Pin pin, PrintWriter out) {
		out.print("NET \"");
		out.print(pin.getNetName());
		out.print("\" LOC = \"");
		out.print(pin.getId());
		out.print("\" ");
		if (iostandard != null) {
			out.print("| IOSTANDARD = ");
			out.print(iostandard);
			out.print(" ");
		}
		if (drive != null) {
			out.print("| DRIVE = ");
			out.print(drive);
			out.print(" ");
		}
		if (slew != null) {
			out.print("| SLEW = ");
			out.print(slew);
			out.print(" ");
		}
		if (additionalInfo != null) {
			for (String s : additionalInfo) {
				out.print("| ");
				out.print(s);
				out.print(' ');
			}
		}
		out.println(";");
	}

	public enum Slew {
		FAST,
		SLOW,
		QUIETIO
	}

}
