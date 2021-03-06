import java.util.Scanner;

public class Menu implements Run, IMenu{

    Scanner keyboard = new Scanner(System.in);
    boolean exit;

    private User user;
    private Bank bank;

    public Menu(User user, Bank bank){
        this.user = user;
        this.bank = bank;
    }


    public void run(){
        while(!exit){
            print();
            int choice = Login.getInput(keyboard, 8);
            performAction(choice);
        }
    }

    private void print(){

        System.out.println("\nSelect:\n");
        System.out.println("1) Make Deposit  2) Make withdrawal  3) Send money  4) Show history");
        System.out.println("5) Display account info  6) Savings Account 7) Get a loan  0) Exit\n");

    }

    private void performAction(int choice) {
            switch (choice) {
                case 0:
                    exit = exitOrLogout();
                    break;
                case 1:
                    makeDeposit();
                    break;
                case 2:
                    makeWithdrawal();
                    break;
                case 3:
                    sendMoneyTo();
                    break;
                case 4:
                    showTransactions();
                    break;
                case 5:
                    displayAccount();
                    break;
                case 6:
                    mySavingsAccount();
                    break;
                case 7:
                    askToTakeLoan();
                    break;
                default:
                    System.out.println("Error. Pick a number from 0 to 6.");
            }

    }


    //1
    private void makeDeposit() {
        Account pickedAccount = pickAccount();
        double amount = askForAmount();
        pickedAccount.deposit(amount);
        user.writeDownTransaction("deposit", amount,pickedAccount);
    }

    //2
    private Account pickAccount(){
        String account;
        Account pickedAccount = user.getAccount();
        boolean valid = false;
        if (user.hasSavingsAccount()){
            while(!valid){
                System.out.println("Pick an account [savings/checking]: ");
                account = keyboard.nextLine();
                if (account.equalsIgnoreCase("checking") || account.equalsIgnoreCase("savings")){
                    if (account.equalsIgnoreCase("savings")){
                        pickedAccount = user.getSavingsAccount();
                    }
                    valid=true;
                }
                else {System.out.println("Incorrect input.");}
            }
        } return pickedAccount;

    }
    private Double askForAmount() {
        System.out.println("Enter the amount of money:");

        while (true) {
            try {
                double amount = new Scanner(System.in).nextDouble();
                if (amount<=0){
                    System.out.println("You can't type negative number. Type again.");continue;
                }
                return amount;
            } catch (Exception e) {
                System.out.println("Type a double. Use commas.");
            }
        }
    }
    private void makeWithdrawal(){
        Account pickedAccount = pickAccount();
        double amount = askForAmount();
        pickedAccount.withdraw(amount);
        user.writeDownTransaction("withdrawal", amount,pickedAccount);
    }

    //3
    private void sendMoneyTo() {
        boolean valid = false;
        while (!valid) {
            System.out.println("Enter the name of the user you would like to send money to. To exit type 'exit'.");
            String username = new Scanner(System.in).nextLine();
            if (username.equalsIgnoreCase("exit")){break;}
            if (bank.getUser(username) != null){
                sendMoney(bank.getUser(username));
                valid = true;
            }
            else {
                System.out.println("Incorrect username. Try again.");
            }
        }
    }
    private void sendMoney(User receiver) {

        System.out.println("How much money would you like to sent?: ");
        boolean valid=false;
        while (!valid){
        double amount = askForAmount();
        if (amount > user.getAccount().getBalance()){
            System.out.println("You don't have enough money. Type again.");
        } else{
            bank.getUser(user.getUsername()).getAccount().addMoney(amount);
            user.getAccount().takeMoney(amount);
            receiver.getAccount().addMoney(amount);
            user.writeDownTransaction("money transfer", amount,receiver.getAccount());
            System.out.println();
            System.out.println("Money has been successfully sent.");
            valid = true;}
    }
  }



    //4
    private void showTransactions() {
        user.showTransactions();
    }

    //5
    private void mySavingsAccount() {
        if(user.hasSavingsAccount()){
            System.out.println("You have a bank account. What you want to do?");
            System.out.println("0) See the details\n1) Transfer Money");
            int choice = Login.getInput(new Scanner(System.in), 1);
            switch (choice) {
                case 0 -> System.out.println(user.getSavingsAccount().toString());
                case 1 -> TransferMoneySavingsAccount();
            }
        } else {
            System.out.println("You don't have a saving account. Do you want to create one? [yes/no]: ");
            boolean valid = false;
            while (!valid) {
                String answer = new Scanner(System.in).nextLine();
                if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("no")) {
                    if (answer.equalsIgnoreCase("yes")){
                        createSavingsAccount();
                        System.out.println("Account created.");
                    }
                    valid = true;
                } else {
                    System.out.println("Incorrect input.");
                }
                }
            }
        }
    private void TransferMoneySavingsAccount() {
        boolean valid = false;
        System.out.println("You are going to transfer money to your personal savings account.");
        while(!valid){
        double amount = askForAmount();
        if (amount<user.getAccount().getBalance()){
            System.out.println("You have just transfered " + amount + " $ to your savings account.");
            user.getAccount().takeMoney(amount);
            user.getSavingsAccount().addMoney(amount);
            user.writeDownTransaction("to savings account", amount, user.getSavingsAccount());
            valid=true;

        } else {
            System.out.println("You don't have enough money.");
        }
        }
    }
    private void createSavingsAccount() {
        SavingsAccount savingsAccount = new SavingsAccount(0);
        savingsAccount.setOwner(user.getUsername());
        user.addSavingsAccount(savingsAccount);
        bank.getSavingAccounts().add(savingsAccount);
    }


    //6
    private void displayAccount() {
        System.out.println(user.toString());
    }

    //7
    private void askToTakeLoan() {

        if (user.getNumberOfLoans() > 2) {
            System.out.println("You can't take a loan. You have a maximum number of loans.");
        } else {

            int maxLoan = checkLoan();

            System.out.println("Do you want to take a loan? [yes/no]: ");
            boolean valid = false;

            while (!valid) {

                String answer = new Scanner(System.in).nextLine();

                if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("no")) {
                    if (answer.equalsIgnoreCase("yes")){
                        takeLoan(maxLoan);
                    }
                        valid = true;
                }
                else {
                    System.out.println("Incorrect input.");
                }
            }
        }
    }
    private void takeLoan(int maxLoan){
        System.out.println("You're taking a loan.");

        while (true){

            double amount;
            amount = askForAmount();

            if (amount > maxLoan) {
                System.out.println("You can't take this loan. Your maximum loan is " + maxLoan + ". ");
                System.out.print("Type again.");
            }
            else {
                    user.takeLoan(amount);
                    System.out.println("You have just taken a loan. Current balance: " + user.getAccount().getBalance());
                    break;
            }
        }
    }
    private int checkLoan(){
        double balance = user.getAccount().getBalance();
        double percentage = 1;
        int availableLoan;
        if (user.getNumberOfLoans() >0) {percentage = .75;}
        if (user.getNumberOfLoans() >1){percentage = .5;}
        if(balance < 20000){
            if (balance < 10000){
                availableLoan = (int)(balance * percentage * 1.3);
                System.out.println("Available loan: " +availableLoan);return availableLoan;
            } else {availableLoan = (int)(balance * percentage * 1.5);
                System.out.println("Available loan: " +availableLoan);return availableLoan;}
        } if (balance> 2000){availableLoan = (int)(balance * percentage * 1.75);
            System.out.println("Available loan: " +availableLoan);return availableLoan;}
        return -1;
    }



    //0
    private boolean exitOrLogout() {
        System.out.println("Do you want to exit the system or log into a different account? (exit/logout): ");
        String answer = keyboard.nextLine();
        if (answer.equalsIgnoreCase("exit") || answer.equalsIgnoreCase("logout")) {
            if (answer.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye, " + user.getUsername());
                System.exit(0);
            } else {
                System.out.println("Logging out. Goodbye, " + user.getUsername());
                return true;
            }
        } else {
            System.out.println("Wrong input");
        }
        return false;
    }











}
