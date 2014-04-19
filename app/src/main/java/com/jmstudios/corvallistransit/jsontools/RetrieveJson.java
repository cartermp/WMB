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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public abstract class RetrieveJson extends AsyncTask<String, Void, String>
{

    String[] comparisonValues;
    String ros;
    String[] additionalContent;
    String[] innerArrays;
    Set<HashMap<String,String>> set;

    public RetrieveJson(String[] targets, String routeOStop, String[] specifics, String[] iArrays)
    {
        additionalContent = specifics;
        comparisonValues = targets;
        ros = routeOStop;
        innerArrays = iArrays;

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
        set = new HashSet<HashMap<String, String>>();
        //turn our retrieved String into a Json Object, then return a string array of it's contents
        try
        {
            JSONObject jObject = new JSONObject(aVoid);
            JSONArray jResults = jObject.getJSONArray(ros);
            for(int x = 0; x < jResults.length(); x++)
            {
                if(innerArrays != null)
                {
                    for(String str: innerArrays)
                    {
                        JSONArray jsonArray = jResults.getJSONObject(x).getJSONArray(str);
                        addHashMap(jsonArray, str.toUpperCase());
                    }
                }

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

                HashMap<String,String> hm = new HashMap<String, String>();

                for(String aVal: comparisonValues)
                {
                    if(shouldAdd)
                    {
                        //sb.append(aVal);
                        //sb.append(jResults.getJSONObject(x).getString(aVal));
                        hm.put(aVal, jResults.getJSONObject(x).getString(aVal));
                    }
                }

                set.add(hm);
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
        TreeSet finalResult = new TreeSet<HashMap>(set);
        onResponseReceived(finalResult);
    }

    private void addHashMap(JSONArray jArray, String tag)
    {
        for( int x = 0; x < jArray.length(); x++)
        {
            try {
                boolean shouldAdd = true;
                if (additionalContent != null) {
                    shouldAdd = false;
                    for (String aVal : comparisonValues) {
                        String inResult = jArray.getJSONObject(x).getString(aVal);
                        for (String item : additionalContent) {
                            if (inResult.equals(item)) {
                                shouldAdd = true;
                                break;
                            }
                        }
                    }
                }

                HashMap<String, String> hm = new HashMap<String, String>();

                for (String aVal : comparisonValues) {
                    if (shouldAdd) {
                        //sb.append(aVal);
                        //sb.append(jResults.getJSONObject(x).getString(aVal));
                        hm.put(tag+aVal, jArray.getJSONObject(x).getString(aVal));
                    }
                }
                set.add(hm);
            }
            catch(JSONException je)
            {
                System.out.println("Json Exception occured in 'addHashMap'");
                je.printStackTrace();
            }
        }
    }


    //our callback, note the 'abstract' mark in the method name,
    //this indicates that this method Must be overridden by the caller
    public abstract void onResponseReceived(TreeSet ts);
}
