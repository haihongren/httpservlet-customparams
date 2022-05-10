package com.newrelic.fit.instrumentation;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * From: https://howtodoinjava.com/servlets/httpservletrequestwrapper-example-read-request-body/
 */
public class RequestWrapper extends HttpServletRequestWrapper {
    private final String body;
    private final JSONObject jsonbody;
    private boolean hasJsonBody=false;
    public RequestWrapper(HttpServletRequest request) throws IOException {
        //So that other request method behave just like before
        super(request);

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
        //Store request pody content in 'body' variable
        body = stringBuilder.toString();
        jsonbody= convertToJson();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        ServletInputStream servletInputStream = new ServletInputStream() {
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return servletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    //Use this method to read the request body N times
    public String getBody() {
        return this.body;
    }

    public JSONObject convertToJson() {
        JSONObject json;
        try {
            json = new JSONObject(this.body);
            hasJsonBody=true;
        } catch (JSONException err) {
            hasJsonBody=false;
            json = new JSONObject();
        }
        return json;
    }
    public boolean hasJsonBody(){
        return hasJsonBody;
    }
    public String bodyHasKey(String key) {

        if ( jsonbody.has(key)) {
            return jsonbody.getString(key);
        } else return null;
    }
    public String getJsonBody() {

        return jsonbody.toString();
    }
}