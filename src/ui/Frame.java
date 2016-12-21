package ui;

import main.Manager;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Frame
{
    private JFrame frame;

    private Manager mgr;

    public Frame(Manager mgr)
    {
        this.mgr = mgr;
    }

    //TODO unclear definition show vs switchUI
    public void show(UI ui)
    {
        if(frame != null)
            return;

        SwingUtilities.invokeLater(()->{
            frame = new JFrame("main.Backup");

            frame.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {}

                @Override
                public void windowClosing(WindowEvent e) {
                    mgr.closeFrame();
                }

                @Override
                public void windowClosed(WindowEvent e) {}
                @Override
                public void windowIconified(WindowEvent e) {}
                @Override
                public void windowDeiconified(WindowEvent e) {}
                @Override
                public void windowActivated(WindowEvent e) {}
                @Override
                public void windowDeactivated(WindowEvent e) {}
            });

            frame.setContentPane(ui.getPanel());

            frame.setSize(400, 400);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    public void hide()
    {
        if(frame == null)
            return;

        SwingUtilities.invokeLater(() ->{
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        });
    }

    public boolean isVisible() {
        return frame != null;
    }

    public void switchUI(UI ui)
    {
        if(frame == null)
            throw new NullPointerException();

        SwingUtilities.invokeLater(()->
        {
            frame.setContentPane(ui.getPanel());

            frame.revalidate();
            frame.repaint();
        });
    }

    public JFrame getFrame()
    {
        return frame;
    }
}