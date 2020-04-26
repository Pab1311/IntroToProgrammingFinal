package finalProject;

import java.lang.Math;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		/** The final Project for COP 2006
		 * The point of this code is to act as a budget calculator:
		 *    See this Main for what you should be able to run with your Classes
		 *    
		 *    This is the basic form of a budget calculator:
		 *    	We will get the user's basic information -> build this (first to do)
		 *      Create instances of our other classes & initialize
		 *      Perform a few debits & credits -> you will build this
		 *      Determine if the user can put back savings and ask -> build 
		 */
		
		// Ask for user name & other information here to be sent to Accounts
		String ownersName;
		double initialCheckingsAmount;
		double initialSavingsAmount;
		String userVerification;
		Scanner scan = new Scanner(System.in);
		
		while (true) {
			System.out.println("Enter the name of the accountholder: ");
			ownersName = scan.nextLine();
			System.out.println("Enter the amount currently in your checkings: ");
			initialCheckingsAmount = scan.nextDouble();
			scan.nextLine();
			System.out.println("Enter the amount currently in your savings: ");
			initialSavingsAmount = scan.nextDouble();
			scan.nextLine();
			if (ownersName.isBlank()) {
				System.out.println("You have to enter the accountholder's name.");
			} else if (initialCheckingsAmount < 15){
				System.out.println("You must have a mininum of $15 in your checkings.");
			} else if (initialSavingsAmount < 5) {
				System.out.println("You must have a mininum of $5 in your savings.");
			} else {
				BigDecimal checkings = new BigDecimal(Double.toString(initialCheckingsAmount));
				BigDecimal savings = new BigDecimal(Double.toString(initialSavingsAmount));
				checkings = checkings.setScale(2, RoundingMode.HALF_EVEN);
				savings = savings.setScale(2, RoundingMode.HALF_EVEN);
				System.out.printf("[%s] owns the account and has $%.2f in checkings and $%.2f being in savings. Is this correct? [y/n]: ",
						ownersName, checkings, savings);
				userVerification = scan.nextLine();
				if (userVerification.toLowerCase().startsWith("y")){
					break;
			}
		}
	}
		
		Accounts acts = new Accounts(ownersName, initialCheckingsAmount, initialSavingsAmount);

		//Add while/for loop which asks for transactions (credits/debits) here
		// Note feel free to rename any method (this is just an example)
		
		int ownerInteraction = 0;
		String reasonForInteraction;
		String useSavings;
		boolean ownerWantsToContinue = true;
		double amount;
		double amountNeeded;
		String ownerDecision = "";
		
		// Create or replace existing transactions log file
		acts.transactionsLogger();
		
		while (ownerWantsToContinue) {
			// If minimum amounts are met.
			if ((acts.getCheckingsAmount() >= 15.00) && (acts.getSavingsAmount() >= 5.00)) {
				System.out.println("____________ \nChoose 1 for Withdraw");
	            System.out.println("Choose 2 for Deposit");
	            System.out.println("Choose 3 for Check Balance");
	            System.out.println("Choose 4 for EXIT");
				ownerInteraction = scan.nextInt();
			// If Checkings Account is below mininum amount of $15.00
			} else if (acts.getCheckingsAmount() < 15.00) {
				while (true) {
				amountNeeded = 15.00 - acts.getCheckingsAmount();
				BigDecimal requiredAmount = new BigDecimal(Double.toString(amountNeeded));
				String warning = String.format("%s checking account still needs $%.2f. Would you like to add more money [y/n]?", acts.getOwner(), requiredAmount);
				System.out.println(warning);
				ownerDecision = scan.nextLine();
				if (ownerDecision.toLowerCase().startsWith("n")) {
					acts.notEnoughMoney();
					System.out.println("Not enough money in checkings to keep account active. Goodbye!");
					try {
						acts.closeTransactionsLog();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.exit(1);
				} else if (ownerDecision.toLowerCase().startsWith("y")) {
					ownerInteraction = 2;
					break;
				}
			  }
			// If Savings Account is below mininum amount of $5.00
			} else if (acts.getSavingsAmount() < 5.00) {
				while(true) {
					amountNeeded = 5.00 - acts.getSavingsAmount();
					BigDecimal requiredAmount = new BigDecimal(Double.toString(amountNeeded));
					String warning = String.format("%s savings account still needs $%.2f. Would you like to add more money [y/n]?", acts.getOwner(), requiredAmount);
					System.out.println(warning);
					ownerDecision = scan.nextLine();
					if (ownerDecision.toLowerCase().startsWith("n")) {
						acts.notEnoughMoney();
						System.out.println("Not enough money in savings to keep account active. Goodbye!");
						try {
							acts.closeTransactionsLog();
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.exit(1);
					} else if (ownerDecision.toLowerCase().startsWith("y")) {
						ownerInteraction = 2;
						break;
					}
				}
			}
			switch(ownerInteraction) {
				case 1:
					System.out.println("Enter the amount you are withdrawing: ");
					amount = scan.nextDouble();
					scan.nextLine();
					System.out.println("Enter the reason for your withdrawal: ");
					reasonForInteraction = scan.nextLine();
					System.out.println("Would you like to withdraw this money from your savings [y/n]? ");
					useSavings = scan.nextLine();
						if ((acts.getSavingsAmount() - amount < 0) && (useSavings.toLowerCase().startsWith("y")) && (acts.getCheckingsAmount() - amount > 0)) {
							System.out.println("You do not have enough funds in your savings to make this withdrawal. Withdrawing from checkings instead.");
							acts.minusCredit(reasonForInteraction, Math.abs(amount));
						} else if (useSavings.toLowerCase().startsWith("y") && (acts.getSavingsAmount() - amount >= 0)) {
							acts.minusCredit(reasonForInteraction, Math.abs(amount), "savings");
				 		} else if (useSavings.toLowerCase().startsWith("n") && (acts.getCheckingsAmount() - amount >= 0)) {
				 			acts.minusCredit(reasonForInteraction, amount);
				 		} else {
				 			System.out.println("You do not have enough funds in your checkings or savings to make this withdrawal.");
				 		}
					break;
					
				case 2:
					System.out.println("Enter the amount you are depositing: ");
					amount = scan.nextDouble();
					scan.nextLine();
					System.out.println("Enter the reason for your deposit: ");
					reasonForInteraction = scan.nextLine();
					System.out.println("Would you like to add this money to your savings [y/n]? ");
					useSavings = scan.nextLine();
					if (useSavings.toLowerCase().startsWith("y")) {
						acts.addDebit(reasonForInteraction, Math.abs(amount), "savings");
					} else {
						acts.addDebit(reasonForInteraction, Math.abs(amount));
					}
					break;
					
				case 3:
					String overview = String.format("You have $%.2f in checkings. \nYou have $%.2f in savings.", acts.getCheckingsAmount(), acts.getSavingsAmount());
					System.out.println(overview);
					break;
					
				default:
					scan.nextLine();
					ownerWantsToContinue = false;
					
					break;
			}

	}			
		// Keep repeating adds/subtracts until user is done (make sure to check minimums)
		
		// These two should print out the Debits & Credits (see assignment for more)
		System.out.println(acts.printDebits());
		System.out.println(acts.printCredits() + "\n__________");
		
		System.out.println(acts.toString()); // should print out totals as directed in assignment
		
		/* Now you would check if 20% of total debits (might need to add to Accounts for this)
		 *       was used or if there is more. If there is money to save then offer to save it.
		 *       ... And reprint the toString after cause this is different now.
		 */
		
		String ownerWantsToSaveTwentyPercent = "";
		double totalIncomePercentage = (acts.getTotalCredit() / (acts.getStartingBalance() + acts.getTotalDebit())) * 100;
		
		System.out.println(String.format("%s made $%.2f", acts.getOwner(), acts.getTotalDebit()));
		System.out.println(String.format("%s spent $%.2f", acts.getOwner(), acts.getTotalCredit()));
		if (!(acts.twentyPercent() == Double.POSITIVE_INFINITY)) {
			System.out.println(String.format("This means you spent %.2f percent of what you made and %.2f percent of your total income.", acts.twentyPercent(), totalIncomePercentage));
			if (acts.twentyPercent() <= 80.00) {
				System.out.println("You still have 20% or more of your total debits. Would you like to put 20% into your savings [y/n]?");
				ownerWantsToSaveTwentyPercent = scan.nextLine();
				double twentyPercentToSavings = (acts.getTotalDebit() - acts.getTotalCredit()) * 0.20;
				
				// Assuming debits are already in account and you are simply transferring twenty percent of total debits from checkings into savings
				if (ownerWantsToSaveTwentyPercent.toLowerCase().startsWith("y") && (acts.getCheckingsAmount() - twentyPercentToSavings) > 15.00) {
					acts.minusCredit("moved from Credits to Savings", twentyPercentToSavings);
					acts.addDebit("20% of total debits", twentyPercentToSavings, "savings");
					System.out.println(acts.toString());
				} else if (ownerWantsToSaveTwentyPercent.toLowerCase().startsWith("y") && (acts.getCheckingsAmount() - twentyPercentToSavings) < 15.00) {
					System.out.println("Cannot move debits to savings without being below mininum amount of checkings.");
				}
			}
		}
		
		
		System.out.println("Have a nice day!");
		scan.close();
		
		try {
			acts.closeTransactionsLog();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}




