package copy.profiler;

import copy.CopyState;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class DiskIOProfiler
{
    //TODO stability (write speeds have extreme drop-offs/rises
    private static final int REFRESH_RATE = (int) (1.0 / 10);

    private JPanel panel;

    private Timer t;

    private ProfilerHelper helper;

    private JLabel speed, time, timeRemaining;

    private ProfilerDiagram diagram;

    private ProfilerCache cache;

    public DiskIOProfiler(CopyState state)
    {
        cache = new ProfilerCache();
        diagram = new ProfilerDiagram(cache);
        helper = new ProfilerHelper(state);

        if(SwingUtilities.isEventDispatchThread())
        {
            initUI();
        }else{
            try {
                SwingUtilities.invokeAndWait(this::initUI);
            } catch (InterruptedException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    //public API

    private void initUI()
    {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        speed = new JLabel("");
        panel.add(speed);

        time = new JLabel("");
        panel.add(time);

        timeRemaining = new JLabel("");
        panel.add(timeRemaining);

        panel.add(diagram.getComponent());
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
        ProfilerDataPoint dataPoint = helper.getLastPoint();
        cache.place(dataPoint);

        SwingUtilities.invokeLater(()->{
            diagram.update();

            speed.setText(dataPoint.current.toString());
            time.setText(dataPoint.totalTimeRunning.toString());
            timeRemaining.setText(dataPoint.timeRemaining.toString());

            panel.revalidate();
            panel.repaint();
        });
    }
}
