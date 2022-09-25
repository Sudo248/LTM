package org.sudo248.protocols;

import java.util.regex.Pattern;

public class ProtocolImpl implements Protocol{

    private static final Pattern patternSpace = Pattern.compile(" ");
    private static final Pattern patternComma = Pattern.compile(",");

    private final String providedProtocol;

    public ProtocolImpl(String providedProtocol) {
        if (providedProtocol == null) throw new IllegalArgumentException();
        this.providedProtocol = providedProtocol;
    }

    @Override
    public boolean acceptProvidedProtocol(String inputProtocolHeader) {
        if (providedProtocol.isEmpty()) {
            return true;
        }

        String protocolHeader = patternSpace.matcher(inputProtocolHeader).replaceAll("");
        String[] headers = patternComma.split(protocolHeader);
        for (String header : headers) {
            if (providedProtocol.equals(header)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getProvidedProtocol() {
        return this.providedProtocol;
    }

    @Override
    public Protocol copy() {
        return new ProtocolImpl(this.providedProtocol);
    }

    @Override
    public String toString() {
        return this.providedProtocol;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Protocol protocol = (Protocol) obj;

        return protocol.getProvidedProtocol().equals(providedProtocol);
    }

    @Override
    public int hashCode() {
        return providedProtocol.hashCode();
    }
}
