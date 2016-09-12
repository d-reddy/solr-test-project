package org.reddy.helpers;

import com.google.api.client.http.HttpResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.reddy.datacontracts.Properties;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dreddy on 8/27/2016.
 */
public class Solr {
    static final PropertyConfig config = new PropertyConfig();
    static final String host = config.getProperty("solrHost");

    public static void index(Properties properties) throws Exception {
        //first set to clean state
        Solr.cleanup(properties);

        //index documents
        StringJoiner joiner = new StringJoiner(",");

        properties.index.data.forEach((cmd) -> {
            String toAdd = cmd.toString();
            joiner.add(toAdd.substring(1,toAdd.length()-1));
        });

        String joinedString = "{".concat(joiner.toString()).concat("}");

        HttpResponse indexResponse = HttpClient.post(host.concat(properties.index.path), joinedString);

        if(!indexResponse.isSuccessStatusCode()){
            throw new Exception("Failed to index documents.");
        }
    }

    public static void cleanup(Properties properties) throws Exception {
        //reset, start from a clean, known state, in case cleanup from last run was not successful
        HttpResponse resetResponse = HttpClient.post(host.concat(properties.cleanup.path), properties.cleanup.data.toString());

        if(!resetResponse.isSuccessStatusCode()){
            throw new Exception("Failed to initialize a clean state.");
        }
    }

    public static ArrayList<Integer> search(Properties properties) throws Exception {
        //search
        HttpResponse searchResponse = HttpClient.get(host.concat(properties.search.path));

        if(!searchResponse.isSuccessStatusCode()){
            throw new Exception("Failed to retrieve correct documents.");
        }

        String result = HttpClient.responseToString(searchResponse);

        JsonParser jsonParser = new JsonParser();
        JsonObject jo = (JsonObject)jsonParser.parse(result);
        JsonArray docs = jo.getAsJsonObject("response").getAsJsonArray("docs");

        ArrayList<Integer> searchDocResults = new ArrayList<Integer>();

        docs.forEach((val) -> {
            searchDocResults.add(val.getAsJsonObject().get("reddy_item_id").getAsInt());
        });

        return searchDocResults;
    }

    public static String searchRaw(Properties properties) throws Exception {
        //search
        HttpResponse searchResponse = HttpClient.get(host.concat(properties.search.path));

        if(!searchResponse.isSuccessStatusCode()){
            throw new Exception("Failed to retrieve correct documents.");
        }

        String result = HttpClient.responseToString(searchResponse);

        return result;
    }

    public static ArrayList<Integer> index_search_cleanup(Properties properties) throws Exception {
        Solr.index(properties);
        ArrayList<Integer> searchResults = Solr.search(properties);
        Solr.cleanup(properties);
        return searchResults;
    }

    public static ArrayList<Double> pullDocStatistics(Properties properties, String pattern) throws Exception {

        Solr.cleanup(properties);

        Solr.index(properties);

        Pattern r = Pattern.compile(pattern);

        ArrayList<Double> stats = new ArrayList();

        String result = Solr.searchRaw(properties);

        JsonParser jsonParser = new JsonParser();
        JsonObject jo = (JsonObject)jsonParser.parse(result);
        JsonObject docs = jo.getAsJsonObject("debug").getAsJsonObject("explain");

        // Now create matcher object.
        Matcher m = r.matcher(docs.toString());
        while (m.find()) {
            stats.add(Double.parseDouble(m.group(1)));
        }

        Solr.cleanup(properties);

        return stats;
    }
}