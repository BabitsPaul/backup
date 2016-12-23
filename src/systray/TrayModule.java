package systray;

import copy.CopyManager;

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
        super(suffix(in, SUFFIX_DISPLAY_LENGTH) + "->" + suffix(out, SUFFIX_DISPLAY_LENGTH));

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

    public void setTray(SysTray tray)
    {
        this.tray = tray;
    }

    public void updatePaused()
    {
        pc.setLabel("Continue");
        paused = true;
    }

    /**
     * called when the copyprocess continues its work
     */
    public void updateContinue()
    {
        pc.setLabel("Pause");
        paused = false;
    }

    public void updateCompleted()
    {
        pc.setLabel("\t");
        pc.setEnabled(false);
    }

    public void dispose()
    {
        tray.removeModule(this);
    }

    private static String suffix(String s, int len)
    {
        if(len > s.length())
            return s;
        else
            return "..." + s.substring(s.length() - len);
    }
}
