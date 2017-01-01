package copy.profiler;

import copy.CopyState;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class DiskIOProfiler
{
    private static final int REFRESH_RATE = (int) (1.0 / 60);

    private JPanel panel;

    private CopyState state;

    private Timer t;

    private ProfilerHelper helper;

    public DiskIOProfiler(CopyState state)
    {
        this.state = state;

        helper = new ProfilerHelper(state);
        initUI();
    }

    private void initUI()
    {
        panel = new JPanel();
        panel.add(new JLabel("Under construction"));
    }

    public void start()
    {
        t = new Timer(REFRESH_RATE, e->update());
        t.setCoalesce(true);
        t.start();
    }

    public void pauseProfiling()
    {
        t.stop();
        helper.processPaused();
    }

    public void continueProfiling()
    {
        t.start();
        helper.processContinued();
    }

    public void terminate()
    {
        t.stop();

    }

    public JComponent getComponent()
    {
        return panel;
    }

    private void update()
    {
        //update values of the helper
        helper.interpolationTick();


    }
}
