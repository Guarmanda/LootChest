package eu.decentholo.holograms.api.utils.collection;

import java.util.ArrayList;


import eu.decentholo.holograms.api.utils.Common;

public class DList<T> extends ArrayList<T> {

    public DList() {
        super();
    }

    public DList(int cap) {
        super(cap);
    }

    public T random() {
        return get(randomIndex());
    }

    /**
     * Pop the first item off this list and return it
     *
     * @return the item or null if the list is empty
     */
    public T pop() {
        if (isEmpty()) return null;
        return remove(0);
    }

    public T popRandom() {
        if (isEmpty()) return null;
        if (size() == 1) {
            return pop();
        }
        return remove(randomIndex());
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public boolean hasElements() {
        return !isEmpty();
    }

    public int randomIndex() {
        return Common.randomInt(0, size() - 1);
    }

}
