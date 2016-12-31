package copy;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class DiskIOProfiler
{
    //TODO

    //only needs to be traversed inorder
    private List<Long> interpolatedSpeed = new LinkedList<>();

    private CopyState state;

    public DiskIOProfiler(CopyState state)
    {
        this.state = state;
    }

    public void start()
    {

    }

    public void terminate()
    {

    }

    public JComponent getComponent()
    {
        return new JLabel("Under construction");
    }
}
