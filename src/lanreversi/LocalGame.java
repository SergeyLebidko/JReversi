package lanreversi;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import javax.swing.SwingUtilities;
import static lanreversi.JReversi.*;

//Данный класс реализует локальную игру
public class LocalGame extends Thread {

    private int[][] m;                    //Игровое поле
    private final int rows;               //Количество строк на игровом поле
    private final int cols;               //Количество столбцов на игровом поле

    //Константы для представления содержимого ячеек игрового поля
    private static final int PLAYER = 1;
    private static final int OPPONENT = -1;
    private static final int EMPTY = 0;

    private Coord playerStroke = null;    //Координаты клетки, в которую походил игрок

    //Вспомогательные переменные для представления координат
    private int xStroke;
    private int yStroke;

    //Вспомогательный список для представления наборов координат
    LinkedList<Coord> l = new LinkedList<>();

    //В конструктор передается rows - количество строк на игровом поле, cols - количество столбцов на игровом поле
    public LocalGame(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        m = new int[rows][cols];
    }

    @Override
    public void run() {
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
        m[rows / 2][cols / 2 - 1] = OPPONENT;
        m[rows / 2 - 1][cols / 2] = OPPONENT;

        //Объявляем внутренние вспомогательные переменные
        //Координаты очередного хода


        //Признаки отсутствия доступных ходов. Если оба равны true, то игра окончена
        boolean notPlayerAvailable = false;        //Если равна true, нет доступных ходов у игрока
        boolean notOpponentAvailable = false;      //Если равна true, нет доступных ходов у компьютера

        //Основной цикл, реализующий игру
        while (true) {
            //Первый этап ищем ячейки, в которые может походить игрок
            l = getAvailableCellList(m, PLAYER);
            notPlayerAvailable = l.isEmpty();
            if (!notPlayerAvailable) {

                //Помечаем цветом доступные игроку клетки
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            gui.setEnabledCells(l);
                        }
                    });
                } catch (InterruptedException | InvocationTargetException ex) {
                }

                //Получаем ход игрока
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
                            return;
                        }
                    }
                }

                //Обрабатываем ход игрока
                //Сперва делаем все ячейки поля недоступными и отображаем ход игрока
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            gui.setEnabledCells(null);
                            gui.setPlayerChecker(new Coord(yStroke, xStroke));
                        }
                    });
                } catch (InterruptedException | InvocationTargetException ex) {
                }

                //Теперь получаем список перевернувшихся фишек и отображаем его на экране
                l=getRevertCells(m, PLAYER, yStroke, xStroke);
                try {
                    showChekersList(l, PLAYER);
                } catch (InterruptedException ex) {
                    return;
                }

                //Теперь фиксируем изменения после хода в игровой матрице
                m=getNewMatr(m, PLAYER, yStroke, xStroke);

            }

            //Второй этап - ищем ячейки, в которые может походить компьютер
            l = getAvailableCellList(m, OPPONENT);
            notOpponentAvailable=l.isEmpty();
            if(!notOpponentAvailable){

                //Вставить код расчета хода компьютера...

            }

            //Третий этап - проверка завершения работы
            if(notPlayerAvailable & notOpponentAvailable){

                //Вставить код вывода сообщения о завершении игры
                System.out.println("Игра окончена...");
                return;

            }
        }

    }

    //Метод необходим для отображения очередного хода на экране
    private void showChekersList(LinkedList<Coord> coordList, int n) throws InterruptedException{
        for(Coord coord: coordList){
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        if(n==PLAYER)gui.setPlayerChecker(coord);
                        if(n==OPPONENT)gui.setOpponentChecker(coord);
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) {
            }
            Thread.sleep(500);
        }
    }

    //Метод возвращает список ячеек, которые перевернутся после хода y,x в матрицу m0 фишкой цвета n
    private LinkedList<Coord> getRevertCells(int[][] m0, int n, int y, int x){
        LinkedList<Coord> res=new LinkedList<>();
        int r = m0.length;
        int c = m0[0].length;

        //Проверяем первый особый случай: ячейка выходит за пределы матрицы
        if ((y < 0) | (y >= r) | (x < 0) | (x >= c)) {
            return res;
        }

        //Проверяем второй особый случай: ячейка, в которую пытаемся поставить фишку не пуста
        if (m0[y][x] != EMPTY) {
            return res;
        }

        int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
        int[] dy = {-1, -1, 0, 1, 1, 1, 0, -1};
        int result = 0;
        int k;                 //Множитель
        int x0;                //Промежуточные координаты
        int y0;
        LinkedList<Coord> s = new LinkedList<>();    //Промежуточный список координат

        //Во внешнем цикле перебираем направления
        for (int t = 0; t < 8; t++) {
            k = 0;
            s.clear();
            while (true) {
                k++;
                x0 = x + k * dx[t];
                y0 = y + k * dy[t];
                if ((y0 < 0) | (y0 >= r) | (x0 < 0) | (x0 >= c)) {
                    s.clear();
                    break;
                }
                if (m0[y0][x0] == EMPTY) {
                    s.clear();
                    break;
                }
                if (m0[y0][x0] == n) {
                    break;
                }
                s.add(new Coord(y0, x0));
            }
            if (!s.isEmpty()) res.addAll(s);
        }

        //Возвращаем результат
        return res;
    }

    //Метод возвращает матрицу, в которую будет преобразована матрица m0, согласно ходу фишки цвета n в клетку y,x
    private int[][] getNewMatr(int[][] m0, int n, int y, int x) {
        //Сначала создаем копию исходной матрицы
        int[][] res;
        int r = m0.length;
        int c = m0[0].length;
        res = new int[r][c];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                res[i][j] = m0[i][j];
            }
        }

        //Проверяем первый особый случай: ячейка выходит за пределы матрицы
        if ((y < 0) | (y >= r) | (x < 0) | (x >= c)) {
            return res;
        }

        //Проверяем второй особый случай: ячейка, в которую пытаемся поставить фишку не пуста
        if (m0[y][x] != EMPTY) {
            return res;
        }

        //Проверяем третий особый случай: ячейка y,x - недоступна для хода
        if(!isCellAvailable(res, n, y, x)) return res;

        //Фиксируем ход
        res[y][x]=n;

        int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
        int[] dy = {-1, -1, 0, 1, 1, 1, 0, -1};
        int result = 0;
        int k;                 //Множитель
        int x0;                //Промежуточные координаты
        int y0;
        LinkedList<Coord> s = new LinkedList<>();    //Промежуточный список координат

        //Во внешнем цикле перебираем направления
        for (int t = 0; t < 8; t++) {
            k = 0;
            s.clear();
            while (true) {
                k++;
                x0 = x + k * dx[t];
                y0 = y + k * dy[t];
                if ((y0 < 0) | (y0 >= r) | (x0 < 0) | (x0 >= c)) {
                    s.clear();
                    break;
                }
                if (m0[y0][x0] == EMPTY) {
                    s.clear();
                    break;
                }
                if (m0[y0][x0] == n) {
                    break;
                }
                s.add(new Coord(y0, x0));
            }
            if (!s.isEmpty()) {
                for (Coord coord : s) {
                    res[coord.y][coord.x] = n;
                }
            }
        }

        //Возвращаем результат
        return res;
    }

    //Метод подсчитывает количество очков, которые в матрице m0 набирает игрок с фишками n
    private int getScore(int[][] m0, int n) {
        int result = 0;
        int r = m0.length;
        int c = m0[0].length;
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                if (m0[i][j] == n) {
                    result++;
                }
            }
        }
        return result;
    }

    //Метод возвращает список ячеек в матрице m0 доступных для установки фишки цвета n
    private LinkedList<Coord> getAvailableCellList(int[][] m0, int n) {
        LinkedList<Coord> l = new LinkedList<>();
        int r = m0.length;
        int c = m0[0].length;
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                if (isCellAvailable(m0, n, i, j)) {
                    l.add(new Coord(i, j));
                }
            }
        }
        return l;
    }

    //Метод определяет доступность ячейки y,x в матрице m0 для установки фишки цвета n (ячейка считается доступной, если имеет ненулевой рейтинг)
    private boolean isCellAvailable(int[][] m0, int n, int y, int x) {
        return (getCellRate(m0, n, y, x) > 0);
    }

    //Метод подсчитывает рейтинг ячейки y,x в матрице m0 для цвета n (рейтинг - количество фишек, которые перевернутся после этого хода)
    private int getCellRate(int[][] m0, int n, int y, int x) {
        int r = m0.length;
        int c = m0[0].length;
        if ((y < 0) | (y >= r) | (x < 0) | (x >= c)) {
            return 0;
        }
        if (m0[y][x] != EMPTY) {
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
                if ((y0 < 0) | (y0 >= r) | (x0 < 0) | (x0 >= c)) {
                    s = 0;
                    break;
                }
                if (m0[y0][x0] == EMPTY) {
                    s = 0;
                    break;
                }
                if (m0[y0][x0] == n) {
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
