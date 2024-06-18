package edu.weber.cs.w01353438.duckduckjeep.tasks;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class GetPublicIPAddressTask extends AsyncTask<Void, Void, String[]> {

    private static final String TAG = "GetPublicIPAddressTask";

    private OnTaskCompleted listener;

    public GetPublicIPAddressTask(OnTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected String[] doInBackground(Void... voids) {
        String publicIPAddress = null;
        String[] locationInfo = new String[6]; // [0] = country, [1] = city, [2] = regionName, [3] = latitude, [4] = longitude
        HttpURLConnection connection = null;

        try {
            // Get public IP address
            URL url = new URL("https://ifconfig.co/ip");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            publicIPAddress = reader.readLine();
            reader.close();

            // Get location information using ip-api.com
            if (publicIPAddress != null && !publicIPAddress.isEmpty()) {
                URL locationUrl = new URL("http://ip-api.com/json/" + publicIPAddress);
                connection = (HttpURLConnection) locationUrl.openConnection();
                connection.setRequestMethod("GET");
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                locationInfo[0] = jsonResponse.getString("status");
                locationInfo[1] = jsonResponse.getString("country");
                locationInfo[2] = jsonResponse.getString("city");
                locationInfo[3] = jsonResponse.getString("regionName");
                locationInfo[4] = String.valueOf(jsonResponse.getDouble("lat"));
                locationInfo[5] = String.valueOf(jsonResponse.getDouble("lon"));
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error getting public IP address or location information", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        if(Objects.equals(locationInfo[0], "failed") || locationInfo[0] == null) {
            locationInfo[1] = "NA";
            locationInfo[2] = "NA";
            locationInfo[3] = "NA";
            locationInfo[4] = "0.0";
            locationInfo[5] = "0.0";
        }

    return locationInfo;
    }



    @Override
    protected void onPostExecute(String[] locationInfo) {
        if (listener != null) {
            listener.onTaskCompleted(locationInfo);
        }
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(String[] locationInfo);
    }
}
