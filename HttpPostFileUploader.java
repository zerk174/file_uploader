package org.imf.skb111.lis.controller.file_uploader;

import java.util.StringJoiner;

/**
 * Created by laden on 08.07.2019.
 */
public class HttpPostFileUploader implements IFileUploader {

    private StringJoiner executeResult = new StringJoiner("\r\n");

    @Override
    public boolean execute() {
        executeResult.add("HttpPostFileUploader");
        return false;
    }

    @Override
    public String getExecuteResult() {
        return executeResult.toString();
    }
}
