package name.martingeisse.esdk.plot.render;

import com.google.common.html.HtmlEscapers;
import name.martingeisse.esdk.core.util.MathUtil;
import name.martingeisse.esdk.core.util.vector.Vector;
import name.martingeisse.esdk.plot.*;
import name.martingeisse.esdk.plot.variable.MemorySample;
import name.martingeisse.esdk.plot.variable.NumberFormat;
import name.martingeisse.esdk.plot.variable.VariablePlotDescriptor;
import name.martingeisse.esdk.plot.variable.VectorFormat;

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
        out.println("<html><head><style type=\"text/css\">");
        out.println("table {border-collapse: collapse}");
        out.println("th {padding: 5px 10px; text-align: center;}");
        out.println("td {border: 1px solid #444; padding: 5px 10px; text-align: center;}");
        out.println("</style></head><body>");
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
            String text = format.render(descriptor, (Vector) sample);
            //noinspection UnstableApiUsage
            out.print(HtmlEscapers.htmlEscaper().escape(text));
        } else if (descriptor instanceof VariablePlotDescriptor.Memory) {
            VariablePlotDescriptor.Memory memoryDescriptor = (VariablePlotDescriptor.Memory)descriptor;
            int addressBits = MathUtil.bitsNeededFor(memoryDescriptor.rowCount);
            if (sample instanceof MemorySample.Full) {
                // TODO render multiple values per line
                MemorySample.Full fullSample = (MemorySample.Full)sample;
                for (int i = 0; i < fullSample.values.size(); i++) {
                    out.print(NumberFormat.HEXADECIMAL_PADDED.render(addressBits, i));
                    out.print(": ");
                    out.println(memoryDescriptor.rowFormat.render(descriptor, fullSample.values.get(i)));
                }
            } else if (sample instanceof MemorySample.Delta) {
                MemorySample.Delta deltaSample = (MemorySample.Delta)sample;
                for (MemorySample.Delta.RowPatch rowPatch : deltaSample.rowPatches) {
                    out.print(NumberFormat.HEXADECIMAL_PADDED.render(addressBits, rowPatch.rowIndex));
                    out.print(": ");
                    out.println(memoryDescriptor.rowFormat.render(descriptor, rowPatch.value));
                }
            } else {
                out.print("???");
            }
        } else {
            out.print("???");
        }
    }

}
