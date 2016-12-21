package main;

import copy.CopyOp;
import copy.CopyProcessWatcher;
import copy.CopyState;
import copy.Predictor;
import ui.*;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.function.Predicate;

public class Manager
{
    //TODO use multiple frames

    private SysTray tray;

    private Frame frame;

    private SelectorUI selectorUI;

    private CopyOp op;

    private UI active;

    public Manager()
    {

    }

    public void setup()
    {
        tray = new SysTray(this);
        tray.create();

        active = selectorUI = new SelectorUI(this);

        frame = new Frame(this);
        frame.show(selectorUI);
    }

    public void attemptExit()
    {
        frame.hide();
        tray.dispose();
    }

    ////////////////////////////////////////////////////////////////////
    // frame control                                                  //
    ////////////////////////////////////////////////////////////////////

    public boolean frameVisible()
    {
        return frame.isVisible();
    }

    public void showFrame()
    {
        frame.show(active);
    }

    public void hideFrame()
    {
        frame.hide();
    }

    public void closeFrame()
    {
        frame.hide();
    }

    ////////////////////////////////////////////////////////////////////
    // copying                                                        //
    ////////////////////////////////////////////////////////////////////

    public void newCopyProcess(String in, String out, List<Predicate<File>> f)
    {
        //there should be no other copy-operations running in parrallel
        //TODO multiple copy operations in parrallel
        assert op == null;

        if(!new File(in).exists())
        {
            JOptionPane.showMessageDialog(frame.getFrame(), "Input doesn't exist", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CopyState state = new CopyState(in, out);

        //create new UI
        CopyUI ui = CopyUI.createUI(this, state);
        frame.switchUI(ui);

        //create and launch new predictor
        Predictor predictor = new Predictor(in, state);
        predictor.start();

        //create new copyop
        op = new CopyOp(new CopyProcessWatcher[]{predictor, ui, tray}, state);
        op.start(f);
    }

    public void triggerCopying()
    {
        if(op.isPaused())
            op.continueCopying();
        else
            op.pauseCopying();
    }

    public void cancelCopying()
    {
        op.stop();
    }

    public void disposeCopyUI() {
        frame.switchUI(selectorUI);
    }
}
