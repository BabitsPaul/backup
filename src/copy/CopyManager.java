package copy;

import mgr.Manager;
import systray.TrayModule;
import ui.CopyUI;

public class CopyManager
{
    private String in, out;

    private Manager manager;

    private TrayModule module;

    private Precomputer precomputer;

    private CopyState state;

    private CopyUI ui;

    private CopyOp op;

    private CopyLog log;

    private boolean running = true;

    public CopyManager(Manager manager, String in, String out)
    {
        //TODO strip eventual trailing slashes

        this.in = in;
        this.out = out;
        this.manager = manager;

        log = new CopyLog();
        module = new TrayModule(this, in, out);
        state = new CopyState(in, out);
        precomputer = new Precomputer(state);
        ui = new CopyUI(this, state);
        op = new CopyOp(this, state, log);

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
        //copyop is already closed
        ui.backupComplete(normalTermination);
        module.updateCompleted();

        manager.managerDisposed(this);

        running = false;
    }
}
