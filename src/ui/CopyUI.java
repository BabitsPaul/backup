package ui;

import copy.CopyProcessWatcher;
import copy.CopyState;
import main.Manager;
import ui.copy.ByteProgress;
import ui.copy.FileProgress;
import ui.copy.FilesProgress;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class CopyUI
    extends UI
    implements CopyProcessWatcher
{
    private static final int FRAME_REFRESH_RATE = 60;

    //TODO refactor UI

    private ByteProgress byteProg;
    private FileProgress fileProg;
    private FilesProgress filesProg;

    private JButton pc;

    private JButton done;

    private Timer t;

    private JPanel panel;

    private Manager mgr;

    private CopyUI(Manager mgr, CopyState state)
    {
        this.mgr = mgr;

        byteProg = new ByteProgress(state);
        fileProg = new FileProgress(state);
        filesProg = new FilesProgress(state);
    }

    /**
     * wrapping the UI-creation in this method asserts the UI can only be used once it's
     * been created
     *
     * the method itself ensures that the creation is done within the event dispatch thread
     * and can thus be called anywhere
     *
     * @param mgr
     * @param state
     * @return
     */
    public static CopyUI createUI(Manager mgr, CopyState state)
    {
        CopyUI ui = new CopyUI(mgr, state);

        if(SwingUtilities.isEventDispatchThread())
            ui.createUI(state);
        else
        {
            try{
                SwingUtilities.invokeAndWait(()->{
                    ui.createUI(state);
                });
            }catch (InterruptedException | InvocationTargetException e)
            {
                //TODO report error
                return null;
            }
        }

        return ui;
    }

    private synchronized void createUI(CopyState state)
    {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //header
        JPanel header = new JPanel();
        panel.add(header);
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));

        header.add(new JLabel(""));
        header.add(new JLabel(state.getInputFile()));
        header.add(new JLabel("          "));
        header.add(new JLabel(state.getOutputFile()));

        //progress
        panel.add(filesProg.createUI());
        panel.add(byteProg.createUI());
        panel.add(fileProg.createUI());

        //footer
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
        panel.add(footer);

        JButton cancel = new JButton("Cancel");
        footer.add(cancel);
        cancel.addActionListener(e -> mgr.cancelCopying());

        pc = new JButton("Pause/Continue");
        footer.add(pc);
        pc.setEnabled(false);
        pc.addActionListener(e -> mgr.triggerCopying());

        done = new JButton("New backup");
        done.setEnabled(false);
        done.addActionListener(e->mgr.disposeCopyUI());
        footer.add(done);

        //init updater
        t = new Timer(0, e-> updateUI());
        t.setDelay((int) (1000.0/ FRAME_REFRESH_RATE));
        t.setCoalesce(true);
        t.setRepeats(true);
    }

    public synchronized JPanel getPanel() {
        return panel;
    }

    private void updateUI()
    {
        panel.invalidate();

        fileProg.updateUI();
        filesProg.updateUI();
        byteProg.updateUI();

        panel.validate();
        panel.repaint();
    }

    @Override
    public void copyingPaused()
    {
        t.stop();

        SwingUtilities.invokeLater(()->{
            pc.setText("Continue");
            pc.revalidate();
            pc.repaint();
        });
    }

    @Override
    public void copyingContinued() {
        t.restart();

        SwingUtilities.invokeLater(()->{
            pc.setText("Pause");
            pc.revalidate();
            pc.repaint();
        });
    }

    @Override
    public void copyingStarted()
    {
        SwingUtilities.invokeLater(()->{
            pc.setEnabled(true);
            pc.setText("Pause");
            pc.revalidate();
            pc.repaint();
        });

        t.start();
    }

    @Override
    public void copyingTerminated()
    {
        SwingUtilities.invokeLater(()->{
            if(panel.getParent() == null)
                return;

            panel.invalidate();

            //update button states
            done.setEnabled(true);
            pc.setEnabled(false);

            //TODO add state (i.e. aborted or completed")
            JLabel label = new JLabel("Completed");
            panel.add(label);

            panel.revalidate();
            panel.repaint();
        });

        //no need to update ui, copying is complete
        t.stop();
    }
}