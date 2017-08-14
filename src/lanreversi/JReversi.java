package lanreversi;

import javax.swing.SwingUtilities;

//Главный класс, с которого начинается выполнение программы. Содержит в себе ключевые объекты программы
public class JReversi{

    public static GUI gui;                 //Используемый графический интерфейс
    public static LocalGame localGame;     //Поток, реализующий локальную игру
    public static String playerName="";    //Имя игрока

    //Размеры игрового поля
    private static final int cols=8;
    private static final int rows=8;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui=new GUI(cols, rows);
                playerName=gui.getPlayerName();
            }
        });
    }

}
