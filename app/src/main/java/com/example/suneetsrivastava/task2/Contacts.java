package com.example.suneetsrivastava.task2;

/**
 * Created by suneetsrivastava on 03/01/18.
 */

public class Contacts {
    String name;
    String no;

    Contacts(String no, String name){
        this.no=no;
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }
}
