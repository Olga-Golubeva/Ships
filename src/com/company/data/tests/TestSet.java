package com.company.data.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

public class TestSet {
    @Test
    public void test_set_overlapping (){
        Set<String> set1 = new TreeSet<>();
        Set<String> set2 = new TreeSet<>();


        set1.add("1_1");
        set1.add("1_2");

        set2.add("2_1");
        set2.add("2_2");

        var result = set1.addAll(set2);

        Assertions.assertTrue(set1.size() == 4);
    }

    @Test

    public void using_split(){
        String name = "Olga_test_1";
        var result = name.split("_");

        for (var item:result
             ) {
            System.out.println(item);
        }


    }

}
