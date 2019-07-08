package org.imf.skb111.lis.controller.file_uploader;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.imf.skb111.lis.dblink.exceptions.ExceptionEventHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by laden on 08.07.2019.
 */
public class FtpFileUploader implements IFileUploader {

    private File uploadFile = null;
    private String remoteDir = "";
    private String hostname = "";
    private int ftpPort = 21;
    private String login = "";
    private String password = "";
    private FTPClient ftpClient;

    public FtpFileUploader(File uploadFile, String remoteDir, String hostname, int ftpPort, String login, String password) {
        this.uploadFile = uploadFile;
        this.remoteDir = remoteDir;
        this.hostname = hostname;
        this.ftpPort = ftpPort;
        this.login = login;
        this.password = password;
    }

    public FtpFileUploader(File uploadFile, String hostname, int ftpPort, String login, String password) {
        this(uploadFile, "", hostname, ftpPort, login, password);
    }

    public FtpFileUploader(File uploadFile, String hostname) {
        this(uploadFile, "", hostname, 21, "", "");
    }

    public String getRemoteDir() {
        remoteDir = remoteDir.trim();
        if (remoteDir.length() > 0 && (remoteDir.substring(remoteDir.length() - 1).equals("/") || remoteDir.substring(remoteDir.length() - 1).equals("\\"))) {
            remoteDir = remoteDir.substring(0, remoteDir.length() - 2);
        }
        return remoteDir;
    }

    public void setRemoteDir(String remoteDir) {
        this.remoteDir = remoteDir;
    }

    public File getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(File uploadFile) {
        this.uploadFile = uploadFile;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(int ftpPort) {
        this.ftpPort = ftpPort;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean execute() {
        System.out.println("FtpFileUploader");
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(getHostname(), getFtpPort());
            if (!getLogin().equals("")) {
                ftpClient.login(getLogin(), getPassword());
            }else{
                ftpClient.login("anonymous", "anonymous@anonymous.com");
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            if (!getUploadFile().exists()) {
                System.out.println(String.format("Local file for uploading [%s] not exist", getUploadFile().getAbsolutePath()));
                return false;
            }

            String remoteFile = (!getRemoteDir().equals("")) ? String.format("%s/%s", getRemoteDir(), getUploadFile().getName()) : getUploadFile().getName();
            InputStream inputStream = new FileInputStream(uploadFile);

            System.out.println(String.format("Start uploading file [%s] into remote file [%s]", uploadFile.getAbsolutePath(), remoteFile));

            OutputStream os = ftpClient.storeFileStream(remoteFile);
            if (os == null) {
                //something went wrong
                System.out.println(String.format("Error transferring file: %s", ftpClient.getReplyString()));
                return false;
            }
            byte[] bytes = new byte[4096];
            int read = 0;
            while ((read = inputStream.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }
            inputStream.close();
            os.close();

            boolean completed = ftpClient.completePendingCommand();
            if (completed) {
                System.out.println(String.format("Local file [%s] successfully uploaded into remote file [%s]", uploadFile.getAbsolutePath(), remoteFile));
            }
            return true;
        } catch (Exception ex) {
            ExceptionEventHandler.handle(ex);
        } finally {
            try {
                if (ftpClient != null && ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (Exception ex) {
                ExceptionEventHandler.handle(ex);
            }
        }
        return false;
    }
}
