package jaywhy13.gycweb.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import jaywhy13.gycweb.GYCMainActivity;

/**
 * Created by Jay on 11/10/13.
 */
public class InitTask extends AsyncTask {

    private final Context context;

    public InitTask(Context context){
        this.context = context;
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Thread.sleep(3000);
            return new Object();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        // Dismiss the splash screen
        Intent intent = new Intent(context, GYCMainActivity.class);
        context.startActivity(intent);
    }
}
