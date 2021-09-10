package com.zsuuu.quickmeapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

 

public final class StorageAccessFrameworkHelper {

    public static final int SAF_SDCARD_REQUEST_CODE = 44;

    private StorageAccessFrameworkHelper() {

    }

    public static void findSDCardWithDialog(final Activity activity) {
        AlertDialog.Builder builder;
        if (SettingsHelper.getUseDarkTeme(activity)) {
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(activity, R.style.DarkAlertDialogTheme);
            builder = new AlertDialog.Builder(contextThemeWrapper);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        builder.setTitle(R.string.sdcard).setMessage(R.string.how_to_locate_sdcard)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        findSDCard(activity);
                    }
                });
        builder.show();
    }

    public static OutputStream getOutputStreamWithSAF(Context context, File newFile) throws IOException {
        OutputStream outputStream = null;
        if (newFile.getParentFile().canWrite()) {
            outputStream = new FileOutputStream(newFile);
        } else {
             
            String uriString = SettingsHelper.getSdcardRoot(context);
            if (uriString == null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    throw new IOException(context.getString(R.string.file_write_access_denied));
                }
                throw new IOException(context.getString(R.string.write_permission_denied_if_sdcard_grant_permission_in_settings));
            }
            DocumentFile sdCardDirectory = DocumentFile.fromTreeUri(context, Uri.parse(uriString));
            String sdCardName = sdCardDirectory.getName();
            String newFilePathString = newFile.getAbsolutePath();
            String[] newFileSplitPath = newFilePathString.split("/");
            boolean foundSDCard = false;
            boolean fileNotFound = false;
            for (int i = 0; (i < newFileSplitPath.length) && !fileNotFound; i++) {
                if (!foundSDCard) {
                    if (sdCardName.equals(newFileSplitPath[i])) {
                        foundSDCard = true;
                    }
                } else if (i == newFileSplitPath.length - 1) {
                    outputStream = context.getContentResolver().openOutputStream(
                            sdCardDirectory.createFile("", newFileSplitPath[newFileSplitPath.length - 1])
                                    .getUri());
                } else {
                    fileNotFound = true;
                    for (int j = 0; j < sdCardDirectory.listFiles().length; j++) {
                        if (newFileSplitPath[i].equals(sdCardDirectory.listFiles()[j].getName())) {
                            fileNotFound = false;
                            sdCardDirectory = sdCardDirectory.listFiles()[j];
                        }
                    }
                }
            }
            if (!foundSDCard) {
                throw new IOException(context.getString(R.string.file_write_access_denied));
            } else if (fileNotFound) {
                throw new IOException(context.getString(R.string.file_not_found));
            }
        }
        return outputStream;
    }

    @TargetApi(21)
    private static void findSDCard(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), SAF_SDCARD_REQUEST_CODE);
        }
    }

     
    public static String findLikelySDCardPathFromSDCardName(Context context, String sdCardName) {
        if (sdCardName != null) {
            String[] externalStorageDirs = getExternalStorageDirectories(context);
            for (int i = 0; i < externalStorageDirs.length; i++) {
                if (externalStorageDirs[i].contains(sdCardName)) {
                    return externalStorageDirs[i];
                }
            }
        }
        return null;
    }

     
    private static String[] getExternalStorageDirectories(Context context) {

        List<String> results = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {              File[] externalDirs = context.getExternalFilesDirs(null);

            for (File file : externalDirs) {
                String path = file.getPath().split("/Android")[0];

                boolean addPath = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addPath = Environment.isExternalStorageRemovable(file);
                } else {
                    addPath = Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(file));
                }

                if (addPath) {
                    results.add(path);
                }
            }
        }

        if (results.isEmpty()) {                           String output = "";
            try {
                ProcessBuilder processBuilder = new ProcessBuilder().command("sh", "-c", "mount | grep /dev/block/vold");
                final Process process = processBuilder.redirectErrorStream(true).start();
                 
                process.waitFor();
                final InputStream is = process.getInputStream();
                final byte[] buffer = new byte[1024];
                while (is.read(buffer) != -1) {
                    output = output + new String(buffer);
                }
                is.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if (!output.trim().isEmpty()) {
                String devicePoints[] = output.split("\n");
                for (String voldPoint : devicePoints) {
                    String[] splitVoldPoint = voldPoint.split(" ");
                    if (splitVoldPoint.length > 2) {
                        results.add(voldPoint.split(" ")[2]);
                    }
                }
            }
        }

                  

        String[] storageDirectories = new String[results.size()];
        for (int i = 0; i < results.size(); ++i) storageDirectories[i] = results.get(i);

        return storageDirectories;
    }

    public static boolean canSupportSDCardOnAndroidVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static String getDocumentFilePath(DocumentFile documentFile) {
        return getDocumentFilePath(documentFile, null);
    }

     
    public static String getDocumentFilePath(DocumentFile documentFile, String childFileName) {

        if (documentFile == null) {
            return new String();
        }

        StringBuilder pathNameBuilder = new StringBuilder();
        pathNameBuilder.append(documentFile.getName());
        while (documentFile.getParentFile() != null) {
            pathNameBuilder.insert(0, File.separator);
            pathNameBuilder.insert(0, documentFile.getParentFile().getName());
            documentFile = documentFile.getParentFile();
        }

        if (childFileName != null) {
            pathNameBuilder.append(File.separator);
            pathNameBuilder.append(childFileName);
        }

        return pathNameBuilder.toString();
    }
    public static InputStream getFileInputStream(Context context, String inputFileName) throws FileNotFoundException{
        DocumentFile inputFile = GlobalDocumentFileStateHolder.getInputFileParentDirectory().findFile(inputFileName);
        if (inputFile == null) {
            throw new FileNotFoundException();
        }
        return context.getContentResolver().openInputStream(inputFile.getUri());
    }
    public static OutputStream getFileOutputStream(Context context, String outputFilename) throws FileNotFoundException{
        DocumentFile outputFile = GlobalDocumentFileStateHolder.getOutputFileParentDirectory().createFile("", outputFilename);
        if (outputFile == null) {
            throw new FileNotFoundException();
        }
        return context.getContentResolver().openOutputStream(outputFile.getUri());
    }
}
