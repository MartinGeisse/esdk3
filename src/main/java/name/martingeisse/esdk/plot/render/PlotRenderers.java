package name.martingeisse.esdk.plot.render;

import name.martingeisse.esdk.plot.builder.ClockedPlotter;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class PlotRenderers {

    private PlotRenderers() {
    }

    public static void toHtml(ClockedPlotter plotter, File file, boolean mkdir) throws IOException {
        if (mkdir && !file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IOException("could not create parent folder(s): " + file.getParentFile());
        }
        try (FileOutputStream outStream = new FileOutputStream(file)) {
            try (OutputStreamWriter writer = new OutputStreamWriter(outStream, StandardCharsets.UTF_8)) {
                try (PrintWriter printWriter = new PrintWriter(writer)) {
                    new HtmlRenderer(printWriter).renderStandalone(plotter.buildPlot());
                }
            }
        }
    }

}
