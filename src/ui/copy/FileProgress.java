package ui.copy;

import copy.CopyState;

import javax.swing.*;

public class FileProgress
    extends UIComponent
{
    private JTextField currentFile;

    private JProgressBar bar;

    private JTextField percent;

    public FileProgress(CopyState state)
    {
        super(state);
    }

    public JPanel createUI()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        currentFile = new JTextField();
        currentFile.setEditable(false);
        panel.add(currentFile);

        bar = new JProgressBar(0, STEPS);
        bar.setBorderPainted(true);
        panel.add(bar);

        percent = new JTextField("Calculating...");
        percent.setEditable(false);
        panel.add(percent);

        return panel;
    }

    public void updateUI()
    {
        currentFile.setText(getState().getCurrentFile());

        double progress = ((double) getState().getFileBytesCopied()) / getState().getFileBytes();

        bar.setValue((int) (progress * STEPS));
        percent.setText(String.format("%." + PERCENT_DECIMAL_PLACES + "f%%", progress * 100));
    }
}