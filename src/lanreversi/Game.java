package lanreversi;

//Данный класс реализует локальную игру
public class Game extends Thread {

    private Coord playerStroke = null;    //Координаты клетки, в которую походил игрок

    private boolean stop = false;

    @Override
    public void run() {
        synchronized (this) {
            while (true) {
                if (playerStroke != null) {
                    System.out.println("Щелчок в ячейку в строке: " + playerStroke.y + ". В столбце: " + playerStroke.x);
                    playerStroke = null;
                }
                if (stop)break;
                try {
                    wait(50);
                } catch (InterruptedException ex) {}
            }
        }
    }

    //Метод принимает координаты выбранной пользователем ячейки
    public void playerStroke(Coord coord) {
        synchronized (this) {
            playerStroke = coord;
        }
    }

    //Метод останавливает текущий поток
    public void stopThread() {
        synchronized (this) {
            stop = true;
        }
    }

}
