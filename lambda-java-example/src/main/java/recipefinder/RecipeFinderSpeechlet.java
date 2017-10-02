package recipefinder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;



//Handle speechlet request

public class RecipeFinderSpeechlet implements Speechlet {

	private static final String ITEM_SLOT = "Recipe";
	private final String USER_AGENT = "Mozilla/5.0";
    private static final Logger log = LoggerFactory.getLogger(RecipeFinderSpeechlet.class);

    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any initialization logic goes here
    }

    //When the skill gets launhed
    //when the user says "Alexa, start 'Recipe Finder' "
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        String speechOutput = "Welcome to Recipe Finder. You can list all the ingredients you have at your kitchen,"
                + "and I will help you find the best recipe..."
                + "Now, what ingredients do you have?";

        //If the user does not reply or says something that is hard to understand
        String repromptText = "For instructions on what you can say, please say help me.";

        return newAskResponse(speechOutput, repromptText);

    }

    //When Alexa gets the input from the user -- "kale, beef, garlic"
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        //If the user is giving the correct intent input
        if ("GetRecipeIntent".equals(intentName)) {
            return GetRecipeResponse(intent);
        }
        //If the user needs help
        else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelp();
        }
        //If the user says stop
        else if ("AMAZON.StopIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Bye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        }
        //If the user cancels the request
        else if ("AMAZON.CancelIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Bye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        }
        //Error handling for invalid input
        else {
            throw new SpeechletException("Invalid Intent");
        }
    }


    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any cleanup logic goes here
    }


    /**
    * logic for getting the recipes
    * SpeechletResponse: defines the text to speak to the user
     * @throws Exception
    */

    private SpeechletResponse GetRecipeResponse(Intent intent) {
        //String response = sendGet("cheese,vegan");
        //String message = parseJsonParams(response);

        Slot itemSlot = intent.getSlot(ITEM_SLOT);
        String input = "";
        if (itemSlot != null && itemSlot.getValue() != null) {
          input = itemSlot.getValue();
        }
    	log.info(input);

    	//Replace the space with comma for the query param
    	input = input.replace(" ", ",");

    	String response, message = "", link = "";

    	try{
        	response = sendGet(input);
        	message = parseJsonParams(response);
            link = extractMoreInfo(response);
    	}catch (Exception e){
    		e.printStackTrace();
    		message = "caught a network exception";
    	};

    	//If the message is not empty
    	if (message != null){
            SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
            outputSpeech.setSsml(message);

            //setup card for debugging
            SimpleCard card = new SimpleCard();
            card.setTitle("User Input" + link);
            card.setContent(input);

            return SpeechletResponse.newTellResponse(outputSpeech, card);
    	}
    	//If it cannot find the recipe
    	else{
            String speechOutput =
                    "I'm sorry, I couldn't find that recipe for you right now"
            		+ "What else do you need help with?";
            String repromptSpeech = "What else can I help with?";
            return newAskResponse(speechOutput, repromptSpeech);
    	}

    }


    //function for HTTP GET request
    private String sendGet(String q) throws Exception { //need to throw exception to comply with Java's Catch or Specify requirement

        //TODO:
        //insert parameters for web service, all are hardcoded atm

        String query = q;

        //endpoint basename
        String url = "https://api.edamam.com/search?q="+query+"&app_id=ee303027&app_key=81e0095fb042c3fbd7f27524f77c0ac4&from=0&to=1&calories=gte%20591,%20lte%20722&health=alcohol-free";
        //String url = "http://addb.absolutdrinks.com/drinks/absolut-cosmopolitan/?apiKey=9cdfee635b854c3792ac51c4957d61c6";

        //url object
        URL urlObject = new URL(url);


        //openConnection() returns a URLConnection instance that represents a connection to the remote object referred to by the URL,
        //then casts the return instance to type HttpURLConnection.

        //parses the URL, finds the protocol, and creates the HttpURLConnection object
        HttpURLConnection connectionInstance = (HttpURLConnection) urlObject.openConnection();

        //HTTP method is GET
        connectionInstance.setRequestMethod("GET");

        //set the http headers and all that bullshit
        connectionInstance.setRequestProperty("User-Agent", USER_AGENT); //identify client specs to server, in this case simulate 'Mozilla/5.0'


        //Pretty sure this line is redundant since, as Java Docs put it:
        //"Operations that depend on being connected will implicitly perform the connection, if necessary."
        //i.e. that geResponseCode shit below

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
        JSONObject current = resHits.getJSONObject(0);
        //out = current.getJSONObject("recipe").getString("label");

        String recipeNames[] = new String[resHits.length()];
        int x=0;
        for (int i=0; i<resHits.length();i++){
            current = resHits.getJSONObject(i);
            recipeNames[i] = current.getJSONObject("recipe").getString("label");

            recipeName = current.getJSONObject("recipe").getString("label");
            ingredients = current.getJSONObject("recipe").getJSONArray("ingredientLines");
            out = "<speak>With those ingredients you could make "+recipeName+".";
						out = out + " The full list of ingredients you will need for this dish are ";
            for (x=0; x<ingredients.length(); x++){
                out = out + "<break time=\"0.3s\"/>" + ingredients.getString(x) + ",";
            }
        }

        if (x>0){
            out = out + "</speak>";
        }

        return out;
    }

    //get url to steps
    private String extractMoreInfo(String inputJson) throws Exception {
        String jsonString = inputJson;
        //parser
        JSONTokener tokener = new JSONTokener(jsonString);
        //object representation of string
        JSONObject res = (JSONObject) tokener.nextValue();
        JSONArray resHits = res.getJSONArray("hits");
        String out = "";
        JSONObject current = resHits.getJSONObject(0);
        out = current.getJSONObject("recipe").getString("shareAs");
        return out;
    }

    //When the user needs help
    private SpeechletResponse getHelp(){
        String speechOutput = "You only need to list all the items,"
            + "for example, you can say kale, beef, garlic,"
            + "Now, what do you have at your kitchen?";

        String repromptText = "Or you can just say one word kale,"
            + "or exit... Now, what do you have at your kitchen?";

        return newAskResponse(speechOutput, repromptText);
    }

    //Just for prompting user for response and reprompt if they don't answer
    private SpeechletResponse newAskResponse(String stringOutput, String repromptText) {
           PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
           outputSpeech.setText(stringOutput);

           PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech();
           repromptOutputSpeech.setText(repromptText);
           Reprompt reprompt = new Reprompt();
           reprompt.setOutputSpeech(repromptOutputSpeech);

           return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
       }
}
