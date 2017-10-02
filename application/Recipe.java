//import required Java libraries
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
//Java library concerning JSON parsing
import org.json.*;


// javac -cp .:../libs/java-json.jar Recipe.java - TO COMPILE
// java -cp .:../libs/java-json.jar Recipe - TO DEPLOY

//main class for API communication, same name as .java file
//sample Alcohol API, lol
public class Recipe {

	//class fields, simulating browser
	private final String USER_AGENT = "Mozilla/5.0";


	//main loop, starting point for JVM
	public static void main(String[] args) throws Exception {

		//instance of main class
		Recipe http = new Recipe();

		System.out.println("1) Send HTTP GET to API");
		String response = http.sendGet("cheese,vegan");

		String message = http.parseJsonParams(response);
		System.out.println(message);
	}

	//function for HTTP GET request
	private String sendGet(String q) throws Exception { //need to throw exception to comply with Java's Catch or Specify requirement

		/*
		TODO:
		insert parameters for web service, all are hardcoded atm
		*/
		String query = q;

		//endpoint basename
		String url = "https://api.edamam.com/search?q="+query+"&app_id=ee303027&app_key=81e0095fb042c3fbd7f27524f77c0ac4&from=0&to=1&calories=gte%20591,%20lte%20722&health=alcohol-free";
		//String url = "http://addb.absolutdrinks.com/drinks/absolut-cosmopolitan/?apiKey=9cdfee635b854c3792ac51c4957d61c6";

		//url object
		URL urlObject = new URL(url);

		/*
		openConnection() returns a URLConnection instance that represents a connection to the remote object referred to by the URL,
		then casts the return instance to type HttpURLConnection.
		*/
		//parses the URL, finds the protocol, and creates the HttpURLConnection object
		HttpURLConnection connectionInstance = (HttpURLConnection) urlObject.openConnection();

		//HTTP method is GET
		connectionInstance.setRequestMethod("GET");

		//set the http headers and all that bullshit
		connectionInstance.setRequestProperty("User-Agent", USER_AGENT); //identify client specs to server, in this case simulate 'Mozilla/5.0'

		/*
		Pretty sure this line is redundant since, as Java Docs put it:
		"Operations that depend on being connected will implicitly perform the connection, if necessary."
		i.e. that geResponseCode shit below
		*/
		//execute request
		connectionInstance.connect();

		//debug info
		//debugging is for noobs
		int HTTPResponseCode = connectionInstance.getResponseCode();

		System.out.println("Sending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + HTTPResponseCode);

		//read response from API
		//why tf do I need buffer/input readers to read a server response?
		BufferedReader inReader = new BufferedReader(
		        new InputStreamReader(connectionInstance.getInputStream())
		);
		String inputLine;
		StringBuffer response = new StringBuffer();

		//store responses to string
		while ((inputLine = inReader.readLine()) != null) {
			response.append(inputLine);
		}
		inReader.close();

		return response.toString();
	}

	/*
	Same structure as get request
	NOTE: setDoInput() required to write to the endpoint resource
	Also, not sure if parameters are passed properly??
	*/
	//function for HTTP POST request
	private void sendPost() throws Exception {

		String url = "https://selfsolve.apple.com/wcResults.do";
		URL urlObject = new URL(url);
		HttpsURLConnection connectionInstance = (HttpsURLConnection) urlObject.openConnection();

		//add request header
		connectionInstance.setRequestMethod("POST");
		connectionInstance.setRequestProperty("User-Agent", USER_AGENT);
		connectionInstance.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

		// Send post request
		connectionInstance.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(connectionInstance.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int HTTPResponseCode = connectionInstance.getResponseCode();
		System.out.println("Sending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + HTTPResponseCode);

		BufferedReader inReader = new BufferedReader(
		        new InputStreamReader(connectionInstance.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = inReader.readLine()) != null) {
			response.append(inputLine);
		}
		inReader.close();

		//print result
		System.out.println(response.toString());

	}

	private String parseJsonParams(String inputJson) throws Exception {
		String jsonString = inputJson;
		
		//parser
		JSONTokener tokener = new JSONTokener(jsonString);

		//object representation of string
		JSONObject res = (JSONObject) tokener.nextValue(); 
		JSONArray resHits = res.getJSONArray("hits");

		//taking top recipe only, since from and to tags are 0, 1
		String recipeName = "default";
		JSONArray ingredients;

		String out = "default";
		String recipeNames[] = new String[resHits.length()];
		int x=0;
		for (int i=0; i<resHits.length();i++){
			JSONObject current = resHits.getJSONObject(i);
			recipeNames[i] = current.getJSONObject("recipe").getString("label");
			
			recipeName = current.getJSONObject("recipe").getString("label");
			ingredients = current.getJSONObject("recipe").getJSONArray("ingredientLines");
			out = "With those ingredients you could make "+recipeName+".";
			for (x=0; x<ingredients.length(); x++){
				out = out + ingredients.getString(x) + ",";
			}
		}

		if (x>0){
			out = out + " are the required ingredients";
		}
		return out;
	}
}
