package com.fyp.searcher.util;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Utility {


    public static String[] su = {"Document","Sentence"};

    public static String[] pos = {"Any","Verb","Noun","Adjective","Adverb","Adposition","Conjunction","Article","Numeral","Particle","Pronoun"};

    public static String[] operator = {"AND","OR","NOT"};

    public static HashMap<String,String> POS_MAP;
    static {
        POS_MAP = new HashMap<>();
        POS_MAP.put("Any","ANY");
        POS_MAP.put("Verb","VERB");
        POS_MAP.put("Noun","NOUN");
        POS_MAP.put("Adjective","ADJ");
        POS_MAP.put("Adverb","ADV");
        POS_MAP.put("Adposition","ADP");
        //POS_MAP.put("Conjunction","CONJ");
        //POS_MAP.put("Article","DET");
        POS_MAP.put("Numeral","NUM");
        POS_MAP.put("Particle","PRT");
        //POS_MAP.put("Pronoun","PRON");
    }

    public static ArrayList<String> convertPOS(ArrayList<String> pos_list){
        return pos_list.stream().map(pos-> POS_MAP.get(pos)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static void alert(Alert.AlertType type, String title, String header, String content){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showMaterialDialog(StackPane root, Node nodeToBeBlurred, List<JFXButton> controls, String header, String body) {
        BoxBlur blur = new BoxBlur(3, 3, 3);
        if (controls.isEmpty()) {
            controls.add(new JFXButton("Okay"));
        }
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(root, dialogLayout, JFXDialog.DialogTransition.TOP);

        controls.forEach(controlButton -> {
            controlButton.getStylesheets().add(Utility.class.getResource("/com/fyp/searcher/style/style.css").toString());
            controlButton.getStyleClass().add("button-raised");
            controlButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent) -> dialog.close());
        });

        dialogLayout.setHeading(new Label(header));
        dialogLayout.setBody(new Label(body));
        dialogLayout.setActions(controls);
        dialog.show();
        dialog.setOnDialogClosed((JFXDialogEvent event1) -> nodeToBeBlurred.setEffect(null));
        nodeToBeBlurred.setEffect(blur);
    }
}
