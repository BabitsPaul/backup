package copy.task;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskEventQueue
{
    private static Thread t;

    private static boolean running = false;

    private static Queue<TaskEvent> queue;

    private static final Object evtLock = new Object();

    public synchronized static void start()
    {
        if(running)
            throw new IllegalStateException("Queue is already running");

        running = true;
        queue = new ConcurrentLinkedQueue<>();
        t = new Thread(()->{
            while(running)
            {
                //wait for an object, if the queue is empty
                if(queue.isEmpty()){
                    try{
                        synchronized (evtLock)
                        {
                            evtLock.wait();
                        }
                    }catch (InterruptedException ignored) {
                        //thread will get interrupted on terminaten of the queue
                        return;
                    }
                }

                TaskEvent e = queue.poll();

                e.getSource().eventDispatched(e);
            }
        });
        t.setName("TaskEventQueue");
        t.setDaemon(true);
        t.start();
    }

    public synchronized static void stop()
    {
        if(!running)
            throw new IllegalStateException("Queue isn't running");

        running = false;

        t.interrupt();
    }

    public static void dispatchEvent(TaskEvent e)
    {
        if(!running)
            throw new IllegalStateException("Queue isn't running");

        queue.offer(e);
        synchronized (evtLock)
        {
            evtLock.notify();
        }
    }
}
