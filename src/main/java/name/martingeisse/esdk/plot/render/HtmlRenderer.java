package name.martingeisse.esdk.plot.render;

import name.martingeisse.esdk.core.util.vector.Vector;
import name.martingeisse.esdk.plot.DesignPlot;
import name.martingeisse.esdk.plot.Event;
import name.martingeisse.esdk.plot.ValuePlotDescriptor;
import name.martingeisse.esdk.plot.VectorFormat;

import java.io.PrintWriter;

public class HtmlRenderer {

    protected final PrintWriter out;

    public HtmlRenderer(PrintWriter out) {
        this.out = out;
    }

    public void renderStandalone(DesignPlot designPlot) {
        beginDocument();
        renderEmbedded(designPlot);
        endDocument();
    }

    public void beginDocument() {
        out.println("<html><body>");
    }

    public void endDocument() {
        out.println("</body></html>");
    }

    public void renderEmbedded(DesignPlot designPlot) {
        out.println("<table><tr>");
        for (ValuePlotDescriptor valuePlotDescriptor : designPlot.valuePlotDescriptors) {
            out.println("<th>" + valuePlotDescriptor.name + "</th>");
        }
        out.println("</tr>");
        for (Event event : designPlot.events) {
            out.println("<tr>");
            for (int i = 0; i < event.samples.size(); i++) {
                out.println("<td>");
                renderSample(designPlot.valuePlotDescriptors.get(i), event.samples.get(i));
                out.println("</td>");
            }
            out.println("</tr>");
        }
    }

    public void renderSample(ValuePlotDescriptor descriptor, Object sample) {
        if (descriptor instanceof ValuePlotDescriptor.Bit) {
            out.print((Boolean) sample ? "1" : "0");
        } else if (descriptor instanceof ValuePlotDescriptor.Vector) {
            VectorFormat format = ((ValuePlotDescriptor.Vector) descriptor).format;
            out.print(format.render(descriptor, (Vector) sample));
        } else {
            out.print("???");
        }
    }

}
