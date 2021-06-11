package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build(){
        logger.info("Building " + this.getClass().getName());
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //outputting the title
        ImageView title=new ImageView(this.getClass().getResource("/images/TetrECS.png").toExternalForm());
        title.setPreserveRatio(true);
        title.setFitWidth(600);
        animateTitle(title);


        var titleBox=new HBox(title);
        mainPane.setTop(titleBox);
        titleBox.setAlignment(Pos.CENTER);

        //creating a box for the menu buttons
        var menu=new VBox(19);

        var play = new Text("Play");
        var multiplayer = new Text("Multiplayer");
        var instructions = new Text("How To Play");
        var exit = new Text("Exit");

        //styling the text labels using a css file
        play.getStyleClass().add("menuItem");
        multiplayer.getStyleClass().add("menuItem");
        instructions.getStyleClass().add("menuItem");
        exit.getStyleClass().add("menuItem");
        menu.getChildren().addAll(play,multiplayer,instructions,exit);
        menu.setAlignment(Pos.CENTER);
        mainPane.setCenter(menu);

        //Bind the mouse action to the startGame method in the menu
        play.setOnMouseClicked(this::startGame);

        //Bind the mouse action to the shutDown method in menu
        exit.setOnMouseClicked(this::shutDown);

        //Bind the mouse action to the start instruction in menu
        instructions.setOnMouseClicked(this::startInstruction);

        //Bind the mouse action to the multiplayer in menu
        multiplayer.setOnMouseClicked(this::startMultiplayer);

    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        Multimedia.playBackgroundMusic("music.mp3");
        scene.setOnKeyPressed((e) -> {
            if(e.getCode() != KeyCode.ESCAPE) return;
            logger.info("Going back to BYE BYE screen");
            gameWindow.shutdown();
        });
    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(MouseEvent event) {
        Multimedia.stop();
        Multimedia.playBackgroundMusic("intro.mp3");
        gameWindow.startChallenge();
    }

    /**
     * Handle when Exit is pressed
     * @param event
     */
    private void shutDown(MouseEvent event){
        gameWindow.shutdown();
    }

    /**
     * Handle opening the Instruction scene
     * @param event
     */
    private void startInstruction(MouseEvent event){
        gameWindow.instructionsScene();
    }

    private void startMultiplayer(MouseEvent event){gameWindow.startMultiplayer();}

    /**
     * Applies a fade transition and a scale transition to the image view passed
     * @param imageView
     */
    private void animateTitle(ImageView imageView){
        imageView.setTranslateY(20);
        FadeTransition fadeTransition=new FadeTransition(new Duration(3000),imageView);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
        imageView.setEffect(new DropShadow());
        ScaleTransition scaleTransition=new ScaleTransition(new Duration(5000),imageView);
        scaleTransition.setToX(1.3);
        scaleTransition.play();
    }

}
