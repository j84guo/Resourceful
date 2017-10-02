//import required Java libraries
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

//main class for API communication, same name as .java file
//sample Alcohol API, lol
public class Alcohol {

	//class fields, simulating browser
	private final String USER_AGENT = "Mozilla/5.0";

	//main loop, starting point for JVM
	public static void main(String[] args) throws Exception {
		
		//instance of main class
		Alcohol http = new Alcohol();

		System.out.println("1) Send HTTP GET to API");
		http.sendGet();

		System.out.println("2) Send HTTP POST to API");
		http.sendPost();

	}

	//function for HTTP GET request
	private void sendGet() throws Exception { //need to throw exception to comply with Java's Catch or Specify requirement
		
		/*
		TODO:
		insert parameters for web service, all are hardcoded atm
		*/

		//endpoint basename
		String url = "http://addb.absolutdrinks.com/drinks/absolut-cosmopolitan/?apiKey=9cdfee635b854c3792ac51c4957d61c6";

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

		//store respones to string
		while ((inputLine = inReader.readLine()) != null) {
			response.append(inputLine);
		}
		inReader.close();

		//Output to out stream
		System.out.println(response.toString());

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

		//add reuqest header
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

}