package me.shetj.customviewdemo;



import androidx.core.app.Person;


import org.junit.Test;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;


class TestInfo {

    @Test
    public static <T extends Person, U> void main(String[] args) throws Exception {
        Method method = TestInfo.class.getMethod("main", String[].class);
        TypeVariable<?>[] tvs = method.getTypeParameters();//返回声明顺序的 TypeVariable 对象的数组
        System.out.println("声明的类型变量有：" + Arrays.toString(tvs));//[T, U]

        for (int i = 0; i < tvs.length; i++) {
            GenericDeclaration gd = tvs[i].getGenericDeclaration();
            System.out.println("【GenericDeclaration】" + gd);//public static void com.bqt.Test.main(java.lang.String[]) throws java.lang.Exception
            System.out.println(gd.getTypeParameters()[i] == tvs[i]);//true。    GenericDeclaration和TypeVariable两者相互持有对方的引用

            System.out.println(tvs[i] + "  " + tvs[i].getName() + "  " + Arrays.toString(tvs[i].getBounds()));//T  T  [class com.bqt.Person] 和 U  U  [class java.lang.Object]
        }
    }
}
