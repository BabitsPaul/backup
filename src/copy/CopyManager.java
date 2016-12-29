package copy;

import mgr.Manager;
import systray.TrayModule;
import ui.WindowManager;
import ui.copy.CopyUI;

public class CopyManager
{
    private Manager manager;

    private TrayModule module;

    private Precomputer precomputer;

    private CopyUI ui;

    private CopyOp op;

    private CopyLog log;

    private boolean running = true;

    //clean up
    private boolean hardCleanup = false;    //TODO setter???

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

        log = new CopyLog();
        cleanupHelper = new CleanupHelper(in, out, log);
        module = new TrayModule(this, in, out);
        precomputer = new Precomputer(state);
        ui = new CopyUI(this, state, windowManager);
        op = new CopyOp(this, state, log);

        cleanupHelper.onStart();

        ui.createUI();
        op.start();
        precomputer.start();
    }

    public void pauseProcess()
    {
        op.pauseProcess();
        ui.pauseBackup();
        module.updatePaused();
    }

    public void continueProcess()
    {
        op.continueProcess();
        ui.continueBackup();
        module.updateContinue();
    }

    public void abortProcess()
    {
        precomputer.abort();
        op.abort();
        ui.backupComplete(false);
        module.updateCompleted();
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

    public void processComplete(boolean normalTermination)
    {
        cleanupHelper.onTermination();

        //copyop is already closed
        ui.backupComplete(normalTermination);
        module.updateCompleted();

        manager.managerDisposed(this);

        running = false;

        if(!normalTermination && hardCleanup)
            cleanupHelper.cleanUp();
    }

    public CopyLog getLog() {
        return log;
    }
}
