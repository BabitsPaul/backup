package ui.copy;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLabelUI;
import java.awt.*;

/**
 * this LabelUI alters the behavior of the label to prepend the ... and cuts of at
 * the beginning instead of appending and cutting at the end if an overflow happens
 */
public class LeftDotLabelUI
    extends MetalLabelUI
{
    @Override
    protected String layoutCL(JLabel label, FontMetrics fontMetrics, String text, Icon icon, Rectangle viewR, Rectangle iconR, Rectangle textR) {
        return new StringBuilder(
                super.layoutCL(label, fontMetrics, new StringBuilder(text).reverse().toString(), icon, viewR, iconR, textR)
                ).reverse().toString();
    }
}
