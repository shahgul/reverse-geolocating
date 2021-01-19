import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileGrabber extends Application{
    Button button;
    Stage window;
    File file;
    static String s;

    public static void main(String[] args) {
        launch(args);
        System.out.println(s);
    }

    @Override
    //main javafx code
    public void start(Stage primaryStage){
        window = primaryStage;
        primaryStage.setTitle("Geolocating Window");
        button = new Button();
        button.setText("Click Me");
        FileChooser fileChooser = new FileChooser();
        //File selectedFile;
        button.setOnAction(event -> {
            file = fileChooser.showOpenDialog(window);
            System.out.println(file);
            s = file.getPath();
        });
        StackPane layout = new StackPane();
        layout.getChildren().add(button);
        Scene scene = new Scene(layout, 300, 250);
        window.setScene(scene);
        window.show();
        /*
        //using lambda
        button.setOnAction(e -> System.out.println("bchg"));
        //this class hadles the event (is an anonymous inner class)
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            System.out.println("bchg");
            }
        });*/
    }
}