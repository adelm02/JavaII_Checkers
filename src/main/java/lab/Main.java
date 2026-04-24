package lab;

import cz.vsb.checkers.CheckersApiApplication;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Objects;

@Log
public class Main extends Application {

    private static final int MENU_WIDTH = 500;
    private static final int MENU_HEIGHT = 500;
    private static final int LOGIN_WIDTH = 500;
    private static final int LOGIN_HEIGHT = 350;
    private static final int STATS_WIDTH = 800;
    private static final int STATS_HEIGHT = 600;
    private static final int GAME_WINDOW_WIDTH = 850;
    private static final int GAME_WINDOW_HEIGHT = 1000;
    private static final int BOARD_SIZE = 800;
    private static final String SAVE_FILE = "savedGame.bin";

    private static ConfigurableApplicationContext springContext;

    private DataManager dataManager;
    private Stage primaryStage;

    public static void main(String[] args) {
        springContext = new SpringApplicationBuilder(CheckersApiApplication.class)
                .headless(false)
                .run(args);
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.dataManager = new DataManager();
        primaryStage.setTitle("Checkers");
        showMainMenu();
        primaryStage.show();
        log.info("Application started");
    }

    @Override
    public void stop() {
        if (dataManager != null) dataManager.close();
        if (springContext != null) springContext.close();
    }

    private void showMainMenu() {
        VBox root = createLayout();
        Label title = new Label("Checkers");
        title.getStyleClass().add("title-label");

        root.getChildren().addAll(
                title,
                createButton("Nová hra", "button-action", e -> showLoginScreen()),
                createButton("Načíst hru", "button", e -> loadGameAction()),
                createButton("Statistiky", "button", e -> showStatistics()),
                createButton("Konec", "button-cancel", e -> primaryStage.close())
        );
        switchScene(root, MENU_WIDTH, MENU_HEIGHT);
    }

    private void showLoginScreen() {
        VBox root = createLayout();
        Label title = new Label("Přihlášení hráčů");
        title.getStyleClass().add("subtitle-label");

        TextField whiteName = new TextField();
        whiteName.setPromptText("Bílý hráč");
        TextField blackName = new TextField();
        blackName.setPromptText("Černý hráč");

        Button loginBtn = createButton("Hrát", "button-action", e -> {
            String w = whiteName.getText().trim();
            String b = blackName.getText().trim();
            if (w.isEmpty() || b.isEmpty() || w.equals(b)) {
                showAlert("Chyba", "Zadejte dvě různá jména.");
                return;
            }
            try {
                startGame(dataManager.loginPlayer(w), dataManager.loginPlayer(b), null);
            } catch (RuntimeException ex) {
                log.severe("REST error: " + ex.getMessage());
                showAlert("Chyba", "Nepodařilo se komunikovat s REST API.");
            }
        });

        root.getChildren().addAll(
                title,
                new Label("Bílý:"), whiteName,
                new Label("Černý:"), blackName,
                loginBtn,
                createButton("Zpět", "button-cancel", e -> showMainMenu())
        );
        switchScene(root, LOGIN_WIDTH, LOGIN_HEIGHT);
    }

    private void showStatistics() {
        TabPane tabs;
        try {
            tabs = new TabPane();
            tabs.getTabs().add(createTab("Top Hráči", createPlayersTable()));
            tabs.getTabs().add(createTab("Historie", createHistoryTable()));
        } catch (RuntimeException e) {
            log.severe("Stats error: " + e.getMessage());
            showAlert("Chyba", "Nepodařilo se načíst statistiky z REST API.");
            return;
        }

        VBox root = createLayout();
        root.getChildren().addAll(tabs, createButton("Zpět", "button-cancel", e -> showMainMenu()));
        switchScene(root, STATS_WIDTH, STATS_HEIGHT);
    }

    private void startGame(Player white, Player black, GameModel loadedModel) {
        Label infoLabel = new Label();
        infoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");

        Board board = new Board(BOARD_SIZE, BOARD_SIZE, infoLabel, loadedModel, white, black, dataManager);

        VBox root = createLayout();
        root.getChildren().addAll(infoLabel, board, createButton("Ukončit hru", "button-cancel", e -> handleExit(board)));
        switchScene(root, GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT);
    }

    private void loadGameAction() {
        Image w = loadImage("/images/white.png");
        Image b = loadImage("/images/black.png");
        Image qw = loadImage("/images/qeenW.png");
        Image qb = loadImage("/images/qeenB.png");

        GameModel model = GameModel.loadGame(SAVE_FILE, w, b, qw, qb,
                msg -> showAlert("Info", msg),
                win -> {}, dataManager);
        if (model != null) startGame(null, null, model);
    }

    private void handleExit(Board board) {
        if (board.isGameEnded()) {
            showMainMenu();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ukončit hru");
        alert.setHeaderText("Hra probíhá");
        alert.setContentText("Chcete hru uložit před ukončením?");

        ButtonType yes = new ButtonType("Ano");
        ButtonType no = new ButtonType("Ne");
        ButtonType cancel = new ButtonType("Zrušit", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yes, no, cancel);
        styleAlert(alert);

        alert.showAndWait().ifPresent(type -> {
            if (type == yes) {
                board.getGameModel().saveGame(SAVE_FILE);
                showMainMenu();
            } else if (type == no) {
                showMainMenu();
            }
        });
    }

    private TableView<Player> createPlayersTable() {
        TableView<Player> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Player, String> name = new TableColumn<>("Jméno");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Player, Integer> wins = new TableColumn<>("Výhry");
        wins.setCellValueFactory(new PropertyValueFactory<>("gamesWon"));

        TableColumn<Player, String> rate = new TableColumn<>("Úspěšnost");
        rate.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.1f %%", c.getValue().getWinRate())));

        table.getColumns().addAll(name, wins, rate);
        table.getItems().addAll(dataManager.getTopPlayers(10));
        return table;
    }

    private TableView<GameResult> createHistoryTable() {
        TableView<GameResult> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<GameResult, String> w = new TableColumn<>("Bílý");
        w.setCellValueFactory(new PropertyValueFactory<>("whitePlayerName"));

        TableColumn<GameResult, String> b = new TableColumn<>("Černý");
        b.setCellValueFactory(new PropertyValueFactory<>("blackPlayerName"));

        TableColumn<GameResult, String> win = new TableColumn<>("Vítěz");
        win.setCellValueFactory(new PropertyValueFactory<>("winner"));

        TableColumn<GameResult, String> time = new TableColumn<>("Čas");
        time.setCellValueFactory(c -> {
            long secs = c.getValue().getGameDurationSeconds();
            return new SimpleStringProperty(String.format("%d:%02d", secs / 60, secs % 60));
        });

        table.getColumns().addAll(w, b, win, time);
        table.getItems().addAll(dataManager.getAllResults());
        return table;
    }

    private VBox createLayout() {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new javafx.geometry.Insets(20));
        return box;
    }

    private Button createButton(String text, String styleClass,
                                javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button btn = new Button(text);
        btn.getStyleClass().add(styleClass);
        btn.setOnAction(action);
        return btn;
    }

    private Tab createTab(String title, Control content) {
        Tab tab = new Tab(title, content);
        tab.setClosable(false);
        return tab;
    }

    private void switchScene(Parent root, int width, int height) {
        Scene scene = new Scene(root, width, height);
        try {
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        } catch (Exception ignored) {}
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        styleAlert(alert);
        alert.showAndWait();
    }

    private void styleAlert(Alert alert) {
        try {
            alert.getDialogPane().getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
            alert.getDialogPane().getStyleClass().add("my-dialog");
        } catch (Exception ignored) {}
    }

    private Image loadImage(String path) {
        return new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
    }
}