package mahmutcankahya.com.mzeappdemo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//http://karatren.epizy.com/?sf=1
public class HttpHandler {
    public HttpHandler(){

    }
    public String makeServiceCall(String requestUrl){
        String response=null;

        try {
            URL url =new URL(requestUrl);

            HttpURLConnection connection =(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");


            InputStream in= new BufferedInputStream(connection.getInputStream());
            response=convertStreamToString(in);
        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    private String convertStreamToString(InputStream in) {
        BufferedReader reader=new BufferedReader(new InputStreamReader(in));
        StringBuilder stringBuilder=new StringBuilder();

        String satir="";
        try {
            while ((satir=reader.readLine())!=null){
                stringBuilder.append(satir);
            }
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  stringBuilder.toString();
    }
}
