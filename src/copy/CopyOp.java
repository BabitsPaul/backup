package copy;

import util.io.AbstractIOObject;
import util.io.FileObject;
import util.io.IOObjectIterator;

import javax.swing.*;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Function;

//TODO implement task-interface for management of long running tasks
public class CopyOp
{
    private static final int BUFFER_SIZE = 4096;
    private static final int FLUSHING_STEP = 5000;

    private Function<AbstractIOObject, AbstractIOObject> PEER_TRANSFORM;//TODO replace by more generalized version

    private CopyState state;

    private CopyManager manager;

    private CopyLog log;

    private Thread t;

    private final byte[] buffer = new byte[BUFFER_SIZE];

    //abortion
    private volatile boolean keepRunning = true;

    //pausing
    private volatile boolean paused = false;

    private final Object pausingLock = new Object();

    public CopyOp(CopyManager manager, CopyState state, CopyLog log)
    {
        this.manager = manager;
        this.state = state;
        this.log = log;

        PEER_TRANSFORM = abstractIOObject -> new FileObject(
                state.getFileOut() + "/" + abstractIOObject.getName().substring(state.getFileIn().length()),
                abstractIOObject.isLeave());
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

        //wake up thread in case it's paused
        continueProcess();
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

    @SuppressWarnings("ConstantConditions")
    private void copy()
    {
        //TODO input-file somehow gets transformed into incorrect sequence?
        try {
            //prepare output directory
            Files.createDirectories(FileSystems.getDefault().getPath(state.getFileOut()));
        }catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, "Failed to create directory " + state.getFileOut(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);

            manager.backupComplete();
            t = null;
            return;
        }

        try {
            IOObjectIterator iter = new IOObjectIterator(new FileObject(state.getFileIn()));
            while (iter.hasNext() && keepRunning) {
                AbstractIOObject in = iter.next(),
                        peer = PEER_TRANSFORM.apply(in);

                try {
                    if (in.isLeave()) {
                        if (peer.lastAltered().after(in.lastAltered()))
                            log.reportFileUptoDate(in.getName());
                        else {
                            state.setCurrentSource(in.getName());
                            copyFile(in.getInputStream(), PEER_TRANSFORM.apply(in).getOutputStream());
                        }
                    } else {
                        AbstractIOObject obj = PEER_TRANSFORM.apply(in);

                        if (!obj.exists())
                            obj.create();
                    }
                } catch (IOException e) {
                    log.reportCopyError(e.getMessage(), in.getName());
                }
            }
        }catch (Exception e)
        {
            log.reportUnknownException(e);
        }

        //notify the manager about completion of the code
        manager.backupComplete();

        //just a bit of cleanup
        t = null;
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

    private void copyFile(InputStream in, OutputStream out)
        throws IOException
    {
        int index, count = 0;
        while((index = in.read(buffer)) > 0 && keepRunning)
        {
            out.write(buffer, 0, index);

            //only flush every FLUSHING_STEP loop (probably outer loop???)
            if((++count % FLUSHING_STEP) == 0)
            {
                out.flush();
                checkPausedState();
            }

            state.currentFileProgress(index);
        }

        //final flush to make sure the entire io was flushed
        out.flush();
    }
}
