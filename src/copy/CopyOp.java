package copy;

import javax.swing.*;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class CopyOp
{
    //TODO ignore files that are already in folder in uptodate version

    //TODO process seems to stop writing from time to time

    private static final int FLUSH_STEPS = (1 << 10);
    private static final int BUFFER_SIZE = 4096;

    private byte[] buffer = new byte[BUFFER_SIZE];

    private String in, out;

    //state of the copying process
    private CopyState state;

    //termination flag
    private boolean keepRunning = true;

    //watchers of this proceess
    private CopyProcessWatcher[] watchers;

    //pausing
    private final Object pausingLock = new Object();
    private boolean paused = false;

    public CopyOp(CopyProcessWatcher[] watchers, CopyState state)
    {
        this.watchers = watchers;
        this.state = state;
    }

    public void start(List<Predicate<File>> filter)
    {
        Thread t = new Thread(()-> copy(filter));
        t.setName("Copy thread");
        t.start();
    }

    public void copy(List<Predicate<File>> filter)
    {
        for(CopyProcessWatcher w : watchers)
            w.copyingStarted();

        this.in = state.getInputFile();
        this.out = state.getOutputFile();

        try {
            //must already exist
            File inf = new File(in),
                    outf = new File(out);

            //crete directories for output
            if (!outf.exists()) {
                Files.createDirectories(FileSystems.getDefault().getPath(out));
            }

            List<File> files = new LinkedList<>();
            files.add(inf);

            while(!files.isEmpty() && keepRunning)
            {
                checkLock();

                File f = files.remove(0);

                //ignore FileProgress lacking permission
                if(!f.canRead())
                {
                    //TODO notify user about FileProgress that failed to be backuped
                    continue;
                }

                File peer = new File(out + "/" + f.getAbsolutePath().substring(in.length()));

                if(f.isDirectory())
                {
                    //register children for processing
                    for(File c : f.listFiles())
                        files.add(c);

                    if(peer.exists())
                        continue;

                    if(!peer.mkdir())
                    {
                        JOptionPane.showMessageDialog(null, "Error creating directory " + peer.getAbsolutePath() ,
                                "Error", JOptionPane.ERROR_MESSAGE);
                        cleanUp();
                        return;
                    }
                }else{
                    //ignore files that don't need updating
                    if(peer.exists() && peer.lastModified() > f.lastModified())
                        continue;

                    copyFile(f, peer);
                }
            }
        }catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, "Unknown error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        copyingFinished();
    }

    private void copyingFinished()
    {
        state.setCopyingComplete(true);
        for(CopyProcessWatcher w : watchers)
            w.copyingTerminated();
    }

    private void copyFile(File in, File out)
    {
        //update current file and file-length
        state.setCurrentFile(in.getAbsolutePath());
        state.setFileBytes(in.length());

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(in));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(out)))
        {
            int index;
            int ct = 0;
            while((index = bis.read(buffer)) > 0 && keepRunning)
            {
                bos.write(buffer, 0, index);

                //update state for copied bytes
                state.setFileBytesCopied(state.getFileBytesCopied() + index);
                state.setTotalBytesCopied(state.getTotalBytesCopied() + index);

                ct++;
                ct %= FLUSH_STEPS;
                if(ct == 0)
                    bos.flush();

                //process currently read block before pausing
                checkLock();
            }

            bis.close();
            bos.flush();
            bos.close();
        }catch (IOException e)
        {
            //TODO report error and perform eventual cleanup for file
        }

        //update counter of copied files
        state.setFilesCopied(state.getFilesCopied() + 1);
        //reset file counter of copied bytes
        state.setFileBytesCopied(0);
    }

    /**
     * cleans up after failed copying
     */
    private void cleanUp()
    {
        File of = new File(out);

        if(of.exists())
        {
            if(!of.delete())
            {
                //TODO report error while removing file to user
            }
        }
    }

    private void checkLock()
    {
        if(paused)
        {
            synchronized (pausingLock)
            {
                try{
                    pausingLock.wait();
                }catch (InterruptedException ignored) {}
            }
        }
    }

    public void pauseCopying()
    {
        paused = true;

        for(CopyProcessWatcher w : watchers)
            w.copyingPaused();
    }

    public void continueCopying()
    {
        //reverse lock and notify waiting method
        paused = false;
        synchronized (pausingLock)
        {
            pausingLock.notify();
        }

        for(CopyProcessWatcher w : watchers)
            w.copyingContinued();
    }

    public void stop()
    {
        keepRunning = false;

        cleanUp();

        for(CopyProcessWatcher w : watchers)
            w.copyingTerminated();
    }

    public boolean isPaused()
    {
        return paused;
    }
}