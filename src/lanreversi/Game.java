package lanreversi;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import javax.swing.SwingUtilities;
import static lanreversi.JReversi.*;

//Данный класс реализует локальную игру
public class Game extends Thread {

    private final int[][] m;              //Игровое поле
    private final int rows;               //Количество строк на игровом поле
    private final int cols;               //Количество столбцов на игровом поле

    //Константы для представления содержимого ячеек игрового поля
    private static final int PLAYER = 1;
    private static final int OPPENENT = -1;
    private static final int EMPTY = 0;

    private Coord playerStroke = null;    //Координаты клетки, в которую походил игрок

    //Вспомогательный список координат
    LinkedList<Coord> l = new LinkedList<>();

    //В конструктор передается rows - количество строк на игровом поле, cols - количество столбцов на игровом поле
    public Game(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        m = new int[rows][cols];
    }

    @Override
    public void run() {

        System.out.println("Игра запущена...");

        //Сбрасываем результаты прошлой игры
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    gui.clearBoard();
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
        }
        m[rows / 2 - 1][cols / 2 - 1] = PLAYER;
        m[rows / 2][cols / 2] = PLAYER;
        m[rows / 2][cols / 2 - 1] = OPPENENT;
        m[rows / 2 - 1][cols / 2] = OPPENENT;

        //Объявляем внутренние вспомогательные переменные
        //Координаты очередного хода
        int xStroke;
        int yStroke;

        //Основной цикл, реализующий игру
        while (true) {
            //Первый этап - показать игроку доступные для его хода ячейки
            l = getAvailableCellList(PLAYER);
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        gui.setEnabledCells(l);
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) {
            }

            //Фрагмент кода, получающий ход игрока
            synchronized (this) {
                playerStroke = null;
                while (true) {
                    if (playerStroke != null) {
                        xStroke = playerStroke.x;
                        yStroke = playerStroke.y;
                        break;
                    }
                    try {
                        wait(50);
                    } catch (InterruptedException ex) {

                        System.out.println("Игра остановлена...");

                        return;
                    }
                }
            }

            System.out.println("Щелчок в ячейку в строке "+yStroke+" "+" столбце "+xStroke);

        }

    }

    //Метод получает список ячеек доступных для установки фишки цвета n
    private LinkedList<Coord> getAvailableCellList(int n) {
        LinkedList<Coord> l = new LinkedList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (isCellAvailable(n, i, j)) {
                    l.add(new Coord(i, j));
                }
            }
        }
        return l;
    }

    //метод определяет доступность ячейки для установки фишки
    private boolean isCellAvailable(int n, int y, int x) {
        return (getCellRate(n, y, x) > 0);
    }

    //Метод подсчитывает рейтинг ячейки
    private int getCellRate(int n, int y, int x) {
        if ((y < 0) | (y >= rows) | (x < 0) | (x >= cols)) {
            return 0;
        }
        if (m[y][x] != EMPTY) {
            return 0;
        }

        int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
        int[] dy = {-1, -1, 0, 1, 1, 1, 0, -1};
        int result = 0;
        int k;                 //Множитель
        int x0;                //Промежуточные координаты
        int y0;
        int s;                 //Количество клеток, фишки в которых могут быть перевернуты по текущему направлению t

        //Во внешнем цикле перебираем направления
        for (int t = 0; t < 8; t++) {
            k = 0;
            s = 0;
            while (true) {
                k++;
                x0 = x + k * dx[t];
                y0 = y + k * dy[t];
                if ((y0 < 0) | (y0 >= rows) | (x0 < 0) | (x0 >= cols)) {
                    s = 0;
                    break;
                }
                if (m[y0][x0] == EMPTY) {
                    s = 0;
                    break;
                }
                if (m[y0][x0] == n) {
                    break;
                }
                s++;
            }
            result += s;
        }
        return result;
    }

    //Метод принимает координаты выбранной пользователем ячейки
    public void playerStroke(Coord coord) {
        synchronized (this) {
            playerStroke = coord;
        }
    }

}
