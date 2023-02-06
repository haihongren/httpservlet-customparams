package com.newrelic.fit.instrumentation;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ResponseWrapper extends javax.servlet.http.HttpServletResponseWrapper {
    private String body="{}";
    private JSONObject jsonbody = new JSONObject();

    private ServletOutputStream outputStream;
    private PrintWriter writer;
    private OutputStreamWrapper copier;
    private String encoding;

    public ResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        encoding=response.getCharacterEncoding();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            super.getOutputStream();
        }

        if (outputStream == null) {
            outputStream = getResponse().getOutputStream();
            copier = new OutputStreamWrapper(outputStream);
        }
        return copier;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStream != null) {
            super.getWriter();
        }
        if (writer == null) {
            copier = new OutputStreamWrapper(getResponse().getOutputStream());
            writer = new PrintWriter(new OutputStreamWriter(copier, getResponse().getCharacterEncoding()), true);
        }
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (writer != null) {
            writer.flush();
        } else if (outputStream != null) {
            copier.flush();
            byte[] copy = getCopy();
            body=new String(copy,encoding);
            convertToJson();
       }
    }

    private byte[] getCopy() {
        if (copier != null) {
            return copier.getCopy();
        } else {
            return new byte[0];
        }
    }

    private void convertToJson() {
        try {
            this.jsonbody = new JSONObject(this.body);
        } catch (JSONException err) {
            this.jsonbody = new JSONObject();
        }
    }
    public String bodyHasKey(String key) {
        if ( jsonbody.has(key)) {
            return jsonbody.getString(key);
        } else
            return null;
    }
}

