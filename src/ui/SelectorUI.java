package ui;

import mgr.Manager;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

public class SelectorUI
{
    private static final String INITIAL_IN = System.getProperty("user.home") + "/Documents/test";
    private static final String INITIAL_OUT = System.getProperty("user.home") + "/Desktop/tmp";

    private Manager mgr;

    private JFrame frame;

    private WindowManager windowManager;

    public SelectorUI(Manager mgr, WindowManager windowManager)
    {
        this.mgr = mgr;
        this.windowManager = windowManager;
    }

    public void create()
    {
        SwingUtilities.invokeLater(()->{
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 3));

            JLabel inLabel = new JLabel("Input");
            panel.add(inLabel);

            JTextField in = new JTextField(INITIAL_IN);
            in.setEnabled(true);
            panel.add(in);
            inLabel.setLabelFor(in);

            JButton selectIn = new JButton("...");
            selectIn.addActionListener(e->{
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jfc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "dir";
                    }
                });
                jfc.setAcceptAllFileFilterUsed(false);
                jfc.setVisible(true);

                if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                    in.setText(jfc.getSelectedFile().getAbsolutePath());

                in.setEnabled(true);
            });
            panel.add(selectIn);

            JLabel outLabel = new JLabel("Output");
            panel.add(outLabel);

            JTextField out = new JTextField(INITIAL_OUT);
            panel.add(out);
            outLabel.setLabelFor(out);

            JButton selectOut = new JButton("...");
            selectOut.addActionListener(e->{
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jfc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "dir";
                    }
                });
                jfc.setAcceptAllFileFilterUsed(false);
                jfc.setVisible(true);

                if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                    out.setText(jfc.getSelectedFile().getAbsolutePath());

                out.setEnabled(true);
            });
            panel.add(selectOut);

            JButton start = new JButton("Start");
            start.addActionListener(e->{
                String inputFile = in.getText();

                if(!new File(inputFile).exists())
                {
                    JOptionPane.showMessageDialog(null, "Input-file doesn't exist: " + inputFile,
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                mgr.newCopy(in.getText(), out.getText());
            });
            panel.add(start);
            in.addActionListener(e->out.requestFocus());
            out.addActionListener(e->start.doClick());

            frame = windowManager.requestFrame("Backup");
            frame.setContentPane(panel);
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    public void dispose()
    {
        frame.setVisible(false);
        frame.dispose();
    }

    public void show()
    {
        frame.setVisible(true);
    }

    public void hide()
    {
        frame.setVisible(false);
    }
}
