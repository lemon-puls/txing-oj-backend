package com.bitdf.txing.oj.ceshi.twoSum;

import java.util.Scanner;

/**
 * @author lemon123456
 * @date 2023-12-06 18:30:23
 */
public class Main {

    public static void main(String[] args) {
        // 请开始您的作答
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        ListNode head1 = new ListNode(-1);
        ListNode head2 = new ListNode(-1);
        ListNode tail1 = head1;
        ListNode tail2 = head2;
        for (int i = 0; i < n; i++) {
            tail1.next = new ListNode(scanner.nextInt());
            tail1 = tail1.next;
        }
        for (int i = 0; i < m; i++) {
            tail2.next = new ListNode(scanner.nextInt());
            tail2 = tail2.next;
        }
        head1 = head1.next;
        head2 = head2.next;
        ListNode head = null, tail = null;
        int carry = 0;
        while (head1 != null || head2 != null) {
            int n1 = head1 != null ? head1.val : 0;
            int n2 = head2 != null ? head2.val : 0;
            int sum = n1 + n2 + carry;
            if (head == null) {
                head = tail = new ListNode(sum % 10);
            } else {
                tail.next = new ListNode(sum % 10);
                tail = tail.next;
            }
            carry = sum / 10;
            if (head1 != null) {
                head1 = head1.next;
            }
            if (head2 != null) {
                head2 = head2.next;
            }
        }
        if (carry > 0) {
            tail.next = new ListNode(carry);
        }
        ListNode curr = head;
        while (curr != null) {
            System.out.print(curr.val + " ");
            curr = curr.next;
        }
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}