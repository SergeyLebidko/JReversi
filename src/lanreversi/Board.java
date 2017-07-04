package lanreversi;

import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public final class Board extends JPanel{

    //Количество клеток на игровом поле
    private final int cols;    //Количество столбцов
    private final int rows;    //Количество строк

    private int playerColor=Cell.BLACK;    //Цвет фишек игрока
    private int opponentColor=Cell.WHITE;  //Цвет фишек противника
    private int currentStyle=0;            //Текущий стиль ячеек

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
        clear();
    }

    //Метод сбрасывает игровое поле до начальной конфигурации
    public void clear(){
        for(int i=0;i<rows;i++)
            for(int j=0;j<cols;j++){
                c[i][j].setContent(Cell.EMPTY);
                c[i][j].setStyle(currentStyle);
            }
        c[rows/2-1][cols/2-1].setContent(playerColor);
        c[rows/2][cols/2].setContent(playerColor);
        c[rows/2][cols/2-1].setContent(opponentColor);
        c[rows/2-1][cols/2].setContent(opponentColor);
    }

    //Метод возвращает текущий цвет фишек игрока
    public int getPlayerColor(){
        return playerColor;
    }

    //Метод возвращает текущий цвет фишек фишек противника
    public int getOpponentColor(){
        return opponentColor;
    }

    //Метод изменяет цвет фишек игрока и противника
    public void revert(){
        int t;
        t=playerColor;
        playerColor=opponentColor;
        opponentColor=t;
        for(int i=0;i<rows;i++)
            for(int j=0;j<cols;j++){
                if(c[i][j].isEmpty())continue;
                if(c[i][j].getContent()==playerColor){
                    c[i][j].setContent(opponentColor);
                    continue;
                }
                c[i][j].setContent(playerColor);
            }
    }

    //Метод возвращает имя текущего стиля
    public String getCurrentStyleName(){
        return Cell.getStyleNames()[currentStyle];
    }

    //Метод изменяет стиль фишек
    public void nextStyle(){
        currentStyle++;
        if(currentStyle>=Cell.getCountStyles())currentStyle=0;
        for(int i=0;i<rows;i++)
            for(int j=0;j<cols;j++)c[i][j].setStyle(currentStyle);
    }

}
