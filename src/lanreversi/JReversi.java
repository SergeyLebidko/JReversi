package lanreversi;

import javax.swing.SwingUtilities;

//Главный класс, с которого начинается выполнение программы. Содержит в себе ключевые объекты программы
public class JReversi{

    public static GUI gui;                 //Используемый графический интерфейс
    public static LocalGame localGame;     //Поток, реализующий локальную игру
    public static String playerName="";    //Имя игрока

    //Размеры игрового поля
    private static final int cols=10;
    private static final int rows=10;

    //Тестовые переменные
    public static int totalCount=0;

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
