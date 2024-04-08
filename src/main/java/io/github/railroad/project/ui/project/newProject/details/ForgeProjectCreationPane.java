package io.github.railroad.project.ui.project.newProject.details;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.railroad.project.Project;
import io.github.railroad.project.data.ForgeProjectData;
import io.github.railroad.task.TaskManager;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import static io.github.railroad.utility.FileHandler.copyUrlToFile;
import static io.github.railroad.utility.FileHandler.UnZipFile;
import static io.github.railroad.Railroad.manager;

public class ForgeProjectCreationPane extends BorderPane {
    private final ForgeProjectData data;

    private final TaskManager taskManager = new TaskManager();
    private final MFXProgressSpinner progressSpinner = new MFXProgressSpinner();
    private final Label progressPercentageLabel = new Label("0%");
    private final MFXProgressBar progressBar = new MFXProgressBar();
    private final Label progressLabel = new Label("");
    private final Label taskLabel = new Label();
    private final Label errorLabel = new Label();

    private final ListProperty<Throwable> errors = new SimpleListProperty<>(FXCollections.observableArrayList());

    public ForgeProjectCreationPane(ForgeProjectData data) {
        this.data = data;

        progressSpinner.progressProperty().bind(progressBar.progressProperty());
        progressSpinner.setRadius(50);
        progressPercentageLabel.textProperty().bind(progressBar.progressProperty().multiply(100).asString("%.0f%%"));
        setCenter(progressSpinner);

        var progressBox = new VBox(10, progressPercentageLabel, progressBar, progressLabel, taskLabel);
        progressBox.setAlignment(Pos.CENTER);
        setBottom(progressBox);

        var errorIcon = new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
        errorIcon.setIconSize(24);
        errorIcon.setIconColor(Color.ORANGERED);
        errorLabel.setGraphic(errorIcon);
        errorLabel.textProperty().bind(errors.sizeProperty().asString().concat(" errors"));
        errorLabel.visibleProperty().bind(errors.sizeProperty().greaterThan(0));
        errorLabel.managedProperty().bind(errorLabel.visibleProperty());

        errors.addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                progressBox.getChildren().add(errorLabel);
            } else {
                progressBox.getChildren().remove(errorLabel);
            }
        });

        setTop(new Label("Creating project..."));
        setAlignment(getTop(), Pos.CENTER);
        progressBar.setProgress(0);
        // Download Link
        //https://maven.minecraftforge.net/net/minecraftforge/forge/1.20.2-48.1.0/forge-1.20.2-48.1.0-mdk.zip
        String filenametodownload = this.data.minecraftVersion().id() + "-" + this.data.forgeVersion().id();
        String projectPath = (this.data.projectPath().replace("\\","/") + "/" + this.data.projectName());
        try {
            Files.createDirectories(Paths.get(projectPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        copyUrlToFile(
                "https://maven.minecraftforge.net/net/minecraftforge/forge/"+filenametodownload+"/forge-"+filenametodownload+"-mdk.zip",
                Path.of(projectPath+"/"+filenametodownload+".zip"));
        progressBar.setProgress(.2);
        try {
            UnZipFile(projectPath+"/"+filenametodownload+".zip",projectPath);
            progressBar.setProgress(.5);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        manager.NewProject(new Project(Path.of(projectPath), this.data.projectName()));
        progressBar.setProgress(.6);
    }
}
