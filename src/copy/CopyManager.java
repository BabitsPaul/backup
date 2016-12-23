package copy;

import mgr.Manager;
import systray.TrayModule;
import ui.CopyUI;

public class CopyManager
{
    private String in, out;

    private Manager manager;

    private TrayModule module;

    private CopyState state;

    private CopyUI ui;

    private CopyOp op;

    public CopyManager(Manager manager, String in, String out)
    {
        //TODO strip eventual trailing slashes
        this.in = in;
        this.out = out;
        this.manager = manager;
        this.module = new TrayModule(this, in, out);

        state = new CopyState(in, out);
        ui = new CopyUI(this, state);
        op = new CopyOp(this, state);

        ui.createUI();
        op.start();
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
        op.abort();
        ui.backupComplete(false);
        module.updateCompleted();
    }

    public void dispose()
    {
        ui.disposeUI();
        module.dispose();

        manager.managerDisposed(this);
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
    }
}
