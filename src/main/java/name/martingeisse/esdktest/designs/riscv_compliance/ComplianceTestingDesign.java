package name.martingeisse.esdktest.designs.riscv_compliance;

import name.martingeisse.esdk.core.Design;
import name.martingeisse.esdk.core.library.clocked.Clock;
import name.martingeisse.esdk.core.library.signal.BitConstant;
import name.martingeisse.esdk.core.library.simulation.ClockGenerator;
import name.martingeisse.esdktest.designs.components.bus.SingleSlaveDirectConnection;
import name.martingeisse.esdktest.designs.components.buslib.BytePlaneMemory;
import name.martingeisse.esdktest.designs.components.riscv.RiscvCpu;

public class ComplianceTestingDesign extends Design {

    public final RiscvCpu cpu;
    public final BytePlaneMemory memory;

    public ComplianceTestingDesign() {
        Clock clock = new Clock(new BitConstant(false));
        new ClockGenerator(clock, 40);
        cpu = new RiscvCpu();
        memory = new BytePlaneMemory(16);
        SingleSlaveDirectConnection.build(cpu.bus, memory.bus);
    }

}
