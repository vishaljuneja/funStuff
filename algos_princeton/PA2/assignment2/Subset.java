public class Subset {
    public static void main(String[] args){
        int k = Integer.parseInt(args[0]);
        if(k<=0) return;
        String set[] = new String[k];
        //if(k<=0) throw new Exception("k should be greater than zero");
        int count = 1;
        while(!StdIn.isEmpty()){
            String str = StdIn.readString();
            if(count<=k){
                set[count-1] = str;
            } else {
                double m = count - k;
                double probability = k/(k+m);
                double random = StdRandom.random();
                //StdOut.println(str+" "+probability+" "+random+" "+m);
                if(random<=probability) {
                    set[StdRandom.uniform(k)] = str;
                }
            }
            count++;
        }
        StdRandom.shuffle(set);
        for(int i=0; i<k; i++){
            StdOut.println(set[i]);
        }
    }
}