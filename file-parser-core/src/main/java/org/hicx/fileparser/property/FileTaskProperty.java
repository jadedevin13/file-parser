package org.hicx.fileparser.property;

import java.util.Set;

public interface FileTaskProperty {
    Set<String> getSupportedTypes();
    String getSourcePath();
    String getDestinationPath();
}
