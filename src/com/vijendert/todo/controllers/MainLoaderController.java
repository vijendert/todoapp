package com.vijendert.todo.controllers;

import com.vijendert.todo.Main;
import com.vijendert.todo.utils.Utils;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
/**
 * This is the class which acts as facade for other 
 * scenes. Namely addTask and schedule.
 * @author vijen
 */
public class MainLoaderController implements Initializable {


    public AnchorPane getLoader_anchor_pane() {
        return loader_anchor_pane;
    }

    @FXML
    AnchorPane loader_anchor_pane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("Jai Ho");
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/addtask.fxml"));
            AnchorPane addTaskPane = loader.load();
            AddTaskController addTaskController = loader.getController();
            System.out.println("Loader: "+addTaskController);
            //storing the controller for addTask scene in utils 
            Utils.storeController("AddTaskController",addTaskController);
            loader = new FXMLLoader(Main.class.getResource("views/view.fxml"));
            AnchorPane viewPane = loader.load();
            ViewController viewController = loader.getController();
             //storing the controller for schedule scene in utils 
            Utils.storeController("ViewController",viewController);
            loader_anchor_pane.getChildren().addAll(addTaskPane,viewPane);
            //we get all the children of this node
            ObservableList<Node> allModules = loader_anchor_pane.getChildren();
            /*
            This pane has 3 children. A JFXSpinner , addTask anchor pane and 
            view anchor pane.
            We start from 2 to make visible false because we want the schedule 
            not to be visible.Rest all visible.
            */
            for(int i = 2;i<allModules.size();i++){
                allModules.get(i).setVisible(false);
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
