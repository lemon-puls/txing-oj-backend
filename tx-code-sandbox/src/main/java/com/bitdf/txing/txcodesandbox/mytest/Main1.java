public class Main1 {

    public static void main(String[] args) {
        long startTimes = System.currentTimeMillis();
//        Solution solution = new Solution();
//        solution.answer();
        try {
            Class<?> cls = Class.forName("Solution");
            Object solutionInstance = cls.newInstance();
            cls.getDeclaredMethod("answer").invoke(solutionInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTimes = System.currentTimeMillis();
        System.out.println();
        System.out.println(endTimes - startTimes);
    }
}
