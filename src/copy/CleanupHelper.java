package copy;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * in case of an aborted backup, this class
 * can be used to delete files that were created until
 * the process failed/was cancelled
 */
public class CleanupHelper
{
    //TODO use IOObjectDiff
    private List<File> toDelete;

    private String in, out;

    private CopyLog copyLog;

    public CleanupHelper(String in, String out, CopyLog copyLog)
    {
        this.in = in;
        this.out = out;
        this.copyLog = copyLog;
        this.toDelete = new LinkedList<>();
    }

    /**
     * should be called before the manager started
     *
     * this checks whether any of the parent-directories that will hold the content
     * need to be created. If such a directory is created, the top-most directory that
     * gets created will be deleted
     *
     * Otherwise all other folders that were created during the process need to be deleted.
     * In this case all affected directories will be compared to check which directories need
     * to be deleted and which should be kept
     */
    public void onStart()
    {
        //check for the lowest folder in the output that is created on the backup process
        File out = new File(this.out);
        File toDelete = null;

        while(out != null && !out.exists())
        {
            toDelete = out;
            out = out.getParentFile();
        }

        if(toDelete != null)
        {
            this.toDelete = Arrays.asList(toDelete);
            return;
        }

        //create record of files that can be deleted
        listToDeleteRecursive(new File(in), this.out);
    }

    /**
     * Recursively generates a list of files that can be deleted without affecting already
     * present files
     *
     * @param in the io that gets inspected
     * @param out the peer in the output-directory
     */
    private void listToDeleteRecursive(File in, String out)
    {
        if(!new File(out).exists())
        {
            toDelete.add(new File(out));
        }
        else
        {
            Set<String> files = new HashSet<>();

            try{
                Iterator<Path> iter = Files.newDirectoryStream(FileSystems.getDefault().getPath(in.getAbsolutePath())).iterator();
                while(iter.hasNext())
                    files.add(iter.next().toFile().getName());

                iter = Files.newDirectoryStream(FileSystems.getDefault().getPath(out)).iterator();
                while(iter.hasNext()) {
                    File f = iter.next().toFile();

                    //files that aren't present yet need to be deleted on termination
                    if(!files.contains(f.getName()))
                        toDelete.add(f);
                }
            }catch (IOException e)
            {
                copyLog.reportUnknownException(e);
            }

            //scan already present directories for files that need to be deleted
            files.forEach(fileName -> listToDeleteRecursive(new File(in.getAbsolutePath() + "/" + fileName),
                    out + "/" + fileName));
        }
    }

    /**
     * Runs a recursive deletion of all files in the trees based on
     * an elements of {@link CleanupHelper#toDelete}
     */
    public void cleanUp()
    {
        if(toDelete == null)
        {
            JOptionPane.showMessageDialog(null, "Can't cleanup files");
            return;
        }

        //ignore files that haven't been created by the backup process,
        //as toDelete is build prematurely
        for(File f : toDelete)
            if(f.exists())
               cleanUpRecursive(f);
    }

    /**
     * Recursively deletes a directory-structure by deleting
     * it's childnodes and afterwards it's containing directories
     *
     * @param f the io to delete
     * @see CleanupHelper#cleanUp()
     */
    private void cleanUpRecursive(File f)
    {
        if(f.isDirectory())
        {
            for(File child : f.listFiles())
                cleanUpRecursive(child);
        }

        try {
            Files.delete(FileSystems.getDefault().getPath(f.getAbsolutePath()));
        }catch (IOException e)
        {
            copyLog.reportCopyError("Failed to delete", f.getAbsolutePath());
            copyLog.reportUnknownException(e);
        }
    }
}
