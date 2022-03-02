package name.martingeisse.esdktest.designs.riscv_compliance;

import name.martingeisse.esdk.core.Design;
import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.clocked.Clock;
import name.martingeisse.esdk.core.library.signal.BitConstant;
import name.martingeisse.esdk.core.library.signal.connector.ClockConnector;
import name.martingeisse.esdk.core.library.simulation.ClockGenerator;
import name.martingeisse.esdk.core.library.simulation.ClockedSimulationDesignItem;
import name.martingeisse.esdk.core.library.simulation.SimulationTimeLimit;
import name.martingeisse.esdktest.designs.components.bus.BusBuilder;
import name.martingeisse.esdktest.designs.components.bus.BusSlaveInterface;
import name.martingeisse.esdktest.designs.components.bus.SimulationBusSlave;
import name.martingeisse.esdktest.designs.components.buslib.BytePlaneMemory;
import name.martingeisse.esdktest.designs.components.riscv.ExceptionPropagator;
import name.martingeisse.esdktest.designs.components.riscv.RiscvCpu;
import name.martingeisse.esdktest.designs.components.riscv.TraceLogger;

import java.io.File;
import java.io.IOException;

public class ComplianceTestingDesign extends Design {

    private final Toplevel toplevel;

    public ComplianceTestingDesign() {
        toplevel = new Toplevel();
    }

    public void loadMemoryContents(File file) throws IOException {
        toplevel.memory.loadContents(file);
    }

    public int[] getOutput() {
        int begin = toplevel.controlDevice.complianceDataBegin >> 2;
        int end = toplevel.controlDevice.complianceDataEnd >> 2;
        int[] output = new int[end - begin];
        for (int i =0; i < output.length; i++) {
            output[i] = toplevel.memory.getWord(begin + i);
        }
        return output;
    }

    public static final class Toplevel extends Component {

        public final RiscvCpu cpu;
        public final BytePlaneMemory memory;
        public final ControlDevice controlDevice;

        public Toplevel() {
            Clock clock = new Clock(new BitConstant(false));
            new ClockGenerator(clock, 40);

            BusBuilder busBuilder = new BusBuilder();

            cpu = new RiscvCpu();
            cpu.clock.connect(clock);
            cpu.reset.connect(constant(false));
            busBuilder.setMaster(cpu.bus);
            cpu.interrupt.connect(constant(false));
            // TODO remove
            new TraceLogger(cpu);
            new SimulationTimeLimit(40 * 1000);
            new ExceptionPropagator(cpu);
            new ClockedSimulationDesignItem(cpu.clock) {

                @Override
                public void computeNextState() {
                    if (cpu.bus.wordAddress.getValue().getAsUnsignedInt() == 596 / 4 && cpu.bus.write.getValue()) {
                        throw new RuntimeException("***");
                    }
                }

                @Override
                public void updateState() {

                }

            };

            memory = new BytePlaneMemory(16);
            memory.clock.connect(clock);
            busBuilder.attachSlave(memory.bus, 8, 0);

            controlDevice = new ControlDevice();
            controlDevice.clock.connect(clock);
            busBuilder.attachSlave(controlDevice.bus, 8, 0xff000000);

            busBuilder.build();
        }

    }

    public static final class ControlDevice extends Component {

        public final ClockConnector clock = inClock();
        public final BusSlaveInterface bus = new BusSlaveInterface(this, 20, 32);
        public int complianceDataBegin;
        public int complianceDataEnd;

        public ControlDevice() {
            new SimulationBusSlave(clock, bus) {

                @Override
                protected int getReadData(int wordAddress) {
                    return 0;
                }

                @Override
                protected void executeWrite(int wordAddress, int data, int writeMask) {
                    switch ((wordAddress * 4) & 0xff) {

                        case (-4 & 0xff):
                            getDesign().stopSimulation();
                            break;

                        case (-8 & 0xff):
                            complianceDataBegin = data;
                            break;

                        case (-12 & 0xff):
                            complianceDataEnd = data;
                            break;

                    }
                }

            };
        }

    }

}
