package copy;

public interface CopyProcessWatcher
{
    //TODO make manager and systray implement this listener

    //TODO getter for state???

    default void copyingPaused(){}

    default void copyingContinued(){}

    /**
     * called when the copying process terminates
     * either by cancelling or normal termination
     */
    default void copyingTerminated(){}

    default void copyingStarted(){}
}