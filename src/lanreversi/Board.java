package lanreversi;

import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class Board extends JPanel{

    //Количество клеток на игровом поле
    private final int cols;    //Количество столбцов
    private final int rows;    //Количество строк

    private final Cell[][] c;  //Клетки игрового поля

    public Board(int cols, int rows) {
        super(new GridLayout(rows, cols, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.cols=cols;
        this.rows=rows;
        c=new Cell[rows][cols];
        for(int i=0;i<rows;i++)
            for(int j=0;j<cols;j++){
                c[i][j]=new Cell(i, j, Cell.EMPTY, 0);
                this.add(c[i][j]);
            }
    }
    
}
