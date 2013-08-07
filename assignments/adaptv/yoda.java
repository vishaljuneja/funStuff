import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/*
 * Some comments ->
 *  Thanks for the assignment. I really liked both P42 (The hitchikers guide to the galaxy) and the yoda references :).
 * 
 * Assumptions made:
 * 	a) N is usually small compared to the text files
 *  b) -r and -c options are used separately (if they are used together often, I would prefer lumping file processing into a single constructor 
 *  											as file reads are generally expensive)
 * 
 * Class descriptions: 
 * OrderedHashMap -> takes a HashMap and converts it into TreeMap(values as keys) for finding top N or bottom N entries
 * TopTransactions -> Maintains only top N transactions at a time (hence utilizing on space, assuming N is small)
 * 
 * 
 * Can I make this run faster?
 * 	Probably using a similar method for maintaining only Top N candidates in TopTransactions instead of using an OrderedHashMap which rearranges the data into a TreeMap
 * incurring MlogM penalty. (M = size of data)
 * 
 * Can I reduce space requirements?
 * 	cust_visits and customer_purchases may be combined into a single HashMap
 * 
 * Generally, I would consider using a relational db for generating these kind of reports and supporting more queries on data
 */



public class yoda {

	private HashMap<String, Double> price_index;
	private HashMap<String, Integer> cust_visits;
	private HashMap<String, Integer> book_sales;
	
	private HashMap<String, Double> customer_purchases;
	
	private int N;
	
	private TopTransactions tt_tracker; // top transactions tracker
	
	 // ----------------------> Constructors
	
	public yoda(String price_file, String transactions_file, int n) {
		// initialize private variables
		price_index = new HashMap<String, Double>();
		cust_visits = new HashMap<String, Integer>();		
		book_sales = new HashMap<String, Integer>();
		
		this.N = n;
		tt_tracker = new TopTransactions(N);
		
		// load the data into intermediate invariants
		loadPrices(price_file);
		loadTransactionsReport(transactions_file);
	}
	
	public yoda(String price_file, String transactions_file) {
		price_index = new HashMap<String, Double>();
		customer_purchases = new HashMap<String, Double>();
		
		// load the data into intermediate invariants
		loadPrices(price_file);
		loadTransactionsEligibility(transactions_file);
	}
	
	// -----------------------------> public API
	
	/**
	 * top N frequent visitors
	 * @return comma separated (cust_id visits) records
	 */
	public String topVisitors() {
		return new OrderedHashMap(cust_visits).getTop(N);
	}
	
	/**
	 * top N transactions
	 * @return comma separated (cust_id transaction_sum) records
	 */
	public String topTransactions() {
		return tt_tracker.get();
	}

	/**
	 * top N sellers (books)
	 * @return comma separated (book_id num_sold) records (decreasing order of num_sold)
	 */
	public String bestSellers() {
		return new OrderedHashMap(book_sales).getTop(N);
	}
	
	/**
	 * least sold books
	 * @return comma separated (book_id num_sold) records (increasing order of num_sold)
	 */
	public String worstSellers() {
		return new OrderedHashMap(book_sales).getBottom(N);
	}
	
	/**
	 * is customer eligible for a discount?
	 * @param cust_id customer Id
	 * @param cut_off price cut_off for 10% discount
	 * @return 1 if true, 0 otherwise
	 */
	public int isEligible(String cust_id, double cut_off) {
		if(customer_purchases.containsKey(cust_id)) {
			double val = customer_purchases.get(cust_id);
			if(val > cut_off) return 1;
		}
		return 0;
	}
	
	// ------------------------> private functions
	
	// read price data
	private void loadPrices(String price_file) {
		BufferedReader br;
		try {
			// read book prices
			br = new BufferedReader(new FileReader(price_file));
			String line = br.readLine();
			while(line != null) {
				String vals[] = line.split(",");
				price_index.put(vals[0], Double.parseDouble(vals[1]));
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			System.out.println("Error reading prices " + e);
		}
	}

	// read transactions to check customer eligibility for a discount
	private void loadTransactionsEligibility (String transactions) {
		BufferedReader br;
		try {
			// read transactions
			br = new BufferedReader(new FileReader(transactions));
			String line = br.readLine();
			while(line != null) {
				String vals[] = line.split(",");
				line = br.readLine();
				String cust_id = vals[0];
				Double sum = 0.0;   // transaction total
				for (int i = 1; i < vals.length; i++) {
					String book_id = vals[i];
					sum += price_index.get(book_id); // will give Null pointer exception if the records in two files are not coherent
				}
				updateCustomerTransactions(cust_id, sum);
			}
			br.close();
			
		} catch (IOException e) {
			System.out.println("Error reading transactions file " + e);
		}
	}
	

	// read transaction data to generate report (top N)
	private void loadTransactionsReport (String transactions) {
		BufferedReader br;
		try {
			
			// initialize book sales with all books sales set to 0
			for (String id : price_index.keySet()) {
				book_sales.put(id, 0);
			}
			
			// read transactions
			br = new BufferedReader(new FileReader(transactions));
			String line = br.readLine();
			while(line != null) {
				String vals[] = line.split(",");
				line = br.readLine();
				String cust_id = vals[0];
				Double sum = 0.0;   // transaction total
				for (int i = 1; i < vals.length; i++) {
					String book_id = vals[i];
					updateSales(book_id);
					sum += price_index.get(book_id); // will give Null pointer exception if the records in two files are not coherent
				}
				tt_tracker.add(cust_id, sum);
				updateCustomerVisits(cust_id);
			}
			br.close();
			
		} catch (IOException e) {
			System.out.println("Error reading transactions file " + e);
		}
	}
	
	// update sales records for books
	private void updateSales(String book_id) {
		Integer sale = 1;
		sale += book_sales.get(book_id);		
		book_sales.put(book_id, sale);
	}

	// update customer visitation records
	private void updateCustomerVisits(String cust_id) {
		Integer visits = 1;
		if(cust_visits.containsKey(cust_id)) {
			visits += cust_visits.get(cust_id);
		}
		cust_visits.put(cust_id, visits);
	}
	
	// update customer records for visits and sum up all purchases
	private void updateCustomerTransactions(String cust_id, double transaction_sum) {
		
		Double total = transaction_sum;
		if(customer_purchases.containsKey(cust_id)) {
			total += customer_purchases.get(cust_id);
		}
		customer_purchases.put(cust_id, total);
	}

	// -----------------> A test client for yoda

	public static void main(String[] args) {
		
		// parse command line options
		
		if (args.length <= 4) {
			printUsage();
			throw new IllegalArgumentException("incorrect number of arguments, see usage!");
		}
	
		String price_file = null;
		String transactions_file = null;
		
		Integer n = null;
		Double cut_off = null;
		String cust_id = null;
		
		for (int i = 0; i < args.length; i++) {
			try {
				if (args[i].equals("-t")) {
					transactions_file = args[i+1];
					i++;
					continue;
				} else if (args[i].equals("-p")) {
					price_file = args[i+1];
					i++;
					continue;
				} else if (args[i].equals("-r")) {
					n = Integer.parseInt(args[i+1]);
					if (n <= 0) throw new IllegalArgumentException();
					i++;
					continue;
				} else if (args[i].equals("-d")) {
					cut_off = Double.parseDouble(args[i+1]);
					i++;
					continue;
				} else if (args[i].equals("-c")) {
					cust_id = args[i+1];
					i++;
					continue;
				} else {
					throw new IllegalArgumentException("Illegal argument encountered "+args[i]);
				}
			} catch(Exception e){
				printUsage();
				e.printStackTrace();
				System.exit(0);
			}
		}
		
		if (transactions_file == null || price_file == null) {	// files not set
			printUsage();
			throw new IllegalArgumentException();
		}
		
		// generate report
		if (n!=null) {
			yoda y = new yoda(price_file, transactions_file, n);

			System.out.println(y.topVisitors());
			System.out.println(y.topTransactions());
			System.out.println(y.bestSellers());
			System.out.println(y.worstSellers());
		}
		
		//check eligibility
		if (cut_off != null && cust_id != null) {
			yoda y = new yoda(price_file, transactions_file);
			
			System.out.println(y.isEligible(cust_id, cut_off));
		}
	}
	
	public static void printUsage(){
		System.out.println("Usage: yoda -t <transactions_file> -p <prices_file> -r [>=1] -d [price_cutoff] -c [customer_id]\n");
	}
}
