package lanreversi;

public class LANReversi extends Thread{

    private GUI gui;    //Графический интерфейс, с которым будем работать

    //Размеры игрового поля
    private final int cols=8;
    private final int rows=8;

    public LANReversi() {}

    private void initGame(){
        gui=new GUI(cols, rows);
    }

    public static void main(String[] args) {
        LANReversi game=new LANReversi();
        game.initGame();
    }

}
