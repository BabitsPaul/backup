package copy;

import copy.task.Task;
import copy.task.TaskEventID;

import java.io.File;
import java.util.PriorityQueue;
import java.util.Queue;

public class Precomputer
    extends Task
{
    private CopyState state;

    private boolean keepRunning = true;

    public Precomputer(CopyState state)
    {
        this.state  = state;
    }

    public void start()
    {
        Thread t = new Thread(this::precompute);
        t.setName("Precompute");
        t.start();
    }

    private void precompute()
    {
        fireEvent(TaskEventID.TASK_STARTED);

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

        if(keepRunning)
            fireEvent(TaskEventID.TASK_COMPLETED);
        else
            fireEvent(TaskEventID.TASK_ABORTED);
    }

    public void abort()
    {
        keepRunning = false;
    }
}
