package name.martingeisse.esdktest.designs.components.bus;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.signal.connector.VectorConnector;

import java.util.ArrayList;
import java.util.List;

/**
 * Note: Each slave must decode at least one address bit. If a slave were to decode zero address bits,
 * it would use the whole address space as local or ignored bits, and that's an edge case we don't want
 * to deal with here.
 */
public class BusBuilder {

    private BusMasterInterface master;
    private List<SlaveAttachment> slaveAttachments = new ArrayList<>();

    public void setMaster(BusMasterInterface master) {
        this.master = master;
    }

    /**
     * This method intentionally takes the number of decoded address bits as a parameter, even though
     * it could be derived from the slave's local address bits. This is because most designs will want
     * to decode fewer bits to reduce bus logic, which effectively mirrors the slave at multiple
     * addresses.
     *
     * Note: Uses byte address for more readable code!
     */
    public void attachSlave(BusSlaveInterface slave, int decodedAddressBits, int baseByteAddress) {
        if (slave == null) {
            throw new IllegalArgumentException("slave is null");
        }
        if (decodedAddressBits < 1 || decodedAddressBits > 30) {
            throw new IllegalArgumentException("invalid number of decoded address bits: " + decodedAddressBits);
        }
        if ((baseByteAddress & (-1 >>> decodedAddressBits)) != 0) {
            throw new IllegalArgumentException("invalid baseByteAddress for " + decodedAddressBits +
                    " decoded address bits: " + Integer.toHexString(baseByteAddress));
        }
        if (decodedAddressBits + slave.wordAddress.getWidth() > 30) {
            throw new IllegalArgumentException("decoded address bits (" + decodedAddressBits +
                    " do not leave enough address bits for slave-local word address (" +
                    slave.wordAddress.getWidth() + " needed)");
        }
        for (SlaveAttachment otherAttachment : slaveAttachments) {
            int commonBits = Math.min(decodedAddressBits, otherAttachment.decodedAddressBits);
            checkOverlap(commonBits, baseByteAddress, otherAttachment.baseByteAddress);
        }
        slaveAttachments.add(new SlaveAttachment(slave, decodedAddressBits, baseByteAddress));
    }

    private void checkOverlap(int decodedAddressBits, int baseAddress1, int baseAddress2) {
        int mask = ~(-1 >>> decodedAddressBits);
        if ((baseAddress1 & mask) == (baseAddress2 & mask)) {
            throw new IllegalArgumentException("bus decoding overlap between " +
                    Integer.toHexString(baseAddress1) + " and " + Integer.toHexString(baseAddress2) +
                    " (" + decodedAddressBits + " decoded address bits)");
        }
    }

    public void build() {
        if (master == null) {
            throw new IllegalStateException("no master attached");
        }
        new ResultingBus(this);
    }

    private static final class SlaveAttachment {

        final BusSlaveInterface slave;
        final int decodedAddressBits;
        final int baseByteAddress;

        SlaveAttachment(BusSlaveInterface slave, int decodedAddressBits, int baseByteAddress) {
            this.slave = slave;
            this.decodedAddressBits = decodedAddressBits;
            this.baseByteAddress = baseByteAddress;
        }

    }

    private static class ResultingBus extends Component {

        ResultingBus(BusBuilder builder) {
            BitSignal partialAcknowledge = constant(true);
            VectorSignal partialReadData = constant(32, 0);
            for (SlaveAttachment attachment : builder.slaveAttachments) {

                VectorSignal upperAddressBits = builder.master.wordAddress.select(29, 30 - attachment.decodedAddressBits);
                int templateBits = attachment.baseByteAddress >> (32 - attachment.decodedAddressBits);
                attachment.decoderSignal = eq(upperAddressBits, templateBits);

                attachment.slave.enable.connect(and(builder.master.enable, attachment.decoderSignal));
                attachment.slave.write.connect(builder.master.write);
                connectLowerBits(attachment.slave.wordAddress, builder.master.wordAddress);
                connectLowerBits(attachment.slave.writeData, builder.master.writeData);
                attachment.slave.writeMask.connect(builder.master.writeMask);
                partialAcknowledge = when(attachment.decoderSignal, attachment.slave.acknowledge, partialAcknowledge);
                partialReadData = when(attachment.decoderSignal, zeroExtend32(attachment.slave.readData), partialReadData);
            }


                TODO build


        }

        private void connectLowerBits(VectorConnector connector, VectorSignal source) {
            if (connector.getWidth() == source.getWidth()) {
                connector.connect(source);
            } else {
                connector.connect(source.select(connector.getWidth() - 1, 0));
            }
        }

        private VectorSignal zeroExtend32(VectorSignal signal) {
            if (signal.getWidth() == 32) {
                return signal;
            } else {
                return concat(constant(32 - signal.getWidth(), 0), signal);
            }
        }

    }

}
