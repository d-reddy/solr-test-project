package org.reddy.helpers;

/**
 * Created by dreddy on 8/26/2016.
 */
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Charsets;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpClient {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static HttpRequestFactory HTTP_REQUEST = HTTP_TRANSPORT.createRequestFactory();

    private enum REQUEST {
        GET,
        POST
    }
    public static HttpResponse get(String url) throws IOException {
        return request(REQUEST.GET, url, null);
    }

    public static HttpResponse post(String url, String payload) throws IOException {
        return request(REQUEST.POST, url, payload);
    }

    private static HttpResponse request(REQUEST req, String url, String payload) throws IOException {
        GenericUrl uri = new GenericUrl(url);
        HttpRequest request = null;

        switch(req){
            case POST: {
                ByteArrayContent dataBytes = ByteArrayContent.fromString("application/json", payload);
                request = HTTP_REQUEST.buildPostRequest(uri, dataBytes);
                break;
            }
            case GET:
            default: {
                request = HTTP_REQUEST.buildGetRequest(uri);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType("application/json");
                request.setHeaders(headers);
                break;
            }
        }

        return request.execute();
    }

    public static String responseToString(HttpResponse response) throws IOException {
        InputStream in = response.getContent();
        return CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
    }
}
