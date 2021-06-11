package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.network.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class InstructionScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(InstructionScene.class);

    /**
     * The main ane of this scene
     */
    private GridPane gridPane = new GridPane();

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public InstructionScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Instructions Scene");
    }

    /**
     * Initialises the Instructions Scene
     */
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

    /**
     * Build the Instruction window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        //creating and styling the pane
        var instructionsPane = new StackPane();
        instructionsPane.setMaxWidth(gameWindow.getWidth());
        instructionsPane.setMaxHeight(gameWindow.getHeight());
        instructionsPane.getStyleClass().add("menu-background");
        root.getChildren().add(instructionsPane);

        //creating a pane for instructions and pieces
        var mainPane = new BorderPane();
        instructionsPane.getChildren().add(mainPane);
        ImageView instruction = new ImageView(new Image(this.getClass().getResource("/images/Instructions.png").toExternalForm()));
        instruction.setPreserveRatio(true);
        instruction.setFitWidth(650);
        mainPane.setCenter(instruction);

        mainPane.setBottom(gridPane);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        createDynamicPieces();
    }

    /**
     * Creates the 15 game pieces' grids
     */
    public void createDynamicPieces() {
        int i = 0;
        while (i <= 14) {
            int a = 0;
            while (a <= 4) {
                int b = 0;
                while (b <= 2) {
                    gridPane.add(new PieceBoard(GamePiece.createPiece(i), 50, 50), a, b);
                    i++;
                    b++;
                }
                a++;
            }
        }
    }
}
