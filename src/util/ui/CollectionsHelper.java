package util.ui;

import java.util.HashMap;
import java.util.Map;

public class CollectionsHelper
{
    public static <K, V> Map<K, V> createMap(Object... o)
    {
        if(o.length % 2 != 0)
            throw new IllegalArgumentException("Missing value for key " + o[o.length - 1]);

        Map<K, V> result = new HashMap<>();

        for(int i = 0; i < o.length; i += 2)
            result.put((K) o[i], (V) o[i + 1]);

        return result;
    }
}
