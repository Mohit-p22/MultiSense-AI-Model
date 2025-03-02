// ArticlesFragment.java
package com.example.changelanguage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArticlesFragment extends Fragment {
    private EditText userInput;
    private TextView textViewResponse;
    private ProgressBar progressBar;
    private Button btnAnalyze;

    private String selectedLanguage = "English"; // Default

    private static final String REQUEST_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=YOUR_API_KEY";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_article, container, false);

        userInput = view.findViewById(R.id.user_input);
        textViewResponse = view.findViewById(R.id.response_text);
        progressBar = view.findViewById(R.id.progress_bar);
        btnAnalyze = view.findViewById(R.id.analyze_button);

        // Retrieve stored language selection
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        selectedLanguage = sharedPreferences.getString("selectedLanguage", "English");

        btnAnalyze.setOnClickListener(v -> fetchGeminiResponse());

        return view;
    }

    private void fetchGeminiResponse() {
        String inputText = userInput.getText().toString().trim();
        if (inputText.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter some text!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnAnalyze.setEnabled(false);
        textViewResponse.setText("Analyzing your request...");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String response = fetchResponseFromGemini(inputText);

            handler.post(() -> {
                progressBar.setVisibility(View.GONE);
                btnAnalyze.setEnabled(true);

                if (response != null) {
                    textViewResponse.setText(response);
                    Toast.makeText(getActivity(), "Analysis complete!", Toast.LENGTH_SHORT).show();
                } else {
                    textViewResponse.setText("Failed to fetch response. Please try again.");
                    Toast.makeText(getActivity(), "Error fetching response", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private String fetchResponseFromGemini(String inputText) {
        try {
            URL url = new URL(REQUEST_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Construct JSON request body
            JSONObject requestBody = new JSONObject();
            JSONArray contentsArray = new JSONArray();
            JSONObject contentObject = new JSONObject();
            JSONArray partsArray = new JSONArray();
            JSONObject textObject = new JSONObject();

            selectedLanguage = (selectedLanguage.equals("English")?"English":"केवल देवनागरी लिपि का प्रयोग करें");
            // Use selected language ("English" or "Hindi") in the prompt
            textObject.put("text", "Hi Gemini, " + inputText + ". Provide an effective, short, and important response in " + selectedLanguage + " within 100 words, ensuring useful information. striclty don't use okay,hi,lets and other starting intro -just start with start with result of input text");
            partsArray.put(textObject);
            contentObject.put("parts", partsArray);
            contentsArray.put(contentObject);
            requestBody.put("contents", contentsArray);

            // Send request
            OutputStream os = conn.getOutputStream();
            os.write(requestBody.toString().getBytes("UTF-8"));
            os.close();

            // Read response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return parseGeminiResponse(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private String parseGeminiResponse(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray candidatesArray = jsonObject.getJSONArray("candidates");
            JSONObject contentObject = candidatesArray.getJSONObject(0).getJSONObject("content");
            JSONArray partsArray = contentObject.getJSONArray("parts");
            return partsArray.getJSONObject(0).getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error parsing response.";
        }
    }
}
