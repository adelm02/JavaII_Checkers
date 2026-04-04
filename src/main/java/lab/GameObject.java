package lab;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;
import java.io.Serializable;

@Log
@ToString
public abstract class GameObject implements DrawableSimulable, Positionable, Serializable {
    private static final long serialVersionUID = 1L;

    @Setter protected transient Image image;
    private final double pieceScale = 1.8;

    @Getter protected int row;
    @Getter protected int col;

    public GameObject(Image image, int row, int col) {
        this.image = image;
        this.row = row;
        this.col = col;
    }

    @Override
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public void draw(GraphicsContext gc, int squareSize) {
        if (image == null) return;
        double pieceSize = squareSize * pieceScale;
        double aspect = image.getHeight() / image.getWidth();
        double drawWidth = pieceSize;
        double drawHeight = pieceSize * aspect;
        double offsetX = (squareSize - drawWidth) / 2;
        double offsetY = (squareSize - drawHeight) / 2;
        gc.drawImage(image, col * squareSize + offsetX, row * squareSize + offsetY, drawWidth, drawHeight);
    }
}