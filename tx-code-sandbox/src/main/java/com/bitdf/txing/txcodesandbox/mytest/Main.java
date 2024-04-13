//import java.util.Arrays;
//import java.util.Scanner;
//
//public class Main {
//
//    public static final String TIME_FLAG_START = "time&&&";
//
//    public static final String TIME_FLAG_END = "&&&time";
//
//    public static void main(String[] args) {
//        long startTimes = System.currentTimeMillis();
//        Solution solution = new Solution();
//        solution.answer();
//        long endTimes = System.currentTimeMillis();
//        System.out.println();
//        System.out.println(TIME_FLAG_START + (endTimes - startTimes) + TIME_FLAG_END);
//    }
//}
//
//class Solution {
//    public void answer() {
//        Scanner scanner = new Scanner(System.in);
//        int n = scanner.nextInt();
//        int[] nums = new int[n];
//        for (int i = 0; i < n; i++) {
//            nums[i] = scanner.nextInt();
//        }
//        int sum = Arrays.stream(nums).sum();
//        System.out.print(sum);
//    }
//}

public class Main {
    public static void main(String[] args) {
        String s = "Exception in thread \"main\" java.lang.NullPointerException: Cannot invoke \"String.length()\" because \"<local4>\" is null\n" +
                "\tat Solution.answer(Main.java:28)\n" +
                "\tat Main.main(Main.java:12)";
        int i = s.indexOf("\tat ");
        s = s.substring(0, i);
        System.out.println(s);
    }
}