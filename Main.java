import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

// RaceSpec - V3 Final Simulation Interface
// Central circuit simulation with dual telemetry, controls and live graphs.
// Track layout SVGs are downloaded locally by DownloadTrackMaps.ps1.

public class Main extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    private static final Color BACKGROUND = Color.web("#05070A");
    private static final Color PANEL = Color.web("#0D1117");
    private static final Color BORDER = Color.web("#202733");
    private static final Color MUTED = Color.web("#8B95A5");
    private static final Color TEXT = Color.web("#F1F5F9");
    private static final Color ORANGE = Color.web("#FF8A00");
    private static final Color TURQUOISE = Color.web("#19E6D0");

    // Public fallback used when the requested circuit SVG is not stored locally.
    private static final String REMOTE_BASE =
        "https://raw.githubusercontent.com/julesr0y/f1-circuits-svg/main/"
        + "circuits/minimal/white-outline/";

    // Physics timestep stays fixed. Playback is accelerated independently.
    private static final double TIME_STEP = 0.02;
    private static final double DEFAULT_SIMULATION_SPEED = 1.0;
    private static final double MIN_SIMULATION_SPEED = 0.5;
    private static final double MAX_SIMULATION_SPEED = 5.0;

    private static final String[] CIRCUITS =
    {
        "Melbourne",
        "Shanghai",
        "Suzuka",
        "Bahrain",
        "Jeddah",
        "Miami",
        "Montreal",
        "Monaco",
        "Barcelona",
        "Spielberg",
        "Silverstone",
        "Spa",
        "Hungary",
        "Zandvoort",
        "Monza",
        "Madrid",
        "Baku",
        "Singapore",
        "Austin",
        "Mexico City",
        "Interlagos",
        "Las Vegas",
        "Lusail",
        "Abu Dhabi"
    };

    private static final Map<String, String> LAYOUT_IDS =
        new HashMap<>();

    static
    {
        LAYOUT_IDS.put("Melbourne", "melbourne-2");
        LAYOUT_IDS.put("Shanghai", "shanghai-1");
        LAYOUT_IDS.put("Suzuka", "suzuka-2");
        LAYOUT_IDS.put("Bahrain", "bahrain-1");
        LAYOUT_IDS.put("Jeddah", "jeddah-1");
        LAYOUT_IDS.put("Miami", "miami-1");
        LAYOUT_IDS.put("Montreal", "montreal-6");
        LAYOUT_IDS.put("Monaco", "monaco-6");
        LAYOUT_IDS.put("Barcelona", "catalunya-6");
        LAYOUT_IDS.put("Spielberg", "spielberg-3");
        LAYOUT_IDS.put("Silverstone", "silverstone-8");
        LAYOUT_IDS.put("Spa", "spa-francorchamps-4");
        LAYOUT_IDS.put("Hungary", "hungaroring-3");
        LAYOUT_IDS.put("Zandvoort", "zandvoort-5");
        LAYOUT_IDS.put("Monza", "monza-7");
        LAYOUT_IDS.put("Madrid", "madring-1");
        LAYOUT_IDS.put("Baku", "baku-1");
        LAYOUT_IDS.put("Singapore", "marina-bay-4");
        LAYOUT_IDS.put("Austin", "austin-1");
        LAYOUT_IDS.put("Mexico City", "mexico-city-3");
        LAYOUT_IDS.put("Interlagos", "interlagos-2");
        LAYOUT_IDS.put("Las Vegas", "las-vegas-1");
        LAYOUT_IDS.put("Lusail", "lusail-1");
        LAYOUT_IDS.put("Abu Dhabi", "yas-marina-2");
    }

    private ComboBox<String> circuitSelector;
    private Spinner<Integer> lapSelector;
    private Slider simulationSpeedSlider;
    private Label simulationSpeedLabel;
    private Button playButton;
    private Button pauseButton;
    private Button resetButton;
    private Button simulationViewButton;
    private Button analysisViewButton;

    private StackPane contentSwitcher;
    private VBox simulationView;
    private VBox analysisView;

    private TextField dreamNameInput;
    private TextField dreamMassInput;
    private TextField dreamPowerInput;
    private TextField dreamTorqueInput;
    private TextField dreamCdInput;
    private TextField dreamClInput;
    private TextField dreamGripInput;
    private TextField dreamBrakeTorqueInput;
    private TextField dreamBrakeBiasInput;
    private Label analysisStatusLabel;
    private Label dreamCarNameLabel;

    private String dreamDisplayName = "Dream Car";

    private Label circuitInfoLabel;
    private Label statusLabel;
    private Label verdictLabel;
    private Label gapLabel;

    private Label mclMass;
    private Label mclPower;
    private Label mclTorque;
    private Label mclPowerWeight;
    private Label mclSpeed;
    private Label mclRPM;
    private Label mclGear;
    private Label mclThrottle;
    private Label mclBrake;
    private Label mclDRS;
    private Label mclLap;
    private Label mclLapTime;
    private Label mclDownforce;
    private Label mclDrag;
    private Label mclGrip;
    private Label mclBrakeForce;
    private Label mclLateralG;

    private Label dreamMass;
    private Label dreamPower;
    private Label dreamTorque;
    private Label dreamPowerWeight;
    private Label dreamSpeed;
    private Label dreamRPM;
    private Label dreamGear;
    private Label dreamThrottle;
    private Label dreamBrake;
    private Label dreamDRS;
    private Label dreamLap;
    private Label dreamLapTime;
    private Label dreamDownforce;
    private Label dreamDrag;
    private Label dreamGrip;
    private Label dreamBrakeForce;
    private Label dreamLateralG;

    private LineChart<Number, Number> speedChart;
    private LineChart<Number, Number> gChart;
    private XYChart.Series<Number, Number> mclSpeedSeries;
    private XYChart.Series<Number, Number> dreamSpeedSeries;
    private XYChart.Series<Number, Number> mclGSeries;
    private XYChart.Series<Number, Number> dreamGSeries;

    private PerformanceCalculator mclCalculator;
    private PerformanceCalculator dreamCalculator;
    private LapResult mclResult;
    private LapResult dreamResult;

    private double mclLapTimeValue;
    private double dreamLapTimeValue;
    private int selectedLaps = 3;

    private double simulationClock;
    private double simulationSpeed = DEFAULT_SIMULATION_SPEED;
    private double accumulator;
    private double previousMclSpeed;
    private double previousDreamSpeed;
    private boolean running;
    private long lastFrame = -1;

    private TrackPanel trackPanel;
    private AnimationTimer timer;

    @Override
    public void start(Stage stage)
    {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setStyle("-fx-background-color: #05070A;");

        root.setTop(createHeader());

        trackPanel = new TrackPanel();
        simulationView = createMainArea();
        analysisView = createAnalysisArea();

        contentSwitcher = new StackPane();
        contentSwitcher.getChildren().add(simulationView);
        root.setCenter(contentSwitcher);
        root.setBottom(createBottomArea());

        Scene scene = new Scene(root, 1600, 980);

        circuitSelector.valueProperty().addListener(
            (obs, oldValue, newValue) ->
            {
                if (newValue != null && !newValue.equals(oldValue))
                {
                    loadCircuit(newValue);
                }
            }
        );

        lapSelector.valueProperty().addListener(
            (obs, oldValue, newValue) ->
            {
                if (newValue != null && newValue != oldValue)
                {
                    selectedLaps = newValue;
                    resetSimulation();
                }
            }
        );

        simulationSpeedSlider.valueProperty().addListener(
            (obs, oldValue, newValue) ->
            {
                simulationSpeed = newValue.doubleValue();
                simulationSpeedLabel.setText(
                    String.format("%.1fx", simulationSpeed)
                );
            }
        );

        playButton.setOnAction(e -> startSimulation());
        pauseButton.setOnAction(e -> pauseSimulation());
        resetButton.setOnAction(e -> resetSimulation());

        simulationViewButton.setOnAction(e -> showSimulationView());
        analysisViewButton.setOnAction(e -> showAnalysisView());

        timer = new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                if (lastFrame < 0)
                {
                    lastFrame = now;
                    return;
                }

                double frameSeconds =
                    (now - lastFrame) / 1_000_000_000.0;

                lastFrame = now;

                if (!running)
                {
                    return;
                }

                accumulator +=
                    frameSeconds * simulationSpeed;

                while (accumulator >= TIME_STEP)
                {
                    updateSimulationStep();
                    accumulator -= TIME_STEP;

                    if (!running)
                    {
                        break;
                    }
                }
            }
        };

        timer.start();

        stage.setTitle("RaceSpec - Vehicle Dynamics Simulator");
        stage.setScene(scene);
        stage.setMinWidth(1250);
        stage.setMinHeight(820);
        stage.show();

        Platform.runLater(this::applyChartColours);

        loadDefaultDreamInputs();
        dreamDisplayName = "Dream Car";
        circuitSelector.setValue("Monza");
    }

    private HBox createHeader()
    {
        VBox titleBox = new VBox(2);

        Label title = new Label("RACESPEC");
        title.setTextFill(TEXT);
        title.setFont(Font.font("Arial", 26));

        Label subtitle = new Label(
            "VEHICLE DYNAMICS / DUAL CAR CIRCUIT SIMULATION"
        );
        subtitle.setTextFill(MUTED);
        subtitle.setFont(Font.font("Arial", 10));

        titleBox.getChildren().addAll(title, subtitle);

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_RIGHT);

        simulationViewButton = createNavButton("SIMULATION", ORANGE);
        analysisViewButton = createNavButton("ANALYSIS", TEXT);

        controls.getChildren().addAll(
            simulationViewButton,
            analysisViewButton,
            smallLabel("CIRCUIT"),
            circuitSelector = createCircuitSelector(),
            smallLabel("LAPS"),
            lapSelector = createLapSelector()
        );

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(4, 4, 14, 4));
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, controls);

        return header;
    }

    private ComboBox<String> createCircuitSelector()
    {
        ComboBox<String> selector =
            new ComboBox<>(
                FXCollections.observableArrayList(CIRCUITS)
            );

        selector.setPrefWidth(240);
        selector.setStyle(controlStyle());
        return selector;
    }

    private Spinner<Integer> createLapSelector()
    {
        Spinner<Integer> spinner = new Spinner<>();

        spinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1,
                10,
                3
            )
        );

        spinner.setPrefWidth(80);
        spinner.setStyle(controlStyle());
        return spinner;
    }

    private VBox createMainArea()
    {
        VBox left = createCarPanel(
            "McLaren MCL39",
            ORANGE,
            true
        );

        VBox right = createCarPanel(
            "RACE SPEC DREAM CAR",
            TURQUOISE,
            false
        );

        VBox center = new VBox(8);
        center.setMinWidth(1020);
        HBox.setHgrow(center, Priority.ALWAYS);

        circuitInfoLabel = new Label();
        circuitInfoLabel.setTextFill(MUTED);
        circuitInfoLabel.setFont(Font.font("Arial", 11));

        gapLabel = new Label("PACE GAP 0.000 laps");
        gapLabel.setTextFill(TEXT);
        gapLabel.setFont(Font.font("Arial", 12));

        HBox mapHeader = new HBox();
        mapHeader.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(circuitInfoLabel, Priority.ALWAYS);
        mapHeader.getChildren().addAll(
            circuitInfoLabel,
            gapLabel
        );

        StackPane mapCard = new StackPane(trackPanel);
        mapCard.setStyle(cardStyle());
        mapCard.setPadding(new Insets(8));
        mapCard.setMinHeight(630);
        mapCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(mapCard, Priority.ALWAYS);

        verdictLabel = new Label("READY");
        verdictLabel.setTextFill(TEXT);
        verdictLabel.setFont(Font.font("Arial", 14));
        verdictLabel.setAlignment(Pos.CENTER);
        verdictLabel.setMaxWidth(Double.MAX_VALUE);

        StackPane verdictCard = new StackPane(verdictLabel);
        verdictCard.setPadding(new Insets(8));
        verdictCard.setStyle(cardStyle());

        center.getChildren().addAll(
            mapHeader,
            mapCard,
            verdictCard
        );

        HBox result = new HBox(10);
        result.getChildren().addAll(
            left,
            center,
            right
        );

        VBox wrapper = new VBox();
        wrapper.getChildren().add(result);
        VBox.setVgrow(result, Priority.ALWAYS);
        return wrapper;
    }

    private VBox createAnalysisArea()
    {
        VBox root = new VBox(12);
        root.setPadding(new Insets(4));

        Label title = new Label("DREAM CAR ANALYSIS");
        title.setTextFill(TURQUOISE);
        title.setFont(Font.font("Arial", 20));

        Label subtitle = new Label(
            "Edit the Dream Car inputs below. The same configuration is used by the simulation. Lower valid lap time wins."
        );
        subtitle.setTextFill(MUTED);
        subtitle.setFont(Font.font("Arial", 11));

        VBox inputCard = createCard("DREAM CAR INPUTS");

        dreamNameInput = createTextInput("Dream Car");
        dreamMassInput = createTextInput("760");
        dreamPowerInput = createTextInput("820");
        dreamTorqueInput = createTextInput("820");
        dreamCdInput = createTextInput("0.90");
        dreamClInput = createTextInput("3.80");
        dreamGripInput = createTextInput("1.90");
        dreamBrakeTorqueInput = createTextInput("13500");
        dreamBrakeBiasInput = createTextInput("0.58");

        addInputRow(inputCard, "Car Name", dreamNameInput);
        addInputRow(inputCard, "Mass (kg)", dreamMassInput);
        addInputRow(inputCard, "Power (kW)", dreamPowerInput);
        addInputRow(inputCard, "Torque (Nm)", dreamTorqueInput);
        addInputRow(inputCard, "Drag Coefficient", dreamCdInput);
        addInputRow(inputCard, "Downforce Coefficient", dreamClInput);
        addInputRow(inputCard, "Tyre Grip", dreamGripInput);
        addInputRow(inputCard, "Brake Torque (Nm)", dreamBrakeTorqueInput);
        addInputRow(inputCard, "Front Brake Bias", dreamBrakeBiasInput);

        Button applyButton = createButton(
            "APPLY TO SIMULATION",
            TURQUOISE
        );
        applyButton.setPrefWidth(210);
        applyButton.setOnAction(e -> applyDreamSettings());

        Button resetInputsButton = createButton(
            "DEFAULTS",
            TEXT
        );
        resetInputsButton.setOnAction(
            e -> loadDefaultDreamInputs()
        );

        HBox inputButtons = new HBox(8);
        inputButtons.setAlignment(Pos.CENTER_LEFT);
        inputButtons.getChildren().addAll(
            applyButton,
            resetInputsButton
        );

        analysisStatusLabel = new Label(
            "Changes are applied to the simulation when you press APPLY."
        );
        analysisStatusLabel.setTextFill(MUTED);
        analysisStatusLabel.setWrapText(true);

        VBox tablesCard = createCard("MATHEMATICAL PERFORMANCE ANALYSIS");
        analysisTablesContainer = new VBox(10);
        tablesCard.getChildren().add(analysisTablesContainer);

        analysisSummaryLabel = new Label(
            "Apply the Dream Car configuration to populate the analysis tables."
        );
        analysisSummaryLabel.setTextFill(MUTED);
        analysisSummaryLabel.setFont(Font.font("Arial", 11));
        analysisSummaryLabel.setWrapText(true);

        VBox content = new VBox(12);
        content.getChildren().addAll(
            inputCard,
            inputButtons,
            analysisStatusLabel,
            analysisSummaryLabel,
            tablesCard
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle(
            "-fx-background: #05070A;"
            + "-fx-border-color: transparent;"
        );
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(
            title,
            subtitle,
            scroll
        );

        return root;
    }

    private Label analysisSummaryLabel;
    private VBox analysisTablesContainer;

    private TextField createTextInput(String value)
    {
        TextField field = new TextField(value);
        field.setPrefWidth(180);
        field.setStyle(
            "-fx-background-color: #111821;"
            + "-fx-border-color: #2A3442;"
            + "-fx-text-fill: #F1F5F9;"
        );
        return field;
    }

    private void addInputRow(
        VBox card,
        String name,
        TextField input
    )
    {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(name);
        label.setTextFill(MUTED);
        label.setFont(Font.font("Arial", 11));
        HBox.setHgrow(label, Priority.ALWAYS);

        row.getChildren().addAll(label, input);
        card.getChildren().add(row);
    }

    private void loadDefaultDreamInputs()
    {
        dreamNameInput.setText("Dream Car");
        dreamMassInput.setText("760");
        dreamPowerInput.setText("820");
        dreamTorqueInput.setText("820");
        dreamCdInput.setText("0.90");
        dreamClInput.setText("3.80");
        dreamGripInput.setText("1.90");
        dreamBrakeTorqueInput.setText("13500");
        dreamBrakeBiasInput.setText("0.58");
        analysisStatusLabel.setText(
            "Default Dream Car values restored. Press APPLY to use them."
        );

        dreamDisplayName = "Dream Car";

        if (dreamCarNameLabel != null)
        {
            dreamCarNameLabel.setText("DREAM CAR");
        }

        if (dreamSpeedSeries != null)
        {
            dreamSpeedSeries.setName("Dream Car");
            dreamGSeries.setName("Dream Car");
        }

        if (analysisSummaryLabel != null)
        {
            updateAnalysisSummary(analysisSummaryLabel);
        }
    }

    private void applyDreamSettings()
    {
        try
        {
            dreamDisplayName = getDreamName();

            dreamCalculator =
                createDreamCalculatorFromInputs();

            updateStaticLabels();

            if (dreamCarNameLabel != null)
            {
                dreamCarNameLabel.setText(
                    dreamDisplayName.toUpperCase()
                );
            }

            if (dreamSpeedSeries != null)
            {
                dreamSpeedSeries.setName(dreamDisplayName);
                dreamGSeries.setName(dreamDisplayName);
            }

            resetSimulation();

            updateAnalysisSummary(
                analysisSummaryLabel
            );

            analysisStatusLabel.setText(
                dreamDisplayName
                + " is now active in the simulation."
            );

            showSimulationView();
        }
        catch (Exception ex)
        {
            analysisStatusLabel.setText(
                "INPUT ERROR: " + ex.getMessage()
            );
        }
    }

    private String getDreamName()
    {
        String value =
            dreamNameInput.getText();

        if (value == null || value.trim().isEmpty())
        {
            return "Dream Car";
        }

        return value.trim();
    }

    private double parseDreamInput(
        TextField field,
        String name
    )
    {
        try
        {
            return Double.parseDouble(
                field.getText().trim()
            );
        }
        catch (NumberFormatException ex)
        {
            throw new IllegalArgumentException(
                "Invalid " + name + "."
            );
        }
    }

    private PerformanceCalculator createDreamCalculatorFromInputs()
    {
        String name =
            getDreamName();

        double mass =
            parseDreamInput(dreamMassInput, "mass");

        double power =
            parseDreamInput(dreamPowerInput, "power");

        double torque =
            parseDreamInput(dreamTorqueInput, "torque");

        double cd =
            parseDreamInput(dreamCdInput, "drag coefficient");

        double cl =
            parseDreamInput(dreamClInput, "downforce coefficient");

        double grip =
            parseDreamInput(dreamGripInput, "tyre grip");

        double brakeTorque =
            parseDreamInput(
                dreamBrakeTorqueInput,
                "brake torque"
            );

        double brakeBias =
            parseDreamInput(
                dreamBrakeBiasInput,
                "front brake bias"
            );

        Vehicle vehicle = new Vehicle(
            name,
            mass,
            0.360,
            1.42,
            cd,
            cl,
            0.010,
            3.55,
            1.62,
            1.60,
            0.28,
            0.44,
            1.05
        );

        Engine engine = new Engine(
            "RaceSpec Hybrid V10",
            10,
            power,
            torque,
            15000.0,
            3000.0,
            145.0,
            8500.0,
            13500.0,
            4800.0,
            8200.0,
            0.75,
            1.00,
            120.0,
            5500.0,
            14500.0
        );

        double[] ratios =
        {
            3.15, 2.40, 1.88, 1.55,
            1.30, 1.10, 0.94, 0.81
        };

        Gearbox gearbox = new Gearbox(
            8,
            ratios,
            3.25,
            0.97,
            14000.0
        );

        Tyre tyre = new Tyre(
            "RaceSpec Advanced Slick",
            grip,
            9.5,
            0.405,
            0.720,
            1.38,
            100.0,
            0.09,
            0.012,
            0.04,
            grip,
            grip * 1.05,
            0.095,
            0.095,
            0.009,
            85.0
        );

        Aerodynamics aero =
            new Aerodynamics(vehicle);
        aero.setFrontAeroBalance(0.44);

        Brakes brakes = new Brakes(
            brakeTorque,
            0.94,
            brakeBias
        );

        return new PerformanceCalculator(
            vehicle,
            engine,
            gearbox,
            tyre,
            aero,
            brakes
        );
    }

    private void updateAnalysisSummary(Label summary)
    {
        if (summary == null)
        {
            return;
        }

        summary.setText(
            "ACTIVE DREAM CAR: "
            + dreamDisplayName
            + "   |   Circuit: "
            + (circuitSelector == null || circuitSelector.getValue() == null
                ? "-"
                : circuitSelector.getValue())
        );

        updateAnalysisTables();
    }

    private void updateAnalysisTables()
    {
        if (analysisTablesContainer == null)
        {
            return;
        }

        analysisTablesContainer.getChildren().clear();

        if (mclCalculator == null || dreamCalculator == null
            || mclResult == null || dreamResult == null)
        {
            Label waiting = new Label("No active simulation data.");
            waiting.setTextFill(MUTED);
            analysisTablesContainer.getChildren().add(waiting);
            return;
        }

        double velocity = 60.0;

        analysisTablesContainer.getChildren().add(
            createAnalysisTable(
                "VEHICLE SPECIFICATIONS",
                new String[][]
                {
                    {"Mass (kg)", format(mclCalculator.getVehicle().getMass()),
                     format(dreamCalculator.getVehicle().getMass()),
                     lowerWinner(mclCalculator.getVehicle().getMass(),
                                  dreamCalculator.getVehicle().getMass())},
                    {"Power (kW)", format(mclCalculator.getEngine().getMaximumPower()),
                     format(dreamCalculator.getEngine().getMaximumPower()),
                     higherWinner(mclCalculator.getEngine().getMaximumPower(),
                                  dreamCalculator.getEngine().getMaximumPower())},
                    {"Torque (Nm)", format(mclCalculator.getEngine().getMaximumTorque()),
                     format(dreamCalculator.getEngine().getMaximumTorque()),
                     higherWinner(mclCalculator.getEngine().getMaximumTorque(),
                                  dreamCalculator.getEngine().getMaximumTorque())},
                    {"Power-to-Weight", format(mclCalculator.getPowerToWeight()),
                     format(dreamCalculator.getPowerToWeight()),
                     higherWinner(mclCalculator.getPowerToWeight(),
                                  dreamCalculator.getPowerToWeight())},
                    {"Top Speed (km/h)", format(mclCalculator.getEstimatedTopSpeed()),
                     format(dreamCalculator.getEstimatedTopSpeed()),
                     higherWinner(mclCalculator.getEstimatedTopSpeed(),
                                  dreamCalculator.getEstimatedTopSpeed())}
                }
            )
        );

        analysisTablesContainer.getChildren().add(
            createAnalysisTable(
                "DYNAMIC PERFORMANCE @ 60.0 m/s",
                new String[][]
                {
                    {"Downforce (N)", format(mclCalculator.getDownforce(velocity)),
                     format(dreamCalculator.getDownforce(velocity)),
                     higherWinner(mclCalculator.getDownforce(velocity),
                                  dreamCalculator.getDownforce(velocity))},
                    {"Drag (N)", format(mclCalculator.getAerodynamicDrag(velocity)),
                     format(dreamCalculator.getAerodynamicDrag(velocity)),
                     lowerWinner(mclCalculator.getAerodynamicDrag(velocity),
                                  dreamCalculator.getAerodynamicDrag(velocity))},
                    {"Tyre Grip (N)", format(mclCalculator.getMaximumGripForce(velocity)),
                     format(dreamCalculator.getMaximumGripForce(velocity)),
                     higherWinner(mclCalculator.getMaximumGripForce(velocity),
                                  dreamCalculator.getMaximumGripForce(velocity))},
                    {"Braking Force (N)", format(mclCalculator.getMaximumBrakingForce(velocity)),
                     format(dreamCalculator.getMaximumBrakingForce(velocity)),
                     higherWinner(mclCalculator.getMaximumBrakingForce(velocity),
                                  dreamCalculator.getMaximumBrakingForce(velocity))},
                    {"Lateral Accel (m/s²)", format(mclCalculator.getMaximumLateralAcceleration(velocity)),
                     format(dreamCalculator.getMaximumLateralAcceleration(velocity)),
                     higherWinner(mclCalculator.getMaximumLateralAcceleration(velocity),
                                  dreamCalculator.getMaximumLateralAcceleration(velocity))}
                }
            )
        );

        analysisTablesContainer.getChildren().add(
            createAnalysisTable(
                "LAP PERFORMANCE",
                new String[][]
                {
                    {"Lap Time (s)", format(mclResult.getLapTime()),
                     format(dreamResult.getLapTime()),
                     lowerWinner(mclResult.getLapTime(),
                                  dreamResult.getLapTime())},
                    {"Maximum Speed (km/h)",
                     format(mclResult.getMaximumVelocity() * 3.6),
                     format(dreamResult.getMaximumVelocity() * 3.6),
                     higherWinner(mclResult.getMaximumVelocity(),
                                  dreamResult.getMaximumVelocity())},
                    {"Average Speed (km/h)",
                     format(mclResult.getAverageVelocity() * 3.6),
                     format(dreamResult.getAverageVelocity() * 3.6),
                     higherWinner(mclResult.getAverageVelocity(),
                                  dreamResult.getAverageVelocity())}
                }
            )
        );

        String finalWinner = getWinnerFromLapTimes();
        String verdictText = getWinnerBanner();

        Label finalLabel = new Label(
            "FINAL CIRCUIT RESULT: "
            + verdictText
            + "   |   Lap Advantage: "
            + format(Math.abs(mclResult.getLapTime() - dreamResult.getLapTime()))
            + " s"
        );
        if (finalWinner.equals("MCL39"))
        {
            finalLabel.setTextFill(ORANGE);
        }
        else if (finalWinner.equals("Dream Car"))
        {
            finalLabel.setTextFill(TURQUOISE);
        }
        else
        {
            finalLabel.setTextFill(TEXT);
        }
        finalLabel.setFont(Font.font("Arial", 14));
        finalLabel.setWrapText(true);

        analysisTablesContainer.getChildren().add(finalLabel);
    }

    private VBox createAnalysisTable(
        String title,
        String[][] rows
    )
    {
        VBox box = createCard(title);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(4);
        grid.setMaxWidth(Double.MAX_VALUE);

        addTableCell(grid, "Parameter", 0, 0, MUTED, true);
        addTableCell(grid, "McLaren MCL39", 1, 0, ORANGE, true);
        addTableCell(grid, dreamDisplayName, 2, 0, TURQUOISE, true);
        addTableCell(grid, "Winner", 3, 0, MUTED, true);

        for (int i = 0; i < rows.length; i++)
        {
            int row = i + 1;

            addTableCell(
                grid, rows[i][0], 0, row, MUTED, false
            );
            addTableCell(
                grid, rows[i][1], 1, row, TEXT, false
            );
            addTableCell(
                grid, rows[i][2], 2, row, TEXT, false
            );

            Color winnerColor = MUTED;

            if (rows[i][3].equals("Dream Car"))
            {
                winnerColor = TURQUOISE;
            }
            else if (rows[i][3].equals("MCL39"))
            {
                winnerColor = ORANGE;
            }

            addTableCell(
                grid, rows[i][3], 3, row, winnerColor, false
            );
        }

        box.getChildren().add(grid);
        return box;
    }

    private void addTableCell(
        GridPane grid,
        String text,
        int column,
        int row,
        Color color,
        boolean header
    )
    {
        Label label = new Label(text);
        label.setTextFill(color);
        label.setFont(
            Font.font(
                "Arial",
                header ? 10 : 11
            )
        );
        label.setMaxWidth(Double.MAX_VALUE);

        if (column > 0)
        {
            label.setAlignment(Pos.CENTER_RIGHT);
        }

        grid.add(label, column, row);
    }

    private String higherWinner(
        double mcl,
        double dream
    )
    {
        if (dream > mcl)
        {
            return "Dream Car";
        }

        if (mcl > dream)
        {
            return "MCL39";
        }

        return "Equal";
    }

    private String lowerWinner(
        double mcl,
        double dream
    )
    {
        if (dream < mcl)
        {
            return "Dream Car";
        }

        if (mcl < dream)
        {
            return "MCL39";
        }

        return "Equal";
    }

    private String getWinnerFromLapTimes()
    {
        if (mclResult == null || dreamResult == null)
        {
            return "Equal";
        }

        if (mclResult.getLapTime() <= 0 || dreamResult.getLapTime() <= 0)
        {
            return "Equal";
        }

        // Final winner rule: the car with the lower valid simulated lap time wins.
        if (dreamResult.getLapTime() < mclResult.getLapTime())
        {
            return "Dream Car";
        }

        if (mclResult.getLapTime() < dreamResult.getLapTime())
        {
            return "MCL39";
        }

        return "Equal";
    }

    private String getWinnerBanner()
    {
        String winner = getWinnerFromLapTimes();

        if (winner.equals("MCL39"))
        {
            return "MCLAREN WINNER";
        }

        if (winner.equals("Dream Car"))
        {
            return "DREAM CAR WINNER";
        }

        return "EQUAL RESULT";
    }

    private void showSimulationView()
    {
        if (contentSwitcher != null)
        {
            contentSwitcher.getChildren().setAll(
                simulationView
            );
        }

        if (simulationViewButton != null)
        {
            simulationViewButton.setStyle(navButtonStyle(ORANGE));
        }

        if (analysisViewButton != null)
        {
            analysisViewButton.setStyle(navButtonStyle(TEXT));
        }
    }

    private void showAnalysisView()
    {
        if (contentSwitcher != null)
        {
            contentSwitcher.getChildren().setAll(
                analysisView
            );
        }

        if (simulationViewButton != null)
        {
            simulationViewButton.setStyle(navButtonStyle(TEXT));
        }

        if (analysisViewButton != null)
        {
            analysisViewButton.setStyle(navButtonStyle(TURQUOISE));
        }
    }

    private Button createNavButton(
        String text,
        Color accent
    )
    {
        Button button = new Button(text);
        button.setPrefWidth(105);
        button.setStyle(navButtonStyle(accent));
        return button;
    }

    private String navButtonStyle(Color accent)
    {
        return
            "-fx-background-color: #111821;"
            + "-fx-border-color: #2A3442;"
            + "-fx-text-fill: "
            + toHex(accent)
            + ";"
            + "-fx-font-weight: bold;";
    }

    private VBox createCarPanel(
        String name,
        Color accent,
        boolean mclaren
    )
    {
        VBox panel = new VBox(8);
        panel.setPrefWidth(235);
        panel.setMinWidth(220);
        panel.setMaxWidth(250);

        Label heading = new Label(name);
        heading.setTextFill(accent);
        heading.setFont(Font.font("Arial", 17));

        if (!mclaren)
        {
            dreamCarNameLabel = heading;
        }

        Label role = new Label(
            mclaren ? "REFERENCE VEHICLE" : "USER CONCEPT"
        );
        role.setTextFill(MUTED);
        role.setFont(Font.font("Arial", 9));

        panel.getChildren().addAll(
            heading,
            role,
            createVehicleCard(mclaren),
            createLiveCard(mclaren),
            createForceCard(mclaren)
        );

        return panel;
    }

    private VBox createVehicleCard(boolean mclaren)
    {
        VBox box = createCard("VEHICLE");

        Label mass = valueLabel();
        Label power = valueLabel();
        Label torque = valueLabel();
        Label powerWeight = valueLabel();

        if (mclaren)
        {
            mclMass = mass;
            mclPower = power;
            mclTorque = torque;
            mclPowerWeight = powerWeight;
        }
        else
        {
            dreamMass = mass;
            dreamPower = power;
            dreamTorque = torque;
            dreamPowerWeight = powerWeight;
        }

        addRow(box, "Mass", mass);
        addRow(box, "Power", power);
        addRow(box, "Torque", torque);
        addRow(box, "Power / Weight", powerWeight);

        return box;
    }

    private VBox createLiveCard(boolean mclaren)
    {
        VBox box = createCard("LIVE DYNAMICS");

        Label speed = valueLabel();
        Label rpm = valueLabel();
        Label gear = valueLabel();
        Label throttle = valueLabel();
        Label brake = valueLabel();
        Label drs = valueLabel();
        Label lap = valueLabel();
        Label lapTime = valueLabel();

        if (mclaren)
        {
            mclSpeed = speed;
            mclRPM = rpm;
            mclGear = gear;
            mclThrottle = throttle;
            mclBrake = brake;
            mclDRS = drs;
            mclLap = lap;
            mclLapTime = lapTime;
        }
        else
        {
            dreamSpeed = speed;
            dreamRPM = rpm;
            dreamGear = gear;
            dreamThrottle = throttle;
            dreamBrake = brake;
            dreamDRS = drs;
            dreamLap = lap;
            dreamLapTime = lapTime;
        }

        addRow(box, "Speed", speed);
        addRow(box, "RPM", rpm);
        addRow(box, "Gear", gear);
        addRow(box, "Throttle", throttle);
        addRow(box, "Brake", brake);
        addRow(box, "DRS", drs);
        addRow(box, "Lap", lap);
        addRow(box, "Lap Time", lapTime);

        return box;
    }

    private VBox createForceCard(boolean mclaren)
    {
        VBox box = createCard("FORCES / GRIP");

        Label downforce = valueLabel();
        Label drag = valueLabel();
        Label grip = valueLabel();
        Label brakeForce = valueLabel();
        Label lateralG = valueLabel();

        if (mclaren)
        {
            mclDownforce = downforce;
            mclDrag = drag;
            mclGrip = grip;
            mclBrakeForce = brakeForce;
            mclLateralG = lateralG;
        }
        else
        {
            dreamDownforce = downforce;
            dreamDrag = drag;
            dreamGrip = grip;
            dreamBrakeForce = brakeForce;
            dreamLateralG = lateralG;
        }

        addRow(box, "Downforce", downforce);
        addRow(box, "Drag", drag);
        addRow(box, "Tyre Grip", grip);
        addRow(box, "Brake Force", brakeForce);
        addRow(box, "Lateral G", lateralG);

        return box;
    }

    private VBox createBottomArea()
    {
        VBox bottom = new VBox(8);
        bottom.setPadding(new Insets(10, 0, 0, 0));

        HBox controls = new HBox(8);
        controls.setAlignment(Pos.CENTER);

        playButton = createButton("PLAY", ORANGE);
        pauseButton = createButton("PAUSE", TEXT);
        resetButton = createButton("RESET", TEXT);

        Label speedText = smallLabel("SIM SPEED");

        simulationSpeedSlider = new Slider(
            MIN_SIMULATION_SPEED,
            MAX_SIMULATION_SPEED,
            DEFAULT_SIMULATION_SPEED
        );
        simulationSpeedSlider.setShowTickMarks(true);
        simulationSpeedSlider.setShowTickLabels(true);
        simulationSpeedSlider.setMajorTickUnit(1.0);
        simulationSpeedSlider.setMinorTickCount(1);
        simulationSpeedSlider.setPrefWidth(260);
        simulationSpeedSlider.setStyle(
            "-fx-control-inner-background: #111821;"
            + "-fx-accent: #FF8A00;"
        );

        simulationSpeedLabel = new Label("1.0x");
        simulationSpeedLabel.setTextFill(ORANGE);
        simulationSpeedLabel.setFont(Font.font("Arial", 11));
        simulationSpeedLabel.setMinWidth(35);

        statusLabel = new Label("READY");
        statusLabel.setTextFill(MUTED);
        statusLabel.setFont(Font.font("Arial", 10));

        controls.getChildren().addAll(
            playButton,
            pauseButton,
            resetButton,
            speedText,
            simulationSpeedSlider,
            simulationSpeedLabel,
            statusLabel
        );

        HBox.setHgrow(statusLabel, Priority.ALWAYS);
        statusLabel.setAlignment(Pos.CENTER_RIGHT);

        speedChart = createChart(
            "SPEED vs DISTANCE",
            "Distance (km)",
            "Speed (km/h)"
        );

        gChart = createChart(
            "G-FORCE vs DISTANCE",
            "Distance (km)",
            "G"
        );

        mclSpeedSeries = createSeries("MCL39");
        dreamSpeedSeries = createSeries("Dream Car");
        mclGSeries = createSeries("MCL39");
        dreamGSeries = createSeries("Dream Car");

        speedChart.getData().addAll(
            mclSpeedSeries,
            dreamSpeedSeries
        );

        gChart.getData().addAll(
            mclGSeries,
            dreamGSeries
        );

        HBox graphs = new HBox(10);
        graphs.getChildren().addAll(
            speedChart,
            gChart
        );

        HBox.setHgrow(speedChart, Priority.ALWAYS);
        HBox.setHgrow(gChart, Priority.ALWAYS);

        speedChart.setPrefHeight(235);
        gChart.setPrefHeight(235);

        bottom.getChildren().addAll(
            controls,
            graphs
        );

        return bottom;
    }

    private XYChart.Series<Number, Number> createSeries(
        String name
    )
    {
        XYChart.Series<Number, Number> series =
            new XYChart.Series<>();
        series.setName(name);
        return series;
    }

    private LineChart<Number, Number> createChart(
        String title,
        String xLabel,
        String yLabel
    )
    {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);

        xAxis.setTickLabelFill(TEXT);
        yAxis.setTickLabelFill(TEXT);

        LineChart<Number, Number> chart =
            new LineChart<>(xAxis, yAxis);

        chart.setTitle(title);
        chart.setAnimated(false);
        chart.setLegendVisible(true);
        chart.setCreateSymbols(false);
        chart.setAlternativeRowFillVisible(false);
        chart.setAlternativeColumnFillVisible(false);
        chart.setStyle(
            "-fx-background-color: #05070A;"
            + "-fx-plot-background-color: #000000;"
            + "-fx-padding: 5;"
        );

        return chart;
    }

    private void applyChartColours()
    {
        applySeriesColour(
            mclSpeedSeries,
            "#FF8A00"
        );

        applySeriesColour(
            dreamSpeedSeries,
            "#19E6D0"
        );

        applySeriesColour(
            mclGSeries,
            "#FF8A00"
        );

        applySeriesColour(
            dreamGSeries,
            "#19E6D0"
        );
    }

    private void applySeriesColour(
        XYChart.Series<Number, Number> series,
        String colour
    )
    {
        if (series != null && series.getNode() != null)
        {
            series.getNode().setStyle(
                "-fx-stroke: "
                + colour
                + "; -fx-stroke-width: 2.5;"
            );
        }
    }

    private VBox createCard(String title)
    {
        VBox box = new VBox(5);
        box.setPadding(new Insets(8));
        box.setStyle(cardStyle());

        Label heading = new Label(title);
        heading.setTextFill(MUTED);
        heading.setFont(Font.font("Arial", 10));

        box.getChildren().add(heading);
        return box;
    }

    private String cardStyle()
    {
        return
            "-fx-background-color: #0D1117;"
            + "-fx-border-color: #202733;"
            + "-fx-border-width: 1;"
            + "-fx-border-radius: 5;"
            + "-fx-background-radius: 5;";
    }

    private String controlStyle()
    {
        return
            "-fx-background-color: #111821;"
            + "-fx-border-color: #2A3442;"
            + "-fx-text-fill: #F1F5F9;";
    }

    private Label smallLabel(String text)
    {
        Label label = new Label(text);
        label.setTextFill(MUTED);
        label.setFont(Font.font("Arial", 10));
        return label;
    }

    private Label valueLabel()
    {
        Label label = new Label("-");
        label.setTextFill(TEXT);
        label.setFont(Font.font("Arial", 11));
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER_RIGHT);
        return label;
    }

    private void addRow(
        VBox box,
        String name,
        Label value
    )
    {
        HBox row = new HBox(5);

        Label label = new Label(name);
        label.setTextFill(MUTED);
        label.setFont(Font.font("Arial", 10));

        HBox.setHgrow(label, Priority.ALWAYS);
        row.getChildren().addAll(label, value);
        box.getChildren().add(row);
    }

    private Button createButton(
        String text,
        Color accent
    )
    {
        Button button = new Button(text);
        button.setPrefWidth(88);
        button.setStyle(
            "-fx-background-color: #111821;"
            + "-fx-border-color: #2A3442;"
            + "-fx-text-fill: "
            + toHex(accent)
            + ";"
            + "-fx-font-weight: bold;"
        );
        return button;
    }

    private String toHex(Color color)
    {
        return String.format(
            "#%02X%02X%02X",
            (int) Math.round(color.getRed() * 255),
            (int) Math.round(color.getGreen() * 255),
            (int) Math.round(color.getBlue() * 255)
        );
    }

    private void loadCircuit(String name)
    {
        pauseSimulation();

        try
        {
            mclCalculator =
                McLarenMCL39.createPerformanceCalculator();

            dreamCalculator =
                createDreamCalculatorFromInputs();

            Circuit circuit = new Circuit(name);

            LapSimulation mclSimulation =
                new LapSimulation(
                    mclCalculator,
                    circuit
                );

            LapSimulation dreamSimulation =
                new LapSimulation(
                    dreamCalculator,
                    circuit
                );

            mclResult = mclSimulation.simulate();
            dreamResult = dreamSimulation.simulate();

            mclLapTimeValue = mclResult.getLapTime();
            dreamLapTimeValue = dreamResult.getLapTime();

            circuitInfoLabel.setText(
                name
                + "  |  "
                + String.format(
                    "%.3f km",
                    circuit.getTrackLength()
                )
                + "  |  "
                + circuit.getNumberOfCorners()
                + " corners"
            );

            updateStaticLabels();
            trackPanel.loadLayout(name);
            updateAnalysisSummary(analysisSummaryLabel);
            resetSimulation();
        }
        catch (Exception ex)
        {
            statusLabel.setText(
                "LOAD ERROR: " + ex.getMessage()
            );
        }
    }

    private void updateStaticLabels()
    {
        mclMass.setText(
            format(mclCalculator.getVehicle().getMass())
            + " kg"
        );
        mclPower.setText(
            format(mclCalculator.getEngine().getMaximumPower())
            + " kW"
        );
        mclTorque.setText(
            format(mclCalculator.getEngine().getMaximumTorque())
            + " Nm"
        );
        mclPowerWeight.setText(
            format(mclCalculator.getPowerToWeight())
            + " kW/t"
        );

        dreamMass.setText(
            format(dreamCalculator.getVehicle().getMass())
            + " kg"
        );
        dreamPower.setText(
            format(dreamCalculator.getEngine().getMaximumPower())
            + " kW"
        );
        dreamTorque.setText(
            format(dreamCalculator.getEngine().getMaximumTorque())
            + " Nm"
        );
        dreamPowerWeight.setText(
            format(dreamCalculator.getPowerToWeight())
            + " kW/t"
        );
    }

    private void startSimulation()
    {
        if (mclResult == null || dreamResult == null)
        {
            return;
        }

        running = true;
        lastFrame = -1;
        statusLabel.setText(
            "RUNNING  |  "
            + selectedLaps
            + " LAP(S)  |  "
            + String.format("%.1fx SPEED", simulationSpeed)
        );
        String earlyWinner = getWinnerFromLapTimes();

        if (earlyWinner.equals("MCL39"))
        {
            verdictLabel.setText("MCLAREN CURRENTLY AHEAD");
            verdictLabel.setTextFill(ORANGE);
        }
        else if (earlyWinner.equals("Dream Car"))
        {
            verdictLabel.setText(
                dreamDisplayName.toUpperCase()
                + " CURRENTLY AHEAD"
            );
            verdictLabel.setTextFill(TURQUOISE);
        }
        else
        {
            verdictLabel.setText("SIMULATION RUNNING");
            verdictLabel.setTextFill(TEXT);
        }
    }

    private void pauseSimulation()
    {
        running = false;
        lastFrame = -1;

        if (statusLabel != null)
        {
            statusLabel.setText("PAUSED");
        }
    }

    private void resetSimulation()
    {
        running = false;
        simulationClock = 0;
        accumulator = 0;
        lastFrame = -1;
        previousMclSpeed = 0;
        previousDreamSpeed = 0;

        if (mclSpeedSeries != null)
        {
            mclSpeedSeries.getData().clear();
            dreamSpeedSeries.getData().clear();
            mclGSeries.getData().clear();
            dreamGSeries.getData().clear();
        }

        if (statusLabel != null)
        {
            statusLabel.setText("READY");
        }

        if (verdictLabel != null)
        {
            verdictLabel.setText("READY");
        }

        if (gapLabel != null)
        {
            gapLabel.setText("PACE GAP 0.000 laps");
        }

        if (trackPanel != null)
        {
            trackPanel.setCarProgress(0, 0);
        }

        updateDisplayedTelemetry(
            mclCalculator,
            true,
            0,
            mclLapTimeValue,
            selectedLaps,
            0
        );

        updateDisplayedTelemetry(
            dreamCalculator,
            false,
            0,
            dreamLapTimeValue,
            selectedLaps,
            0
        );

        updateAnalysisTables();
    }

    private void updateSimulationStep()
    {
        if (mclResult == null || dreamResult == null)
        {
            return;
        }

        double totalMclTime =
            mclLapTimeValue * selectedLaps;

        double totalDreamTime =
            dreamLapTimeValue * selectedLaps;

        double totalTime =
            Math.max(
                totalMclTime,
                totalDreamTime
            );

        simulationClock += TIME_STEP;

        if (simulationClock > totalTime)
        {
            simulationClock = totalTime;
        }

        double mclProgress =
            totalMclTime > 0
            ? simulationClock / totalMclTime
            : 1;

        double dreamProgress =
            totalDreamTime > 0
            ? simulationClock / totalDreamTime
            : 1;

        mclProgress = clamp(mclProgress, 0, 1);
        dreamProgress = clamp(dreamProgress, 0, 1);

        double mclLapPhase =
            getLapPhase(simulationClock, mclLapTimeValue);

        double dreamLapPhase =
            getLapPhase(simulationClock, dreamLapTimeValue);

        updateDisplayedTelemetry(
            mclCalculator,
            true,
            mclProgress,
            mclLapTimeValue,
            selectedLaps,
            previousMclSpeed
        );

        updateDisplayedTelemetry(
            dreamCalculator,
            false,
            dreamProgress,
            dreamLapTimeValue,
            selectedLaps,
            previousDreamSpeed
        );

        trackPanel.setCarProgress(
            mclLapPhase,
            dreamLapPhase
        );

        gapLabel.setText(
            String.format(
                "PACE GAP  %+.3f laps",
                (
                    dreamProgress
                    - mclProgress
                ) * selectedLaps
            )
        );

        if (simulationClock >= totalTime)
        {
            finishSimulation();
        }
    }

    private double getLapPhase(
        double clock,
        double lapTime
    )
    {
        if (lapTime <= 0)
        {
            return 0;
        }

        if (clock <= 0)
        {
            return 0;
        }

        double phase =
            (clock % lapTime) / lapTime;

        if (clock >= lapTime * selectedLaps)
        {
            return 1.0;
        }

        return clamp(phase, 0, 1);
    }

    private void updateDisplayedTelemetry(
        PerformanceCalculator calculator,
        boolean mclaren,
        double progress,
        double lapTime,
        int laps,
        double previousSpeed
    )
    {
        if (calculator == null)
        {
            return;
        }

        Circuit circuit =
            new Circuit(circuitSelector.getValue());

        double distanceProgress =
            progress * laps;

        double lapPhase =
            distanceProgress % 1.0;

        if (progress >= 1.0)
        {
            lapPhase = 1.0;
        }

        double speed =
            calculateDisplayedSpeed(
                calculator,
                circuit,
                lapPhase
            );

        double acceleration =
            (
                speed - previousSpeed
            ) / TIME_STEP;

        if (mclaren)
        {
            previousMclSpeed = speed;
        }
        else
        {
            previousDreamSpeed = speed;
        }

        int gear =
            calculator.getBestGear(
                speed / 3.6
            );

        gear = Math.max(1, gear);

        double rpm =
            calculator.getRPMForSpeed(
                speed / 3.6,
                gear
            );

        rpm = calculator.getEngine().applyRevLimiter(rpm);

        boolean braking =
            isNearBrakingZone(
                circuit,
                lapPhase
            );

        boolean drs =
            !braking
            && speed > 170
            && isLongStraightArea(
                circuit,
                lapPhase
            );

        double throttle =
            braking
            ? 0
            : clamp(
                0.62 + speed / 900.0,
                0,
                1
            );

        double downforce =
            calculator.getDownforce(
                speed / 3.6
            );

        double drag =
            calculator.getAerodynamicDrag(
                speed / 3.6
            );

        double grip =
            calculator.getMaximumGripForce(
                speed / 3.6
            );

        double brakeForce =
            calculator.getMaximumBrakingForce(
                speed / 3.6
            );

        double lateralG =
            estimateLateralG(
                calculator,
                circuit,
                lapPhase,
                speed
            );

        int currentLap =
            (int) Math.floor(distanceProgress) + 1;

        if (progress >= 1.0)
        {
            currentLap = laps;
        }

        double currentLapTime =
            progress >= 1
            ? lapTime
            : (distanceProgress % 1.0) * lapTime;

        double graphDistance =
            distanceProgress * circuit.getTrackLength();

        if (mclaren)
        {
            mclSpeed.setText(format(speed) + " km/h");
            mclRPM.setText(format(rpm) + " rpm");
            mclGear.setText(String.valueOf(gear));
            mclThrottle.setText(format(throttle * 100) + " %");
            mclBrake.setText(braking ? "100 %" : "0 %");
            mclDRS.setText(drs ? "OPEN" : "CLOSED");
            mclLap.setText(currentLap + " / " + laps);
            mclLapTime.setText(format(currentLapTime) + " s");

            mclDownforce.setText(format(downforce) + " N");
            mclDrag.setText(format(drag) + " N");
            mclGrip.setText(format(grip) + " N");
            mclBrakeForce.setText(format(brakeForce) + " N");
            mclLateralG.setText(format(lateralG) + " g");

            addGraphPoint(
                mclSpeedSeries,
                graphDistance,
                speed
            );

            addGraphPoint(
                mclGSeries,
                graphDistance,
                Math.abs(acceleration) / 9.80665
            );
        }
        else
        {
            dreamSpeed.setText(format(speed) + " km/h");
            dreamRPM.setText(format(rpm) + " rpm");
            dreamGear.setText(String.valueOf(gear));
            dreamThrottle.setText(format(throttle * 100) + " %");
            dreamBrake.setText(braking ? "100 %" : "0 %");
            dreamDRS.setText(drs ? "OPEN" : "CLOSED");
            dreamLap.setText(currentLap + " / " + laps);
            dreamLapTime.setText(format(currentLapTime) + " s");

            dreamDownforce.setText(format(downforce) + " N");
            dreamDrag.setText(format(drag) + " N");
            dreamGrip.setText(format(grip) + " N");
            dreamBrakeForce.setText(format(brakeForce) + " N");
            dreamLateralG.setText(format(lateralG) + " g");

            addGraphPoint(
                dreamSpeedSeries,
                graphDistance,
                speed
            );

            addGraphPoint(
                dreamGSeries,
                graphDistance,
                Math.abs(acceleration) / 9.80665
            );
        }
    }

    private double calculateDisplayedSpeed(
        PerformanceCalculator calculator,
        Circuit circuit,
        double progress
    )
    {
        double maximumSpeed =
            calculator.getEstimatedTopSpeed();

        double speed = maximumSpeed;
        int corners = circuit.getNumberOfCorners();

        for (int i = 1; i <= corners; i++)
        {
            double cornerPosition =
                (i - 0.5) / corners;

            double difference =
                circularDifference(
                    progress,
                    cornerPosition
                );

            double width = 0.035;

            if (difference < width)
            {
                double ratio =
                    difference / width;

                double smooth =
                    0.5
                    - 0.5
                    * Math.cos(
                        ratio * Math.PI
                    );

                double apexSpeed =
                    circuit.getCornerApexSpeed(i);

                double target =
                    Math.min(
                        maximumSpeed,
                        apexSpeed
                    );

                speed =
                    target
                    + (
                        speed - target
                    ) * smooth;
            }
        }

        double variation =
            0.965
            + 0.035
            * Math.sin(
                progress * Math.PI * 2.0
            );

        return clamp(
            speed * variation,
            35.0,
            maximumSpeed
        );
    }

    private boolean isNearBrakingZone(
        Circuit circuit,
        double progress
    )
    {
        int zones =
            circuit.getNumberOfBrakingZones();

        for (int i = 1; i <= zones; i++)
        {
            double position =
                (i - 0.20) / zones;

            if (
                circularDifference(
                    progress,
                    position
                ) < 0.022
            )
            {
                return true;
            }
        }

        return false;
    }

    private boolean isLongStraightArea(
        Circuit circuit,
        double progress
    )
    {
        double ratio =
            circuit.getLongestStraightLength()
            / circuit.getTrackLength();

        double width =
            clamp(
                ratio * 0.55,
                0.05,
                0.18
            );

        return
            progress < width
            || progress > 1.0 - width;
    }

    private double estimateLateralG(
        PerformanceCalculator calculator,
        Circuit circuit,
        double progress,
        double speed
    )
    {
        double strongest = 0;

        for (
            int i = 1;
            i <= circuit.getNumberOfCorners();
            i++
        )
        {
            double position =
                (i - 0.5)
                / circuit.getNumberOfCorners();

            if (
                circularDifference(
                    progress,
                    position
                ) < 0.04
            )
            {
                double radius =
                    circuit.getCornerRadius(i);

                if (radius > 0)
                {
                    double lateralAcceleration =
                        (
                            speed / 3.6
                        )
                        * (
                            speed / 3.6
                        )
                        / radius;

                    strongest =
                        Math.max(
                            strongest,
                            lateralAcceleration
                            / 9.80665
                        );
                }
            }
        }

        return Math.min(strongest, 8.0);
    }

    private double circularDifference(
        double a,
        double b
    )
    {
        double difference =
            Math.abs(a - b);

        return Math.min(
            difference,
            1.0 - difference
        );
    }

    private void addGraphPoint(
        XYChart.Series<Number, Number> series,
        double x,
        double y
    )
    {
        if (series == null)
        {
            return;
        }

        if (series.getData().size() > 1000)
        {
            series.getData().remove(0);
        }

        series.getData().add(
            new XYChart.Data<>(x, y)
        );
    }

    private void finishSimulation()
    {
        running = false;
        lastFrame = -1;

        String winner = getWinnerFromLapTimes();
        double advantage =
            Math.abs(
                mclLapTimeValue
                - dreamLapTimeValue
            );

        String winnerText = getWinnerBanner();

        if (winner.equals("MCL39"))
        {
            verdictLabel.setTextFill(ORANGE);
        }
        else if (winner.equals("Dream Car"))
        {
            verdictLabel.setTextFill(TURQUOISE);
        }
        else
        {
            verdictLabel.setTextFill(TEXT);
        }

        String displayName = dreamDisplayName;

        if (displayName == null || displayName.trim().isEmpty())
        {
            displayName = "Dream Car";
        }

        verdictLabel.setText(
            winnerText
            + "\n"
            + "MCL39 vs "
            + displayName.toUpperCase()
            + "   |   LAP ADVANTAGE: "
            + format(advantage)
            + " s"
        );

        statusLabel.setText(
            "COMPLETE  |  "
            + selectedLaps
            + " LAP(S)"
        );

        updateAnalysisTables();
    }

    private String format(double value)
    {
        return String.format("%.2f", value);
    }

    private double clamp(
        double value,
        double minimum,
        double maximum
    )
    {
        return Math.max(
            minimum,
            Math.min(maximum, value)
        );
    }

    // =====================================================
    // CENTRAL CIRCUIT MAP
    // =====================================================

    private class TrackPanel extends StackPane
    {
        private final WebView webView;
        private final WebEngine webEngine;
        private final Label mapStatus;

        private String currentLayoutId;
        private boolean mapReady;

        TrackPanel()
        {
            setStyle(
                "-fx-background-color: #000000;"
            );

            webView = new WebView();
            webEngine = webView.getEngine();

            webView.setContextMenuEnabled(false);
            webView.setZoom(1.0);
            webView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            webView.setMinSize(0, 0);
            StackPane.setAlignment(webView, Pos.CENTER);
            webView.setStyle(
                "-fx-background-color: #000000;"
            );

            mapStatus = new Label("MAP LOADING...");
            mapStatus.setTextFill(MUTED);
            mapStatus.setFont(Font.font("Arial", 10));

            getChildren().addAll(
                webView,
                mapStatus
            );

            StackPane.setAlignment(
                mapStatus,
                Pos.BOTTOM_LEFT
            );

            StackPane.setMargin(
                mapStatus,
                new Insets(0, 0, 8, 10)
            );

            webEngine.getLoadWorker().stateProperty().addListener(
                (obs, oldState, newState) ->
                {
                    if (newState == Worker.State.SUCCEEDED)
                    {
                        mapReady = true;
                        mapStatus.setText(
                            "LIVE CIRCUIT MAP"
                        );

                        styleSvg();
                        installMarkers();
                        setCarProgress(0, 0);
                    }
                    else if (newState == Worker.State.FAILED)
                    {
                        mapReady = false;
                        mapStatus.setText(
                            "MAP LOAD FAILED - CHECK SVG FILE"
                        );
                    }
                }
            );
        }

        void loadLayout(String circuitName)
        {
            currentLayoutId =
                LAYOUT_IDS.get(circuitName);

            mapReady = false;

            if (currentLayoutId == null)
            {
                mapStatus.setText(
                    "NO MAP LAYOUT"
                );
                return;
            }

            mapStatus.setText(
                "LOADING " + circuitName.toUpperCase()
            );

            File localFile = findLocalMap(
                currentLayoutId + ".svg"
            );

            if (localFile != null)
            {
                try
                {
                    // Read the SVG into memory and load it as page content.
                    // This avoids WebView file-URI loading issues on Windows.
                    String svg = Files.readString(
                        localFile.toPath(),
                        StandardCharsets.UTF_8
                    );

                    webEngine.loadContent(
                        svg,
                        "image/svg+xml"
                    );
                }
                catch (IOException ex)
                {
                    mapReady = false;
                    mapStatus.setText(
                        "MAP READ FAILED: " + ex.getMessage()
                    );
                }

                return;
            }

            final String requestedLayout =
                currentLayoutId;

            Task<String> downloadTask = new Task<String>()
            {
                @Override
                protected String call() throws Exception
                {
                    String urlString =
                        REMOTE_BASE
                        + requestedLayout
                        + ".svg";

                    URL url = new URL(urlString);

                    HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(15000);
                    connection.setRequestProperty(
                        "User-Agent",
                        "RaceSpec-VehicleDynamics"
                    );

                    int responseCode =
                        connection.getResponseCode();

                    if (responseCode != 200)
                    {
                        throw new IOException(
                            "HTTP " + responseCode
                        );
                    }

                    byte[] data;

                    try (InputStream input =
                        connection.getInputStream())
                    {
                        data = input.readAllBytes();
                    }
                    finally
                    {
                        connection.disconnect();
                    }

                    String svg =
                        new String(
                            data,
                            StandardCharsets.UTF_8
                        );

                    File mapFolder =
                        new File("maps");

                    if (!mapFolder.exists())
                    {
                        mapFolder.mkdirs();
                    }

                    Files.writeString(
                        new File(
                            mapFolder,
                            requestedLayout + ".svg"
                        ).toPath(),
                        svg,
                        StandardCharsets.UTF_8
                    );

                    return svg;
                }
            };

            downloadTask.setOnSucceeded(
                event ->
                {
                    if (
                        currentLayoutId != null
                        && requestedLayout.equals(
                            currentLayoutId
                        )
                    )
                    {
                        webEngine.loadContent(
                            downloadTask.getValue(),
                            "image/svg+xml"
                        );
                    }
                }
            );

            downloadTask.setOnFailed(
                event ->
                {
                    mapReady = false;

                    Throwable error =
                        downloadTask.getException();

                    String message =
                        error == null
                        ? "UNKNOWN ERROR"
                        : error.getMessage();

                    mapStatus.setText(
                        "MAP DOWNLOAD FAILED: "
                        + message
                    );
                }
            );

            Thread thread =
                new Thread(downloadTask);

            thread.setDaemon(true);
            thread.start();
        }

        private File findLocalMap(String fileName)
        {
            File[] candidates =
            {
                new File(fileName),
                new File("maps" + File.separator + fileName),
                new File("data" + File.separator + "maps"
                    + File.separator + fileName)
            };

            for (File file : candidates)
            {
                if (file.exists() && file.isFile())
                {
                    return file;
                }
            }

            return null;
        }

        void setCarProgress(
            double mclProgress,
            double dreamProgress
        )
        {
            if (!mapReady)
            {
                return;
            }

            runMarkerScript(
                "mclMarker",
                mclProgress
            );

            runMarkerScript(
                "dreamMarker",
                dreamProgress
            );
        }

        private void styleSvg()
        {
            String script =
                "(function(){"
                + "var svg=document.documentElement;"
                + "svg.setAttribute('width','100%');"
                + "svg.setAttribute('height','100%');"
                + "svg.setAttribute('preserveAspectRatio','xMidYMid meet');"
                + "svg.style.background='#000000';"
                + "svg.style.display='block';"
                + "svg.style.margin='0 auto';"
                + "var paths=document.querySelectorAll('path');"
                + "var minX=Infinity,minY=Infinity,maxX=-Infinity,maxY=-Infinity;"
                + "for(var i=0;i<paths.length;i++){"
                + "var p=paths[i];"
                + "if(typeof p.getBBox!=='function'){continue;}"
                + "var b=p.getBBox();"
                + "minX=Math.min(minX,b.x);"
                + "minY=Math.min(minY,b.y);"
                + "maxX=Math.max(maxX,b.x+b.width);"
                + "maxY=Math.max(maxY,b.y+b.height);"
                + "p.style.fill='none';"
                + "p.style.stroke='#8D98A6';"
                + "p.style.strokeWidth='10px';"
                + "p.style.strokeLinecap='round';"
                + "p.style.strokeLinejoin='round';"
                + "}"
                + "if(isFinite(minX)&&isFinite(minY)){"
                + "var pad=35;"
                + "svg.setAttribute('viewBox',(minX-pad)+' '+(minY-pad)+' '+((maxX-minX)+pad*2)+' '+((maxY-minY)+pad*2));"
                + "}"
                + "document.documentElement.style.overflow='hidden';"
                + "document.body && (document.body.style.margin='0');"
                + "document.body && (document.body.style.background='#000000');"
                + "document.body && (document.body.style.display='flex');"
                + "document.body && (document.body.style.alignItems='center');"
                + "document.body && (document.body.style.justifyContent='center');"
                + "document.body && (document.body.style.width='100vw');"
                + "document.body && (document.body.style.height='100vh');"
                + "})()";

            try
            {
                webEngine.executeScript(script);
            }
            catch (Exception ex)
            {
                mapStatus.setText(
                    "MAP STYLE WARNING"
                );
            }
        }

        private void installMarkers()
        {
            String script =
                "(function(){"
                + "var svg=document.documentElement;"
                + "function add(id,fill){"
                + "var old=document.getElementById(id);"
                + "if(old){old.remove();}"
                + "var c=document.createElementNS('http://www.w3.org/2000/svg','circle');"
                + "c.setAttribute('id',id);"
                + "c.setAttribute('r','7');"
                + "c.setAttribute('fill',fill);"
                + "c.setAttribute('stroke','#FFFFFF');"
                + "c.setAttribute('stroke-width','2');"
                + "c.style.filter='drop-shadow(0 0 4px '+fill+')';"
                + "svg.appendChild(c);"
                + "}"
                + "add('mclMarker','#FF8A00');"
                + "add('dreamMarker','#19E6D0');"
                + "})()";

            try
            {
                webEngine.executeScript(script);
            }
            catch (Exception ex)
            {
                mapReady = false;
                mapStatus.setText(
                    "MARKER SETUP FAILED"
                );
            }
        }

        private void runMarkerScript(
            String markerId,
            double progress
        )
        {
            double safeProgress =
                clamp(progress, 0, 1);

            String script =
                "(function(){"
                + "var paths=Array.from(document.querySelectorAll('path'))"
                + ".filter(function(p){return typeof p.getTotalLength==='function';});"
                + "if(paths.length===0){return 'NO_PATH';}"
                + "var p=paths[0];"
                + "var best=p.getTotalLength();"
                + "for(var i=1;i<paths.length;i++){"
                + "var len=paths[i].getTotalLength();"
                + "if(len>best){best=len;p=paths[i];}"
                + "}"
                + "var pt=p.getPointAtLength(best*"
                + safeProgress
                + ");"
                + "var c=document.getElementById('"
                + markerId
                + "');"
                + "if(!c){return 'NO_MARKER';}"
                + "c.setAttribute('cx',pt.x);"
                + "c.setAttribute('cy',pt.y);"
                + "return 'OK';"
                + "})()";

            try
            {
                Object result =
                    webEngine.executeScript(script);

                if (
                    result != null
                    && result.toString().equals("NO_PATH")
                )
                {
                    mapStatus.setText(
                        "MAP PATH NOT FOUND"
                    );
                }
            }
            catch (Exception ex)
            {
                mapStatus.setText(
                    "MARKER UPDATE ERROR"
                );
            }
        }
    }

}
