package org.sudo248.handshake;

import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Implementation of a handshake builder
 */

public class HandshakeBuilderImpl implements HandshakeBuilder {

    /**
     * Attribute for the content of the handshake
     */
    private byte[] content;

    /**
     * Attribute for the http fields and values
     */
    private final TreeMap<String, String> map;

    public HandshakeBuilderImpl() {
        map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    @Override
    public Iterator<String> iterateHttpFields() {
        return Collections.unmodifiableSet(map.keySet()).iterator();
    }

    @Override
    public String getFieldValue(String name) {
        String value = map.get(name);
        return value != null ? value : "";
    }

    @Override
    public boolean hasFieldValue(String name) {
        return map.containsKey(name);
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public void put(String name, String value) {
        map.put(name, value);
    }
}
