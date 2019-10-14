package com.stackroute;


import com.google.api.gax.rpc.BidiStream;
import com.google.cloud.dialogflow.v2.*;
import com.google.protobuf.ByteString;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;
import ws.schild.jave.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class DemoClass {
//    static void detectIntentStream(String projectId, String audioFilePath, String sessionId) {
//        // String projectId = "YOUR_PROJECT_ID";
//        // String audioFilePath = "path_to_your_audio_file";
//        // Using the same `sessionId` between requests allows continuation of the conversation.
//        // String sessionId = "Identifier of the DetectIntent session";
//
//        // Instantiates a client
//        try (SessionsClient sessionsClient = SessionsClient.create()) {
//            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
//            SessionName session = SessionName.of(projectId, sessionId);
//
//            // Instructs the speech recognizer how to process the audio content.
//            // Note: hard coding audioEncoding and sampleRateHertz for simplicity.
//            // Audio encoding of the audio content sent in the query request.
//            InputAudioConfig inputAudioConfig = InputAudioConfig.newBuilder()
//                    .setAudioEncoding(AudioEncoding.AUDIO_ENCODING_LINEAR_16)
//                    .setLanguageCode("en-US") // languageCode = "en-US"
//                    .setSampleRateHertz(16000) // sampleRateHertz = 16000
//                    .build();
//
//            // Build the query with the InputAudioConfig
//            QueryInput queryInput = QueryInput.newBuilder().setAudioConfig(inputAudioConfig).build();
//
//            // Create the Bidirectional stream
//            BidiStream<StreamingDetectIntentRequest, StreamingDetectIntentResponse> bidiStream =
//                    sessionsClient.streamingDetectIntentCallable().call();
//
//            // The first request must **only** contain the audio configuration:
//            bidiStream.send(StreamingDetectIntentRequest.newBuilder()
//                    .setSession(session.toString())
//                    .setQueryInput(queryInput)
//                    .build());
//
//            try (FileInputStream audioStream = new FileInputStream(audioFilePath)) {
//                // Subsequent requests must **only** contain the audio data.
//                // Following messages: audio chunks. We just read the file in fixed-size chunks. In reality
//                // you would split the user input by time.
//                byte[] buffer = new byte[4096];
//                int bytes;
//                while ((bytes = audioStream.read(buffer)) != -1) {
//                    bidiStream.send(
//                            StreamingDetectIntentRequest.newBuilder()
//                                    .setInputAudio(ByteString.copyFrom(buffer, 0, bytes))
//                                    .build());
//                }
//            }
//
//            // Tell the service you are done sending data
//            bidiStream.closeSend();
//
//            for (StreamingDetectIntentResponse response : bidiStream) {
//                QueryResult queryResult = response.getQueryResult();
//                System.out.println("====================");
//                System.out.format("Intent Display Name: %s\n", queryResult.getIntent().getDisplayName());
//                System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
//                System.out.format("Detected Intent: %s (confidence: %f)\n",
//                        queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
//                System.out.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText());
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public static void main(String[] args) throws Exception {
        File source = new File("source.mp4");
        File target = new File("target.wav");
        AudioAttributes audio = new AudioAttributes();
        audio.setChannels(1);
        audio.setCodec("pcm_s16le");
        audio.setSamplingRate(16000);
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("wav");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder();
        encoder.encode(new MultimediaObject(source), target, attrs);
        DemoClass obj = new DemoClass();

        try{
        File soundFile = new File("target.wav");
        WavFile inputWavFile = WavFile.openWavFile(soundFile);
            System.out.println("splitting...............");

        // Get the number of audio channels in the wav file
        int numChannels = inputWavFile.getNumChannels();
        // set the maximum number of frames for a target file,
        // based on the number of milliseconds assigned for each file
        int maxFramesPerFile = (int) inputWavFile.getSampleRate() * 25000 / 1000;

        // Create a buffer of maxFramesPerFile frames
        double[] buffer = new double[maxFramesPerFile * numChannels];

        int framesRead;
        int fileCount = 0;
        do {
            // Read frames into buffer
            framesRead = inputWavFile.readFrames(buffer, maxFramesPerFile);
            WavFile outputWavFile = WavFile.newWavFile(
                    new File("out" + (fileCount + 1) + ".wav"),
                    inputWavFile.getNumChannels(),
                    framesRead,
                    inputWavFile.getValidBits(),
                    inputWavFile.getSampleRate());

            // Write the buffer
            outputWavFile.writeFrames(buffer, framesRead);
            outputWavFile.close();
//            detectIntentStream("speechtotext-pghvqu","out.wav","123456789");
            obj.detectIntentAudio("speechtotext-pghvqu","out" + (fileCount + 1) + ".wav","123456789","en-US");
            fileCount++;
        } while (framesRead != 0);

        // Close the input file
        inputWavFile.close(); obj.detectIntentAudio("speechtotext-pghvqu","out" + (fileCount + 1) + ".wav","123456789","en-US");

    } catch (Exception e) {
        System.err.println(e);
    }
//        Configuration configuration = new Configuration();
//
//        // Load model from the jar
//        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
//        // You can also load model from folder
//        // configuration.setAcousticModelPath("file:en-us");
//        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
//        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
//
//        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
//        InputStream stream = new FileInputStream("source1.wav");
//        stream.skip(44);
//
//        // Simple recognition with generic model
//        recognizer.startRecognition(stream);
//        SpeechResult result;
//        while ((result = recognizer.getResult()) != null) {
//
//            System.out.format("Hypothesis: %s\n", result.getHypothesis());
//
//            System.out.println("List of recognized words and their times:");
//            for (WordResult r : result.getWords()) {
//                System.out.println(r);
//            }
//
//            System.out.println("Best 3 hypothesis:");
//            for (String s : result.getNbest(3))
//                System.out.println(s);
//
//        }
//        recognizer.stopRecognition();

    }
    public QueryResult detectIntentAudio(
            String projectId,
            String audioFilePath,
            String sessionId,
            String languageCode)
            throws Exception {
        // Instantiates a client
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);
            System.out.println("Session Path: " + session.toString());

            // Note: hard coding audioEncoding and sampleRateHertz for simplicity.
            // Audio encoding of the audio content sent in the query request.
            AudioEncoding audioEncoding = AudioEncoding.AUDIO_ENCODING_LINEAR_16;
            int sampleRateHertz = 16000;

            // Instructs the speech recognizer how to process the audio content.
            InputAudioConfig inputAudioConfig = InputAudioConfig.newBuilder()
                    .setAudioEncoding(audioEncoding) // audioEncoding = AudioEncoding.AUDIO_ENCODING_LINEAR_16
                    .setLanguageCode(languageCode) // languageCode = "en-US"
                    .setSampleRateHertz(sampleRateHertz) // sampleRateHertz = 16000
                    .build();

            // Build the query with the InputAudioConfig
            QueryInput queryInput = QueryInput.newBuilder().setAudioConfig(inputAudioConfig).build();

            // Read the bytes from the audio file
            byte[] inputAudio = Files.readAllBytes(Paths.get(audioFilePath));

            // Build the DetectIntentRequest
            DetectIntentRequest request = DetectIntentRequest.newBuilder()
                    .setSession(session.toString())
                    .setQueryInput(queryInput)
                    .setInputAudio(ByteString.copyFrom(inputAudio))
                    .build();

            // Performs the detect intent request
            DetectIntentResponse response = sessionsClient.detectIntent(request);

            // Display the query result
            QueryResult queryResult = response.getQueryResult();
            System.out.println("====================");
            System.out.format("Query Text: '%s'\n", queryResult.getQueryTextBytes().toStringUtf8());
            return queryResult;
        }
    }
}
