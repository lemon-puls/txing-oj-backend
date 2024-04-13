import java.util.*;

public class Main {

    public static final String TIME_FLAG_START = "time&&&";

    public static final String TIME_FLAG_END = "&&&time";

    public static void main(String[] args) {
        long startTimes = System.currentTimeMillis();
        Solution solution = new Solution();
        solution.answer();
        long endTimes = System.currentTimeMillis();
        System.out.println();
        System.out.println(TIME_FLAG_START + (endTimes - startTimes) + TIME_FLAG_END);
    }
}class Solution {

    void answer() {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = scanner.nextInt();
        }
        int sum = Arrays.stream(nums).sum();
        System.out.print(sum); 
    }
}