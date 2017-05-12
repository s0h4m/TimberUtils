package cc.soham.timberutils.output.file;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by sohammondal on 20/08/16.
 * A wrapper on {@link FileWriter} to enable:
 * - management of a singleton {@link FileWriterWrapper}
 * - lifecycle management of {@link FileWriter}
 */
public class FileWriterWrapper {
    public static FileWriterWrapper fileWriterWrapper;
    /**
     * The object responsbile for writing to disk
     */
    private FileWriter fileWriter;

    /**
     * Singleton implementation of FileWriterWrapper
     *
     * @param context
     * @return
     */
    public static FileWriterWrapper getFileWriterWrapper(final Context context) {
        if (fileWriterWrapper == null) {
            fileWriterWrapper = new FileWriterWrapper();
            fileWriterWrapper.initializeFileWriterIfNotInitialized(context);
        }
        return fileWriterWrapper;
    }

    /**
     * Get the stored {@link FileWriter} object
     *
     * @param context
     * @return
     */
    public FileWriter getFileWriter(final Context context) {
        initializeFileWriterIfNotInitialized(context);
        return fileWriter;
    }

    /**
     * Flush the contents of the {@link FileWriter} and close the stream
     */
    public void flushAndCloseFileWriter() throws IOException {
        if (fileWriter != null) {
            fileWriter.flush();
            fileWriter.close();
            fileWriter = null;
        }
    }

    /**
     * Initialize the {@link FileWriter} object, involves:
     * - create the folder (default implementation)
     * - create a file
     *
     * @param context
     */
    public void initializeFileWriterIfNotInitialized(Context context) {
        if (fileWriter == null) {
            try {
                File tempLogDir = new File(LogFileWriter.getLogFolderPath(context));
                if (!tempLogDir.exists())
                    tempLogDir.mkdir();
                File file = File.createTempFile(LogFileWriter.FILENAME + "_" + System.currentTimeMillis(), null, tempLogDir);
                fileWriter = new FileWriter(file, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
