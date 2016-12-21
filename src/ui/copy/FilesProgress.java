package ui.copy;

import copy.CopyState;

import javax.swing.*;

public class FilesProgress
    extends UIComponent
{
    private JTextField currentFileField,
                        progressPercent;

    private JProgressBar progressBar;

    public FilesProgress(CopyState state)
    {
        super(state);
    }

    @Override
    public JPanel createUI()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        currentFileField = new JTextField();
        currentFileField.setEditable(false);
        panel.add(currentFileField);

        progressBar = new JProgressBar(0, STEPS);
        progressBar.setBorderPainted(true);
        panel.add(progressBar);

        progressPercent = new JTextField("Calculating...");
        progressPercent.setEditable(false);
        panel.add(progressPercent);

        return panel;
    }

    @Override
    public void updateUI()
    {
        currentFileField.setText(getState().getCurrentFile());

        if(!getState().isPrecomputationComplete())
            return;

        double progress = ((double) getState().getFilesCopied()) / getState().getFiles();
        progressBar.setValue((int) (STEPS * progress));
        progressPercent.setText(String.format("%." + PERCENT_DECIMAL_PLACES + "f%%", progress * 100));
    }
}
