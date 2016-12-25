package ui;

import copy.CopyManager;
import copy.CopyState;

import javax.swing.*;
import java.awt.*;

public class CopyUI
{
    private static final int UPDATE = 17;

    private CopyManager mgr;

    private CopyState state;

    private JFrame frame;

    //updateables

    private UpdateableLabel totalFilesLabel, currentFileLabel, totalBytesLabel;

    private UpdateableProgressBar totalFilesBar, currentFileBar, totalBytesBar;

    private PercentProgress totalFilesProgress, currentFileProgress, totalBytesProgress;

    private Timer t;

    //control
    private JButton pc;

    private JButton cancel;

    private boolean paused = false;

    private JButton dispose;

    public CopyUI(CopyManager mgr, CopyState state)
    {
        this.mgr = mgr;
        this.state = state;
    }

    public void createUI()
    {
        SwingUtilities.invokeLater(()->{
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(0, 3));

            //header
            panel.add(new JLabel(state.getFileIn()));
            panel.add(new JLabel(" ---> "));
            panel.add(new JLabel(state.getFileOut()));

            //files
            totalFilesLabel = new UpdateableLabel(()->state.isPrecomputationComplete(), "%d/%d files",
                    ()->state.getTotalFileProgress(), ()->state.getTotalFiles());
            panel.add(totalFilesLabel);

            totalFilesBar = new UpdateableProgressBar(()->state.isPrecomputationComplete(), ()->state.getTotalFileProgress(),
                    ()->state.getTotalFiles());
            panel.add(totalFilesBar);

            totalFilesProgress = new PercentProgress(()->state.isPrecomputationComplete(), ()->state.getTotalFileProgress(),
                                                        ()->state.getTotalFiles());
            panel.add(totalFilesProgress);

            //bytes
            totalBytesLabel = new UpdateableLabel(()->state.isPrecomputationComplete(), "%d/%d bytes",
                    ()->state.getTotalBytesProgress(), ()->state.getTotalBytes());
            panel.add(totalBytesLabel);

            totalBytesBar = new UpdateableProgressBar(()->state.isPrecomputationComplete(), ()->state.getTotalBytesProgress(),
                    ()->state.getTotalBytes());
            panel.add(totalBytesBar);

            totalBytesProgress = new PercentProgress(()->state.isPrecomputationComplete(), ()->state.getTotalBytesProgress(),
                    ()->state.getTotalBytes());
            panel.add(totalBytesProgress);

            //current file
            currentFileLabel = new UpdateableLabel(()->true, "%s", ()->state.getCurrentFile());
            panel.add(currentFileLabel);

            currentFileBar = new UpdateableProgressBar(()->true, ()->state.getCurrentFileProgress(), ()->state.getCurrentFileLength());
            panel.add(currentFileBar);

            currentFileProgress = new PercentProgress(()->true, ()->state.getCurrentFileProgress(), ()->state.getCurrentFileLength());
            panel.add(currentFileProgress);

            //control panel
            cancel = new JButton("Cancel");
            cancel.addActionListener(e->mgr.abortProcess());
            panel.add(cancel);

            pc = new JButton("Pause");
            pc.addActionListener(e->{
                if(paused)
                    mgr.continueProcess();
                else
                    mgr.pauseProcess();
            });
            panel.add(pc);

            dispose = new JButton("Dispose");
            dispose.addActionListener(e->mgr.dispose());
            dispose.setEnabled(false);
            panel.add(dispose);

            //TODO logging functionality???

            //main frame
            frame = new JFrame(state.getFileIn() + " --> " + state.getFileOut());
            frame.setContentPane(panel);
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            frame.setSize(100, 100);
            frame.setVisible(true);

            //timer
            t = new Timer(UPDATE, e-> SwingUtilities.invokeLater(()->{
                totalFilesLabel.update();
                totalFilesBar.update();
                totalFilesProgress.update();

                totalBytesLabel.update();
                totalBytesBar.update();
                totalBytesProgress.update();

                currentFileLabel.update();
                currentFileBar.update();
                currentFileProgress.update();

                frame.revalidate();
                frame.repaint();
            }));
            t.setCoalesce(true);
            t.setRepeats(true);
            t.start();
        });
    }

    public void disposeUI()
    {
        SwingUtilities.invokeLater(()->
        {
            t.stop();

            frame.setVisible(false);
            frame.dispose();
        });
    }

    public void show()
    {
        SwingUtilities.invokeLater(()->frame.setVisible(true));
    }

    public void pauseBackup()
    {
        SwingUtilities.invokeLater(()->{
            t.stop();

            paused = true;

            pc.setText("Continue");
        });
    }

    public void continueBackup()
    {
        SwingUtilities.invokeLater(()->{
            t.start();

            paused = false;

            pc.setText("Pause");
        });
    }

    public void backupComplete(boolean normalTermination)
    {
        SwingUtilities.invokeLater(()->{
            t.stop();

            cancel.setEnabled(false);
            pc.setEnabled(false);
            dispose.setEnabled(true);

            frame.getGlassPane().add(new PopupMenu(normalTermination ? "Complete" : "Aborted"));
        });
    }
}
