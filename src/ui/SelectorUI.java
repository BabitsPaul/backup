package ui;

import mgr.Manager;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

public class SelectorUI
{
    private Manager mgr;

    private JFrame frame;

    public SelectorUI(Manager mgr)
    {
        this.mgr = mgr;
    }

    public void create()
    {
        SwingUtilities.invokeLater(()->{
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 3));

            panel.add(new JLabel("Input"));

            JTextField in = new JTextField();
            in.setEnabled(true);
            panel.add(in);

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
            });
            panel.add(selectIn);

            panel.add(new JLabel("Output"));

            JTextField out = new JTextField();
            panel.add(out);

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
            });
            panel.add(selectOut);

            JButton start = new JButton("Start");
            start.addActionListener(e->{
                mgr.newCopy(in.getText(), out.getText());
                hide();
            });
            panel.add(start);

            frame = new JFrame("Backup");
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
