package OxfordDictionaries;

import OxfordDictionaries.model.*;
import OxfordDictionaries.model.request.Request;
import OxfordDictionaries.view.GameWindow;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {
    private String INPUT_API_APP_ID;
    private String INPUT_APP_KEY;

    @Override
    public void start(Stage primaryStage) {
        setEnvVar();

        List<String> args = getParameters().getUnnamed();
        if (args.size() != 2) {
            System.out.println("Incorrect arguments");
            System.exit(-1);
        }
        InputEngine inputEngine = getInputEngine(args.get(0));
        if (inputEngine == null) {
            System.out.println("Incorrect arguments");
            System.exit(-1);
        }

        OutputEngine outputEngine = getOutputEngine(args.get(1));
        if (outputEngine == null) {
            System.out.println("Incorrect arguments");
            System.exit(-1);
        }

        int width = 800;
        int height = 640;
        GameWindow gameWindow = new GameWindow(width, height, inputEngine, outputEngine);

        primaryStage.setTitle("Oxford Dictionaries");
        primaryStage.setScene(gameWindow.getScene());
        primaryStage.setMinHeight(180);
        primaryStage.setMinWidth(300);
        primaryStage.show();

        gameWindow.draw();
    }

    public InputEngine getInputEngine(String engine) {
        if (engine.equals("offline")) {
            return new OfflineInputEngine();
        }
        else if (engine.equals("online")) {
            return new OnlineInputEngine(new Request(INPUT_API_APP_ID, INPUT_APP_KEY));
        }
        return null;
    }

    public OutputEngine getOutputEngine(String engine) {
        if (engine.equals("offline")) {
            return new OfflineOutputEngine();
        }
        else if (engine.equals("online")) {
            return new OnlineOutputEngine();
        }
        return null;
    }

    public void setEnvVar() {
        String apiId = System.getenv("INPUT_API_APP_ID");
        String appKey = System.getenv("INPUT_API_KEY");
        if (apiId == null || appKey == null) {
            System.out.println("Environment variables not set");
            System.exit(-1);
        }

        this.INPUT_API_APP_ID = apiId;
        this.INPUT_APP_KEY = appKey;
    }

    public static void main(String[] args) {
        launch(args);
    }
}