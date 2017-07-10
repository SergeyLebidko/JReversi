package lanreversi;

import java.util.LinkedList;
import javax.swing.SwingUtilities;

//Главный класс, с которого начинается выполнение программы. Содержит в себе ключевые объекты программы
public class JReversi{

    public static GUI gui;                 //Используемый графический интерфейс
    public static Game game;               //Поток, реализующий локальную игру
    public static String playerName="";    //Имя игрока

    //Размеры игрового поля
    public static final int cols=8;
    public static final int rows=8;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui=new GUI(cols, rows);

                LinkedList<Coord> l=new LinkedList<>();
                l.add(new Coord(2, 4));
                l.add(new Coord(3, 5));
                l.add(new Coord(5, 3));
                l.add(new Coord(4, 2));
                gui.setEnabledCells(l);

                playerName=gui.getPlayerName();
            }
        });
    }

}
