package uk.ac.soton.comp1206.scene;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class MultilplayerScene extends BaseScene{

    private static final Logger logger= LogManager.getLogger(MultilplayerScene.class);
    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public MultilplayerScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initialise() {
        //pressing ESC key sends the user to the menu screen
        scene.setOnKeyPressed((e) -> {
            if (e.getCode() != KeyCode.ESCAPE) return;
            logger.info("Going back to BYE BYE screen");
            Multimedia.stop();
            gameWindow.startMenu();
        });
    }

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        //the big pane setup
        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        //the main pane setup
        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        var onMyWay=new ImageView(new Image(this.getClass().getResource("/images/work.jpg").toExternalForm()));
        onMyWay.setPreserveRatio(true);
        onMyWay.setFitWidth(gameWindow.getWidth());

        mainPane.setCenter(onMyWay);
    }
}
