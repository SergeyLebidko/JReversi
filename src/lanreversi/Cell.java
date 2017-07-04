package lanreversi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

public class Cell extends JPanel{

    //Раздел цветовых констант
    private final Color backColor=new Color(190, 190, 190);    //Основной цвет фона ячеек

    //Содержимое ячеек: пустая, с черной шашкой, с белой шашкой
    public static final int EMPTY=0;
    public static final int BLACK=1;
    public static final int WHITE=2;
    private int content;

    //Переменные для хранения координат ячейки на игровом поле
    private final int x;
    private final int y;

    //Стили ячеек
    private static final String[] styleNames={"Переплетение", "Меандр", "Восточный узор", "Классика", "Квадрат"};
    private int style=0;    //Номер используемого стиля

    //Конструктор класса устанавливает координаты ячейки, ее содержимое (пустая ячейка, белая или черная шашка) и стиль шашки
    public Cell(int y, int x, int content, int style) {
        super.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        this.content=content;
        this.x=x;
        this.y=y;
        this.style=style;
    }

    //Метод устанавливает содержимое ячейки: пустая, черная шашка или белая шашка
    public void setContent(int c){
        content=c;
        repaint();
    }

    //Метод возвращает содержимое ячейки
    public int getContent(){
        return content;
    }

    //Метод возвращает true, если ячейка пуста
    public boolean isEmpty(){
        return content==EMPTY;
    }

    //Гетеры координат
    public int getCellX(){
        return x;
    }

    public int getCellY(){
        return y;
    }

    //Гетер количества доступных стилей ячеек
    public int countStyles(){
        return styleNames.length;
    }

    //Гетер списка имен стилей
    public static String[] getStyleNames(){
        return styleNames;
    }

    //Установка нового стиля для ячейки
    public void setStyle(int newStyle){
        if((newStyle<0) | (newStyle>=countStyles()))return;
        style=newStyle;
        if(isEmpty())return;
        repaint();
    }

    //Метод отрисовывает компонент
    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2;
        g2=(Graphics2D)g;

        int w=this.getSize().width;
        int h=this.getSize().height;

        //Установка фона ячейки в зависимости от ее стиля и контента
        g2.setColor(backColor);
        g2.fillRect(0, 0, w, h);

        if(content!=EMPTY){
            Image img=null;
            if(content==BLACK)img=new ImageIcon("res\\checkers\\style"+style+"\\black.png").getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            if(content==WHITE)img=new ImageIcon("res\\checkers\\style"+style+"\\white.png").getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            MediaTracker mt=new MediaTracker(this);
            mt.addImage(img, 1);
            try {
                mt.waitForID(1);
            } catch (InterruptedException ex){}
            g2.drawImage(img, 0, 0, null);
        }
    }

}