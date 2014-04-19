package com.jmstudios.corvallistransit.jsontools;

import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public abstract class RetrieveJson extends AsyncTask<String, Void, String> {

    String[] comparisonValues;
    String ros;
    String[] additionalContent;

    public RetrieveJson(String[] targets, String routeOStop, String[] specifics)
    {
        additionalContent = specifics;
        comparisonValues = targets;
        ros = routeOStop;
    }

    protected String doInBackground(String... voids) {
        if(voids[0] == null)
             return "No url provided.";
        String url = voids[0];
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
            else
            {
                //Log.e(MainActivity.class.toString(), "Failed to download file");
                System.out.println("Failed to download file");
            }
        } catch (ClientProtocolException e) {
            System.out.println("-->Client Protocol Exception in RetrieveJson");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("-->IOException Exception in RetrieveJson");
            e.printStackTrace();
        } catch (IllegalStateException ise) {
            System.out.println("-->IllegalState Exception in RetrieveJson");
            ise.printStackTrace();
        } catch (IllegalArgumentException iae) {
            System.out.println("-->IllegalArgument Exception in RetrieveJson");
            iae.printStackTrace();
        }
        return (!builder.toString().equals(""))?builder.toString():"Nothing found, is your url correct?";
    }

    @Override
    protected void onPostExecute(String aVoid)
    {
        super.onPostExecute(aVoid);

        //turn our retrieved String into a Json Object, then return a string array of it's contents
        Set<String> set = new HashSet<String>();
        try
        {
            JSONObject jObject = new JSONObject(aVoid);
            JSONArray jResults = jObject.getJSONArray(ros);
            StringBuilder sb = new StringBuilder();
            for(int x = 0; x < jResults.length(); x++)
            {
                /*
                sb.append("Route: ");
                sb.append(jResults.getJSONObject(x).getString("Route"));
                sb.append("\n");

                sb.append("Expected: ");
                sb.append(jResults.getJSONObject(x).getString("Expected"));
                sb.append("\n");

                sb.append("Scheduled: ");
                sb.append(jResults.getJSONObject(x).getString("Scheduled"));
                */
                boolean shouldAdd = true;
                if(additionalContent != null)
                {
                    shouldAdd = false;
                    for(String aVal: comparisonValues)
                    {
                        String inResult = jResults.getJSONObject(x).getString(aVal);
                        for(String item: additionalContent)
                        {
                            if(inResult.equals(item))
                            {
                                shouldAdd = true;
                                break;
                            }
                        }
                    }
                }

                for(String aVal: comparisonValues)
                {
                    if(shouldAdd)
                    {
                        sb.append(aVal);
                        sb.append(": ");
                        sb.append(jResults.getJSONObject(x).getString(aVal));
                        sb.append("\n");
                    }
                }

                set.add(sb.toString());

                sb.delete(0,sb.length());
            }
        }
        catch(JSONException jse)
        {
            System.out.println("JSON exception has occurred!");
            jse.printStackTrace();
        }

        //This is the key here,
        //onPostExecute is called on completion, we can have this push
        //our result onto our callback 'onResponseReceived'
        TreeSet finalResult = new TreeSet<String>(set);
        onResponseReceived(finalResult);
    }

    //our callback, note the 'abstract' mark in the method name,
    //this indicates that this method Must be overridden by the caller
    public abstract void onResponseReceived(TreeSet ts);
}
