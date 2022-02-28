/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdktest.designs.components.vga;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.clocked.ClockedItem;
import name.martingeisse.esdk.core.library.signal.connector.BitConnector;
import name.martingeisse.esdk.core.library.signal.connector.ClockConnector;
import name.martingeisse.esdk.core.library.signal.connector.VectorConnector;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.validation.ValidationContext;

/**
 * Simulates a VGA monitor in a very loose way:
 * - it accepts color values as digital values
 * - pixels are accepted based on the clock
 * - sync pulses may be as short as one clock cycle
 * - the acual work is done by a {@link VgaSignalDecoder}
 * <p>
 * While this won't help in debugging sync problems, it should work well enough to debug the rest of the image
 * generation logic.
 */
public final class Monitor extends Component {

	public final ClockConnector clock = inClock();
	public final VectorConnector r = inVector(8);
	public final VectorConnector g = inVector(8);
	public final VectorConnector b = inVector(8);
	public final BitConnector hsync = inBit();
	public final BitConnector vsync = inBit();

	private VgaSignalDecoder vgaSignalDecoder;

	public Monitor() {
		new ClockedItem(clock) {

			private int sampledR, sampledG, sampledB;
			private boolean sampledHsync, sampledVsync;

			@Override
			public VerilogContribution getVerilogContribution() {
				return null;
			}

			@Override
			public void computeNextState() {
				sampledR = r.getValue().getAsUnsignedInt();
				sampledG = g.getValue().getAsUnsignedInt();
				sampledB = b.getValue().getAsUnsignedInt();
				sampledHsync = hsync.getValue();
				sampledVsync = vsync.getValue();

			}

			@Override
			public void updateState() {
				vgaSignalDecoder.consumeDataUnit(sampledR, sampledG, sampledB, sampledHsync, sampledVsync);

			}

		};
	}

	public void vgaSignalDecoder(VgaSignalDecoder vgaSignalDecoder) {
		this.vgaSignalDecoder = vgaSignalDecoder;
	}

	@Override
	public void validate(ValidationContext context) {
		if (vgaSignalDecoder == null) {
			context.reportError("monitor is missing its vgaSignalDecoder");
		}
	}

}
