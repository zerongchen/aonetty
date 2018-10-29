package com.netty.aonet;

public class DemoTest {

    public static void main(String[] args){

        String s = "a";
        String b = new String("a");
        String stra = "hello";
        String strb = "hello";
        System.out.println(s==b);
        System.out.println(stra==strb);

        String str1 = "abc";
        String str2 = "abc";
        String str3 = "abc";
        String str4 = new String("abc");
        String str5 = new String("abc");

        System.out.println(str1==str4);
        System.out.println(str1==str2);
        System.out.println(str3==str2);
        System.out.println(str3==str4);

        Integer ll=null;
        System.out.println(ll.intValue());

    }
}
