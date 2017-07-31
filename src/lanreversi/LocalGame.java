package lanreversi;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import javax.swing.SwingUtilities;
import static lanreversi.JReversi.*;

//Данный класс реализует локальную игру
public class LocalGame extends Thread {

    //Игровое поле
    private int[][] m;

    //Константы для представления содержимого ячеек игрового поля
    private static final int PLAYER = 1;
    private static final int COMPUTER = -1;
    private static final int EMPTY = 0;

    //Максимальная и минимальная глубина перебора, используемые при поиске очередного хода
    private static final int MAX_DEPTH = 6;
    private static final int MIN_DEPTH = 5;

    private Coord playerStroke = null;    //Координаты клетки, в которую походил игрок

    //Вспомогательные переменные для представления координат
    private int xStroke;
    private int yStroke;

    //Вспомогательный список для представления наборов координат
    private LinkedList<Coord> l = new LinkedList<>();

    //Вспомогательная переменная для представления координат отдельной ячейки
    private Coord coordMaxRate;

    //В конструктор передается rows - количество строк на игровом поле, cols - количество столбцов на игровом поле
    public LocalGame(int rows, int cols) {
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
                    gui.setText1(playerName);
                    gui.setText2("2");
                    gui.setText3("2");
                    gui.setText4("Компьютер");
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
        }
        int rows = m.length;
        int cols = m[0].length;
        m[rows / 2 - 1][cols / 2 - 1] = PLAYER;
        m[rows / 2][cols / 2] = PLAYER;
        m[rows / 2][cols / 2 - 1] = COMPUTER;
        m[rows / 2 - 1][cols / 2] = COMPUTER;

        //Объявляем внутренние вспомогательные переменные
        //Признаки отсутствия доступных ходов. Если оба равны true, то игра окончена
        boolean isPlayerCellListEmpty = false;        //Если равна true, нет доступных ходов у игрока
        boolean isComputerCellListEmpty = false;      //Если равна true, нет доступных ходов у компьютера

        //Основной цикл, реализующий игру
        while (true) {
            //Первый этап ищем ячейки, в которые может походить игрок
            l = getAvailableCellList(m, PLAYER);
            isPlayerCellListEmpty = l.isEmpty();
            if (!isPlayerCellListEmpty) {
                //Помечаем цветом доступные игроку клетки
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            gui.setEnabledCells(l);
                        }
                    });
                } catch (InvocationTargetException ex) {
                } catch (InterruptedException ex) {
                    return;
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
                //Отключаем доступность ячеек и отображаем ход игрока
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            gui.setEnabledCells(null);
                            gui.setPlayerChecker(new Coord(yStroke, xStroke));
                        }
                    });
                } catch (InterruptedException ex) {
                    return;
                } catch (InvocationTargetException ex) {
                }
                //Получаем список ячеек, которые перевернутся в результате хода игрока
                l = getRevertCellsList(m, PLAYER, yStroke, xStroke);
                //Фиксируем изменения на игровом поле после хода игрока
                m = getNextMatr(m, PLAYER, yStroke, xStroke);
                //Отображаем количество набранных игроком и компьютером очков на табло
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            gui.setText2("" + getScore(m, PLAYER));
                            gui.setText3("" + getScore(m, COMPUTER));
                        }
                    });
                } catch (InterruptedException ex) {
                    return;
                } catch (InvocationTargetException ex) {
                }
                //Переворачиваем фишки на экране
                try {
                    showChekersList(l, PLAYER);
                } catch (InterruptedException ex) {
                    return;
                }
            }

            //Второй этап - поиск хода компьютера
            l = getAvailableCellList(m, COMPUTER);

            System.out.println("Доступно ходов компьютеру: "+l.size());

            isComputerCellListEmpty = l.isEmpty();
            if (!isComputerCellListEmpty) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            gui.setText4("Компьютер (думаю...)");
                        }
                    });
                } catch (InterruptedException ex) {
                    return;
                } catch (InvocationTargetException ex) {
                }
                //Ищем ход с максимальным рейтингом
                int maxRate = Integer.MIN_VALUE; //Переменная для хранения максимального рейтинга, достижимого из текущего набора доступных ходов
                int rate;                        //Переменная для хранения рейтинга текущего исследуемого хода
                int currentDepth=0;              //Текущая глубина перебора
                coordMaxRate = null;             //Координаты хода с максимальным рейтингом
                if((l.size()>1) & (l.size()<6))currentDepth=MAX_DEPTH;
                if(l.size()>=6)currentDepth=MIN_DEPTH;

                System.out.println("  текущая глубина перебора: "+currentDepth);

                for (Coord coord : l) {
                    rate = getRate(getNextMatr(m, COMPUTER, coord.y, coord.x), COMPUTER, currentDepth);

                    System.out.println("  рейтинг хода: "+rate);

                    if (rate > maxRate) {
                        maxRate = rate;
                        coordMaxRate = coord;
                    }
                }

                System.out.println();

                //Обрабатываем ход компьютера
                //Отображаем ход компьютера
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            gui.setOpponentChecker(coordMaxRate);
                        }
                    });
                } catch (InterruptedException ex) {
                    return;
                } catch (InvocationTargetException ex) {
                }
                //Получаем список ячеек, которые перевернутся в результате хода компьютера
                l = getRevertCellsList(m, COMPUTER, coordMaxRate.y, coordMaxRate.x);
                //Отражаем изменения на игровом поле после хода игрока
                m = getNextMatr(m, COMPUTER, coordMaxRate.y, coordMaxRate.x);
                //Переворачиваем фишки на экране
                try {
                    showChekersList(l, COMPUTER);
                } catch (InterruptedException ex) {
                    return;
                }
                //Отображаем количество набранных игроком и компьютером очков на табло
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            gui.setText2("" + getScore(m, PLAYER));
                            gui.setText3("" + getScore(m, COMPUTER));
                            gui.setText4("Компьютер");
                        }
                    });
                } catch (InterruptedException ex) {
                    return;
                } catch (InvocationTargetException ex) {
                }
            }

            //Третий этап - проверка завершения работы
            if (isPlayerCellListEmpty & isComputerCellListEmpty) {
                final int playerScore = getScore(m, PLAYER);
                final int opponentScore = getScore(m, COMPUTER);
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            if (playerScore > opponentScore) {
                                gui.showMsg("Вы победили!");
                            }
                            if (playerScore < opponentScore) {
                                gui.showMsg("Вы проиграли...");
                            }
                            if (playerScore == opponentScore) {
                                gui.showMsg("Ничья! Победила дружба!");
                            }
                        }
                    });
                } catch (InterruptedException ex) {
                    return;
                } catch (InvocationTargetException ex) {
                }
                return;
            }

        }

    }

    //Методы взаимодействия с пользователем
    //Метод необходим для переворачивания фишек на экране после очередного хода игрока или компьютера
    private void showChekersList(LinkedList<Coord> coordList, int n) throws InterruptedException {
        for (Coord coord : coordList) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        if (n == PLAYER) {
                            gui.setPlayerChecker(coord);
                        }
                        if (n == COMPUTER) {
                            gui.setOpponentChecker(coord);
                        }
                    }
                });
            } catch (InterruptedException ex) {
                throw ex;
            } catch (InvocationTargetException ex) {
            }
            Thread.sleep(150);
        }
    }

    //Метод принимает координаты выбранной пользователем ячейки
    public void playerStroke(Coord coord) {
        synchronized (this) {
            playerStroke = coord;
        }
    }

    //Вспомогательные методы
    //Метод, реализующий рекурсивный поиск оценки очередного хода
    //На входе:
    //m - позиция на игровом поле, которую требуется оценить
    //n - цвет фишек, которые сделали ход
    //depth - текущая глубина рекурсии (если равна 0, рекурсивные вызовы прекращаются)
    private int getRate(int[][] m, int n, int depth) {
        //Получаем размеры переданной в метод матрицы
        int rows=m.length;
        int cols=m[0].length;

        int pScore=0;    //Количество фишек игрока на игровом поле
        int cScore=0;    //Количество фишек компьютера на игровом поле
        int rate=0;      //Рейтинг позиции

        //Переменные, используемые для обнаружения позции, в которой игра считается завершенной
        boolean pAv = false;        //Равен true, если в матрице есть доступные для игрока (PLAYER Available) ходы
        boolean cAv = false;        //Равен true, если в матрице есть доступные для компьютера (COMPUTER Available) ходы
        boolean isEndPos;           //Равен true, если позиция в матрице m - конечна

        int content;     //Содержимое ячейки

        //Подсчитываем рейтинг текущей позиции
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                content = m[i][j];
                switch (content) {
                    case EMPTY: {
                        if(!pAv)pAv=isCellAvailable(m, PLAYER, i, j);
                        if(!cAv)cAv=isCellAvailable(m, COMPUTER, i, j);
                        break;
                    }
                    case PLAYER: {
                        pScore++;
                        break;
                    }
                    case COMPUTER: {
                        cScore++;
                    }
                }
            }
        }
        isEndPos=!(pAv & cAv);

        //Если полученная в матрице m позиция является финальной (игра окончена, ни у кого из игроков больше нет доступных ходов)
        if(isEndPos){
            if(pScore==cScore)return 0;
            rate=10;
            rate=(int)Math.pow(rate, depth+1);
            if(pScore>cScore)rate*=-1;
            return rate;
        }

        //Если полученная в матрице m позиция не является финальной и дальнейшие ходы игроков возможны
        if(pScore>cScore)rate=-1;
        if(pScore==cScore)rate=0;
        if(pScore<cScore)rate=1;

        //Если достигнута максимальная глубина перебора, то возвращаем результат
        if(depth==0)return rate;

        //Рекурсивно вычисляем рейтинг для доступных ходов
        int nNext;    //Цвет фишек, которые ходят следующими
        if(n==PLAYER & cAv)nNext=COMPUTER; else nNext=PLAYER;
        if(n==COMPUTER & pAv)nNext=PLAYER; else nNext=COMPUTER;

        //Получаем список доступных ячеек
        LinkedList<Coord> cellsList=getAvailableCellList(m, nNext);

        //Рекурсивно вычисляем рейтинг
        for(Coord coord: cellsList){
            rate+=getRate(getNextMatr(m, nNext, coord.y, coord.x), nNext, depth-1);
        }

        //Возвращаем результат
        return rate;
    }

    //Метод возвращает список ячеек, которые перевернутся после хода y,x в матрицу m0 фишкой цвета n
    private LinkedList<Coord> getRevertCellsList(int[][] m0, int n, int y, int x) {
        LinkedList<Coord> res = new LinkedList<>();
        int rows = m0.length;
        int cols = m0[0].length;

        //Проверяем первый особый случай: ячейка выходит за пределы матрицы
        if ((y < 0) | (y >= rows) | (x < 0) | (x >= cols)) {
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
                if ((y0 < 0) | (y0 >= rows) | (x0 < 0) | (x0 >= cols)) {
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
                res.addAll(s);
            }
        }

        //Возвращаем результат
        return res;
    }

    //Метод возвращает матрицу, в которую будет преобразована матрица m0, согласно ходу фишки цвета n в клетку y,x
    private int[][] getNextMatr(int[][] m0, int n, int y, int x) {
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
        if (!isCellAvailable(res, n, y, x)) {
            return res;
        }

        LinkedList<Coord> revertList = getRevertCellsList(res, n, y, x);
        for (Coord coord : revertList) {
            res[coord.y][coord.x] = n;
        }

        //Фиксируем ход
        res[y][x] = n;

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

    //Метод определяет доступность ячейки y,x в матрице m0 для установки фишки цвета n
    private boolean isCellAvailable(int[][] m0, int n, int y, int x) {
        int r = m0.length;
        int c = m0[0].length;
        if ((y < 0) | (y >= r) | (x < 0) | (x >= c)) {
            return false;
        }
        if (m0[y][x] != EMPTY) {
            return false;
        }

        int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
        int[] dy = {-1, -1, 0, 1, 1, 1, 0, -1};
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
            if (s > 0) {
                return true;
            }
        }
        return false;
    }

}
