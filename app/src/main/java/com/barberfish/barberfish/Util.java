package com.barberfish.barberfish;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by matthias on 11.10.17.
 */

public class Util {
    public static JSONObject createConfig() throws JSONException {
        JSONObject jObj = new JSONObject();
        jObj.put("encoding", "OGG_OPUS");
        jObj.put("sampleRateHertz", 16000);
        jObj.put("languageCode", "de-DE");
        return jObj;
    }
    public static JSONObject createAudio(InputStream stream) throws JSONException {
        JSONObject jObj = new JSONObject();
        jObj.put("content", encodeStream(stream));
        return jObj;
    }
    public static String encodeStream(InputStream stream) {
        byte[] audioData = new byte[0];
        try {
            audioData = IOUtils.toByteArray(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeBase64String(audioData);
    }
}
