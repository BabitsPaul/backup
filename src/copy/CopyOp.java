package copy;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class CopyOp
{
    private CopyState state;

    private CopyManager manager;

    private Thread t;

    public CopyOp(CopyManager manager, CopyState state)
    {
        this.manager = manager;
        this.state = state;
    }

    public void start()
    {
        if(t != null)
            throw new IllegalStateException("Backup is already running");

        t = new Thread(this::copy);
        t.setName("Backup");
        t.setPriority(Thread.MAX_PRIORITY); //run this with maximum-priority
        t.start();
    }

    public void abort()
    {

    }

    public void pauseProcess()
    {

    }

    public void continueProcess()
    {

    }

    private void copy()
    {
        try {
            //prepare output directory
            Files.createDirectories(FileSystems.getDefault().getPath(state.getFileOut()));
        }catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, "Failed to create directory " + state.getFileOut(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);

            manager.processComplete(false);
            cleanupOnAbort();
            return;
        }

        Queue<File> fileQueue = new PriorityQueue<>();
        fileQueue.offer(new File(state.getFileIn()));

        String in = state.getFileIn(),
                out = state.getFileOut();

        while(!fileQueue.isEmpty())
        {
            File f = fileQueue.poll();

            File peer = new File(out + "/" + f.getAbsolutePath().substring(in.length()));

            if(f.isDirectory())
            {
                if(!peer.exists())
                    if(!peer.mkdir())
                    {
                        //TODO report error while creating file
                        continue;
                    }

                for(File child : f.listFiles())
                    fileQueue.offer(child);
            }
            else
            {
                if(peer.lastModified() > f.lastModified())
                    continue;   //ignore files that are older than their backup version
                else
                    copyFile(f, peer);
            }
        }

        //notify the manager about completion of the code
        manager.processComplete(true);

        //just a bit of cleanup
        t = null;
    }

    private void cleanupOnAbort()
    {
        t = null;

        File out = new File(state.getFileOut());
        if(out.exists() && !out.delete())
                JOptionPane.showMessageDialog(null, "Failed to delete output directory",
                                        "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    private void copyFile(File in, File out)
    {

    }
}
