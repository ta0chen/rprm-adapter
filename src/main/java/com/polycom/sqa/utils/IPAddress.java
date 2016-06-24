package com.polycom.sqa.utils;

import org.apache.log4j.Logger;

public class IPAddress {
    protected Logger     logger  = Logger.getLogger(this.getClass());
    private final int value;

    public IPAddress(final int value) {
        this.value = value;
    }

    public IPAddress(final String stringValue) {
        final String[] parts = stringValue.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException();
        }
        value = (Integer.parseInt(parts[0], 10) << (8 * 3)) & 0xFF000000
                | (Integer.parseInt(parts[1], 10) << (8 * 2)) & 0x00FF0000
                | (Integer.parseInt(parts[2], 10) << (8 * 1)) & 0x0000FF00
                | (Integer.parseInt(parts[3], 10) << (8 * 0)) & 0x000000FF;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof IPAddress) {
            return value == ((IPAddress) obj).value;
        }
        return false;
    }

    public int getOctet(final int i) {

        if (i < 0 || i >= 4) {
            throw new IndexOutOfBoundsException();
        }

        return (value >> (i * 8)) & 0x000000FF;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    public IPAddress next() {
        return new IPAddress(value + 1);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for (int i = 3; i >= 0; --i) {
            sb.append(getOctet(i));
            if (i != 0) {
                sb.append(".");
            }
        }

        return sb.toString();

    }
}
