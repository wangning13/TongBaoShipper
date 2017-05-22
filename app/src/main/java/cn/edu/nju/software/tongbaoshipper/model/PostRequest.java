package cn.edu.nju.software.tongbaoshipper.model;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class PostRequest extends Request<JSONObject> {

    private Map<String, String> params;
    private Response.Listener<JSONObject> listener;

    public PostRequest(String url, Response.Listener<JSONObject> listener,
                       Response.ErrorListener errorListener, Map<String, String> params) {
        super(Method.POST, url, errorListener);
        this.params = params;
        this.listener = listener;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String strJson = new String(networkResponse.data,
                    HttpHeaderParser.parseCharset(networkResponse.headers));
            return Response.success(new JSONObject(strJson),
                    HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(JSONObject jsonObject) {
        this.listener.onResponse(jsonObject);
    }

    @Override
    public Map<String, String> getParams() {
        return this.params;
    }
}
