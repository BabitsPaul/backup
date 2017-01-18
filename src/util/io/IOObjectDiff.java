package util.io;

import java.util.*;
import java.util.function.Function;

public class IOObjectDiff
{
    /**
     * creates a diff of ioobjects that are present in {code: in}, but not in {code: compareTo}
     * for any subtree that isn't existing in the compareTo, only the root will be stored.
     *
     *
     * @param in
     * @param compareTo structure against which is being compared
     * @param translateNode transforms a node from in into the corresponding node in compareTo
     * @return a list of ioobjects from in that aren't present in compareTo
     */
    public static List<AbstractIOObject> list(AbstractIOObject in, AbstractIOObject compareTo,
                                 Function<AbstractIOObject, AbstractIOObject> translateNode)
    {
        if(!in.exists())
            return new ArrayList<>(0);

        List<AbstractIOObject> diff = new LinkedList<>();
        Queue<AbstractIOObject> toInspected = new PriorityQueue<>();
        toInspected.offer(in);

        while(!toInspected.isEmpty())
        {
            AbstractIOObject obj = toInspected.poll();

            if(!translateNode.apply(obj).exists())
                diff.add(obj);
            else
                Collections.addAll(toInspected, obj.listChildren());
        }

        return diff;
    }
}
