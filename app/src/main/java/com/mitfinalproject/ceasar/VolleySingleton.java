import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {

    private static VolleySingleton mInstance;
    private RequestQueue queue;
    private Context mCtx;


    private VolleySingleton(Context context) {
        mCtx = context;
        queue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null)
        {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return queue;
    }

    public void addToRequestQueue(Request request) {
        queue.add(request);
    }


}
