package stork.dk.storkapp.communicationObjects;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * @author Johannes
 */

public class CommunicationsHandler {
    private static final String BASE_URL = "http://e-software.dk:8080/StorkServer_war/";
    private static final String LOGIN_URL = "login";
    private static final String REGISTER_URL = "register";
    private static final String UPDATE_LOCATION_URL = "updateLocation";
    private final static AsyncHttpClient client = new AsyncHttpClient();

    private final static Gson gson = new Gson();

    private static void get(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        client.get(url, params, responseHandler);
    }

    private static void post(Context context, String url, String payload, ResponseHandlerInterface responseHandler) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(payload);
        entity.setContentType("application/json");
        client.post(context, url, entity, "application/json", responseHandler);
    }

    public static void login(Context context, LoginRequest request, ResponseHandlerInterface responseHandler) {
        String payload = gson.toJson(request);
        try {
            post(context, BASE_URL + LOGIN_URL, payload, responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void register(Context context, RegisterUserRequest request, ResponseHandlerInterface responseHandler) {
        String payload = gson.toJson(request);
        try {
            post(context, BASE_URL + REGISTER_URL, payload, responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void updateLocation(Context context, UpdateLocationRequest request, ResponseHandlerInterface responseHandler) {
        String payload = gson.toJson(request);
        try {
            post(context, BASE_URL + UPDATE_LOCATION_URL, payload, responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
