package com.hybris.easyjet.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SessionList<E> extends ArrayList<E> {

    private static final HashMap<String, Integer> positions = new HashMap<>();

    private static final String FIRST = "first";
    private static final String ORIGINAL = "original";

    public SessionList(int initialCapacity) {
        super(initialCapacity);
        positions.put(FIRST, 0);
        positions.put(ORIGINAL, 0);
    }

    public SessionList() {
        super();
        positions.put(FIRST, 0);
        positions.put(ORIGINAL, 0);
    }

    public SessionList(Collection<? extends E> c) {
        super(c);
        positions.put(FIRST, 0);
        positions.put(ORIGINAL, 0);
    }

    private void updatePositions() {
        for (Map.Entry<String, Integer> entry : positions.entrySet()) {
            positions.put(entry.getKey(), entry.getValue() + 1);
        }
    }

    public SessionList push(E e) {
        if (e == null) {
            return null;
        } else {
            super.add(0, e);
            updatePositions();
            return this;
        }
    }

    public SessionList push(E e, String position) {
        if (positions.containsKey(position)) {
            super.set(positions.get(position), e);
        } else {
            super.add(0, e);
            updatePositions();
            positions.put(position, 0);
        }
        return this;
    }

    @Override
    public E get(int index) {
        return super.get(index);
    }

    public E get(String position) {
        if (positions.containsKey(position) && this.size() > positions.get(position)) {
            return this.get(positions.get(position));
        } else {
            return null;
        }
    }
}