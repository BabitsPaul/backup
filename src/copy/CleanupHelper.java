package copy;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CleanupHelper
{
    private File[] toDelete;

    private String in, out;

    private CopyLog copyLog;

    public CleanupHelper(String in, String out, CopyLog copyLog)
    {
        this.in = in;
        this.out = out;
        this.copyLog = copyLog;
    }

    public void onStart()
    {
        //check for the lowest folder in the output that is created on the backup process
        File out = new File(this.out);
        File toDelete = null;

        while(out != null && !out.exists())
        {
            toDelete = out;
            out.getParent();
        }

        if(toDelete != null)
        {
            this.toDelete = new File[]{toDelete};
        }
    }

    public void onTermination()
    {
        //just delete the base-directory
        if(toDelete != null)
            return;

        //list all base-folders that have been created by the process
        try {
            Iterator<Path> iter = Files.newDirectoryStream(FileSystems.getDefault().getPath(this.in)).iterator();
            List<String> files = new LinkedList<>();
            while(iter.hasNext())
                files.add(iter.next().toFile().getAbsolutePath().substring(in.length()));

            iter = Files.newDirectoryStream(FileSystems.getDefault().getPath(out)).iterator();
            while(iter.hasNext())
                files.remove(iter.next().toFile().getAbsolutePath().substring(out.length()));

            toDelete = files.stream().map(s->out + s).map(File::new).toArray(File[]::new);
        }catch (IOException e)
        {
            copyLog.reportUnknownException(e);
        }
    }

    public void cleanUp()
    {
        if(toDelete == null)
        {
            //TODO report error
            return;
        }

        for(File f : toDelete)
            if(!f.delete())
                copyLog.reportCopyError("Failed to delete", f.getAbsolutePath());
    }
}
