package copy;

import java.io.File;
import java.util.LinkedList;

/**
 * calculates the number of FileProgress and bytes that will be copied
 */
public class Predictor
    implements CopyProcessWatcher
{
    private String file;

    private CopyState state;

    private volatile boolean keepRunning = true;

    public Predictor(String file, CopyState state)
    {
        this.file = file;
        this.state = state;
    }

    public void start()
    {
        Thread t = new Thread(()->predict());
        t.setName("copy.Predictor");
        t.setDaemon(true);
        t.start();
    }

    private void predict()
    {
        //TODO check whether a file actually needs to be copied

        int totalFiles = 0;
        long totalBytes = 0l;

        //assume file exists
        LinkedList<File> files = new LinkedList<>();
        files.add(new File(file));

        while(!files.isEmpty() && keepRunning)
        {
            File f = files.removeFirst();

            if(!f.canRead())
                continue;

            if(f.isDirectory())
            {
                for(File c : f.listFiles())
                    files.add(c);
            }else{
                totalFiles++;
                totalBytes += f.length();
            }
        }

        //update state
        state.setFiles(totalFiles);
        state.setTotalBytes(totalBytes);
        state.setPrecomputationComplete(true);
    }

    @Override
    public void copyingTerminated() {
        keepRunning = false;
    }
}