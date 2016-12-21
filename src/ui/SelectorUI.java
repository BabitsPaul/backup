package ui;

import main.Manager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

//TODO proper ui.UI alignment
//TODO file chooser

public class SelectorUI
    extends UI
{
    private JPanel panel;

    private boolean created;

    private Manager mgr;

    public SelectorUI(Manager mgr)
    {
        this.mgr = mgr;

        SwingUtilities.invokeLater(() -> createUI());
    }

    private synchronized void createUI()
    {
        panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel inlabel = new JLabel("Source");
        panel.add(inlabel);

        JTextField in = new JTextField();
        in.setEnabled(true);
        panel.add(in);

        JLabel outLabel = new JLabel("Destination");
        panel.add(outLabel);

        JTextField out = new JTextField();
        panel.add(out);

        JButton start = new JButton("Start");
        panel.add(start);
        start.addActionListener(e -> new SwingWorker<Void, Void>(){
            @Override
            protected Void doInBackground() throws Exception {
                mgr.newCopyProcess(in.getText(), out.getText(), new ArrayList<>());

                return null;
            }
        }.execute());

        created = true;
        notify();
    }

    public synchronized JPanel getPanel()
    {
        if(!created)
            try {
                wait();
            }catch (InterruptedException ignored){}

        return panel;
    }
}