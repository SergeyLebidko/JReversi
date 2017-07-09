package lanreversi;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.TreeMap;

//Данный класс необходим для поиска парнера по игре
public class Radar extends Thread{

    private final int port=50201;                 //Порт, на котором работает радар

    private DatagramSocket socketOUT;             //Сокет, ипользуемый для отправки сообщений
    private DatagramSocket socketIN;              //Сокет, используемый для приема сообщений
    private InetAddress broadcastIP;              //Широковещательный адрес

    private TreeMap<Contact, Long> onlineList;    //Список клиентов-онлайн, поддерживаемый данным классом

    //Переменные для контроля потока
    private Boolean pause=false;    //Временная приостановка отправки эхо-пакетов
    private Boolean stop=false;     //Завершение работы радара

    public Radar() throws Exception {

        //Получаем широковещательный адрес данной сети. При этом проверяем, чтобы сеть являлась сетью класса С
        String[] net;
        net=InetAddress.getLocalHost().getHostAddress().split("\\.");
        int group1=Integer.parseInt(net[0]);
        if(!(group1>=192 & group1<=223))throw new Exception("Сеть не является сетью класса С");
        net[3]="255";
        broadcastIP=InetAddress.getByName(net[0]+"."+net[1]+"."+net[2]+"."+net[3]);

        //Создаем сокеты для приема и отправки сообщений
        socketOUT=new DatagramSocket();
        socketIN=new DatagramSocket(port);
        socketIN.setSoTimeout(1000);

        //Создаем список контактов
        onlineList=new TreeMap<>();

    }

    @Override
    public void run() {
        int socketTimeout=500;   //Таймаут ожидания сообщения
        int threadTimeout=100;   //Таймаут потока
        byte[] buf;              //Буфер для приема/отправки сообщений
        DatagramPacket packet;
    }

    //Приостановка отправки эхо-пакетов
    public void pauseThread(boolean p){
        synchronized(pause){
            pause=p;
        }
    }

    //Остановка потока
    public void stopThread(){
        synchronized(stop){
            stop=true;
        }
    }

}
