package ui.copy;

import copy.CopyLog;
import copy.CopyManager;
import copy.CopyState;
import ui.WindowManager;
import util.ui.LeftDotLabelUI;
import util.ui.PercentProgress;
import util.ui.UpdatableLabel;
import util.ui.UpdatableProgressBar;

import javax.swing.*;
import java.awt.*;

public class CopyUI
{
    private static final int UPDATE = 17;

    private LogUI logUI;

    private CopyManager mgr;

    private CopyState state;

    private JFrame frame;

    //updatables

    private UpdatableLabel totalFilesLabel, currentFileLabel, totalBytesLabel;

    private UpdatableProgressBar totalFilesBar, currentFileBar, totalBytesBar;

    private PercentProgress totalFilesProgress, currentFileProgress, totalBytesProgress;

    private Timer t;

    //control
    private JButton pc;

    private JButton cancel;

    private JButton log;

    private boolean paused = false;

    private JButton dispose;

    private WindowManager windowManager;

    private JComponent diskIOProfilerUI;

    public CopyUI(CopyManager mgr, CopyState state, WindowManager manager, CopyLog log, JComponent diskProfilerUI)
    {
        this.mgr = mgr;
        this.state = state;
        this.windowManager = manager;
        logUI = new LogUI(state, log, manager);
        this.diskIOProfilerUI = diskProfilerUI;
    }

    public void createUI()
    {
        SwingUtilities.invokeLater(()->{
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            //header
            JPanel header = new JPanel();
            header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
            panel.add(header);
            header.add(new JLabel(state.getFileIn()));
            header.add(new JLabel(" ---> "));
            header.add(new JLabel(state.getFileOut()));

            //files
            JPanel statistics = new JPanel();
            statistics.setLayout(new GridLayout(3, 3));
            panel.add(statistics);

            totalFilesLabel = new UpdatableLabel(()->state.isPrecomputationComplete(), "%d/%d files",
                    ()->state.getTotalFileProgress(), ()->state.getTotalFiles());
            statistics.add(totalFilesLabel);

            totalFilesBar = new UpdatableProgressBar(()->state.isPrecomputationComplete(), ()->state.getTotalFileProgress(),
                    ()->state.getTotalFiles());
            statistics.add(totalFilesBar);

            totalFilesProgress = new PercentProgress(()->state.isPrecomputationComplete(), ()->state.getTotalFileProgress(),
                                                        ()->state.getTotalFiles());
            statistics.add(totalFilesProgress);

            //bytes
            totalBytesLabel = new UpdatableLabel(()->state.isPrecomputationComplete(), "%d/%d bytes",
                    ()->state.getTotalBytesProgress(), ()->state.getTotalBytes());
            statistics.add(totalBytesLabel);

            totalBytesBar = new UpdatableProgressBar(()->state.isPrecomputationComplete(), ()->state.getTotalBytesProgress(),
                    ()->state.getTotalBytes());
            statistics.add(totalBytesBar);

            totalBytesProgress = new PercentProgress(()->state.isPrecomputationComplete(), ()->state.getTotalBytesProgress(),
                    ()->state.getTotalBytes());
            statistics.add(totalBytesProgress);

            //current file
            currentFileLabel = new UpdatableLabel(()->true, "%s", ()->state.getCurrentFile());
            statistics.add(currentFileLabel);
            currentFileLabel.setUI(new LeftDotLabelUI());

            currentFileBar = new UpdatableProgressBar(()->true, ()->state.getCurrentFileProgress(), ()->state.getCurrentFileLength());
            statistics.add(currentFileBar);

            currentFileProgress = new PercentProgress(()->true, ()->state.getCurrentFileProgress(), ()->state.getCurrentFileLength());
            statistics.add(currentFileProgress);

            //throughput
            panel.add(diskIOProfilerUI);

            //control panel
            JPanel controls = new JPanel();
            controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
            panel.add(controls);

            cancel = new JButton("Cancel");
            cancel.addActionListener(e->mgr.abortProcess());
            controls.add(cancel);

            pc = new JButton("Pause");
            pc.addActionListener(e->{
                if(paused)
                    mgr.continueProcess();
                else
                    mgr.pauseProcess();
            });
            controls.add(pc);

            dispose = new JButton("Dispose");
            dispose.addActionListener(e->mgr.dispose());
            dispose.setEnabled(false);
            controls.add(dispose);

            log = new JButton("Show log");
            log.addActionListener(e->logUI.showUI());
            log.setEnabled(false);
            controls.add(log);

            //main frame
            frame = windowManager.requestFrame(state.getFileIn() + " --> " + state.getFileOut());
            frame.setContentPane(panel);
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            frame.setVisible(true);

            //timer
            t = new Timer(UPDATE, e->update());
            t.setCoalesce(true);
            t.setRepeats(true);
            t.start();
        });
    }

    private void update()
    {
        SwingUtilities.invokeLater(()->{
            if(totalFilesLabel == null)
                return;

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
        });
    }

    public void disposeUI()
    {
        SwingUtilities.invokeLater(()->
        {
            t.stop();

            frame.setVisible(false);
            frame.dispose();

            logUI.dispose();
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

    public void backupComplete()
    {
        SwingUtilities.invokeLater(()->{
            //set progressbars appropriately
            currentFileBar.setDone();
            totalBytesBar.setDone();
            totalFilesBar.setDone();

            t.stop();

            cancel.setEnabled(false);
            pc.setEnabled(false);
            dispose.setEnabled(true);
            log.setEnabled(true);
        });
    }

    public void precomputationComplete(boolean normalTermination)
    {
        if(normalTermination)
        {
            update();
        }else
        {
            SwingUtilities.invokeLater(()->{
                totalBytesLabel.setErrorState();
                totalFilesLabel.setErrorState();

                totalFilesBar.setDone();
                totalBytesBar.setDone();

                totalBytesProgress.setErrorState();
                totalFilesProgress.setErrorState();

                frame.revalidate();
                frame.repaint();
            });
        }
    }
}
