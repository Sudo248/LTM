package com.sudo248.ltm.api.model.image;

import java.io.Serializable;

public class Image implements Serializable {
    private static final long serialVersionUID = 7748359931882114672L;
    private int size;
    private byte[] content;
    private String name;

    public Image() {
    }

    public Image( String name, int size, byte[] content) {
        this.size = size;
        this.content = content;
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
