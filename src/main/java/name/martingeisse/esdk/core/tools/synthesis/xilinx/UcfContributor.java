package name.martingeisse.esdk.core.tools.synthesis.xilinx;

import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogNames;

import java.io.PrintWriter;

public interface UcfContributor {

    void contributeToUcf(PrintWriter out, VerilogNames verilogNames);

}
