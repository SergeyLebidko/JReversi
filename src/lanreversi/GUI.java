package lanreversi;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GUI{

    private final JFrame frame;   //Главное окно
    private final Board board;    //Игровое поле

    //В конструтор передается колчисетво ячеек, которое будет на игровом поле
    public GUI(int cols, int  rows) {
        //Размеры главного окна
        int W=800;        //Ширина главного окна
        int hTop=30;      //Высота верхней панели
        int hBoard=800;   //Высота игрового поля
        int hBottom=30;   //Высота нижней панели

        //Создаем главное окно
        frame=new JFrame("LANReversi");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("res\\logo.png").getImage());
        frame.setSize(W, (hBoard+hTop+hBottom));
        frame.setResizable(false);
        int xPos=Toolkit.getDefaultToolkit().getScreenSize().width/2-W/2;
        int yPos=Toolkit.getDefaultToolkit().getScreenSize().height/2-(hTop+hBoard+hBottom)/2;
        frame.setLocation(xPos, yPos);

        //Создаем верхнюю панель (панель очков и сообщений)
        Box topPane=Box.createHorizontalBox();
        topPane.setBorder(BorderFactory.createEmptyBorder(5, 8, 0, 8));
        JLabel l1=new JLabel(" ");
        l1.setFont(new Font(null, Font.PLAIN, 16));
        JLabel l2=new JLabel(" ");
        l2.setFont(new Font(null, Font.PLAIN, 16));
        JLabel l3=new JLabel(" ");
        l3.setFont(new Font(null, Font.PLAIN, 16));
        JLabel l4=new JLabel(" ");
        l4.setFont(new Font(null, Font.PLAIN, 16));
        topPane.add(l1);
        topPane.add(Box.createHorizontalStrut(10));
        topPane.add(l2);
        topPane.add(Box.createHorizontalGlue());
        topPane.add(l3);
        topPane.add(Box.createHorizontalStrut(10));
        topPane.add(l4);
        frame.add(topPane, BorderLayout.NORTH);

        //Создаем игровое поле
        board=new Board(cols, rows);
        frame.add(board, BorderLayout.CENTER);

        //Создаем панель с кнопками
        Box bottomPane=Box.createHorizontalBox();
        bottomPane.setBorder(BorderFactory.createEmptyBorder(0, 8, 5, 8));

        JButton startButton=new JButton(new ImageIcon("res\\keys_ico\\startgame_16.png"));
        JButton styleButton=new JButton(new ImageIcon("res\\keys_ico\\style_16.png"));
        JButton colorButton=new JButton(new ImageIcon("res\\keys_ico\\black_16.png"));

        startButton.setToolTipText("Начать новую игру");
        styleButton.setToolTipText(board.getCurrentStyleName());
        colorButton.setToolTipText("Ваш цвет "+(board.getPlayerColor()==Cell.BLACK?"черный":"белый"));

        bottomPane.add(startButton);
        bottomPane.add(Box.createHorizontalGlue());
        bottomPane.add(styleButton);
        bottomPane.add(Box.createHorizontalStrut(5));
        bottomPane.add(colorButton);
        frame.add(bottomPane, BorderLayout.SOUTH);

        //Выводим главное окно на экран и сразу же перемещаем его к центру экрана
        frame.setVisible(true);

    }

}
