package copy.task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Task
{
    private Map<TaskEventID, List<TaskListener>> listeners = new HashMap<>(TaskEventID.values().length);

    public void addTaskListener(TaskListener listener)
    {
        for(TaskEventID id : TaskEventID.values())
            addTaskListener(listener, id);
    }

    public void addTaskListener(TaskListener listener, TaskEventID id)
    {
        List<TaskListener> l;

        if(listeners.containsKey(id))
            l = listeners.get(id);
        else
        {
            l = new LinkedList<>();
            listeners.put(id, l);
        }

        l.add(listener);
    }

    public void removeTaskListener(TaskListener listener)
    {
        listeners.values().forEach(l->l.remove(listener));
    }

    public void removeTaskListener(TaskListener listener, TaskEventID id)
    {
        if(listeners.containsKey(id))
            listeners.get(id).remove(listener);
    }

    protected void fireEvent(TaskEventID id)
    {
        TaskEventQueue.dispatchEvent(new TaskEvent(this, id));
    }

    void eventDispatched(TaskEvent e)
    {
        listeners.get(e.getId()).forEach(l->l.taskUpdate(e));
    }
}