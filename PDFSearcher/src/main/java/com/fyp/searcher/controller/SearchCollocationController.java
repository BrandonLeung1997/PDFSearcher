package com.fyp.searcher.controller;

import com.fyp.searcher.API.API;
import com.fyp.searcher.model.Collocation;
import com.fyp.searcher.util.SearchingValidator;
import com.fyp.searcher.util.Utility;
import com.google.gson.Gson;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SearchCollocationController implements Initializable {
    @FXML
    private JFXListView<String> collocationList;

    @FXML
    TextField wordTextField;

    private Consumer<String> addListener;

    @FXML
    private StackPane collocationPane;

    @FXML
    private ScrollPane collocationScrollPane;

    @FXML
    private JFXCheckBox NounCB, VerbCB, AdCB;

    @FXML
    private JFXSpinner CollocationSpinner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        wordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("a-zA-Z*")) {
                wordTextField.setText(newValue.replaceAll("[^a-zA-Z]", ""));
            }
        });
    }


    public void setAddListener(Consumer<String> addListener){
        this.addListener = addListener;
    }
    @FXML
    public void Add() {
        Arrays.stream(collocationList.getSelectionModel().getSelectedItem().split(" ")).forEach(word->addListener.accept(word));
    }
    @FXML
    public void Check() {
        if (wordTextField.getText().isEmpty() || !wordTextField.getText().matches("[a-zA-Z]+")
            || !(NounCB.isSelected() || VerbCB.isSelected() || AdCB.isSelected())
        ) {
            Utility.showMaterialDialog(
                    collocationPane,
                    collocationScrollPane,
                    new ArrayList<>(),
                    "Please input data correctly.",
                    "Please make sure only one word is typed and letters only\n Either one of the POS is selected.");
        }else {

            try {
                Platform.runLater(() -> {
                    CollocationSpinner.setVisible(true);
                    CollocationSpinner.setPrefWidth(Region.USE_COMPUTED_SIZE);
                    CollocationSpinner.setPrefHeight(Region.USE_COMPUTED_SIZE);
                });

                String keyword = wordTextField.getText();
                ArrayList<String> relations = getRelations();

                CompletableFuture.supplyAsync(()->{
                    ArrayList<Collocation> collocations = null;
                    try {
                        collocations = API.getCollocationByPOS(keyword,relations);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    return collocations;
                }).thenAcceptAsync(collocations->{
                    collocationList
                            .getItems()
                            .addAll(collocations.stream()
                                    .map(Collocation::getCollocation)
                                    .collect(Collectors.toCollection(ArrayList::new))
                            );
                    Platform.runLater(() -> {
                        CollocationSpinner.setVisible(true);
                        CollocationSpinner.setPrefWidth(0);
                        CollocationSpinner.setPrefHeight(0);
                    });
                });

//                API.getCollocation(keyword).thenAcceptAsync(response -> {
//                    Collocation[] collocations = new Gson().fromJson(response.body(), Collocation[].class);
//                    collocationList.getItems().addAll(Arrays.stream(collocations).map(Collocation::getCollocation).collect(Collectors.toCollection(ArrayList::new)));
//                    Platform.runLater(() -> {
//                        CollocationSpinner.setVisible(true);
//                        CollocationSpinner.setPrefWidth(0);
//                        CollocationSpinner.setPrefHeight(0);
//                    });
//                });



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<String> getRelations() {
        ArrayList<String> relations = new ArrayList<>();

        if(NounCB.isSelected()){
            relations.addAll(List.of(new String[]{"N:mod:A","N:prep:N","N:nn:N"}));
        }
        if(VerbCB.isSelected()){
            relations.addAll(List.of(new String[]{"V:obj:N","V:prep:N","V:obj1+2:N","V:obj+prep:N","V:subj:N","V:sc:V","V:mod:A"}));
        }
        if(AdCB.isSelected()){
            relations.addAll(List.of(new String[]{"A:mod:A"}));
        }

        return relations;
    }


}
