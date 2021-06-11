package uk.ac.soton.comp1206.scene;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Scores;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;
import java.util.HashMap;

public class ScoresScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ScoresScene.class);

    private HashMap<String,Integer> scores;

    private IntegerProperty currentScore=new SimpleIntegerProperty();

    private Game game;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        scores= Scores.sortMap();
        this.game=game;
    }

    @Override
    public void initialise() {

    }

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        Text currentScore=new Text("Current score: ");
        currentScore.getStyleClass().add("heading");

        Text currentScoreValue= new Text();
        currentScoreValue.getStyleClass().add("heading");

        currentScoreValue.textProperty().bind(game.scoreProperty().asString());
        HBox currentBox=new HBox(currentScore, currentScoreValue);

        TextField textField= new TextField("Enter your name");
        textField.setFont( new Font("SansSerif", 40));
        textField.setMinHeight(100);
        textField.setMaxWidth(500);
        textField.getStyleClass().add("Textfield");
        mainPane.setCenter(textField);
        mainPane.setTop(currentBox);
        currentBox.setAlignment(Pos.CENTER);
        currentBox.setTranslateY(50);

        var vertical=new VBox(20);
        var top= new Text("Top 10 Scores");
        top.getStyleClass().add("title");

        textField.setOnKeyPressed((e)->{
            if(e.getCode()!= KeyCode.ENTER)return;
            mainPane.getChildren().remove(textField);
            mainPane.getChildren().remove(currentBox);
            Scores.addNewScore(textField.getText(), Integer.parseInt(currentScoreValue.getText()));
            scores=Scores.sortMap();
            VBox scoresBox=new VBox(10);
            mainPane.setCenter(scoresBox);
            BorderPane.setAlignment(scoresBox,Pos.CENTER);
            for(String value: scores.keySet()){
                Text text= new Text(value);
                Text text1=new Text(Integer.toString(scores.get(value)));
                text.getStyleClass().add("hiscore");
                text1.getStyleClass().add("hiscore");
                HBox box=new HBox(11, text,text1);
                scoresBox.getChildren().add(box);
                box.setAlignment(Pos.CENTER);
            }
            scoresBox.setAlignment(Pos.CENTER);
            vertical.getChildren().addAll(top,scoresBox);
            vertical.setAlignment(Pos.CENTER);
            mainPane.setCenter(vertical);
        });
    }
}
