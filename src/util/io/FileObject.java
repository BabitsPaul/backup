package util.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FileObject
    extends AbstractIOObject
{
    //TODO transform to java.nio

    private final Path p;

    private boolean isLeave;

    public FileObject(String name)
    {
        p = createPath(name);
        isLeave = !Files.isDirectory(p);
    }

    public FileObject(String name, boolean isDirectory)
    {
        p = createPath(name);
        isLeave = !isDirectory;
    }

    private static Path createPath(String name)
    {
        try {
            Path tmp = Paths.get(name);

            if(Files.exists(tmp))
                return tmp.toRealPath();
            else
                return tmp;
        }catch (IOException e)
        {
            throw new IllegalArgumentException("Invalid Path", e);
        }
    }

    @Override
    public String getName()
    {
        return p.toString();
    }

    @Override
    public AbstractIOObject[] listChildren()
    {
        try {
            return StreamSupport.stream(Files.newDirectoryStream(p).spliterator(), false).
                        map(Path::toString).map(FileObject::new).toArray(AbstractIOObject[]::new);
        }catch (IOException e)
        {
            throw new IllegalStateException("Can't list children");
        }
    }

    @Override
    public InputStream getInputStream()
            throws IOException
    {
        return Files.newInputStream(p);
    }

    @Override
    public OutputStream getOutputStream()
            throws IOException
    {
        return Files.newOutputStream(p);
    }

    @Override
    public boolean isLeave()
    {
        return isLeave;
    }

    @Override
    public long size()
    {
        //TODO error-handling
        return Files.size(p);
    }

    @Override
    public Date lastAltered() {
        return Files.getLastModifiedTime(p);
    }

    @Override
    public boolean exists() {
        return Files.exists(p);
    }

    @Override
    public boolean delete() throws IOException {
        Files.delete(p);

        return true;
    }

    @Override
    public void create() throws IOException {
        if(isLeave())
        {
            if(!p.mkdir())
                throw new IOException("Failed to create folder " + getName());
        }else
        {
            if(!p.createNewFile())
                throw new IOException("Failed to create file " + getName());
        }
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
