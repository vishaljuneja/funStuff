import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Classes:
 * InfibeamMetaData - stores information for infibeam catalogue (subclass of MetaData)
 * IndixMetaData - stores information for indix product database (subclass of MetaData)
 * MetaData - Abstract class which contains the overridden method equals() responsible
 * 			  for matching task (superclass of InfibeamMetaData and IndixMetaData)
 * 
 * Assumptions: Product name (Airtel Iphone 3GS with 3gb data card) is parsed as "<BrandName e.g. apple,blackberry>{1} <ModelName e.g. Iphone>{1+} <ModelNumber: 3gs>{1+} <Other e.g. 3GB with blah blah blah>{*}"
 * 				OR "<BrandName> <ModelNumber> <ModelName> <Other>"
 * 
 * Q) Can you do it better?
 * A) 1) The rules model can be further fitted on the data and tailored on the false negatives (No match cases where infact a match does exist) but this is a dangerous territory
 * 	  of overfitting and must be treaded with caution. 
 * 	  2) Using edit distances we can match words which are very similar but differently spelled "Karbon vs Karbonn"
 * 
 * Q) What other approached could you try?
 * A) The other approached I would give a shot ->
 * 		a) Decision trees
 * 		b) Character n-gram approaches employed commonly in pilagiarism detectors
 * 
 * Musings: If 80% accuracy is expected from the candidates then perhaps one should be provided with a gold standard and test set? (There does seem to be True Negatives in the
 * 			data-set!).
 * 
 * @author vishal
 */
public class ProductMatcher {
	
	public static final String INFIBEAM_INPUT_FILE = 
			"Mobiles-Infibeam.txt";
	
	public static final String INDIX_INPUT_FILE = 
			"Mobiles-Master-Data.txt";

	public static final String OUTPUT_FILE = 
			"Output.xls";
	
	private HashMap<String, List<IndixMetaData>> mobiles;
	
	public ProductMatcher() {
		mobiles = new HashMap<String, List<IndixMetaData>>();
		readIndixDataset();
		match();
	}
	
	/*
	 * read Indix product meta data and store in a HashMap invariant
	 */
	private void readIndixDataset() {
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader(INDIX_INPUT_FILE));
			String str = br.readLine();
			while(str!=null) {
				String[] vals = str.split("\t");
				IndixMetaData d = new IndixMetaData();
				d.indixId = vals[1];
				d.parseName(vals[0]);
				if(mobiles.containsKey(d.brand)) {
					mobiles.get(d.brand).add(d);
				} else {
					List<IndixMetaData> list = new ArrayList<IndixMetaData>();
					list.add(d);
					mobiles.put(d.brand, list);
				}
				str = br.readLine();
			}
			br.close(); // close file
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Process Infibeam catalogue linewise and compare with Indix database
	 */
	private void match() {
		BufferedReader br;
		BufferedWriter bw;
		try{
			br = new BufferedReader(new FileReader(INFIBEAM_INPUT_FILE));
			bw = new BufferedWriter(new FileWriter(OUTPUT_FILE));
			String str = br.readLine();
			int total = 0;
			int matches = 0;
			while(str!=null) {
				total++;
				String[] vals = str.split("\t");
				InfibeamMetaData d = new InfibeamMetaData();
				d.url = vals[1];
				d.parseName(vals[0]);
				if(mobiles.containsKey(d.brand)) { // compare with all models
					boolean found = false;
					for (IndixMetaData ibmd : mobiles.get(d.brand)) {
						if(d.equals(ibmd)) {
							output(bw, d, ibmd);
							found = true;
							matches++;
							break;
						}
					}
					if(!found) output(bw, d, null);
				} else {
					output(bw, d, null);
				}
				str = br.readLine();
			}
			br.close(); // close input file
			bw.close(); // close output file
			System.out.println("Total products processed: " + total);
			System.out.println("Total matched: " + matches);
			System.out.println("Percent: " + matches*1.0/total);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Output in the required format
	 */
	private void output(BufferedWriter bw, InfibeamMetaData ibmd, IndixMetaData inmd) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(ibmd.getFullName() + "\t");
		sb.append(ibmd.url + "\t");
		if(inmd != null) {
			sb.append(inmd.getFullName() + "\t");
			sb.append(inmd.indixId + "\t");
			sb.append("Match");
		} else {
			sb.append("" + "\t");
			sb.append("" + "\t");
			sb.append("No Match");
		}
		bw.write(sb.toString());
		bw.newLine();
	}

	/*
	 * Checks if string contains a number (which could be a model number)
	 */
	private boolean isModelNo(String string) {
		if(string.matches(".*[\\d]+.*")) return true;
		return false;
	}
	
	private class InfibeamMetaData extends MetaData {
		public String url;
	}
	
	private class IndixMetaData extends MetaData {
		public String indixId;
	}
	
	private abstract class MetaData {
		
		public String brand;
		public List<String> modelName = new ArrayList<String>();
		public List<String> modelNo = new ArrayList<String>(); 
		
		public String fullName;
		
		/*
		 * The main rule engine (matcher) of the program
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if(obj instanceof MetaData) {
				MetaData md = (MetaData) obj;
				if (this.fullName.toLowerCase().contains(md.fullName.toLowerCase())) return true;
				
				if(this.brand.equals(md.brand)) {
					if (this.modelNo.containsAll(md.modelNo) || 
							this.modelNo.containsAll(md.modelNo) ||
							concatenate(this.modelNo).contains(concatenate(md.modelNo))) {
						if (this.modelName.containsAll(md.modelName) || 
								this.modelName.containsAll(md.modelName)) {						
								return true;
						}		
						
						return true;
					}
				}
			}
			return false;
		}
		
		private String concatenate(List<String> list) {
			StringBuilder sb = new StringBuilder();
			for(String s : list) {
				sb.append(s);
			}
			return sb.toString();
		}

		public String getFullName() {
			return fullName;
		}
		
		public void parseName(String product) {
			this.fullName = product;
			product = product.toLowerCase();
			String[] vals = product.split("\\W+"); //remove -, _ , spaces
			this.brand = vals[0];
			for(int i = 1; i < vals.length; i++) {
				if(!isModelNo(vals[i])) {
					this.modelName.add(vals[i]);
				} else {
					this.modelNo.add(vals[i]);
				}
			}	
		}
	}
	
	public static void main(String[] args) {
		new ProductMatcher();
	}
}
