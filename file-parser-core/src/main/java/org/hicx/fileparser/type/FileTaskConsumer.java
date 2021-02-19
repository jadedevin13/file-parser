package org.hicx.fileparser.type;

import org.hicx.fileparser.type.interceptor.FileStatInterceptor;

import java.util.List;
import java.util.concurrent.Future;

public interface FileTaskConsumer {
    Future<Void> consumeFile(String source, String destination);
    void setFileStatInterceptors(List<FileStatInterceptor> fileStatInterceptors);
}
