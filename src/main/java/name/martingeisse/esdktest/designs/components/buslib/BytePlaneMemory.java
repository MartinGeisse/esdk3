package name.martingeisse.esdktest.designs.components.buslib;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.signal.connector.ClockConnector;
import name.martingeisse.esdk.core.util.vector.Vector;
import name.martingeisse.esdktest.designs.components.bus.BusSlaveInterface;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * A bus-attached memory with byte-granular access. The memory uses four Nx8 planes (with N being a parameter)
 * that connect to the bus byte lines. Callers should make sure that N is a multiple of the platform's RAM block
 * size -- for example, N=2048 seems to be a popular choice for FPGAs -- otherwise, the implementation
 * toolchain might round it up to the nearest RAM block size.
 *
 * The following are out of scope for this class:
 * - building a smaller byte-granular memory from a single RAM block and surrounding logic
 * - building a smaller word-granular memory
 */
public final class BytePlaneMemory extends Component {

    public final ClockConnector clock;
    public final BusSlaveInterface bus;

    public final ProceduralMemory memory0, memory1, memory2, memory3;

    /**
     * Each plane has size 2^planeSizeExponent. This is also the total size of the memory in words.
     */
    public BytePlaneMemory(int planeSizeExponent) {
        this.clock = inClock();
        this.bus = new BusSlaveInterface(this, planeSizeExponent, 32);

        int rowCount = (1 << planeSizeExponent);
        memory0 = memory(rowCount, 8);
        memory1 = memory(rowCount, 8);
        memory2 = memory(rowCount, 8);
        memory3 = memory(rowCount, 8);

        var readData0 = vectorRegister(8);
        var readData1 = vectorRegister(8);
        var readData2 = vectorRegister(8);
        var readData3 = vectorRegister(8);

        var secondReadCycle = bitRegister(false);

        on(clock, () -> {
            set(readData0, select(memory0, bus.wordAddress));
            set(readData1, select(memory1, bus.wordAddress));
            set(readData2, select(memory2, bus.wordAddress));
            set(readData3, select(memory3, bus.wordAddress));
            when(and(bus.enable, bus.write), () -> {
                when(select(bus.writeMask, 0), () -> set(select(memory0, bus.wordAddress), select(bus.writeData, 7, 0)));
                when(select(bus.writeMask, 1), () -> set(select(memory1, bus.wordAddress), select(bus.writeData, 15, 8)));
                when(select(bus.writeMask, 2), () -> set(select(memory2, bus.wordAddress), select(bus.writeData, 23, 16)));
                when(select(bus.writeMask, 3), () -> set(select(memory3, bus.wordAddress), select(bus.writeData, 31, 24)));
            });
            set(secondReadCycle, when(and(bus.enable, not(bus.write)), not(secondReadCycle), constant(false)));
        });

        bus.acknowledge = or(bus.write, secondReadCycle);
        bus.readData = concat(readData3, readData2, readData1, readData0);
    }

    public void loadContents(File file) throws IOException {
        loadContents(FileUtils.readFileToByteArray(file));
    }

    public void loadContents(byte[] data) {
        int memorySize = memory0.getMatrix().getRowCount() * 4;
        if (data.length > memorySize) {
            throw new IllegalArgumentException("data (size " + data.length + ") is too large for memory (size + " + memorySize + ")");
        }
        ProceduralMemory[] planes = {memory0, memory1, memory2, memory3};
        for (int i = 0; i < data.length; i++) {
            planes[i % 4].getMatrix().setRow(i / 4, Vector.of(8, data[i] & 0xff));
        }
    }

    public int getWord(int wordAddress) {
        return (memory3.getMatrix().getRow(wordAddress).getAsUnsignedInt() << 24) +
            (memory2.getMatrix().getRow(wordAddress).getAsUnsignedInt() << 16) +
            (memory1.getMatrix().getRow(wordAddress).getAsUnsignedInt() << 8) +
            memory0.getMatrix().getRow(wordAddress).getAsUnsignedInt();
    }

}
