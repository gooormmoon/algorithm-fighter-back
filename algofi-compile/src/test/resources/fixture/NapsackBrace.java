import java.io.*;
import java.util.*;

class Score{
    int p,t;
    public Score(int p, int t) {
        this.p = p;
        this.t = t;
    }
}

public class NapsackBrace{

    static int n,m;
    static int[] dy;
    static boolean[] check;

    public void solution(ArrayList<Score> arr) {
        for(int i=0; i<n; i++){
            check = new boolean[m+1];
            for(int j=m; j>=arr.get(i).t; j--){
                dy[j] = Math.max(dy[j], dy[j-arr.get(i).t] + arr.get(i).p);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        NapsackBrace T = new NapsackBrace();
        Scanner sc = new Scanner(System.in);

        n = sc.nextInt();
        m = sc.nextInt();
        ArrayList<Score> arr = new ArrayList<>();
        dy = new int[m+1];

        for(int i=0; i<n; i++){
            int x = sc.nextInt();
            int y = sc.nextInt();

            arr.add(new Score(x,y));
        }

        T.solution(arr);
        System.out.println(dy[m]);
    }
}