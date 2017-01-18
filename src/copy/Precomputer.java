package copy;

import util.io.FileObject;
import util.io.IOObjectIterator;

import java.io.File;
import java.util.PriorityQueue;
import java.util.Queue;

public class Precomputer
{
    private CopyState state;

    private boolean keepRunning = true;

    private CopyManager manager;

    public Precomputer(CopyState state, CopyManager manager)
    {
        this.state  = state;
        this.manager = manager;
    }

    public void start()
    {
        Thread t = new Thread(this::precompute);
        t.setName("Precompute");
        t.start();
    }

    private void precompute()
    {
        int totalFiles = 0;
        long totalBytes = 0L;

        int prefixLen = state.getFileIn().length();
        String outPrefix = state.getFileOut() + "/";

        Queue<File> queue = new PriorityQueue<>();
        queue.offer(new File(state.getFileIn()));

        while(!queue.isEmpty() && keepRunning)
        {
            File f = queue.poll();

            File peer = new File(outPrefix + f.getAbsolutePath().substring(prefixLen));

            if(f.isDirectory())
                for(File child : f.listFiles())
                    queue.offer(child);

            else if(!peer.exists() || f.lastModified() > peer.lastModified())
            {
                totalBytes += f.length();
                ++totalFiles;
            }
        }

        state.precomputationComplete(totalFiles, totalBytes);

        manager.precomputationComplete(keepRunning);
    }

    private void precompute2()
    {
        int totalFiles = 0;
        long totalBytes = 0L;

        IOObjectIterator iter = new IOObjectIterator(new FileObject(state.getFileIn()), true);
        while(iter.hasNext() && keepRunning)
        {
            totalFiles++;
            totalBytes += iter.next().size();
        }

        state.precomputationComplete(totalFiles, totalBytes);
        manager.precomputationComplete(keepRunning);
    }

    public void abort()
    {
        keepRunning = false;
    }
}
