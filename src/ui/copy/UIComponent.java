package ui.copy;

import copy.CopyState;

import javax.swing.*;

public abstract class UIComponent
{
    public static final int STEPS = 1000;
    public static final int PERCENT_DECIMAL_PLACES = 3;

    private CopyState state;

    public UIComponent(CopyState state)
    {
        this.state = state;
    }

    protected CopyState getState()
    {
        return state;
    }

    public abstract JPanel createUI();

    public abstract void updateUI();
}