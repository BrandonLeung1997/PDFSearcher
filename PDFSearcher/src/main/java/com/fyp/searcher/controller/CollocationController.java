package com.fyp.searcher.controller;

import com.fyp.searcher.API.API;
import com.fyp.searcher.model.Collocation;
import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CollocationController implements Initializable {
    @FXML
    private JFXListView<String> collocationList, selectedCollocationList;

    @FXML
    private Label lblKeyword;

    @FXML
    private JFXSpinner jfxSpinner;

    @FXML
    private FontAwesomeIconView faIcon;

    ArrayList<String> selectedCollocation;

    private Consumer<String> addListener;

    String keyword;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedCollocation = new ArrayList<>();

        faIcon.setVisible(false);

        collocationList.setCellFactory(param -> new CustomCell("Add", selectedCollocationList));
        selectedCollocationList.setCellFactory(param -> new CustomCell("Remove", collocationList));


    }

    public void setCollocationKeyword(String keyword){
        try {
            this.keyword = keyword;
            lblKeyword.setText("Collocation of \"" + this.keyword + "\"");
            API.getCollocation(keyword).thenAcceptAsync(response->{
                Collocation[] collocations = new Gson().fromJson(response.body(), Collocation[].class);
                collocationList.getItems().addAll(Arrays.stream(collocations).map(Collocation::getCollocation).collect(Collectors.toCollection(ArrayList::new)));
                faIcon.setVisible(true);
                jfxSpinner.setVisible(false);
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setAddListener(Consumer<String> addListener){
        this.addListener = addListener;
    }

    public void submit() {
        selectedCollocationList.getItems().forEach(text-> Arrays.stream(text.split(" "))
                .filter(word-> !word.equals(this.keyword))
                .forEach(word->addListener.accept(word)));

        close();
    }

    public void close(){
        Stage stage = (Stage) selectedCollocationList.getScene().getWindow();
        stage.fireEvent(
                new WindowEvent(
                        stage,
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        );
    }

    static class CustomCell extends JFXListCell<String>{
        HBox hBox = new HBox();
        Label collocation_word = new Label("");
        JFXButton btnSelect = new JFXButton();

        public CustomCell(String text, JFXListView<String> listView){
            super();
            Region region1 = new Region();
            HBox.setHgrow(region1, Priority.ALWAYS);
            btnSelect.setText(text);
            btnSelect.getStyleClass().add("button-raised");

            hBox.getChildren().addAll(collocation_word,region1,btnSelect);

            btnSelect.setOnAction(e->{
                listView.getItems().add(getItem());
                getListView().getItems().remove(getItem());
            });

        }

        public void updateItem(String name, boolean empty){
            super.updateItem(name,empty);
            setText(null);
            setGraphic(null);
            if(name != null && !empty){
                collocation_word.setText(name);
                setGraphic(hBox);
            }
        }
    }
}
