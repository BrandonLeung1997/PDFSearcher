package com.fyp.searcher.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import com.fyp.searcher.util.Utility;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class KeywordController  {

    @FXML
    private JFXCheckBox AdjCB, AdpCB, AdvCB, AnyCB, NounCB, NumCB, ParCB, VerbCB;

    @FXML
    private GridPane posGrid;

    @FXML
    private JFXButton deleteButton;

    @FXML
    private TextField keywordTextField;

    @FXML
    private StackPane rootPane;

    @FXML
    private HBox hBoxPane;

    @FXML
    private ChoiceBox<String> operatorChoiceBox;

    private ArrayList<JFXCheckBox> selectedPOS;


    @FXML
    private void add(){
        addListener.accept("");
    }

    @FXML
    private void delete(){
        deleteListener.accept(rootPane,this);
    }

    private Consumer<String> addListener;
    private BiConsumer<StackPane,KeywordController> deleteListener;

    public void setData(Consumer<String> addListener, BiConsumer<StackPane,KeywordController> deleteListener, boolean isFirst, String keyword){
        keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("a-zA-Z*")) {
                keywordTextField.setText(newValue.replaceAll("[^a-zA-Z]", ""));
            }
        });

        this.addListener = addListener;
        this.deleteListener = deleteListener;
        if (isFirst) {
            this.deleteButton.setVisible(false);
            operatorChoiceBox.getItems().addAll(Utility.operator[0], Utility.operator[2]);
        }else
            operatorChoiceBox.getItems().addAll(Utility.operator);
        operatorChoiceBox.setValue(Utility.operator[0]);

        if (!keyword.isEmpty()) {
            keywordTextField.setText(keyword);
        }

        selectedPOS = new ArrayList<>();
        selectedPOS.add(AdjCB);
        selectedPOS.add(AdpCB);
        selectedPOS.add(AdvCB);
        selectedPOS.add(NounCB);
        selectedPOS.add(NumCB);
        selectedPOS.add(ParCB);
        selectedPOS.add(VerbCB);
    }

    public void addStyle(String type){
        switch (type){
            case "KEYWORD" -> keywordTextField.getStyleClass().add("validation-error");
            case "OPERATOR" -> operatorChoiceBox.getStyleClass().add("validation-error");
            case "POS" -> posGrid.getStyleClass().add("validation-error");
        }
    }

    @FXML
    void anyClick() {
        if(AnyCB.isSelected()){
            AdjCB.setSelected(true);
            AdpCB.setSelected(true);
            AdvCB.setSelected(true);
            NounCB.setSelected(true);
            NumCB.setSelected(true);
            ParCB.setSelected(true);
            VerbCB.setSelected(true);
        }else{
            AdjCB.setSelected(false);
            AdpCB.setSelected(false);
            AdvCB.setSelected(false);
            NounCB.setSelected(false);
            NumCB.setSelected(false);
            ParCB.setSelected(false);
            VerbCB.setSelected(false);
        }
    }

    @FXML
    void openCollocationWindow(){
        try {
            if (this.getKeyword() != null && !this.getKeyword().trim().isEmpty()){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fyp/searcher/view/collocation-view.fxml"));
                Parent parent = loader.load();

                CollocationController cc = loader.getController();
                cc.setAddListener(addListener);
                cc.setCollocationKeyword(this.getKeyword());
                Stage stage = new Stage(StageStyle.UNDECORATED);
                stage.setTitle("Collocation");
                stage.setScene(new Scene(parent));

                Rectangle2D screenBounds = Screen.getPrimary().getBounds();
                stage.setMaxWidth(screenBounds.getWidth()/3);
                stage.setMaxHeight(screenBounds.getHeight()/3);
                stage.setWidth(screenBounds.getWidth()/2);
                stage.setHeight(screenBounds.getHeight()/2);


                Scene scene = rootPane.getScene();
                Node nodeToFind = scene.lookup("#topRootPane");
                BoxBlur blur = new BoxBlur(3, 3, 3);
                nodeToFind.setEffect(blur);
                nodeToFind.setDisable(true);
                stage.setOnCloseRequest(windowEvent->{nodeToFind.setEffect(null);nodeToFind.setDisable(false);});

                stage.show();
            }else{
                Utility.showMaterialDialog(rootPane,hBoxPane,new ArrayList<>(),"Please input data correctly.", "Please input keyword before searching collocation.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void resetStyle(){
        keywordTextField.getStyleClass().remove("validation-error");
        operatorChoiceBox.getStyleClass().remove("validation-error");
        posGrid.getStyleClass().remove("validation-error");
    }

    public String getKeyword(){
        return keywordTextField.getText();
    }

    public void setKeyword(String text){
         keywordTextField.setText(text);
    }

    public ArrayList<String> getPOS(){
        return selectedPOS.stream().filter(CheckBox::isSelected).map(Labeled::getText).collect(Collectors.toCollection(ArrayList::new));
    }

    public String getOperator(){
        return operatorChoiceBox.getValue();
    }

    public void deleteSection() {delete();}

    public void reset() {
        keywordTextField.setText("");
        operatorChoiceBox.setValue(Utility.operator[0]);
        AnyCB.setSelected(false);
        selectedPOS.forEach(cb->cb.setSelected(false));
    }

}
