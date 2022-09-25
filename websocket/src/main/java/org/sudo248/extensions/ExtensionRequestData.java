package org.sudo248.extensions;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExtensionRequestData {

    public static final String EMPTY_VALUE = "";

    private final Map<String, String> extensionParameters;

    private String extensionName;

    private ExtensionRequestData() {
        extensionParameters = new LinkedHashMap<>();
    }

    public static ExtensionRequestData parseExtensionRequest(String extensionRequest) {
        ExtensionRequestData extensionRequestData = new ExtensionRequestData();
        String[] parts = extensionRequest.split(";");
        extensionRequestData.extensionName = parts[0].trim();

        for (int i = 0; i < parts.length; i++) {
            String[] keyValue = parts[i].split("=");
            String value = EMPTY_VALUE;

            // Some parameters don't take a value. For those that do, parse the value.
            if (keyValue.length > 1) {
                String tmpValue = keyValue[1].trim();

                // If the value is wrapped in quotes, just get the data between them.
                if ((tmpValue.startsWith("\"") && tmpValue.endsWith("\""))
                        || (tmpValue.startsWith("'") && tmpValue.endsWith("'"))
                        && tmpValue.length() > 2) {
                    tmpValue = tmpValue.substring(1, tmpValue.length() - 1);
                }
                value = tmpValue;
            }

            extensionRequestData.extensionParameters.put(keyValue[0].trim(), value);
        }

        return extensionRequestData;
    }

    public String getExtensionName() {
        return this.extensionName;
    }

    public Map<String, String> getExtensionParameters() {
        return extensionParameters;
    }
}
