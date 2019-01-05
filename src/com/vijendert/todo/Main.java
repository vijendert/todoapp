package com.vijendert.todo;

import com.vijendert.todo.controllers.MainLoaderController;
import com.vijendert.todo.utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        mainViewInitialize();
    }

    /**
     * This is where mainloader.fxml is loaded mainloader.fxml is the one which
     * acts like a facade, a holder for other scenes. As it is loaded other
     * scenes are also loaded.
     */
    private void mainViewInitialize() {
        try {
            System.out.println("Jai Ganesh Sai");
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/mainloader.fxml"));
            AnchorPane pane = loader.load();
            MainLoaderController mainLoaderController = loader.getController();
            //We store the controllers to retrieve later
            Utils.storeController("MainLoaderController", mainLoaderController);
            Scene scene = new Scene(pane);
            /**
             * Adding stylesheet to the application The important thing here is
             * using Main.class we are setting the context path where the main
             * file is . So the path is relative to where the main file is
             * located.
             */
            scene.getStylesheets().addAll(Main.class.getResource("style.css").toExternalForm());
            stage.setTitle("GS_TODO");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
