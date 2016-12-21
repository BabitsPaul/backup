package ui;

import copy.CopyProcessWatcher;
import main.Manager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SysTray
    implements CopyProcessWatcher
{
    private boolean systemTraySupported;

    private TrayIcon trayEntry;

    private Manager mgr;

    private MenuItem pc;

    public SysTray(Manager mgr)
    {
        this.mgr = mgr;
    }

    public void create()
    {
        SwingUtilities.invokeLater(this::createTrayEntry);
    }

    private void createTrayEntry()
    {
        final int width = 128,
                height = 128;

        if(!SystemTray.isSupported())
        {
            systemTraySupported = false;
            return;
        }

        //TODO make icon transparent and replace with proper icon
        //create icon
        Image icon = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) icon.getGraphics();
        g.setColor(new Color(50, 50, 50, 0));
        g.fillRect(0, 0, width, height);
        g.setColor(Color.blue);
        g.fillOval(width / 8, width / 8, width * 6 / 8, height * 6 / 8);
        g.dispose();

        //create menu
        PopupMenu menu = new PopupMenu();

        pc = new MenuItem("Pause/Continue");
        menu.add(pc);
        pc.addActionListener(e -> mgr.triggerCopying());
        pc.setEnabled(false);

        MenuItem ui = new MenuItem("Show/Dispose ui.UI");
        menu.add(ui);
        ui.addActionListener(e -> {
            if(mgr.frameVisible())
                mgr.hideFrame();
            else
                mgr.showFrame();
        });

        MenuItem exit = new MenuItem("Quit");
        menu.add(exit);
        exit.addActionListener(e -> mgr.attemptExit());

        trayEntry = new TrayIcon(icon, "backup", menu);
        trayEntry.setImageAutoSize(true);
        trayEntry.addActionListener(e -> create());

        try {
            SystemTray.getSystemTray().add(trayEntry);
        }catch(AWTException e)
        {
            JOptionPane.showMessageDialog(null, "Failed to register component on systemtray", "Error", JOptionPane.ERROR_MESSAGE);

            systemTraySupported = false;
            trayEntry = null;
            return;
        }

        systemTraySupported = true;
    }

    public void dispose()
    {
        if(!systemTraySupported || trayEntry == null)
            return;

        SystemTray.getSystemTray().remove(trayEntry);
        trayEntry = null;
    }

    @Override
    public void copyingPaused() {
        SwingUtilities.invokeLater(()->{
            pc.setLabel("Continue");
        });
    }

    @Override
    public void copyingContinued() {
        SwingUtilities.invokeLater(()->{
            pc.setLabel("Pause");
        });
    }

    @Override
    public void copyingTerminated() {
        SwingUtilities.invokeLater(()->{
            pc.setEnabled(false);
            pc.setLabel("Pause/Continue");
        });
    }

    @Override
    public void copyingStarted()
    {
        SwingUtilities.invokeLater(()->{
            pc.setEnabled(true);
            pc.setLabel("Pause/Continue");
        });
    }
}
