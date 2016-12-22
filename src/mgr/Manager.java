package mgr;

import copy.CopyManager;
import systray.SysTray;
import systray.TrayModule;
import ui.SelectorUI;

import javax.swing.*;
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
        //release all running backup processes, uis and the system tray
        managers.forEach(CopyManager::dispose);
        tray.dispose();
        selectorUI.dispose();
    }

    public void newCopy(String in, String out)
    {
        TrayModule module = tray.newModule(in, out);
        CopyManager manager = new CopyManager();

        module.setManager(manager);
    }

    public void disposeCopy(CopyManager manager)
    {
        managers.remove(manager);
    }

    public void showSelectorUI()
    {
        selectorUI.show();
    }
}
