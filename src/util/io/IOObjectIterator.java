package util.io;

import java.util.*;

public class IOObjectIterator<T extends AbstractIOObject>
    implements Iterator<T>
{
    //TODO allow specialization
    public static final IOObjectIterator EMPTY = new IOObjectIterator(AbstractIOObject.NONE){
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public AbstractIOObject next() {
            throw new NoSuchElementException();
        }
    };

    private T base;

    private boolean skipNonLeaves;

    private Queue<T> queue;

    public IOObjectIterator(T base)
    {
        this(base, false);
    }

    public IOObjectIterator(T base, boolean skipNonLeaves)
    {
        this.base = base;
        this.skipNonLeaves = skipNonLeaves;

        this.queue = new LinkedList<>();
        queue.offer(base);
    }

    @Override
    public boolean hasNext()
    {
        return !queue.isEmpty();
    }

    @Override
    public T next()
    {
        //skip over all values that aren't leaves
        if(skipNonLeaves)
        {
            while(!queue.isEmpty())
                if(queue.peek().isLeave())
                    break;
                else
                    for(AbstractIOObject child : queue.poll().listChildren())
                        queue.offer((T) child); //TODO more elegant approach

        }

        if(queue.isEmpty())
            throw new NoSuchElementException();

        T tmp = queue.poll();

        for(AbstractIOObject child : tmp.listChildren())
            queue.offer((T) child);

        return tmp;
    }

    @Override
    public String toString()
    {
        return "IOIterator base=" + base + " current=" + queue.peek();
    }
}
