package ru.spbau.mit;

import java.lang.reflect.Array;
import static java.util.Arrays.asList;
import java.util.*;

/**
 * Created by golovanov on 22.03.16.
 */
public class HashMultiset<E> extends AbstractCollection<E> implements Multiset<E>  {

    private LinkedHashMap<E, Entry<E>> store;
    private int size;

    public HashMultiset() {
        this.store = new LinkedHashMap<E, Entry<E>>();
    }

    public HashMultiset(LinkedHashMap<E, Entry<E>> store) {
        this.store = store;
    }

    @Override
    public boolean add(E elem) {
        if (store.containsKey(elem)) {
            EntryImpl<E> numElem = (EntryImpl<E>) store.get(elem);
            numElem.numElem += 1;
        }
        else {
            store.put(elem, new EntryImpl(elem));
        }
        size++;
        return true;
    }

    @Override
    public int count(Object element) {
        return store.get(element).getCount();
    }

    @Override
    public Set<E> elementSet() {
        return store.keySet();
    }

    @Override
    public Set<? extends Entry<E>> entrySet() {
        return null;
    }

    public static class EntryImpl<E> implements Multiset.Entry<E> {

        private E elem;
        private int numElem;

        public EntryImpl() {
            elem = null;
            numElem = 0;
        }

        public EntryImpl(E e) {
            elem = e;
            numElem = 1;
        }

        @Override
        public E getElement() {
            return elem;
        }

        @Override
        public int getCount() {
            return numElem;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        return size;
    }
}
