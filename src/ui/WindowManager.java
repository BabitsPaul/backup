package ui;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class WindowManager
{
    private Runnable onAllWindowsClosed;

    private int count = 0;

    public WindowManager(Runnable onAllWindowsClosed) {
        this.onAllWindowsClosed = onAllWindowsClosed;
    }

    public JFrame requestFrame(String title)
    {
        JFrame result = new JFrame(title){
            @Override
            public void setVisible(boolean visible)
            {
                super.setVisible(visible);

                if(visible)
                    count++;
                else
                    count--;

                if(count == 0)
                    new SwingWorker<Void, Void>(){
                        @Override
                        protected Void doInBackground() throws Exception {
                            onAllWindowsClosed.run();

                            return null;
                        }
                    }.execute();
            }
        };

        return result;
    }
}
