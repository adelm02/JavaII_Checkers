package lab;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;

@ToString(callSuper = true)
public class Piece extends GameObject implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum PieceColor { WHITE, BLACK }

    @Getter @Setter private boolean queen;
    @Getter private final PieceColor color;

    public Piece(Image image, int row, int col, PieceColor color) {
        super(image, row, col);
        this.color = color;
    }

    @Override
    public void draw(javafx.scene.canvas.GraphicsContext gc, int squareSize) {
        super.draw(gc, squareSize);
    }
}