package lanreversi;

//Класс, описывающий базовые параметры других игроков: их имя и адрес в сети

import java.net.InetAddress;

public class Contact implements Comparable<Contact>{

    private final String name;           //Имя контакта
    private final InetAddress address;   //Адрес контакта

    public Contact(String name, InetAddress address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null)return false;
        if(!(obj instanceof Contact))return false;
        Contact c=(Contact)obj;
        return (name.equals(c.getName()) & address.equals(c.getAddress()));
    }

    //Геттеры полей
    public String getName() {
        return name;
    }

    public InetAddress getAddress() {
        return address;
    }

    //Компаратор, необходимый для корректной работы с объектами данного класса в классе TreeMap
    @Override
    public int compareTo(Contact o) {
        return name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return name+"::"+address;
    }

}
