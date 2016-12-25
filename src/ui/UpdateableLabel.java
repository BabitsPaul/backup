package ui;

import javax.swing.*;
import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class UpdateableLabel
    extends JLabel
{
    private static final String DEFAULT_CONTENT = "Calculating...";

    private Supplier<?>[] suppliers;

    private BooleanSupplier updateCondition;

    private String formatString;

    public UpdateableLabel(BooleanSupplier updateCondition, String formatString, Supplier<?>... suppliers)
    {
        this.formatString = formatString;
        this.suppliers = suppliers;
        this.updateCondition = updateCondition;

        if(updateCondition.getAsBoolean())
            update();
        else
            setText(DEFAULT_CONTENT);
    }

    public void update()
    {
        if(updateCondition.getAsBoolean())
            setText(String.format(formatString, Arrays.stream(suppliers).map(s->s.get()).toArray()));
    }
}
