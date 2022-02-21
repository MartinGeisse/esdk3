package name.martingeisse.esdktest.designs.components;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.blackbox.BlackboxInstance;
import name.martingeisse.esdk.core.library.clocked.Clock;
import name.martingeisse.esdk.core.library.signal.BitSignal;

/**
 * Assumes an 48 MHz input clock (as is the case on the OrangeCrab board) and generates an output clock using the
 * specified three dividers. The output clock has (48 * feedbackDivider / inputDivider / outputDivider) MHz, but the
 * dividers must be chosen such that PLL-internal frequencies stay in the specified ranges.
 */
public final class SimplePll {

    public static Clock create(BitSignal inputClock, int inputDivider, int outputDivider, int feedbackDivider) {

        // create clock network
        BlackboxInstance pll = new BlackboxInstance("EHXPLLL");

        // ports
        pll.createBitInputPort("CLKI", inputClock);
//		pll.createBitInputPort("CLKI2", false);
//		pll.createBitInputPort("SEL", false);
//		pll.createBitInputPort("CLKFB", false);
//		pll.createVectorInputPort("PHASESEL", 2, RtlVectorConstant.of(realm, 2, 0));
//		pll.createBitInputPort("PHASEDIR", false);
//		pll.createBitInputPort("PHASESTEP", false);
//		pll.createBitInputPort("PHASELOADREG", false);
        pll.createBitInputPort("STDBY", false);
        pll.createBitInputPort("RST", false);
        pll.createBitInputPort("ENCLKOP", true);
//		pll.createBitInputPort("ENCLKOS", false);
//		pll.createBitInputPort("ENCLKOS2", false);
        pll.createBitInputPort("ENCLKOS3", true); // not clear if this is needed when using INT_OS3 as feedback, probably not

        // "make it work" attributes and parameters
        pll.getAttributes().put("FREQUENCY_PIN_CLKI", 48);

        // The following settings are seemingly used by foboot, but for some reason cause extremely long startup times.
        // Since the attributes are not documented, nobody knows why.
//		pll.getAttributes().put("ICP_CURRENT", "6");
//		pll.getAttributes().put("LPF_RESISTOR", "16");
//		pll.getAttributes().put("MFG_ENABLE_FILTEROPAMP", "1");
//		pll.getAttributes().put("MFG_GMCREF_SEL", "2");

        // input clock
        pll.getParameters().put("CLKI_DIV", inputDivider);

        // output clock
        pll.getParameters().put("CLKOP_ENABLE", "ENABLED");
        pll.getParameters().put("CLKOP_DIV", outputDivider);
        pll.getParameters().put("CLKOP_FPHASE", 0);
        pll.getParameters().put("CLKOP_CPHASE", 0);

        // feedback clock
        pll.getParameters().put("FEEDBK_PATH", "INT_OS3");
        pll.getParameters().put("CLKOS3_ENABLE", "ENABLED");
        pll.getParameters().put("CLKOS3_DIV", 1);
        pll.getParameters().put("CLKOS3_FPHASE", 0);
        pll.getParameters().put("CLKOS3_CPHASE", 0);
        pll.getParameters().put("CLKFB_DIV", feedbackDivider);

        return new Clock(pll.createBitOutputPort("CLKOP"));
    }
}
