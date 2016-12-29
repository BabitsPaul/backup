package ui.copy;

import javax.swing.*;
import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

public class UpdatableProgressBar
    extends JProgressBar
{
    private static final int RESOLUTION = 10000;

    private LongSupplier current, max;
    
    private BooleanSupplier updateCondition;
    
    public UpdatableProgressBar(BooleanSupplier updateCondition, LongSupplier current, LongSupplier max)
    {
        super(0, RESOLUTION);
        super.setBorderPainted(true);
        super.setStringPainted(true);
        super.setIndeterminate(true);

        this.current = current;
        this.max = max;
        this.updateCondition = updateCondition;
    }

    public void update()
    {
        if(updateCondition.getAsBoolean()) {
            super.setIndeterminate(false);
            super.setValue((int) (((double) current.getAsLong()) / max.getAsLong() * RESOLUTION));
        }
    }

    public void setDone() {
        setValue(RESOLUTION);
        setIndeterminate(false);
    }
}
