package com.zsuuu.quickmeapp;

import android.support.v4.provider.DocumentFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

import org.aes.crypt.AESCrypt;

 

public class CryptoThread extends Thread {

         public static boolean operationInProgress = false;

     
    public static final boolean OPERATION_TYPE_ENCRYPTION = true;
    public static final boolean OPERATION_TYPE_DECRYPTION = false;
    public static final int VERSION_1 = 1;
    public static final int VERSION_2 = 2;


    private static HashMap<String, ProgressDisplayer> progressDiplayers = new HashMap<>();

    public interface ProgressDisplayer {
                 void update(boolean operationType, int progress, int completedMessageStringId, int minutesToCompletion, int secondsToCompletion);
    }

    private static final long updateIntervalInBytes = 550000;

    private static long timeOperationStarted = 0;
    private static long lastUpdateAtByteNumber = 0;
    private static long totalBytesRead = 0;
    private static long fileSize = 0;

    private CryptoService cryptoService;
    private static boolean operationType;

    private String inputFileName;
    private String outputFileName;
    private String password;
    private int version;
    private boolean deleteInputFile = false;
    private static int completedMessageStringId = R.string.done;

     
    public CryptoThread(CryptoService cryptoService, String inputFileName, String outputFileName, String password, int version, boolean operationType, boolean deleteInputFile) {
        this.cryptoService = cryptoService;
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.password = password;
        this.version = version;
        this.deleteInputFile = deleteInputFile;
        this.operationType = operationType;
    }

    @Override
    public void run() {
        boolean successful = true;
        operationInProgress = true;
        lastUpdateAtByteNumber = 0;
        totalBytesRead = 0;
        timeOperationStarted = System.currentTimeMillis();

        if (operationType == OPERATION_TYPE_ENCRYPTION) {
            completedMessageStringId = R.string.encryption_completed;
        } else {
            completedMessageStringId = R.string.decryption_completed;
        }

                 int [] timeToCompletion = {-1,-1};
        updateProgressDisplayers(0, 1, timeToCompletion);
        InputStream inputStream = null;
        OutputStream outputStream = null;
                 try {
            inputStream = StorageAccessFrameworkHelper.getFileInputStream(cryptoService, inputFileName);
        } catch (IOException ioe) {
            successful = false;
            ioe.printStackTrace();
            cryptoService.showToastOnGuiThread(R.string.error_could_not_get_input_file);
        }

                 try {
            outputStream = StorageAccessFrameworkHelper.getFileOutputStream(cryptoService, outputFileName);
        } catch (IOException ioe) {
            successful = false;
            ioe.printStackTrace();
            cryptoService.showToastOnGuiThread(ioe.getMessage());
        }

        if (inputStream != null && outputStream != null) {
                         try {
                fileSize = inputStream.available();
                AESCrypt aesCrypt = new AESCrypt(password);
                if (operationType == OPERATION_TYPE_ENCRYPTION) {
                                         aesCrypt.encrypt(version, inputStream, outputStream);
                } else {
                                         aesCrypt.decrypt(fileSize, inputStream, outputStream);
                }
            } catch (GeneralSecurityException gse) {
                successful = false;
                gse.printStackTrace();
                cryptoService.showToastOnGuiThread(R.string.error_platform_does_not_support_the_required_cryptographic_methods);
            } catch (UnsupportedEncodingException uee) {
                successful = false;
                uee.printStackTrace();
                cryptoService.showToastOnGuiThread(R.string.error_utf16_encoding_is_not_supported);
            } catch (IOException ioe) {
                successful = false;
                cryptoService.showToastOnGuiThread(ioe.getMessage());
            } catch (NullPointerException npe) {
                successful = false;
                cryptoService.showToastOnGuiThread(npe.getMessage());
            }
        }

                 if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ioe) {
                successful = false;
                ioe.printStackTrace();
                cryptoService.showToastOnGuiThread(R.string.error_could_not_close_input_file);
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException ioe) {
                successful = false;
                cryptoService.showToastOnGuiThread(R.string.error_could_not_close_output_file);
            }
        }

                 timeToCompletion[0]=0; timeToCompletion[1]=0;
        updateProgressDisplayers(fileSize, fileSize, timeToCompletion);

         
        if (successful && deleteInputFile && operationInProgress) {
            deleteInputFile();
        }
                 cryptoService.stopForeground(false);
        operationInProgress = false;
    }

    public static void updateProgressOnInterval(long bytesRead) {
        totalBytesRead += bytesRead;
        if (totalBytesRead - lastUpdateAtByteNumber > updateIntervalInBytes) {
            int [] timeToCompletion = getTimeToCompletion();
            lastUpdateAtByteNumber = totalBytesRead;
            updateProgressDisplayers(totalBytesRead, fileSize, timeToCompletion);
        }
    }

         private static void updateProgressDisplayers(long workDone, long totalWork, int [] timeToCompletion) {
        int progress = 100;
        if (totalWork != 0) {
            progress = (int) ((workDone * 100) / totalWork);
        }
        for (HashMap.Entry<String, ProgressDisplayer> progressDisplayer : progressDiplayers.entrySet()) {
            if (progressDisplayer.getValue() != null) {
                progressDisplayer.getValue().update(operationType, progress, completedMessageStringId, timeToCompletion[0], timeToCompletion[1]);
            } else {
                progressDiplayers.remove(progressDisplayer.getKey());
            }
        }
    }

     
    private static int[] getTimeToCompletion() {
        int[] timeToCompletion = {0, 0};
        long bytesPerMillisecond = totalBytesRead/(System.currentTimeMillis()-timeOperationStarted);
        long bytesPerSecond = bytesPerMillisecond*1000;
        if (bytesPerSecond != 0) {
            int secondsToCompletion = (int) ((fileSize - totalBytesRead) / bytesPerSecond);
            timeToCompletion[0] = secondsToCompletion / 60; timeToCompletion[1] = secondsToCompletion % 60;
        }
        return timeToCompletion;
    }

    private boolean deleteInputFile() {
        DocumentFile inputFile = GlobalDocumentFileStateHolder.getInputFileParentDirectory().findFile(inputFileName);
        if (inputFile != null) {
            return inputFile.delete();
        } else {
            return false;
        }
    }

    public static void registerForProgressUpdate(String id, ProgressDisplayer progressDisplayer) {
        progressDiplayers.put(id, progressDisplayer);
    }

         public static void cancel() {
        if (operationType == OPERATION_TYPE_ENCRYPTION) {
            completedMessageStringId = R.string.encryption_canceled;
        } else {
            completedMessageStringId = R.string.decryption_canceled;
        }
        operationInProgress = false;
    }

              public static int getProgressUpdate() {
        int progress = 0;
        if (fileSize == 0) {
            progress = 100;
        } else if (operationInProgress) {
            progress = (int) ((totalBytesRead * 100) / fileSize);
        }
        return progress;
    }

    public static boolean getCurrentOperationType() {
        return operationType;
    }

    public static int getCompletedMessageStringId() {
        return completedMessageStringId;
    }
}
