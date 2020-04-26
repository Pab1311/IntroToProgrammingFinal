package finalProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.util.Map;

public class Accounts {
	/**
	 * Handling for credit and debit transactions
	 * Logging transactions onto files
	 * Checking account information
	 */
	
	private String owner = "";
	private Map<String, Double> balance = new HashMap<>();
	private File logFile = new File("./src/transactions.log");
	private BufferedWriter writer;
	private double savingsInterestRate = 0.025;
	private List<Double> debits = new ArrayList<Double>();
	private List<Double> credits = new ArrayList<Double>();
	private double totalDebits = 0;
	private double totalCredits = 0;
	private double percentSpent;
	
	public Accounts(String name, double checkingsAmount, double savingsAmount) {
		/** Create an account given name, initial amount in Checkings Account, and inital amount in Savings Account
		 * @param name Owner's name
		 * @param checkingsAmount (initial to deposit in Checkings account)
		 * @param savingsAmount (inital to deposit in Savings account)
		 */
		
		this.owner = name;
		balance.put("checkings", checkingsAmount);
		balance.put("savings", savingsAmount);
		balance.put("total",(checkingsAmount + savingsAmount));
		balance.put("startingBalance", (checkingsAmount + savingsAmount));
	}
	
	public Accounts(String name) {
		/** Open an account given name.
		 * 		Load mininum amounts onto Checkings and Savings accounts.
		 * 		Build the account at its basic form
		 * 		Will be replaced when owner inputs initial checkings and savings amounts
		 * @param Owner's name
		 */
		owner = name;
		balance.put("checkings", 15.00);
		balance.put("savings", 5.00);
		balance.put("total",(balance.get("checkings") + balance.get("savings")));
		balance.put("startingBalance", (balance.get("checkings") + balance.get("savings")));

	}
	
	protected double getStartingBalance() {
		/** @return the initial total balance of the account
		 */
		return balance.get("startingBalance");
	}
	protected double getPrincipalBalance() {
		/** @return total balance on the account (checkings and savings combined)
		 */
		return balance.get("total");
	}
	
	protected double getCheckingsAmount() {
		/** @return current balance in the checkings account
		 */
		return balance.get("checkings");
	}
	
	protected double getSavingsAmount() {
		/** @return current balance in the savings account
		 */
		return balance.get("savings");
	}

	protected String getOwner() {
		/** @return name of the accountholder
		 */
		return owner;
	}
	
	protected double getTotalDebit() {
		/** @return total amount added(deposited) by owner
		 */
		return totalDebits;
	}
	
	protected double getTotalCredit() {
		/** @return total amount spent(withdrawed) by owner
		 */
		return totalCredits;
	}
	
	protected String printDebits() {
		/** @return report on all debit amounts by user
		 */
		String debitReport = "Amounts debited this session are: ";
		for (int i = 0; i < debits.size(); i++) {
			String debitTransaction = String.format("\n\t$%.2f", debits.get(i));
			debitReport += debitTransaction;
		}
		return debitReport;
	}
	
	protected String printCredits() {
		/** @return report on all credit amounts by user
		 */
		String creditReport = "Amounts credited this session are: ";
		for (int i = 0; i < credits.size(); i++) {
			String creditTransaction = String.format("\n\t- $%.2f", credits.get(i));
			creditReport += creditTransaction;
		}
		return creditReport;
	}
	
	protected void transactionsLogger() {
		/** Check if log file exists
		 *  and if not create it & assign writer
		 *  and if it does, delete & assign writer
		 */
		
		if (logFile.exists()) {
			logFile.delete();
		}
		
		try {
			logFile.createNewFile();
			writer = new BufferedWriter(new FileWriter(logFile, true));
		} catch (FileNotFoundException fnf) {
			fnf.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void notEnoughMoney() {
		/** Notify user that account is below mininum (either savings or checkings)
		 *  Terminate the account and log error.
		 */
		try {
			writer.write("Not enough money to keep account active. Goodbye!");
			writer.flush();
		} catch (IOException e) {
			// Print exception if cannot write to file
			e.printStackTrace();
		}
	}
	
	protected void addDebit(String reasonForInteraction, double deposit) {
		/** Deposit (add) money into checkings account
		 * Update total balance and total checkings amount
		 * Log to transactions file
		 * @param reasonForInteraction (how the user earned the money)
		 * @param amount (to be deposited onto account)
		 */
		balance.put("checkings", balance.get("checkings") + deposit);
		balance.put("total", balance.get("total") + deposit);
		debits.add(deposit);
		totalDebits += deposit;
		
		String transaction = String.format("%s for $%.2f", reasonForInteraction, deposit);
		try {
			writer.write(transaction + "\r\n");
			writer.flush();
		} catch (IOException e) {
			// Print exception if cannot write to file
			e.printStackTrace();
		}
		
	}

	protected void minusCredit(String reasonForInteraction, double withdrawal) {
		/** Withdraw (remove) money from checkings account
		 * Update total balance and total checkings amount
		 * Log to transactions file
		 * @param reasonForInteraction (why the user is withdrawing the money)
		 * @param amount (to be withdrawed from account)
		 */
		balance.put("checkings", balance.get("checkings") - withdrawal);
		balance.put("total", balance.get("total") - withdrawal);
		credits.add(withdrawal);
		totalCredits += withdrawal;
		
		String transaction = String.format("%s for - $%.2f", reasonForInteraction, withdrawal);
		try {
			writer.write(transaction + "\r\n");
			writer.flush();
		} catch (IOException e) {
			// Print exception if cannot write to file
			e.printStackTrace();
		}
		
	}
	
	protected void closeTransactionsLog() throws IOException {
		/** Closes log file after user is done with transactions
		 */
		writer.close();
	}
	
	public String toString() {
		/** Inherits properties of toString 
		 * & Overrides it to print our template.
		 * @return report of account (total balance, current amounts in checkings and savings, savings amount with interest rate in 1 month)
		 */
		String report = String.format("%s, you have $%.2f in total.\nWith $%.2f in Checking and $%.2f in Savings "
				+ "which will be $%.2f next month with interest applied.", owner, balance.get("total"), balance.get("checkings"),
				balance.get("savings"), ((balance.get("savings") * savingsInterestRate) + balance.get("savings")));
		
		return report;
	}

	protected void addDebit(String reasonForInteraction, double deposit, String savings) {
		/** Deposit (add) money into savings account
		 * Update total balance and total savings amount
		 * Log to transactions file
		 * @param reasonForInteraction (how the user earned the money)
		 * @param amount (to be deposited onto account)
		 * @param savings (to notify that amount is being added to savings)
		 */
		balance.put("savings", balance.get("savings") + deposit);
		balance.put("total", balance.get("total") + deposit);
		debits.add(deposit);
		totalDebits += deposit;
		
		String transaction = String.format("%s for $%.2f", reasonForInteraction, deposit);
		try {
			writer.write(transaction + "\r\n");
			writer.flush();
		} catch (IOException e) {
			// Print exception if cannot write to file
			e.printStackTrace();
		}
		
	}

	protected void minusCredit(String reasonForInteraction, double withdrawal, String savings) {
		/** Withdraw (remove) money from savings account
		 * Update total balance and total savings amount
		 * Log to transactions file
		 * @param reasonForInteraction (why the user is withdrawing the money)
		 * @param amount (to be withdrawed from account)
		 * @param savings (to notify that amount is being removed from savings)
		 */
		balance.put("savings", balance.get("savings") - withdrawal);
		balance.put("total", balance.get("total") - withdrawal);
		credits.add(withdrawal);
		totalCredits += withdrawal;
		
		String transaction = String.format("%s for - $%.2f", reasonForInteraction, withdrawal);
		try {
			writer.write(transaction + "\r\n");
			writer.flush();
		} catch (IOException e) {
			// Print exception if cannot write to file
			e.printStackTrace();
		}
		
	}

	protected double twentyPercent() {
		/** Check if twenty percent of total debits is remaining following all transactions
		 * Create percentage of debit spent to use for comparisons
		 * @return percentage of debit spent
		 */
		try {
			percentSpent = totalCredits / totalDebits;
			
			if (percentSpent == Double.POSITIVE_INFINITY || percentSpent == Double.NEGATIVE_INFINITY) {
				throw new ArithmeticException();
			}
		} catch (ArithmeticException ae) {
			System.out.println("You only withdrawed during this session, therefore there is no percentage of income spent.");
		}
		
		percentSpent = percentSpent * 100.00;
		
		
		return percentSpent;
		
	}
	
}
