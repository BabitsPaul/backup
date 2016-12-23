package mgr;

import copy.CopyManager;
import systray.SysTray;
import ui.SelectorUI;

import javax.swing.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Manager
{
    private SysTray tray;

    private SelectorUI selectorUI;

    private Set<CopyManager> managers;

    public Manager()
    {

    }

    public void setup() {
        managers = new HashSet<>();

        tray = new SysTray(this);
        if (!tray.create()) {
            JOptionPane.showMessageDialog(null, "Failed to create SystemTray entry",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            shutdown();
            return;
        }

        selectorUI = new SelectorUI(this);
        selectorUI.create();
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
    }

    public void newCopy(String in, String out)
    {
        selectorUI.hide();

        CopyManager manager = new CopyManager(this, in, out);
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
