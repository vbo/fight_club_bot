package ChatBot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

class HttpRequest {
  private static final String TOKEN = "224259678:AAFl8SSyGhq3gwp99x1YNu5XdCbyIjgP3ns";
  private static final String URL = "https://api.telegram.org/bot";
  private final String method;
  private final String params;
  HttpURLConnection connection;
  
  HttpRequest(String method) {
    this(method, "");
  }

  HttpRequest(String method, String params) {
    this.method = method;
    this.params = params;
    Storage.log("request: " + method + "?" + params);
  }

  public String execute() {
    String result = "";
    try {
      connection = getConnection();
      sendRequest();
      result = getResponse();
    } catch (Exception e) {
      System.out.println("Error while handling http request " + e);
    }
    Storage.log("response: " + result);
    return result;
  }

  private String getResponse() throws IOException {
    InputStream is = connection.getInputStream();
    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
    StringBuilder response = new StringBuilder();
    String line;
    while((line = rd.readLine()) != null) {
      response.append(line);
      response.append('\r');
    }
    rd.close();
    return response.toString();
  }

  private void sendRequest() throws IOException {
    DataOutputStream wr = new DataOutputStream (
        connection.getOutputStream());
    wr.writeBytes(params);
    wr.close();
  }

  private HttpURLConnection getConnection() 
      throws MalformedURLException, IOException, ProtocolException {
    URL url = new URL(URL + TOKEN + "/" + method);
    connection = (HttpURLConnection)url.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type",
        "application/x-www-form-urlencoded");
    connection.setRequestProperty("Content-Length",
        Integer.toString(params.getBytes().length));
    connection.setRequestProperty("Content-Language", "en-US");
    connection.setUseCaches(false);
    connection.setDoOutput(true);
    return connection;
  }
}
