import java.util.Arrays;
import java.util.Scanner;
public class Solution {
    public void answer() {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = scanner.nextInt();
        }
        int sum = Arrays.stream(nums).sum();
        System.out.print(sum / 0);
    }
}