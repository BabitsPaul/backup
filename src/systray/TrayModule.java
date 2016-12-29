package systray;

import copy.CopyManager;
import copy.task.Task;
import copy.task.TaskEventID;
import copy.task.TaskListener;

import java.awt.*;

public class TrayModule
    extends Menu
{
    private static final int SUFFIX_DISPLAY_LENGTH = 8;

    private SysTray tray;

    private CopyManager manager;

    private MenuItem pc;

    private boolean paused = false;

    public TrayModule(CopyManager mgr, String in, String out)
    {
        super(suffix(in) + "->" + suffix(out));

        this.manager = mgr;

        pc = new MenuItem("Pause");
        pc.setEnabled(true);
        pc.addActionListener(e->{
            if(paused)
                manager.continueProcess();
            else
                manager.pauseProcess();
        });
        add(pc);

        MenuItem abort = new MenuItem("Abort");
        add(abort);
        abort.addActionListener(e->manager.abortProcess());

        MenuItem showUI = new MenuItem("Show");
        add(showUI);
        showUI.addActionListener(e-> manager.showUI());

        MenuItem dispose = new MenuItem("Dispose");
        add(dispose);
        dispose.addActionListener(e-> manager.dispose());
    }

    void setTray(SysTray tray)
    {
        this.tray = tray;
    }

    private void updatePaused()
    {
        pc.setLabel("Continue");
        paused = true;
    }

    /**
     * called when the copyprocess continues its work
     */
    private void updateContinue()
    {
        pc.setLabel("Pause");
        paused = false;
    }

    private void updateCompleted()
    {
        pc.setLabel("\t");
        pc.setEnabled(false);
    }

    public void dispose()
    {
        tray.removeModule(this);
    }

    private static String suffix(String s)
    {
        if(TrayModule.SUFFIX_DISPLAY_LENGTH > s.length())
            return s;
        else
            return "..." + s.substring(s.length() - TrayModule.SUFFIX_DISPLAY_LENGTH);
    }

    public void createTaskListener(Task copyop)
    {
        copyop.addTaskListener(e -> updatePaused(), TaskEventID.TASK_PAUSED);
        copyop.addTaskListener(e -> updateContinue(), TaskEventID.TASK_CONTINUED);
        copyop.addTaskListener(e -> updateCompleted(), TaskEventID.TASK_COMPLETED);
        copyop.addTaskListener(e -> updateCompleted(), TaskEventID.TASK_ABORTED);
    }
}
