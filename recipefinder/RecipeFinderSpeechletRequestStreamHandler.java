package recipefinder;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public final class RecipeFinderSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds = new HashSet<String>();
    static {
        //Alexa application ID goes here - developer portal
        supportedApplicationIds.add("your application ID goes here");
    }

    public RecipeFinderSpeechletRequestStreamHandler() {
        super(new RecipeFinderSpeechlet(), supportedApplicationIds);
    }
}
