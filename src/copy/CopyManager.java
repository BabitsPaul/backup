package copy;

import copy.profiler.DiskIOProfiler;
import mgr.Manager;
import systray.TrayModule;
import ui.WindowManager;
import ui.copy.CopyUI;

import javax.swing.*;

public class CopyManager
{
    private Manager manager;

    private TrayModule module;

    private Precomputer precomputer;

    private DiskIOProfiler diskIOProfiler;

    private CopyUI ui;

    private CopyOp op;

    private boolean running = true;

    //paused flag
    private boolean paused = false;

    //clean up
    private boolean hardCleanup = false;

    private CleanupHelper cleanupHelper;

    public CopyManager(Manager manager, String in, String out, WindowManager windowManager)
    {
        //strip trailing slashes
        while (in.endsWith("/"))
            in = in.substring(0, in.length() - 1);

        while (out.endsWith("/"))
            out = out.substring(0, out.length() - 1);

        this.manager = manager;

        CopyState state = new CopyState(in, out);
        CopyLog log = new CopyLog();

        diskIOProfiler = new DiskIOProfiler(state);
        cleanupHelper = new CleanupHelper(in, out, log);
        module = new TrayModule(this, in, out);
        precomputer = new Precomputer(state, this);
        ui = new CopyUI(this, state, windowManager, log, diskIOProfiler.getComponent());
        op = new CopyOp(this, state, log);

        precomputer.start();
        cleanupHelper.onStart();
        ui.createUI();
        op.start();
        diskIOProfiler.start();
    }

    public void pauseProcess()
    {
        if(paused)
            throw new IllegalStateException("Already paused");

        op.pauseProcess();
        ui.pauseBackup();
        module.updatePaused();
        diskIOProfiler.pauseProfiling();
    }

    public void continueProcess()
    {
        if(!paused)
            throw new IllegalStateException("Not paused");

        op.continueProcess();
        ui.continueBackup();
        module.updateContinue();
        diskIOProfiler.continueProfiling();
    }

    public void abortProcess()
    {
        if(!paused)
            pauseProcess();

        if(JOptionPane.showOptionDialog(null, "Abort process?", "Abort",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                new String[]{"Abort process", "Cancel"}, "Cancel") == 1)
        {
            if(paused)
                continueProcess();

            return;
        }

        diskIOProfiler.terminate();
        precomputer.abort();
        op.abort();
        ui.backupComplete();
        module.updateCompleted();

        if(JOptionPane.showOptionDialog(null, "Remove already backedup files?", "Cleanup",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new String[]{"Cleanup", "Keep changes"}, "Keep changes") == 0)
            cleanup();
    }

    public void dispose()
    {
        if(running)
            abortProcess();

        ui.disposeUI();
        module.dispose();
    }

    public void showUI()
    {
        ui.show();
    }

    public TrayModule getTrayModule()
    {
        return module;
    }

    public void backupComplete()
    {
        //copyop is already closed
        ui.backupComplete();
        module.updateCompleted();

        manager.managerDisposed(this);

        running = false;

        if(hardCleanup)
            cleanupHelper.cleanUp();
    }

    private void cleanup()
    {
        hardCleanup = true;

        //wait for the process to terminate
        if(running)
            return;

        cleanupHelper.cleanUp();
    }

    public void precomputationComplete(boolean normalTermination)
    {
        ui.precomputationComplete(normalTermination);
    }
}
