package copy;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

public class CopyLog
{
    private HashMap<String, LinkedList<String>> errorMap;

    private LinkedList<String> uptoDate;

    private LinkedList<String> exceptions;

    public CopyLog()
    {
        errorMap = new HashMap<>();
        uptoDate = new LinkedList<>();
        exceptions = new LinkedList<>();
    }

    public void reportCopyError(String msg, String file)
    {
        LinkedList<String> list;

        if(!errorMap.containsKey(msg))
        {
            list = new LinkedList<>();
            errorMap.put(msg, list);
        }else{
            list = errorMap.get(msg);
        }

        list.add(file);
    }

    public void reportFileUptoDate(File f) {
        uptoDate.add(f.getAbsolutePath());
    }

    public HashMap<String, LinkedList<String>> getErrorMap() {
        return errorMap;
    }

    public LinkedList<String> getUptoDate() {
        return uptoDate;
    }

    public void reportUnknownException(Exception e)
    {
        String lf = System.getProperty("line.separator");

        Throwable ex = e;
        StringBuilder b = new StringBuilder();
        while(ex != null)
        {
            b.append(ex.getMessage()).append(lf);

            for(StackTraceElement ste : ex.getStackTrace())
                b.append(ste.getFileName()).append(":").append(ste.getLineNumber()).
                        append("/").append(ste.getClassName()).append("#").append(ste.getMethodName()).
                        append(lf);

            ex = ex.getCause();

            if(ex != null)
                b.append(lf).append("Caused by:").append(lf);
        }

        exceptions.add(b.toString());
    }

    public LinkedList<String> getStackTraces() {
        return exceptions;
    }
}
