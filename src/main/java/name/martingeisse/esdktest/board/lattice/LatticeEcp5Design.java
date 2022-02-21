package name.martingeisse.esdktest.board.lattice;

import name.martingeisse.esdk.core.Design;
import name.martingeisse.esdk.core.library.pin.BidirectionalPin;
import name.martingeisse.esdk.core.library.pin.InputPin;
import name.martingeisse.esdk.core.library.pin.OutputPin;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.tools.synthesis.lattice.LatticePinConfiguration;

public class LatticeEcp5Design extends Design {

    public InputPin createInputPin(
            String id,
            Ecp5IoType ioType,
            LatticePinModifier... modifiers
    ) {

        // configuration
        LatticePinConfiguration configuration = new LatticePinConfiguration();
        LatticePinModifier.ioType(ioType).apply(configuration);
        LatticePinModifier.apply(configuration, modifiers);

        // pin
        InputPin pin = new InputPin();
        pin.setId(id);
        pin.setConfiguration(configuration);

        return pin;
    }

    public OutputPin createOutputPin(
            String id,
            BitSignal outputSignal,
            Ecp5IoType ioType,
            Ecp5SlewRate slewRate,
            LatticePinModifier... modifiers
    ) {

        // configuration
        LatticePinConfiguration configuration = new LatticePinConfiguration();
        LatticePinModifier.ioType(ioType).apply(configuration);
        if (slewRate != null) {
            LatticePinModifier.slewRate(slewRate).apply(configuration);
        }
        LatticePinModifier.apply(configuration, modifiers);

        // pin
        OutputPin pin = new OutputPin();
        pin.setId(id);
        pin.setConfiguration(configuration);
        pin.setOutputSignal(outputSignal);

        return pin;
    }

    public BidirectionalPin createInOutPin(
            String id,
            BitSignal outputSignal,
            BitSignal enableSignal,
            Ecp5IoType ioType,
            Ecp5SlewRate slewRate,
            LatticePinModifier... modifiers
    ) {

        // configuration
        LatticePinConfiguration configuration = new LatticePinConfiguration();
        LatticePinModifier.ioType(ioType).apply(configuration);
        if (slewRate != null) {
            LatticePinModifier.slewRate(slewRate).apply(configuration);
        }
        LatticePinModifier.apply(configuration, modifiers);

        // pin
        BidirectionalPin pin = new BidirectionalPin();
        pin.setId(id);
        pin.setConfiguration(configuration);
        pin.setOutputSignal(outputSignal);
        pin.setOutputEnableSignal(enableSignal);

        return pin;
    }

}
