package lanreversi;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class GUI{

    private final JFrame frame;   //Главное окно
    private final Board board;    //Игровое поле

    //В конструтор передается колчисетво ячеек, которое будет на игровом поле
    public GUI(int cols, int  rows) {
        //Ширина и высота главного окна
        int W=800;
        int H=800;

        //Создаем главное окно
        frame=new JFrame("LANReversi");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("res\\logo.png").getImage());
        frame.setSize(W, H);
        frame.setResizable(false);

        //Создаем игровое поле
        board=new Board(cols, rows);
        frame.add(board, BorderLayout.CENTER);

        //Выводим главное окно на экран и сразу же перемещаем его к центру экрана
        frame.setVisible(true);
        int xPos=Toolkit.getDefaultToolkit().getScreenSize().width/2-frame.getSize().width/2;
        int yPos=Toolkit.getDefaultToolkit().getScreenSize().height/2-frame.getSize().height/2;
        frame.setLocation(xPos, yPos);

    }

}
