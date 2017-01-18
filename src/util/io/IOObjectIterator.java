package util.io;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;

public class IOObjectIterator
    implements Iterator<AbstractIOObject>
{
    public static final IOObjectIterator EMPTY = new IOObjectIterator(null){
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public AbstractIOObject next() {
            throw new NoSuchElementException();
        }
    };

    private AbstractIOObject base;

    private boolean skipNonLeaves;

    private Queue<AbstractIOObject> queue;

    public IOObjectIterator(AbstractIOObject base)
    {
        this(base, false);
    }

    public IOObjectIterator(AbstractIOObject base, boolean skipNonLeaves)
    {
        this.base = base;
        this.skipNonLeaves = skipNonLeaves;

        this.queue = new PriorityQueue<>();
        queue.offer(base);
    }

    @Override
    public boolean hasNext()
    {
        return queue.isEmpty();
    }

    @Override
    public AbstractIOObject next()
    {
        //skip over all values that aren't leaves
        if(skipNonLeaves)
        {
            while(!queue.isEmpty())
                if(queue.peek().isLeave())
                    break;
                else
                    for(AbstractIOObject child : queue.poll().listChildren())
                        queue.offer(child);

        }

        if(queue.isEmpty())
            throw new NoSuchElementException();

        AbstractIOObject tmp = queue.poll();

        for(AbstractIOObject child : tmp.listChildren())
            queue.offer(child);

        return null;
    }
}
