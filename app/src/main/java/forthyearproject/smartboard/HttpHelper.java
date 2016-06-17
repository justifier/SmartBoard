package forthyearproject.smartboard;

import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.ContentType;
import ch.boye.httpclientandroidlib.entity.mime.MIME;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntityBuilder;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.params.CoreProtocolPNames;
import ch.boye.httpclientandroidlib.util.EntityUtils;

public class HttpHelper {

    private static HttpURLConnection conn = null;
    private static DataOutputStream dos = null;

    HttpHelper(){

    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Failure,Request is currently not possible!";

        } catch (Exception e) {
            Log.e("InputStream", e.toString());
        }

        return result;
    }

    public static String DOWNLOAD(String urls){
        try {
            URL url = new URL(urls);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            //connect
            urlConnection.connect();
            //set the path where we want to save the file
            File SDCardRoot = Environment.getExternalStorageDirectory();
            //create a new file, to save the downloaded file
            File file = new File(SDCardRoot,"downloaded_file.png");
            FileOutputStream fileOutput = new FileOutputStream(file);
            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();
            //this is the total size of the file which we are downloading
            final int totalSize = urlConnection.getContentLength();
            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

    public static String UPDATE(String url,String path){
        String responseBody = "failure";
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpPost post = new HttpPost(url);
        post.addHeader("Accept", "application/json");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(MIME.UTF8_CHARSET);
        File file = new File(path);
        if (file.isFile()){
            if(path.contains("notes"))
                builder.addBinaryBody("notes", file, ContentType.MULTIPART_FORM_DATA, file.getName());
            else if(path.contains("video"))
                builder.addBinaryBody("video", file, ContentType.MULTIPART_FORM_DATA, file.getName());
            else
                builder.addBinaryBody("attachment", file, ContentType.MULTIPART_FORM_DATA, file.getName());
        }


        post.setEntity(builder.build());

        try {
            responseBody = EntityUtils.toString(client.execute(post).getEntity(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.getConnectionManager().shutdown();
        }
        return responseBody;
    }
    // convert inputstream to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
