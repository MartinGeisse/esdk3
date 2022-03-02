package name.martingeisse.esdktest.designs.components.riscv;

import name.martingeisse.esdk.core.library.simulation.ClockedSimulationDesignItem;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;

public class ExceptionPropagator extends ClockedSimulationDesignItem {

    private final RiscvCpu cpu;

    public ExceptionPropagator(RiscvCpu cpu) {
        super(cpu.clock);
        this.cpu = cpu;
    }

    @Override
    public void computeNextState() {
        int exceptionCode = cpu.exceptionCode.getValue().getAsSignedInt();
        if (exceptionCode != 0) {
            throw new RuntimeException("CPU exception, code " + exceptionCode);
        }
    }

    @Override
    public void updateState() {
    }

}
