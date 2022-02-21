package name.martingeisse.esdk.core.tools.synthesis.xilinx;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogNames;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;

import java.io.PrintWriter;

public class FromThruToConstraint extends DesignItem implements UcfContributor, DesignItemOwned {

    private DesignItem fromSignal;
    private DesignItem[] thruSignals;
    private DesignItem toSignal;
    private int nanoseconds;

    public DesignItem getFromSignal() {
        return fromSignal;
    }

    public void setFromSignal(DesignItem fromSignal) {
        this.fromSignal = fromSignal;
    }

    public FromThruToConstraint from(DesignItem fromSignal) {
        setFromSignal(fromSignal);
        return this;
    }

    public DesignItem[] getThruSignals() {
        return thruSignals;
    }

    public void setThruSignals(DesignItem... thruSignals) {
        this.thruSignals = thruSignals;
    }

    public FromThruToConstraint thru(DesignItem... thruSignals) {
        setThruSignals(thruSignals);
        return this;
    }

    public DesignItem getToSignal() {
        return toSignal;
    }

    public void setToSignal(DesignItem toSignal) {
        this.toSignal = toSignal;
    }

    public FromThruToConstraint to(DesignItem toSignal) {
        setToSignal(toSignal);
        return this;
    }

    public void setNanoseconds(int nanoseconds) {
        this.nanoseconds = nanoseconds;
    }

    public FromThruToConstraint nanoseconds(int nanoseconds) {
        setNanoseconds(nanoseconds);
        return this;
    }

    @Override
    public VerilogContribution getVerilogContribution() {
        return new EmptyVerilogContribution();
    }

    public int getNanoseconds() {
        return nanoseconds;
    }

    @Override
    public void contributeToUcf(PrintWriter out, VerilogNames verilogNames) {
        String timespecName = verilogNames.assignGeneratedName(this);
        StringBuilder builder = new StringBuilder();

        if (fromSignal != null) {
            builder.append("NET \"").append(verilogNames.getName(fromSignal.getDesignItem()))
                    .append("\" TNM = ").append(timespecName).append("_FROM;\n");
        }
        if (thruSignals != null) {
            for (int i = 0; i < thruSignals.length; i++) {
                builder.append("NET \"").append(verilogNames.getName(thruSignals[i].getDesignItem()))
                        .append("\" TNM = ").append(timespecName).append("_THRU_").append(i).append(";\n");
            }
        }
        if (toSignal != null) {
            builder.append("NET \"").append(verilogNames.getName(toSignal.getDesignItem()))
                    .append("\" TNM = ").append(timespecName).append("_TO;\n");
        }

        builder.append("TIMESPEC \"TS_");
        builder.append(timespecName);
        builder.append("\" = ");
        if (fromSignal != null) {
            builder.append("FROM \"").append(timespecName).append("_FROM\" ");
        }
        if (thruSignals != null) {
            for (int i = 0; i < thruSignals.length; i++) {
                builder.append("THRU \"").append(timespecName).append("_THRU_").append(i).append("\" ");
            }
        }
        if (toSignal != null) {
            builder.append("TO \"").append(timespecName).append("_TO\" ");
        }
        builder.append(nanoseconds);
        builder.append(" ns;");
        out.println(builder);
    }

}
