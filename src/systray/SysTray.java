package systray;

import mgr.Manager;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SysTray
{
    private boolean supported;

    private Menu copyOperations;

    private Manager mgr;

    private TrayIcon trayIcon;

    public SysTray(Manager mgr)
    {
        this.mgr = mgr;
    }

    public boolean create()
    {
        if(!SystemTray.isSupported())
        {
            supported = false;
            return false;
        }

        //create bufferedimage
        BufferedImage icon = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = icon.getGraphics();
        g.setColor(Color.blue);
        g.fillOval(16, 16, 80, 80);
        g.dispose();

        PopupMenu menu = new PopupMenu();

        //submenu for new backup
        MenuItem newBackup = new MenuItem("New Backup");
        newBackup.addActionListener(e->mgr.showSelectorUI());
        menu.add(newBackup);

        //submenu for copyoperations
        copyOperations = new Menu("Copy Operations");
        menu.add(copyOperations);
        copyOperations.setEnabled(false);

        //shutdown menu item
        MenuItem quit = new MenuItem("Quit");
        menu.add(quit);
        quit.addActionListener(e->mgr.shutdown());

        //register trayIcon
        trayIcon = new TrayIcon(icon, "backup", menu);
        try {
            SystemTray.getSystemTray().add(this.trayIcon);
        }catch (AWTException e)
        {
            supported = false;
            trayIcon = null;
            return false;
        }

        supported = true;

        return true;
    }

    public void dispose()
    {
        if(!supported)
            return;

        SystemTray.getSystemTray().remove(trayIcon);
        trayIcon = null;
    }

    public void newTrayModule(TrayModule m)
    {
        m.setTray(this);

        copyOperations.add(m);
        copyOperations.setEnabled(true);
    }

    public void removeModule(TrayModule module)
    {
        copyOperations.remove(module);

        if(copyOperations.getItemCount() == 0)
            copyOperations.setEnabled(false);
    }
}