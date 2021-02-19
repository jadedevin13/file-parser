package org.hicx.fileparser.type;

import java.io.IOException;
import java.util.Set;

public interface FileTaskProducer {
  void produceFileTasks(String sourcee, Set<String> supportedTypes) throws IOException, InterruptedException;
}
