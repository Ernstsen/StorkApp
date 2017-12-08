package stork.dk.storkapp.communicationObjects;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * @author Johannes, Mathias, Morten
 */

public class CommunicationsHandler {
    private static final String BASE_URL = "http://e-software.dk:8080/StorkServer_war/";
    private static final String LOGIN_URL = "login";
    private static final String REGISTER_URL = "register";
    private static final String GET_USER_URL = "getUser";
    private static final String UPDATE_LOCATION_URL = "updateLocation";
    private static final String GET_FRIENDS_URL = "getFriends";
    private static final String GET_GROUPS_URL = "getGroups";
    private static final String GET_USERS_URL = "getUsers";
    private static final String CHANGE_USER_URL = "changeUser";
    private static final String CHANGE_FRIEND_URL = "friend";
    private static final String CHANGE_GROUP_URL = "changeGroup";
    private static final String UPDATE_GROUP_ACTIVATION = "changeGroupActivation";

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

    public static void getFriends(Map<String, String> params, ResponseHandlerInterface responseHandler) {
        RequestParams requestParams = new RequestParams(params);
        get(BASE_URL + GET_FRIENDS_URL, requestParams, responseHandler);
    }

    public static void getGroups(Map<String, String> params, ResponseHandlerInterface responseHandler) {
        RequestParams requestParams = new RequestParams(params);
        get(BASE_URL + GET_GROUPS_URL, requestParams, responseHandler);
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

    public static void getUser(Map<String, String> params, ResponseHandlerInterface responseHandler) {
        RequestParams requestParams = new RequestParams(params);
        get(BASE_URL + GET_USER_URL, requestParams, responseHandler);
    }

    public static void getUsers(ResponseHandlerInterface responseHandler) {
        get(BASE_URL + GET_USERS_URL, new RequestParams(), responseHandler);
    }

    public static void editUser(Context context, ChangeUserRequest req, ResponseHandlerInterface responseHandler) {
        String payload = gson.toJson(req);
        try {
            post(context, BASE_URL + CHANGE_USER_URL, payload, responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void changeFriends(Context context, FriendChangeRequest req, ResponseHandlerInterface responseHandler){
        String payload = gson.toJson(req);
        try {
            post(context, BASE_URL + CHANGE_FRIEND_URL, payload, responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public static void changeGroup(Context context, ChangeGroupRequest req, ResponseHandlerInterface responseHandler){
        String payload = gson.toJson(req);
        Log.d("LOG json:",payload);
        try {
            post(context, BASE_URL + CHANGE_GROUP_URL, payload, responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void changeGroupActivation(Context context, GroupChangeActivationRequest req, ResponseHandlerInterface responseHandler){
        String payload = gson.toJson(req);
        try{
            post(context, BASE_URL + UPDATE_GROUP_ACTIVATION, payload, responseHandler);
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }
}
