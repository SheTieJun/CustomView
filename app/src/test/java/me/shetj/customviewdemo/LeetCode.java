package me.shetj.customviewdemo;


import android.content.res.AssetManager;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import me.shetj.base.tools.json.GsonKit;

class LeetCode {
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            return false;
        }

        return false;
    }

    /**
     * 字符串反转
     */
    public void reverseString(char[] s) {
        char temp;
        //第i个和第length-i 个进行交换
        for (int i = 0; i < s.length / 2; i++) {
            temp = s[i];
            s[i] = s[s.length - i - 1];
            s[s.length - i - 1] = temp;
        }
    }

    class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

    public String getJson(String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = SelectAreaActivity.this.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 22交换
     *
     * @param head
     * @return
     */
    public ListNode swapPairs(ListNode head) {
        return change2Node(head);
    }

    ListNode change2Node(ListNode head) {
        if (head == null) return null;
        //首先空处理和1处理
        if (head.next == null) return head;
        //取出第2个
        ListNode temp = head.next;
        //第1个连接和后面的连接，空出第2个
        head.next = change2Node(head.next.next);
        //完成第1个和第2个的交换
        temp.next = head;
        //返回
        return temp;
    }


    public ListNode reverseList(ListNode head) {
        ListNode prev = null; //上一个
        ListNode curr = head;
        while (curr != null) {
            //得到下一个
            ListNode nextTemp = curr.next;
            //把当前 的下一个 记录到上一个
            curr.next = prev;
            // 上一个 = 当前
            prev = curr;
            //继续下一个
            curr = nextTemp;
        }
        return prev;
    }


    /**
     * 杨辉三角
     * f(i,j)=f(i−1,j−1)+f(i−1,j)
     * f(i,j)=1 where j=1 or j=i
     *
     * @param numRows
     * @return
     */
    @Test
    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> list = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            List<Integer> integerList = new ArrayList<>();

            for (int j = 0; j <= i; j++) {
                if (i > 1 && j > 0 && i != j) {
                    // f(i,j)=f(i−1,j−1)+f(i−1,j)
                    integerList.add(list.get(i - 1).get(j - 1) + list.get(i - 1).get(j));
                } else {
                    //其余的放1
                    integerList.add(1);
                }
            }
            list.add(integerList);
        }
        System.out.println(GsonKit.objectToJson(list));
        return list;
    }

}
