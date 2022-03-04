package name.martingeisse.esdk.plot.render;

import name.martingeisse.esdk.core.util.vector.Vector;
import name.martingeisse.esdk.plot.DesignPlot;
import name.martingeisse.esdk.plot.Event;
import name.martingeisse.esdk.plot.VariablePlotDescriptor;
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
        for (VariablePlotDescriptor variablePlotDescriptor : designPlot.variablePlotDescriptors) {
            out.println("<th>" + variablePlotDescriptor.name + "</th>");
        }
        out.println("</tr>");
        for (Event event : designPlot.events) {
            out.println("<tr>");
            for (int i = 0; i < event.samples.size(); i++) {
                out.println("<td>");
                renderSample(designPlot.variablePlotDescriptors.get(i), event.samples.get(i));
                out.println("</td>");
            }
            out.println("</tr>");
        }
    }

    public void renderSample(VariablePlotDescriptor descriptor, Object sample) {
        if (descriptor instanceof VariablePlotDescriptor.Bit) {
            out.print((Boolean) sample ? "1" : "0");
        } else if (descriptor instanceof VariablePlotDescriptor.Vector) {
            VectorFormat format = ((VariablePlotDescriptor.Vector) descriptor).format;
            out.print(format.render(descriptor, (Vector) sample));
        } else {
            out.print("???");
        }
    }

}
