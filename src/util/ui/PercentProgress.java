package util.ui;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

public class PercentProgress
    extends JTextField
{
    private static final DecimalFormat format = new DecimalFormat("0.##");

    private LongSupplier current, max;

    private BooleanSupplier updateCondition;

    public PercentProgress(BooleanSupplier updateCondition, LongSupplier current, LongSupplier max)
    {
        setEditable(false);

        this.current = current;
        this.max = max;
        this.updateCondition = updateCondition;
    }

    public void update()
    {
        if(updateCondition.getAsBoolean())
            setText(format.format((double) current.getAsLong() / max.getAsLong() * 100) + "%");
    }

    public void setErrorState() {
        updateCondition = ()->false;
        setText("Error");
    }
}
