package org.imf.skb111.lis.controller.file_uploader;

/**
 * Created by laden on 08.07.2019.
 */
public class FileUploader implements IFileUploader {
    private IFileUploader iFileUploader;

    public FileUploader(IFileUploader iFileUploader) {
        this.iFileUploader = iFileUploader;
    }

    @Override
    public boolean execute() {
        return iFileUploader.execute();
    }

    @Override
    public String getExecuteResult() {
        return iFileUploader.getExecuteResult();
    }
}
