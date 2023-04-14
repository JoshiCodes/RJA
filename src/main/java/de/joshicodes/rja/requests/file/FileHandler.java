package de.joshicodes.rja.requests.file;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.Attachment;
import de.joshicodes.rja.object.InputFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class FileHandler {

    private final String url;

    public FileHandler(String url, RJA rja) {
        if(url == null) {
            throw new IllegalArgumentException("Fileserver URL cannot be null");
        }
        this.url = url;
    }

    public Attachment uploadFile(InputFile file) throws FileNotFoundException {
        if(file == null) {
            throw new IllegalArgumentException("File cannot be null!");
        }
        if((file.getFile() == null || !file.getFile().exists()) && file.getStream() == null) {
            throw new IllegalArgumentException("File does not exist!");
        }
        final String boundary = "********";
        try {
            URL url = new URL(this.url + "/attachments");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            OutputStream outputStream = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);

            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"").append(file.getName()).append("\"; filename=\"").append(file.getName()).append("\"").append("\r\n");
            writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(file.getName())).append("\r\n");
            writer.append("Content-Transfer-Encoding: binary").append("\r\n");
            writer.append("\r\n").flush();

            InputStream inputStream = file.getStream();
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();
            writer.append("\r\n").flush();

            writer.append("--" + boundary + "--").append("\r\n").flush();
            writer.close();

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] outputBuffer = new byte[1024];
            int length;
            while ((length = connection.getInputStream().read(outputBuffer)) != -1) {
                result.write(outputBuffer, 0, length);
            }
            String response = result.toString(StandardCharsets.UTF_8);
            connection.disconnect();

            JsonElement element = JsonParser.parseString(response);

            if(element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();

                return Attachment.from(object);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
