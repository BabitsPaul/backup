package mgr;

import copy.CopyManager;
import copy.task.TaskEventQueue;
import systray.SysTray;
import ui.SelectorUI;
import ui.WindowManager;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

public class Manager
{
    private SysTray tray;

    private SelectorUI selectorUI;

    private Set<CopyManager> managers;

    private WindowManager windowManager;

    public Manager()
    {

    }

    public void setup() {
        TaskEventQueue.start();

        windowManager = new WindowManager(this::allWindowsClosed);

        managers = new HashSet<>();

        tray = new SysTray(this);
        if (!tray.create()) {
            JOptionPane.showMessageDialog(null, "Failed to create SystemTray entry",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            shutdown();
            return;
        }

        selectorUI = new SelectorUI(this, windowManager);
        selectorUI.create();
    }

    public void allWindowsClosed()
    {
        if(!managers.isEmpty())
            return;

        shutdown();
    }

    public void shutdown()
    {
        if(managers.size() != 0)
        {
            if(JOptionPane.showOptionDialog(null, "There are running-backup processes", "Warning",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                    new Object[]{"Abort Backup", "Cancel"}, "Cancel") == 1)
                return;
        }

        //release all running backup processes, uis and the system tray
        managers.forEach(CopyManager::dispose);
        tray.dispose();
        selectorUI.dispose();

        TaskEventQueue.stop();
    }

    public void newCopy(String in, String out)
    {
        selectorUI.hide();

        CopyManager manager = new CopyManager(this, in, out, windowManager);
        managers.add(manager);

        tray.newTrayModule(manager.getTrayModule());
    }

    public void managerDisposed(CopyManager manager)
    {
        managers.remove(manager);
    }

    public void showSelectorUI()
    {
        selectorUI.show();
    }
}
