package name.martingeisse.esdktest.designs.components.character;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.signal.connector.ClockConnector;
import name.martingeisse.esdktest.designs.components.bus.BusSlaveInterface;
import name.martingeisse.esdktest.designs.components.bus.SimulationBusSlave;
import name.martingeisse.esdktest.designs.components.vga.Monitor;
import name.martingeisse.esdktest.designs.components.vga.MonitorPanel;

/**
 * A simulation-only bus slave that behaves like the {@link CharacterDisplay} with respect to bus requests
 * but outputs to a {@link SimulationCharacterDisplayPanel} in a much more efficient way than a {@link Monitor}
 * and {@link MonitorPanel} would do.
 */
public class SimulationCharacterDisplay extends Component {

    public final ClockConnector clock = inClock();
    public final BusSlaveInterface bus = new BusSlaveInterface(this, 12, 8);
    public final byte[] characterMatrix = new byte[32 * 128];

    public SimulationCharacterDisplay() {
        new SimulationBusSlave(clock, bus) {

            @Override
            protected int getReadData(int wordAddress) {
                return 0;
            }

            @Override
            protected void executeWrite(int wordAddress, int data, int writeMask) {
                int column = wordAddress & 127;
                int row = (wordAddress >> 7) & 31;
                characterMatrix[row * 128 + column] = (byte) data;
            }

        };
    }

    public void setCharacter(int x, int y, byte b) {
        if (x < 0 || x >= 128 || y < 0 || y >= 32) {
            throw new RuntimeException("invalid character position: " + x + ", " + y);
        }
        characterMatrix[(y << 7) + x] = b;
    }

}
