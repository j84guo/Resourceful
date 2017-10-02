# echacks

Using Amazon Alexa smart speaker to retrieve useful cooking recipes when you don't know what to cook!!

Authors: Deniza, Alan, Jackson

## To Run

- Clone the repository

### AWS Lambda setup

- Create a jar file of lambda-java-example folder - This will be uploaded to AWS Lambda function
- Create a new AWS Lambda function on AWS console
- Make sure you select us-east region
- Select the runtime as Java 8
- Upload the jar file
- for the handler section, set it to **recipefinder.RecipeFinderSpeechletRequestStreamHandler**
- Click submit and copy the ARN from the top right corner

### Amazon developer console setup

- Add a new skill under Alexa skills kit
- set the invocation name as **recipe finder**
- copy over the intent schema and sample utterances from the repo
- select AWS Lambda ARN and copy your ARN number from AWS
- Enable testing
- Copy your application ID from skill information section
- Go to RecipeFinderSpeechletRequestStreamHandler.java file and paste your application ID over there
- Recompile your jar file and upload to Lambda function again
- Ready to go

## How to use
User: Alexa, start Recipe Finer
Alexa: Welcome to Recipe Finder....
User: Kale, beef, and tomato
Alexa: recipe.....
