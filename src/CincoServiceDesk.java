import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CincoServiceDesk {

    Scanner scanner = new Scanner(System.in);
    ArrayList<User> users = new ArrayList<User>();
    ArrayList<Ticket> tickets = new ArrayList<Ticket>();
    int loggedIn = -1;


    public CincoServiceDesk() {
        initialiseFile("src/accounts.csv");
        initialiseFile("src/tickets.csv");

        // CincoServiceDesk initial main menu
        boolean run = true;
        while (run) {
            System.out.println("""
                Welcome to the Cinco Service Desk tool, please select one of the below options:
                1) Create a new account
                2) Login to account
                3) Debugger
                4) Exit""");

            int selection = validator(4);

            switch (selection) {
                case 1 -> createAccount();
                case 2 -> loginAccount();
                case 3 -> debugMenu();
                case 4 -> run = false;
            }

            // CincoServiceDesk Logged in menu
            while (loggedIn != -1) {
                System.out.print("Welcome " + users.get(loggedIn).getName() + ", ");
                System.out.println("""
                        Please select one of the below options:
                        1) Create a new Ticket
                        2) Logout""");
                int logSelect = validator(2);
                switch (logSelect) {
                    case 1 -> createTicket();
                    case 2 -> loggedIn = -1;
                }
            }
        }
        System.out.println("Thank you for using the Cinco Service Desk tool.");
    }

    // Create a new account
    public void createAccount() {
        scanner.nextLine();
        System.out.println("Please provide a unique email address");
        String email = emailValidator();
        System.out.println("Please provide your name");
        String name = scanner.nextLine();
        System.out.println("Please provide your phone number");
        String phone = scanner.nextLine();
        System.out.println("Please provide a password to be used with this account");
        String password = passwordValidator();
        System.out.println("""
                Please enter technician level:            
                1) Level 1
                2) Level 2
                3) Not a technician""");
        int technicianLevel = validator(3);

        if (technicianLevel == 3) {
            users.add(new User(email,name,phone,password));
            writeToFile(email + "," + name + "," + phone + "," + password,"src/accounts.csv");
        } else {
            users.add(new Technician(email,name,phone,password,technicianLevel));
            writeToFile(email + "," + name + "," + phone + "," + password + "," + technicianLevel,"src/accounts.csv");
        }
        System.out.println("Account has been created");
    }

    // Login to existing account
    public void loginAccount() {
        if (loggedIn == -1) {
            System.out.println("Please enter your email");
            scanner.nextLine();
            String enteredEmail = scanner.nextLine();
            int match = -1;
            for (int i = 0; i< users.size(); i++) {
                if ((enteredEmail.equals(users.get(i).getEmail()))) {
                    match = i;
                }
            }
            if (match == -1) {
                System.out.println("No user account has been found under that email address, login failed");
            } else {
                boolean run = true;
                while (run) {
                    System.out.println("Please enter the password for " + enteredEmail);
                    String enteredPassword = scanner.nextLine();
                    for (int i = 0; i< users.size(); i++) {
                        if (enteredPassword.equals(users.get(i).getPassword())) {
                            loggedIn = i;
                            run = false;
                            break;
                        }
                    }
                    if (loggedIn == -1) {
                        System.out.println("""
                                Incorrect password:
                                1) Try again
                                2) Forgot password
                                3) Exit login""");
                        int selection = validator(3);
                        scanner.nextLine();
                        if (selection == 2) {
                            System.out.println("Please enter your phone number to reset your password");
                            String enteredPhone = scanner.nextLine();
                            if (enteredPhone.equals(users.get(match).getPhone())) {
                                System.out.println("Please enter a new password you wish to use");;
                                users.get(match).setPassword(passwordValidator());
                                fileRefresh("src/accounts.csv");
                                System.out.println("Password has been reset");
                                run = false;
                            }
                        } else if (selection == 3) {
                            run = false;
                        }
                    }

                }
            }
        }
    }

    // Create .csv files if not existing or read data from provided .csv files
    public void initialiseFile(String fileName) {
        // Search for existing .txt file or create one if now found.
        try {
            File dataList = new File(fileName);
            if (dataList.createNewFile()) {
                System.out.println("Created file " + dataList.getName());
            } else {
                System.out.println(fileName + " file found");
            }
        } catch (IOException e) {
            System.out.println("File error");
        }
        // Read data from file and initiate class instances so the data can be used.
        try {
            File dataFile = new File(fileName);
            Scanner dataReader = new Scanner(dataFile);
            while (dataReader.hasNextLine()) {
                String data = dataReader.nextLine();
                String[] split = data.split(",");
                if (fileName.equals("src/accounts.csv")) {
                    if (split.length == 4) {
                        users.add(new User(split[0],split[1],split[2],split[3]));
                    } else if (split.length == 5) {
                        users.add(new Technician(split[0],split[1],split[2],split[3],Integer.parseInt(split[4])));
                    }
                } else if (fileName.equals("src/tickets.csv")) {
                    tickets.add(new Ticket(Integer.parseInt(split[0]),split[1],split[2],Integer.parseInt(split[3]),split[4],split[5]));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Validate that a number is within target range.
    public int validator(int maxValue) {
        int selection = -1;
        int temp = -1;
        boolean isValid = false;
        while (!isValid) {
            boolean hasNextInt = scanner.hasNextInt();
            if (hasNextInt) {
                temp = scanner.nextInt();
                if (temp > 0 && temp <= maxValue) {
                    selection = temp;
                    isValid = true;
                }
            }
            if (!isValid) {
                System.out.println("Please enter a value between 1 and " + maxValue);
                scanner.nextLine();
            }
        }
        return selection;
    }


    // Write a data line to provided .csv file
    public void writeToFile(String dataLine, String fileName) {
        try {
            BufferedWriter fileWrite = new BufferedWriter(new FileWriter(fileName, true));
            fileWrite.append(dataLine);
            fileWrite.newLine();
            fileWrite.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    // Reset a csv file to reflect any updated changes on created objects
    public void fileRefresh(String filePath) {

        try{
            FileWriter fw = new FileWriter(filePath, false);
            PrintWriter pw = new PrintWriter(fw, false);
            pw.flush();
            pw.close();
            fw.close();

        }catch(Exception exception){
            System.out.println("Exception have been caught");
        }

        if (filePath.equals("src/accounts.csv")) {
            for (int i=0; i<users.size(); i++) {
                writeToFile(users.get(i).detailsString(),filePath);
            }
        } else if (filePath.equals("src/tickets.csv")) {
            for (int i=0; i<users.size(); i++) {
                writeToFile(users.get(i).detailsString(),filePath);
            }
        }
    }

    // Validates that an email is valid. Not complete
    public String emailValidator() {
        boolean run = true;
        String email = scanner.nextLine();
        while (run) {
            String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                System.out.println("Valid email");
                run = false;
            } else {
                System.out.println("Please enter a valid email");
                email = scanner.nextLine();
            }
        }
        return email;
    }

    // Validates that a password meets strong password criteria
    public String passwordValidator() {
        boolean run = true;
        String password = scanner.nextLine();
        while (run) {
            boolean length = false;
            boolean lowerCase = false;
            boolean upperCase = false;
            boolean digit = false;
            boolean specialChar = false;

            if (password.length() >= 8) {
                length = true;
            }
            for (int i=0; i<password.length(); i++) {
                char ch = password.charAt(i);
                if (Character.isLowerCase(ch)) {
                    lowerCase = true;
                } else if (Character.isUpperCase(ch)) {
                    upperCase = true;
                } else if (Character.isDigit(ch)) {
                    digit = true;
                } else {
                    specialChar = true;
                }
            }
            if (length && lowerCase && upperCase && digit && specialChar) {
                System.out.println("Valid password");
                run = false;
            } else {
                System.out.println("Invalid password please enter a new password meeting the below criteria:");
                if (!length) {
                    System.out.println("Include at least 8 characters");
                }
                if (!lowerCase) {
                    System.out.println("Include at least 1 lower case letter");
                }
                if (!upperCase) {
                    System.out.println("Include at least 1 upper case letter");
                }
                if (!digit) {
                    System.out.println("Include at least 1 digit");
                }
                if (!specialChar) {
                    System.out.println("Include at least 1 special character");
                }
                password = scanner.nextLine();
            }
        }
        return password;
    }

    public void createTicket() {
        System.out.println("Create ticket");
    }

    // Testing methods not to be included in finished product

    public void debugMenu() {
        boolean run = true;
        while (run) {
            System.out.println("""
                1) Print all accounts
                2) Print all tickets
                3) Current logged in account
                4) Exit""");
            int selection = validator(4);
            switch (selection) {
                case 1 -> printAccounts();
                case 2 -> printTickets();
                case 3 -> currentAccount();
                case 4 -> run = false;
            }
        }
    }


    public void currentAccount() {
        if (loggedIn == -1) {
            System.out.println("Not logged in");
        } else {
            System.out.println(users.get(loggedIn).detailsString());
        }
    }
    public void printAccounts() {
        for (int i = 0; i< users.size(); i++) {
            System.out.println(users.get(i).detailsString());
        }
    }

    public void printTickets() {
        for (int i=0; i<tickets.size(); i++) {
            System.out.println(tickets.get(i).detailsString());
        }
    }

    public static void main(String[] args) {
        CincoServiceDesk obj = new CincoServiceDesk();
    }
}

// Classes

class User {

    private String email;
    private String name;
    private String phone;
    private String password;

    public User(String email, String name, String phone, String password) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.password = password;
    }

    public String detailsString() {
        return (email + "," + name + "," + phone + "," + password);
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

class Technician extends User {

    private int technicianLevel;

    public Technician(String email, String name, String phone, String password, int technicianLevel) {
        super(email, name, phone, password);
        this.technicianLevel = technicianLevel;
    }

    @Override
    public String detailsString() {
        return super.detailsString() + "," + technicianLevel;
    }

    public int getTechnicianLevel() {
        return technicianLevel;
    }
}

class Ticket {

    private int ticketNumber;
    private String technician;
    private String description;
    private int severity;
    private String status;
    private String assignedTechnician;

    public Ticket(int ticketNumber, String technician, String description, int severity, String status, String assignedTechnician) {
        this.ticketNumber = ticketNumber;
        this.technician = technician;
        this.description = description;
        this.severity = severity;
        this.status = status;
        this.assignedTechnician = assignedTechnician;
    }

    public String detailsString() {
        return (ticketNumber + "," + technician + "," + description + "," + severity + "," + status + "," + assignedTechnician);
    }
}
