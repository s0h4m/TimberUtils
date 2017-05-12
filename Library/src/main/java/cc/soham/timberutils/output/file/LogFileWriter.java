package cc.soham.timberutils.output.file;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cc.soham.timberutils.reporting.Reporting;

/**
 * Created by sohammondal on 17/08/16.
 * Handles all logging operations
 * Class will be "functional" in nature (No global state)
 */
public class LogFileWriter {
    public static final String DEBUG_LOGGER_HEADER = "For troubleshooting purposes only.";

    static final String FILENAME = "l";
    static final String LOGFOLDER = "logs";
    static final String ZIPFILENAME = "logs.zip";

    /**
     * Handle writing a log statement
     *
     * @param timberService
     * @param fileWriterWrapper
     * @param priority
     * @param tag
     * @param message
     * @param t
     * @throws IOException
     */
    public static void handleLog(TimberService timberService, FileWriterWrapper fileWriterWrapper, int priority, String tag, String message, Throwable t) throws IOException {
        logStartMessageIfNotDone(timberService, fileWriterWrapper);
        write(timberService.getApplicationContext(), fileWriterWrapper, priority, tag, message, t);
    }

    /**
     * Zips all the files in a given folder
     *
     * @param timberService
     */
    public static String handleZip(TimberService timberService) {
        return zipFiles(timberService);
    }

    /**
     * Append a message at the very beginning of a log message
     *
     * @param timberService
     * @param fileWriterWrapper
     * @throws IOException
     */
    public static void logStartMessageIfNotDone(TimberService timberService, FileWriterWrapper fileWriterWrapper) throws IOException {
        if (!timberService.isStarted()) {
            write(fileWriterWrapper, timberService.getApplicationContext(), DEBUG_LOGGER_HEADER + ":" + System.currentTimeMillis());
            timberService.setStarted(true);
        }
    }

    /**
     * Writes the typical timber format to a file
     *
     * @param priority
     * @param tag
     * @param message
     * @param t
     */
    public static void write(final Context context, FileWriterWrapper fileWriterWrapper, int priority, String tag, String message, Throwable t) throws IOException {
        write(fileWriterWrapper, context, Reporting.format(priority, tag, message, t));
    }

    /**
     * Write a line to the given {@link FileWriterWrapper}
     *
     * @param fileWriterWrapper
     * @param context
     * @param line
     */
    public static void write(FileWriterWrapper fileWriterWrapper, final Context context, String line) throws IOException {
        fileWriterWrapper.getFileWriter(context).write(line + "\n");
        fileWriterWrapper.getFileWriter(context).flush();
    }

    /**
     * Get the log folder
     *
     * @param context
     * @return
     */
    public static String getLogFolderPath(final Context context) {
        return context.getCacheDir().getPath() + "/" + LOGFOLDER + "/";
    }

    /**
     * Zip files
     *
     * @param timberService
     * @return
     */
    private static String zipFiles(final TimberService timberService) {
        try {
            String resultPath = timberService.getCacheDir().getAbsolutePath() + "/" + ZIPFILENAME;
            FileOutputStream fileOutputStream = new FileOutputStream(resultPath);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            File sourceFolder = new File(getLogFolderPath(timberService));
            if (writeFolderToZipOutputStream(zipOutputStream, sourceFolder)) {
                zipOutputStream.close();
            }
            // close the current File object
            timberService.getFileWriterWrapper().flushAndCloseFileWriter();
            return resultPath;
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Zips all the files in a given folder to a {@link ZipOutputStream}
     *
     * @param zipOutputStream
     * @param sourceFolder
     * @return true if we could zip something, false if the zip operation failed (because of empty folder
     * etc.)
     * @throws IOException
     */
    private static boolean writeFolderToZipOutputStream(ZipOutputStream zipOutputStream, File sourceFolder) throws IOException {
        File[] files = sourceFolder.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                writeFileToZipOutputStream(zipOutputStream, file);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Write a file to a {@link ZipOutputStream}
     * @param zipOutputStream
     * @param file
     * @throws IOException
     */
    private static void writeFileToZipOutputStream(ZipOutputStream zipOutputStream, File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
        writeFileInputStreamToZipOutputStream(zipOutputStream, fileInputStream);
        zipOutputStream.closeEntry();
        fileInputStream.close();
    }

    /**
     * Write the {@link java.io.InputStream} data to the zip {@link java.io.OutputStream}
     * @param zipOutputStream
     * @param fileInputStream
     * @throws IOException
     */
    private static void writeFileInputStreamToZipOutputStream(ZipOutputStream zipOutputStream, FileInputStream fileInputStream) throws IOException {
        int length;
        byte[] buffer = new byte[1024];
        while ((length = fileInputStream.read(buffer)) > 0) {
            zipOutputStream.write(buffer, 0, length);
        }
    }
}
