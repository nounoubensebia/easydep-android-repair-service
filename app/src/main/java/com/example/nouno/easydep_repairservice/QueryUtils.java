package com.example.nouno.easydep_repairservice;

import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nouno on 09/04/2017.
 */

public class QueryUtils {

    public static final String SEND_REQUEST_URL = "http://192.168.1.5/EasyDep/send_request.php";
    public static final String GET_REQUESTS =  "get_repair_service_requests";
    public static final String GET_QUEUE_ELEMENTS = "get_repair_service_queue_elements";
    public static final String GET_PLACE_POSITION_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    public static final String GET_PLACE_PREDICTIONS_URL ="https://maps.googleapis.com/maps/api/place/autocomplete/json?";
    public static final String GET_COMMENTS_URL = "http://192.168.1.5/EasyDep/comment.php";
    public static final String CANCEL_REQUEST_ACTION = "cancel_request_repair_service";
    public static final String SEND_ESTIMATE_ACTION = "send_estimate";
    public static final String CONNECTION_PROBLEM = "connection problem";
    public static final String ACCOUNT_URL = "http://192.168.1.5/EasyDep/repair_service_account_api";
    public static final String SIGNUP_ACTION = "signup";

    public static String makeHttpPostRequest (String urlString,Map<String,String> parameters) throws ConnectionProblemException
    {   String response = null;
        InputStream inputStream=null;
        HttpURLConnection urlConnection=null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            String postParameters = buildParametersString(parameters);
            urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(postParameters);
            out.close();
            urlConnection.connect();
            if (urlConnection.getResponseCode()==200)
            {
                inputStream = urlConnection.getInputStream();
                response=readFromStream(inputStream);
            }
            else
            {
                throw new ConnectionProblemException("status code != 200");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (inputStream!=null)
            {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {

                throw new ConnectionProblemException("connexion introuvable");

            }
            if (urlConnection!=null)
            {
                urlConnection.disconnect();
            }

            return response;
        }
    }

    public static String makeHttpPostJsonRequest (String urlString,String jsonObject) throws ConnectionProblemException
    {

        try {
            jsonObject = new String(jsonObject.getBytes(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String response = null;
        InputStream inputStream=null;
        HttpURLConnection httpCon=null;
        try {
            URL url = new URL(urlString);
            httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setConnectTimeout(15000);
            httpCon.setReadTimeout(15000);
            httpCon.setRequestMethod("POST");
            httpCon.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            httpCon.connect();
            OutputStream os = httpCon.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(jsonObject);
            osw.flush();
            osw.close();
            if (httpCon.getResponseCode()==200)
            {
                inputStream = httpCon.getInputStream();
                response=readFromStream(inputStream);
            }
            else
            {
                throw new ConnectionProblemException("status code != 200");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (inputStream!=null)
            {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {

                throw new ConnectionProblemException("connexion introuvable");

            }
            if (httpCon!=null)
            {
                httpCon.disconnect();
            }
            return response;
        }

    }

    public static String makeHttpGetRequest(String urlString,Map<String,String> map)
    {
        urlString+=buildParametersString(map);
        return makeHttpGetRequest(urlString);
    }


    public static String makeHttpGetRequest (String urlString)
    {

        String jsonResponse ="";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString);
            urlConnection=(HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode()==200){
                inputStream=urlConnection.getInputStream();
                jsonResponse=readFromStream(inputStream);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (inputStream!=null)
            {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection!=null)
            {
                urlConnection.disconnect();
            }

            return jsonResponse;}
    }


    //méthode pour construire les parametres a envoyer par POST ou GET
    //param est la liste des parametres
    public static String buildParametersString(Map<String,String> parameters)
    {
        Set<Map.Entry<String,String>> entrySet = parameters.entrySet();
        Iterator<Map.Entry<String,String>> iterator = entrySet.iterator();
        StringBuilder builder = new StringBuilder("");
        boolean isFirstParameter = true;
        while (iterator.hasNext())
        {
            Map.Entry<String,String> entry = iterator.next();
            if (!isFirstParameter)
            {
                builder.append("&");
            }
            builder.append(entry.getKey()+"="+entry.getValue());
            isFirstParameter=false;
        }
        return builder.toString();
    }

    //méthode pour lire un inputstream (zellal)

    public static String readFromStream (InputStream inputStream)
    {
        StringBuilder builder = new StringBuilder("");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);
        try {
            String line =reader.readLine();
            while (line != null)
            {
                builder.append(line);
                line=reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return builder.toString();
        }
    }
    public static boolean validateEmail(String email)
    {
        String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public static boolean validatePassword(String password)
    {
        return (password.length()>5);
    }
    public  static boolean validateName (String name)
    {
        String regx = "^[\\p{L} .'-]+$";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher;
        matcher = pattern.matcher(name);
        return (matcher.matches()&&(name.length()>1));
    }
    public static LinkedHashMap<String,String> buildSearchSuggestionsParamsMap (String input)
    {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("input",input);
        //map.put("types","geocode");
        map.put("components","country:dz");
        map.put("language","fr_FR");
        map.put("key","AIzaSyAqQHxLWPTvFHDvz5WUwuNAjTa0UuSHbmk");
        return map;
    }

}
