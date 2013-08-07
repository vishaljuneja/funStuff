import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * a simple client program which processes the hashtags.txt file
 * results are written to output.txt
 * @author vishal
 *
 */
public class ClientSegmenter {
	public static final String HASHTAG_FILE = 
			"hashtags.txt";
	
	public static final String OUTPUT_FILE = 
			"output.txt";
	
	public static void main(String[] args) {
		Segmenter segmenter = new Segmenter();
		
		BufferedReader br;
		BufferedWriter bw;
		try{
			br = new BufferedReader(new FileReader(HASHTAG_FILE));
			bw = new BufferedWriter(new FileWriter(OUTPUT_FILE));
			String hashtag = br.readLine();
			while(hashtag!=null) {
				System.out.println("Processing " + hashtag);
				StringBuilder sb = new StringBuilder();
				for(String s : segmenter.segmentHashTag(hashtag)) {
					sb.append(s + " ");
				}
				bw.write(sb.toString().trim());
				bw.newLine();
				hashtag = br.readLine();
			}
			br.close(); // close hashtag file
			bw.close(); // close writer file
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
