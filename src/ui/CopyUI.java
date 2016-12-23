package ui;

import copy.CopyManager;
import copy.CopyState;

import javax.swing.*;

public class CopyUI
{
    private CopyManager mgr;

    private CopyState state;

    private JFrame frame;

    public CopyUI(CopyManager mgr, CopyState state)
    {
        this.mgr = mgr;
        this.state = state;
    }

    public void createUI()
    {
        SwingUtilities.invokeLater(()->{
            frame = new JFrame(state.getFileIn() + " --> " + state.getFileOut());
            frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            frame.setSize(100, 100);
            frame.setVisible(true);
        });
    }

    public void disposeUI()
    {
        SwingUtilities.invokeLater(()->{
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

    }

    public void continueBackup()
    {

    }

    public void backupComplete(boolean normalTermination)
    {

    }
}
