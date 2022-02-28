package name.martingeisse.esdktest.designs.components.character;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.signal.connector.ClockConnector;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.MemoryDataUtil;
import name.martingeisse.esdk.core.util.vector.Vector;
import name.martingeisse.esdktest.designs.components.bus.BusSlaveInterface;

public class CharacterDisplay extends Component {

    public static final byte[] INITIAL_CHARACTER_MATRIX = convertInitialCharacterMatrix(new String[]{
            "################################################################################",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                           Hello world!                                       #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "#                                                                              #",
            "################################################################################",
    });

    public final ClockConnector clock = inClock();
    public final VectorSignal r, g, b;
    public final BitSignal hsync, vsync;
    public final BusSlaveInterface bus = new BusSlaveInterface(this, 12, 8);

    @SuppressWarnings("SuspiciousNameCombination")
    public CharacterDisplay() {

        //
        // stage 1: stage 1: VGA timing
        //

        var stage1x = vectorRegister(10);
        var stage1hblank = bitRegister();
        var stage1hsync = bitRegister();
        var stage1y = vectorRegister(10);
        var stage1vblank = bitRegister();
        var stage1vsync = bitRegister();
        on(clock, () -> {
            when(eq(stage1x, 799), () -> {
                set(stage1hblank, false);
                set(stage1x, 0);
                when(eq(stage1y, 524), () -> {
                    set(stage1vblank, false);
                    set(stage1y, 0);
                }, () -> {
                    when(eq(stage1y, 479), () -> {
                        set(stage1vblank, true);
                    }, eq(stage1y, 489), () -> {
                        set(stage1vsync, false);
                    }, eq(stage1y, 491), () -> {
                        set(stage1vsync, true);
                    });
                    inc(stage1y, 1);
                });
            }, () -> {
                when(eq(stage1x, 639), () -> {
                    set(stage1hblank, true);
                }, eq(stage1x, 655), () -> {
                    set(stage1hsync, false);
                }, eq(stage1x, 751), () -> {
                    set(stage1hsync, true);
                });
                inc(stage1x, 1);
            });
        });

        //
        // stage 2: read character matrix
        //

        var stage2blank = bitRegister();
        var stage2hsync = bitRegister();
        var stage2vsync = bitRegister();
        var characterMatrix = memory(128 * 32, 8);
        MemoryDataUtil.writeByteArrayToMatrix(INITIAL_CHARACTER_MATRIX, characterMatrix.getMatrix());
        var stage2character = vectorRegister(8);
        var stage2pixelX = vectorRegister(3);
        var stage2pixelY = vectorRegister(4);
        on(clock, () -> {
            set(stage2blank, or(stage1hblank, stage1vblank));
            set(stage2hsync, stage1hsync);
            set(stage2vsync, stage1vsync);
            set(stage2character, characterMatrix.select(concat(select(stage1y, 8, 4), select(stage1x, 9, 3))));
            set(stage2pixelX, select(stage1x, 2, 0));
            set(stage2pixelY, select(stage1y, 3, 0));
        });

        //
        // stage 3: read character generator
        //

        var stage3blank = bitRegister();
        var stage3hsync = bitRegister();
        var stage3vsync = bitRegister();
        var stage3pixels = vectorRegister(1);
        var characterGenerator = createCharacterGenerator();
        on(clock, () -> {
            set(stage3blank, stage2blank);
            set(stage3hsync, stage2hsync);
            set(stage3vsync, stage2vsync);
            set(stage3pixels, select(characterGenerator, concat(stage2character, stage2pixelY, stage2pixelX)));
        });

        //
        // stage 4 (non-registered): generate VGA signals
        //

        var outputPixel = and(select(stage3pixels, 0), not(stage3blank));
        this.r = concat(outputPixel, constant(2, 0));
        this.g = concat(outputPixel, constant(2, 0));
        this.b = concat(outputPixel, constant(1, 0), not(stage3blank));
        this.hsync = stage3hsync;
        this.vsync = stage3vsync;

        //
        // bus interface
        //

        bus.acknowledge = constant(true);
        bus.readData = constant(32, 0);
        on(clock, () -> {
            when(and(bus.enable, bus.write), () -> {
                set(characterMatrix.select(bus.wordAddress), bus.writeData);
            });
        });

    }

    private static byte[] convertInitialCharacterMatrix(String[] rows) {
        byte[] result = new byte[128 * 32];
        for (int row = 0; row < Math.min(rows.length, 32); row++) {
            String rowContents = rows[row];
            for (int column = 0; column < Math.min(rowContents.length(), 128); column++) {
                result[row * 128 + column] = (byte) rowContents.charAt(column);
            }
        }
        return result;
    }

    private ProceduralMemory createCharacterGenerator() {
        ProceduralMemory memory = memory(256 * 16 * 8, 1);
        Matrix matrix = memory.getMatrix();
        Vector pixelOn = Vector.of(1, 1);
        Vector pixelOff = Vector.of(1, 0);
        for (int i = 0; i < 256; i++) {
            byte[] singleCharacterData = CharacterGenerator.CHARACTER_DATA[i];
            for (int y = 0; y < 16; y++) {
                byte rowData = singleCharacterData[y];
                for (int x = 0; x < 8; x++) {
                    boolean pixelData = (rowData & (1 << x)) != 0;
                    matrix.setRow(((i * 16) + y) * 8 + x, pixelData ? pixelOn : pixelOff);
                }
            }
        }
        return memory;
    }

}
