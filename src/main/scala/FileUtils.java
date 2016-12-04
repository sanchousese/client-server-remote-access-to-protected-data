import java.io.*;
import java.net.Socket;

public class FileUtils {
    public final static String STOP_KEY = "stop";
    public final static byte[] STOP_BYTES = STOP_KEY.getBytes();

    public static void copy(File f, final OutputStream os) throws IOException {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(f));
        final byte[] buffer = new byte[(int) f.length()];
        is.read(buffer, 0, buffer.length);
        os.write(buffer, 0, buffer.length);
        os.write(STOP_BYTES);
        os.flush();
    }

    public static File receiveData(Socket clientSocket, File file) throws IOException {
        InputStream is = clientSocket.getInputStream();
        FileOutputStream fos = new FileOutputStream(file);
        byte[] content;
        byte[] bytes = new byte[1024 << 8];
        int bytesRead;
        while (true) {
            if (receiveStop(file)) {
                removeStop(file);
                break;
            }
            bytesRead = is.read(bytes);
            fos.write(bytes, 0, bytes.length);
        }

        return file;
    }

    private static boolean receiveStop(File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String currentLine;
        String lastLine = "";
        while ((currentLine = br.readLine()) != null) {
            lastLine = currentLine;
        }

        return lastLine.contains(STOP_KEY);
    }

    private static void removeStop(File f) throws IOException {
        RandomAccessFile raFile = new RandomAccessFile(f, "rw");
        long length = raFile.length();
        raFile.setLength(length - STOP_BYTES.length);
        raFile.close();
    }
}
