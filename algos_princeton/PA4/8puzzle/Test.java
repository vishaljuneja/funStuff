import java.util.Arrays;

public class Test {
    
    public static void main(String args[]){
        int arr[][] = new int[2][5];
        StdOut.println(arr[0].length);
        arr[0][0] = 5;
        arr[1][0] = 10;
        int count = 1;
        for(int[] a1 : arr){
            for(int i = 0; i<a1.length; i++){
                a1[i] = count++;
            }
        }
        int arr1[][] = Arrays.copyOf(arr, arr.length);
        StdOut.println(arr1[0][0]+" "+arr1[1][0]);
        
    }
}