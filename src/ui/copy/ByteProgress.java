package ui.copy;

import copy.CopyState;

import javax.swing.*;

public class ByteProgress
    extends UIComponent
{
    private JLabel totalBytesLable;
    private JTextField progressPercent;
    private JProgressBar progressBar;

    public ByteProgress(CopyState state)
    {
        super(state);
    }

    @Override
    public JPanel createUI()
    {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));

        totalBytesLable = new JLabel("Calculating...");
        result.add(totalBytesLable);

        progressBar = new JProgressBar(0, STEPS);
        progressBar.setBorderPainted(true);
        result.add(progressBar);

        progressPercent = new JTextField("Calculating...");
        progressPercent.setEditable(false);
        result.add(progressPercent);

        return result;
    }

    @Override
    public void updateUI()
    {
        if(!getState().isPrecomputationComplete())
            return;

        totalBytesLable.setText(getState().getTotalBytes() + " bytes");

        double progress = ((double) getState().getTotalBytesCopied()) / getState().getTotalBytes();

        progressBar.setValue((int) (progress * STEPS));
        progressPercent.setText(String.format("%." + PERCENT_DECIMAL_PLACES + "f%%", progress * 100));
    }
}
