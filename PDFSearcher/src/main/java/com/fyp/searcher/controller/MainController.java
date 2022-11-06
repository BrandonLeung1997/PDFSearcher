package com.fyp.searcher.controller;

import animatefx.animation.AnimationFX;
import animatefx.animation.Bounce;
import com.dansoftware.pdfdisplayer.PDFDisplayer;
import com.fyp.searcher.model.Coordinate;
import com.fyp.searcher.util.*;
import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.fyp.searcher.model.Keyword;
import com.fyp.searcher.model.Result;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class MainController implements Initializable{

    @FXML
    private TextField corpusTextField;
    @FXML
    private VBox keywordVBox, rightVBox;
    @FXML
    private JFXListView<String> resultListView;
    @FXML
    private ChoiceBox<String> searchScopeChoiceBox;
    @FXML
    private HBox loadingArea;
    @FXML
    private Circle loadingCircle1, loadingCircle2, loadingCircle3;

    @FXML
    private JFXButton searchButton;
    @FXML
    private ScrollPane keywordListScrollPane;

    @FXML
    private StackPane topRootPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label lblPdfName;

    @FXML
    private JFXSpinner pdfLoadingSpinner;

    LinkedList<KeywordController> keys = new LinkedList<>();

    HashMap<String, Result> results = new HashMap<>();

    Socket socket = null;

    boolean searching = false;

    Bounce circle1, circle2, circle3 = null;

    PDFDisplayer displayer = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            keywordVBox.setMinWidth(keywordListScrollPane.getMaxWidth());
            keywordVBox.prefHeightProperty().bind(keywordListScrollPane.prefHeightProperty());

            circle1 = new Bounce(loadingCircle1);
            circle2 = new Bounce(loadingCircle2);
            circle3 = new Bounce(loadingCircle3);
            circle1.setCycleCount(AnimationFX.INDEFINITE).setDelay(Duration.valueOf("100ms"));
            circle2.setCycleCount(AnimationFX.INDEFINITE).setDelay(Duration.valueOf("200ms"));
            circle3.setCycleCount(AnimationFX.INDEFINITE).setDelay(Duration.valueOf("300ms"));

            loadingArea.setVisible(false);

            searchScopeChoiceBox.getItems().addAll(Utility.su);
            searchScopeChoiceBox.setValue(Utility.su[0]);

            displayer = new PDFDisplayer();
            //displayer = new PDFViewer();
            //https://github.com/Dansoftowner/PDFViewerFX
            WebView webView = (WebView) displayer.toNode();
            webView.setPrefHeight(rightVBox.getPrefHeight());
            webView.setPrefWidth(rightVBox.getPrefWidth());
            rightVBox.getChildren().add(webView);

            addKeyword(true,"");

            resultListViewInit();

            socketInit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    Consumer<String> addListener  = text->addKeyword(false,text);

    BiConsumer<StackPane,KeywordController> delListener = ((stackPane, keywordController) -> {
        keys.remove(keywordController);
        keywordVBox.getChildren().remove(stackPane);
    });

    private void addKeyword(boolean isFirst, String keyword){
        try{
            if(!keyword.equals("") && keys.size() != 1 && keys.getFirst().getKeyword().equals(""))
                keys.getFirst().setKeyword(keyword);

            keys.stream()
                    .map(KeywordController::getKeyword)
                    .filter(k->k.equals(keyword) && !k.equals(""))
                    .findAny().orElseGet(()->{
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("/com/fyp/searcher/view/keyword-component-view.fxml"));
                            StackPane stackPane = fxmlLoader.load();

                            KeywordController keywordController = fxmlLoader.getController();
                            keywordController.setData(addListener,delListener,isFirst,keyword);
                            keys.add(keywordController);

                            keywordVBox.getChildren().add(stackPane);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    });



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void search() {
        if(!searching){
            if(isValidated()){
                results.clear();
                socket.connect();
                searching = true;
                searchButton.setText("Stop searching");
                Platform.runLater(() -> resultListView.getItems().clear());
                loadingArea.setVisible(true);

                circle1.play();
                circle2.play();
                circle3.play();


                List<JSONObject> keywordsJSON = keys.stream()
                                                .map(key-> new Keyword(key.getKeyword(),key.getOperator(),Utility.convertPOS(key.getPOS())))
                                                .map(JSONObject::new)
                                                .collect(Collectors.toList());


                JSONObject obj = new JSONObject();
                obj.put("path", corpusTextField.getText());
                //obj.put("path", "C:\\Users\\Yoman\\PycharmProjects\\fyp\\materials");
                obj.put("keywords", keywordsJSON);
                switch (searchScopeChoiceBox.getValue()) {
                    case "Document" -> socket.emit("search_document", obj);
                    case "Sentence" -> socket.emit("search_sentence", obj);
                }
            }
        }else{
            socket.disconnect();
            searchButton.setText("Search");
            loadingArea.setVisible(false);

            circle1.stop();
            circle2.stop();
            circle3.stop();

            searching = false;
        }
    }

    public void chooseCorpus() {
        try{
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(null);
            if(file != null){
                corpusTextField.setText(file.getAbsolutePath());
            }
        }catch (Exception e){e.printStackTrace();}

    }

    public Boolean isValidated() {
        try {

            StringBuilder error_message = new StringBuilder();
            AtomicBoolean validate = new AtomicBoolean(true);
            if (SearchingValidator.isEmpty().apply(corpusTextField.getText()) != SearchingValidator.ValidationResult.SUCCESS) {
                error_message.append("Please select corpus.\n");
                corpusTextField.getStyleClass().add("validation-error");
                validate.set(false);
            } else corpusTextField.getStyleClass().remove("validation-error");

            if (SearchingValidator.isEmpty().apply(searchScopeChoiceBox.getValue()) != SearchingValidator.ValidationResult.SUCCESS) {
                error_message.append("Please select searching scope.\n");
                searchScopeChoiceBox.getStyleClass().add("validation-error");
                validate.set(false);
            } else searchScopeChoiceBox.getStyleClass().remove("validation-error");

            IntStream.range(0, keys.size()).forEach(
                    index -> {
                        keys.get(index).resetStyle();
                        ValidationResult vr = KeywordValidator.all(KeywordValidator.isEmpty(), KeywordValidator.operatorEmpty(), KeywordValidator.posEmpty()).apply(keys.get(index));
                        vr.getReason().ifPresent(ls -> {
                            ls.forEach((token, reason) -> {
                                keys.get(index).addStyle(token);
                                error_message.append("Keyword box ").append(index).append(": ").append(reason).append(".\n");
                            });
                            validate.set(false);
                        });
                    }
            );

            if (!validate.get())
                Utility.showMaterialDialog(topRootPane,gridPane,new ArrayList<>(),"Please input data correctly.", error_message.toString());

            return validate.get();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void reset() {
        try{
            searchScopeChoiceBox.setValue(Utility.su[0]);
            keys.get(0).reset();
            keys.stream().skip(1).collect(Collectors.toSet()).forEach(KeywordController::deleteSection);
        }catch (Exception e){e.printStackTrace();}
    }

    private void socketInit() {
        try {
            socket = SingletonWebSocket.getInstance();

            Consumer<Object[]> post_search = objects ->{
                JSONObject ob = (JSONObject) objects[0];
                JSONArray matchWords= ob.getJSONArray("Words");
                ArrayList<Coordinate> coordinates = IntStream
                        .range(0,matchWords.length())
                        .mapToObj(matchWords::getJSONObject)
                        .map(word -> new Gson().fromJson(word.toString(), Coordinate.class))
                        .collect(Collectors.toCollection(ArrayList::new));
                String fileName = Paths.get((String) ob.get("MatchDocument")).getFileName().toString();
                results.put(fileName,new Result((String) ob.get("MatchDocument"), coordinates));
                Platform.runLater(() -> resultListView.getItems().add(fileName));
            };

            socket.on("document_search", post_search::accept);

            socket.on("sentence_search", post_search::accept);

            socket.on("end_searching", objects -> {
                Platform.runLater(() -> {
                    searchButton.setText("Search");
                    loadingArea.setVisible(false);

                    circle1.stop();
                    circle2.stop();
                    circle3.stop();


                    Utility.showMaterialDialog(topRootPane,gridPane,new ArrayList<>(),"Searching completed message", "Searching completed.");

                });

                searching = false;
            });

            socket.on("highlight", objects -> {
                //String path = "C:\\Users\\Yoman\\PycharmProjects\\fyp\\display\\display.pdf";
                String path = ".\\display\\display.pdf";

                try {

                    displayer.loadPDF(new File(path));
                    Platform.runLater(() -> {
                        pdfLoadingSpinner.setVisible(false);
                        pdfLoadingSpinner.setPrefWidth(0);
                        pdfLoadingSpinner.setPrefHeight(0);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });


            socket.on(Socket.EVENT_CONNECT, objects -> {
                System.out.println("connect to server successful");
                System.out.println("server message: " + Arrays.toString(objects));
            });
            socket.on(Socket.EVENT_CONNECT_ERROR, objects -> System.out.println("client: " + "连接超时"+ Arrays.toString(objects)));
            socket.on(Socket.EVENT_CONNECT_ERROR, objects -> System.out.println("client: " + "连接失败"+ Arrays.toString(objects)));
            socket.on(Socket.EVENT_DISCONNECT, objects -> {
                System.out.println("disconnect to server successful");
                System.out.println("server message: " + Arrays.toString(objects));
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void resultListViewInit(){
        resultListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(!socket.connected())
                socket.connect();
            String selectedFile = resultListView.getSelectionModel().getSelectedItem();
            Result display_result = results.get(selectedFile);
            if(display_result != null){
                lblPdfName.setText(selectedFile);
                Platform.runLater(() -> {
                    pdfLoadingSpinner.setVisible(true);
                    pdfLoadingSpinner.setPrefWidth(Region.USE_COMPUTED_SIZE);
                    pdfLoadingSpinner.setPrefHeight(Region.USE_COMPUTED_SIZE);
                });
                List<JSONObject> coordinates = display_result.getList_coordinate()
                        .stream()
                        .map(coordinate -> new Gson().toJson(coordinate))
                        .map(JSONObject::new)
                        .collect(Collectors.toList());
                JSONObject obj = new JSONObject();
                obj.put("path", display_result.getPath());
                obj.put("list_coordinates", coordinates);
                socket.emit("highlight", obj);
            }
        });
    }

    @FXML
    void checkCollocation(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fyp/searcher/view/search-collocation-view.fxml"));
            Parent parent = loader.load();
            SearchCollocationController scc = loader.getController();
            scc.setAddListener(addListener);

            Stage stage = new Stage();
            stage.setTitle("Collocation");
            stage.setScene(new Scene(parent));


            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @FXML
    void openBrowser(){
        try {
            if (!resultListView.getSelectionModel().isEmpty()) {
                //String path = "C:\\Users\\Yoman\\PycharmProjects\\fyp\\display\\display_highlight.pdf";
                String path = ".\\display\\display_highlight.pdf";
                Desktop.getDesktop().open(new File(path));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    double x, y;

    @FXML
    void closeApp() {
        System.exit(0);
    }

    @FXML
    void minimizeApp() {
        Stage obj = (Stage)topRootPane.getScene().getWindow();
        obj.setIconified(true);
    }

    @FXML
    void fullScreenApp() {
        Stage stage = (Stage)topRootPane.getScene().getWindow();
        if (stage.isFullScreen())
            stage.setFullScreen(false);
        else
            stage.setFullScreen(true);
    }

    @FXML
    void dragged(MouseEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() -x);
        stage.setY(event.getScreenY() -y);
    }

    @FXML
    void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

}
