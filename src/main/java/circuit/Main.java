package circuit;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main {
    public static void main(String[] args) {

        //com.sun.javafx.application.PlatformImpl.startup(()->{});

        Application a = new Application() {
            @Override
            public void start(Stage primaryStage) throws Exception {

            }
        };
        Editor.main(args);
    }
}
