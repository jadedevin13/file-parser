package org.hicx.fileparser.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@Data
@ConfigurationProperties(prefix = "file-parser.type.simple-local-file")
public class SimpleLocalFileTaskProperty implements FileTaskProperty {
  /** Supported file types of this task producer */
  private Set<String> supportedTypes;
  private String sourcePath;
  private String destinationPath;
}
