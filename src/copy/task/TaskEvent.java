package copy.task;

public class TaskEvent
{
    private Task source;

    private TaskEventID id;

    public TaskEvent(Task source, TaskEventID id)
    {
        this.source = source;
        this.id = id;
    }

    public Task getSource() {
        return source;
    }

    public TaskEventID getId() {
        return id;
    }
}
