package ui.copy;

import copy.CopyLog;
import copy.CopyState;
import ui.WindowManager;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Map;

public class LogUI
{
    private WindowManager windowManager;

    private boolean creationRunning = false,
                        created = false;

    private CopyLog log;

    private CopyState state;

    private JFrame frame;

    public LogUI(CopyState state, CopyLog log, WindowManager windowManager)
    {
        this.state = state;
        this.log = log;
        this.windowManager = windowManager;
    }

    private void createUI()
    {
        if(created || creationRunning)
            return;

        Thread t = new Thread(()->{
            creationRunning = true;

            String lf = System.getProperty("line.separator");

            StringBuilder total = new StringBuilder();
            total.append(" Input: ").append(state.getFileIn()).append(lf);
            total.append(" Output: ").append(state.getFileOut()).append(lf);
            total.append(" Number of files copied: ").append(state.getTotalFileProgress()).append(lf);
            total.append(" Number of bytes copied: ").append(state.getTotalBytesProgress()).append(lf);
            total.append(" Already up to date: ").append(log.getUptoDate().size()).append(lf);
            total.append(" Backup failures: ").append(log.getErrorMap().values().stream().mapToInt(LinkedList::size).sum());

            StringBuilder uptoDate = new StringBuilder();
            for(String file : log.getUptoDate())
                uptoDate.append(file).append(lf);
            //uptoDate.setLength(uptoDate.length() - lf.length());//truncate last linefeed

            StringBuilder errors = new StringBuilder();
            Map<String, LinkedList<String>> errorMap = log.getErrorMap();
            for(String error : errorMap.keySet())
            {
                errors.append("\t").append(error).append(lf);

                for(String file : errorMap.get(error))
                    errors.append(file).append(lf);
            }

            StringBuilder exceptions = new StringBuilder();
            for(String st : log.getStackTraces())
                exceptions.append(st).append("-----------------------------------------------------------------------").
                        append(lf).append(lf);
            if(exceptions.length() > 75)
                exceptions.setLength(exceptions.length() - 71 - 2 * lf.length());

            SwingUtilities.invokeLater(()->{
                JTabbedPane tabbedPane = new JTabbedPane();

                JTextArea totalArea = new JTextArea(total.toString());
                totalArea.setEditable(false);
                tabbedPane.add("Total", new JScrollPane(totalArea));

                JTextArea uptoDateArea = new JTextArea(uptoDate.toString());
                uptoDateArea.setEditable(false);
                tabbedPane.add("Up to date", new JScrollPane(uptoDateArea));

                JTextArea errorArea = new JTextArea(errors.toString());
                errorArea.setEditable(false);
                tabbedPane.add("Errors", new JScrollPane(errorArea));

                JTextArea exceptionArea = new JTextArea(exceptions.toString());
                exceptionArea.setEditable(false);
                tabbedPane.add("Internal errors", new JScrollPane(exceptionArea));

                frame = windowManager.requestFrame("Log");
                frame.setContentPane(tabbedPane);
                frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                frame.setSize(400, 500);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                created = true;
                creationRunning = false;
            });
        });
        t.setName("LogUI Builder");
        t.start();
    }

    public void showUI()
    {
        //create only one screen and asure a frame is created before showing it
        if(!created)
            createUI();
        else if(!creationRunning)
            frame.setVisible(true);
    }

    public void dispose()
    {
        if(frame == null)
            return;

        frame.setVisible(false);
        frame.dispose();
    }
}
