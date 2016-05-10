package ru.spbau.mit;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class SmartList<E> extends AbstractList<E> implements List<E> {
    private Object[] mass;
    private int size;

    public SmartList() {
        mass = null;
        size = 0;
    }

    public SmartList(Collection<? extends E> col) {
        col.forEach(SmartList.this::add);
    }

    @Override
    public int size() {
        return size;
    }
    private void checkIndex(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public E get(int index) {
        checkIndex(index);

        if (size <= 5) {
            return (E) mass[index];
        } else {
            ArrayList<E> list = (ArrayList<E>) mass[0];
            return list.get(index);
        }
    }

    @Override
    public  E set(int index, E element) {
        checkIndex(index);

        E oldElement;
        if (size <= 5) {
            oldElement = (E) mass[index];
            mass[index] = element;
        } else {
            ArrayList<E> list = (ArrayList<E>) mass[0];
            oldElement = list.get(index);
            list.set(index, element);
        }

        return  oldElement;
    }

    private void rShift(int index) {
        for (int i = size - 1; i >= index; --i) {
            mass[i + 1] = mass[i];
        }
    }

    private void lShift(int index) {
        for (int i = index; i < size - 1; ++i) {
            mass[i] = mass[i + 1];
        }
    }

    @Override
    public void add(int index, E element) {
        if (index > size) {
            throw new IndexOutOfBoundsException();
        }

        if (mass == null) {
            mass = new Object[1];
        }

        if (size == 0) {
            mass[0] = element;
        } else if (size == 1) {
            Object elem = mass[0];
            mass = new Object[5];
            mass[0] = elem;
            rShift(index);
            mass[index] = element;
        } else if (size < 5) {
            rShift(index);
            mass[index] = element;
        } else {
            if (size == 5) {
                ArrayList<E> list = new ArrayList<>();
                for (Object elem : mass) {
                    list.add((E) elem);
                }

                mass = new Object[1];
                mass[0] = list;
            }

            ArrayList<E> list = (ArrayList<E>) mass[0];
            list.add(index, element);
        }
        ++size;
    }

    @Override
    public E remove(int index) {
        checkIndex(index);

        E oldElem = null;
        if (size == 1) {
            oldElem = (E) mass[0];
            mass = null;
        } else if (size <= 5) {
            oldElem = (E) mass[index];
            lShift(index);
            if (size - 1 == 1) {
                Object elem = mass[0];
                mass = new Object[1];
                mass[0] = elem;
            }
        } else {
            ArrayList<E> list = (ArrayList<E>) mass[0];
            list.remove(index);
            if (list.size() == 5) {
                mass = new Object[5];

                for (int i = 0; i < list.size(); ++i) {
                    mass[i] = list.get(i);
                }
            }
        }

        --size;
        return oldElem;
    }

}
