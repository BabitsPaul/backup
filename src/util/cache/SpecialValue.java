package util.cache;

import java.util.function.BiFunction;

/**
 * Associated to a specific cachhe
 *
 * keeps track of values inserted into a cache with a certain value
 */
public class SpecialValue<T>
{
    private T t;

    /**
     * first param: currently stored value
     * second param: value to check
     *
     * returns true, if the valuue should be updated
     */
    private BiFunction<T, T, Boolean> valSpecial;

    public SpecialValue(BiFunction<T, T, Boolean> valSpecial)
    {
        this.valSpecial = valSpecial;
    }

    public void checkVal(T t)
    {
        if(valSpecial.apply(this.t, t))
            this.t = t;
    }

    public T getValue()
    {
        return t;
    }
}
