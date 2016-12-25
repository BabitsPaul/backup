package copy;

import javax.swing.*;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.PriorityQueue;
import java.util.Queue;

public class CopyOp
{
    private static final int BUFFER_SIZE = 4096;
    private static final int FLUSHING_STEP = 5000;

    private CopyState state;

    private CopyManager manager;

    private Thread t;

    private final byte[] buffer = new byte[BUFFER_SIZE];

    //abortion
    private volatile boolean keepRunning = true;

    //pausing
    private volatile boolean paused = false;

    private final Object pausingLock = new Object();

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
        keepRunning = false;
    }

    public void pauseProcess()
    {
        paused = true;
    }

    public void continueProcess()
    {
        paused = false;

        synchronized (pausingLock)
        {
            pausingLock.notify();
        }
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

        while(!fileQueue.isEmpty() && keepRunning)
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
        manager.processComplete(keepRunning);

        //just a bit of cleanup
        if(keepRunning)
            t = null;
        else
            cleanupOnAbort();   //TODO probably alternative of keeping created files nevertheless the process was aborted
    }

    private void cleanupOnAbort()
    {
        t = null;

        //TODO alternate cleanup method (e.g. remove all copied files, if existent)
        File out = new File(state.getFileOut());
        if(out.exists() && !out.delete())
                JOptionPane.showMessageDialog(null, "Failed to delete output directory",
                                        "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    private void checkPausedState()
    {
        if(paused)
        {
            try{
                synchronized (pausingLock)
                {
                    pausingLock.wait();
                }
            }catch (InterruptedException ignored){}
        }
    }

    private void copyFile(File in, File out)
    {
        state.setCurrentFile(in);

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(in));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(out)))
        {
            int index, count = 0;
            while((index = bis.read(buffer)) > 0 && keepRunning)
            {
                bos.write(buffer, 0, index);

                //only flush every FLUSHING_STEP loop
                if((++count % FLUSHING_STEP) == 0)
                {
                    bos.flush();
                    checkPausedState();
                }

                state.currentFileProgress(index);
            }

            //final flush to make sure the entire file was flushed
            bos.flush();
        }catch (IOException e)
        {
            //TODO report error
        }
    }
}
