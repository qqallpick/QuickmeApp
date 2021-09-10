package com.zsuuu.quickmeapp.FilePicker;

import android.content.Context;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;

import com.zsuuu.quickmeapp.StorageAccessFrameworkHelper;

import java.util.ArrayList;
import java.util.Arrays;


public class FileBrowser {

    private DocumentFile currentDirectory;
    private FilePicker filePicker;

    public static final DocumentFile internalStorageHome = DocumentFile.fromFile(Environment.getExternalStorageDirectory());
    public static final String PARENT_FILE_NAME = "..";

    public FileBrowser(Context context) {
        currentDirectory = internalStorageHome;
        monitorCurrentPathForChanges();
    }
    public void setFilePicker(FilePicker filePicker) {
        this.filePicker = filePicker;
        updateFileViewer();
    }
    public void updateFileViewer() {
        ArrayList<DocumentFile> files = new ArrayList<>(Arrays.asList(currentDirectory.listFiles()));
        filePicker.setFileList(files);
    }
    private void monitorCurrentPathForChanges() {
         
    }

    public String getCurrentPathName() {
        return StorageAccessFrameworkHelper.getDocumentFilePath(currentDirectory);
    }

         public void setCurrentDirectory(DocumentFile newDirectory) {
        if(!newDirectory.isDirectory()) {
            return;
        }
        currentDirectory = newDirectory;
        monitorCurrentPathForChanges();
        updateFileViewer();
    }

     
    public boolean goToParentDirectory() {
        boolean hadParentDirectory = false;
        if (currentDirectory.getParentFile() != null) {
            setCurrentDirectory(currentDirectory.getParentFile());
            hadParentDirectory = true;
        }
        return hadParentDirectory;
    }

    public String getCurrentDirectoryUriString() {
        return currentDirectory.getUri().toString();
    }

    public DocumentFile getCurrentDirectory() {
        return currentDirectory;
    }
    
}
