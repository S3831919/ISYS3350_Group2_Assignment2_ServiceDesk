import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CincoServiceDesk {

    Scanner scanner = new Scanner(System.in);
    ArrayList<User> users = new ArrayList<User>();
    ArrayList<Ticket> tickets = new ArrayList<Ticket>();
    int loggedIn = -1;


    public CincoServiceDesk() {
        if (initialiseFile("src/accounts.csv")) {
            // Create hardcoded technician accounts if new .csv file is created at initiation.
            users.add(new Technician("harry@mail.com","Harry Styles","0123456789","Harry&7Styles",1));
            writeToFile("harry@mail.com" + "," + "Harry Styles" + "," + "0123456789" + "," + "Harry&7Styles" + "," + 1, "src/accounts.csv");
            users.add(new Technician("nial@mail.com","Niall Horan","1234567890","Niall&7Horan",1));
            writeToFile("nial@mail.com" + "," + "Niall Horan" + "," + "1234567890" + "," + "Niall&7Horan" + "," + 1,"src/accounts.csv");
            users.add(new Technician("liam@mail.com","Liam Payne","2345678901","Liam&7Payne",1));
            writeToFile("liam@mail.com" + "," + "Liam Payne" + "," + "2345678901" + "," + "Liam&7Payne" + "," + 1,"src/accounts.csv");
            users.add(new Technician("louis@mail.com","Louis Tomlinson","3456789012","Louis&7Tomlinson",2));
            writeToFile("louis@mail.com" + "," + "Louis Tomlinson" + "," + "3456789012" + "," + "Louis&7Tomlinson" + "," + 2,"src/accounts.csv");
            users.add(new Technician("zayn@mail.com","Zayn Malik","4567890123","Zane&7Malik",2));
            writeToFile("zayn@mail.com" + "," + "Zayn Malik" + "," + "4567890123" + "," + "Zane&7Malik" + "," + 2,"src/accounts.csv");
        }
        initialiseFile("src/tickets.csv");

        // CincoServiceDesk initial main menu
        boolean run = true;
        while (run) {
            System.out.println("Welcome to the Cinco Service Desk tool, please select one of the below options:\n" +
                    "1) Create a new account\n" +
                    "2) Login to account\n" +
                    "3) System owner login\n" +
                    "4) Exit");

            int selection = validator(4);

            switch (selection) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    loginAccount();
                    break;
                case 3:
                    systemOwner();
                    break;
                case 4:
                    run = false;
            }

            // CincoServiceDesk Logged in menu
            while (loggedIn != -1 && users.get(loggedIn).getTechnicianLevel() > 0) {
                System.out.print("Welcome " + users.get(loggedIn).getName() + ", Technician Level " + users.get(loggedIn).getTechnicianLevel() +"\n");
                System.out.println("Please select one of the below options:\n" +
                        "1) Create a new ticket\n" +
                        "2) Show your open tickets\n" +
                        "3) Show all tickets\n" +
                        "4) Change ticket status\n" +
                        "5) Change ticket level\n" +
                        "6) Logout");
                int logSelect = validator(6);
                switch (logSelect) {
                    case 1:
                        createTicket();
                        break;
                    case 2:
                        showYourOpenTickets();
                        break;
                    case 3:
                        printTickets();
                        break;
                    case 4:
                        changeTicketStatus();
                        break;
                    case 5:
                        changeTicketLevel();
                        break;
                    case 6:
                        loggedIn = -1;
                        break;
                }
            }

            while (loggedIn != -1 && users.get(loggedIn).getTechnicianLevel() == 0) {
                System.out.print("Welcome " + users.get(loggedIn).getName() + ",");
                System.out.println("Please select one of the below options:\n" +
                        "1) Create a new ticket\n" +
                        "2) Show your open tickets\n" +
                        "3) Logout");
                int logSelect = validator(3);
                switch (logSelect) {
                    case 1:
                        createTicket();
                        break;
                    case 2:
                        showYourTickets();
                        break;
                    case 3:
                        loggedIn = -1;
                        break;
                }
            }
        }
        System.out.println("Thank you for using the Cinco Service Desk tool.");
    }

    // Create a new account
    public void createAccount() {
        scanner.nextLine();
        System.out.println("Type \"e\" at any time to exit back to main menu");
        System.out.println("Please provide a unique email address");
        String email = emailValidator();
        if (email.equals("e")) {return;}
        System.out.println("Please provide your name");
        String name = nameValidator();
        if (name.equals("e")) {return;}
        System.out.println("Please provide your phone number");
        String phone = validatePhone();
        if (phone.equals("e")) {return;}
        System.out.println("Please provide a password to be used with this account");
        String password = passwordValidator();
        if (password.equals("e")) {return;}
        System.out.println("Please enter technician level:\n" +
                "1) Level 1\n" +
                "2) Level 2\n" +
                "3) Not a technician\n" +
                "4) Exit");
        int technicianLevel = validator(4);

        if (technicianLevel == 4) {
            return;
        } else if (technicianLevel == 3) {
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
                        System.out.println("Incorrect password:\n" +
                                "1) Try again\n" +
                                "2) Forgot password\n" +
                                "3) Exit login");
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
    public boolean initialiseFile(String fileName) {
        // Search for existing .txt file or create one if now found.
        boolean fileCreated = false;
        try {
            File dataList = new File(fileName);
            if (dataList.createNewFile()) {
                System.out.println("Created file " + dataList.getName());
                fileCreated = true;
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
                    tickets.add(new Ticket(Integer.parseInt(split[0]),split[1],split[2],split[3],split[4],Integer.parseInt(split[5]),split[6],split[7]));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileCreated;
    }

    public void changeTicketStatus() {
        System.out.println("Please enter a number to select which ticket you would like to change the status for");
        int selection = validator(showYourTickets() + 1) -1;
        System.out.println(selection);
        if (tickets.get(selection).getStatus().equals("Open")) {
            System.out.println("What status would you like to change this ticket to\n" +
                    tickets.get(selection).detailsString() + "\n" +
                    "1) Closed & Resolved\n" +
                    "2) Closed & Unresolved\n" +
                    "3) Exit");
            int changeSelect = validator(3);
            if (selection == 3) {
                return;
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime currentTime = LocalDateTime.now();
            String closeDateTime = dtf.format(currentTime);
            if (changeSelect == 1) {
                tickets.get(selection).setStatus("Closed & Resolved");
            } else if (changeSelect == 2) {
                tickets.get(selection).setStatus("Closed & Unresolved");
            }
            tickets.get(selection).setCloseDateTime(closeDateTime);
            fileRefresh("src/tickets.csv");
        }
        else {
            System.out.println("Unable to change status of archived ticket");
        }

    }

    // Lets a technician change the level of a ticket.
    public void changeTicketLevel() {
        scanner.nextLine();
        System.out.println("Please enter a number to select which ticket you would like to change severity for");
        int selection = validator(showYourTickets() + 1) -1;
        System.out.println("What severity would you like to change this ticket to\n" +
                tickets.get(selection).detailsString() + "\n" +
                "1) Low\n" +
                "2) Medium\n" +
                "3) High\n" +
                "4) Exit");
        int severity = validator(4);
        String severityString = "";
        if (severity == 4) {
            return;
        } else if (severity == 1) {
            severityString = "Low";
        } else if (severity == 2) {
            severityString = "Medium";
        } else if (severity == 3) {
            severityString = "High";
        }

        System.out.println("Severity has been changed to " + severityString);
        tickets.get(selection).setSeverity(severity);
        tickets.get(selection).setAssignedTechnician(users.get(assignTechnician(severity)).getEmail());
        fileRefresh("src/tickets.csv");
    }

    // Show all the tickets of a logged-in user.

    public int showYourTickets() {
        int count = -1;
        int displayCount = 0;
        for (int i=0; i<tickets.size(); i++) {
            count += 1;
            if (users.get(loggedIn).getEmail().equals(tickets.get(i).getStaff())) {
                if (tickets.get(i).getStatus().equals("Closed & Resolved") || tickets.get(i).getStatus().equals("Closed & Unresolved")) {
                    if (within24Hours(tickets.get(i).getCloseDateTime())) {
                        displayCount += 1;
                        System.out.println(displayCount + ") " + tickets.get(i).detailsString());
                    } else {
                        displayCount += 1;
                        System.out.println(displayCount + ") - Archived - " + tickets.get(i).detailsString());
                    }
                } else {
                    displayCount += 1;
                    System.out.println(displayCount + ") " + tickets.get(i).detailsString());
                }
            }
        }
        return count;
    }

    public int showYourOpenTickets() {
        int count = -1;
        int displayCount = 0;
        for (int i=0; i<tickets.size(); i++) {
            count++;
            if (users.get(loggedIn).getEmail().equals(tickets.get(i).getStaff())) {
                if (tickets.get(i).getStatus().equals("Open")) {
                    displayCount++;
                    System.out.println(displayCount + ") " + tickets.get(i).detailsString());
                }
            }
        }
        return count;
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
            for (int i=0; i<tickets.size(); i++) {
                writeToFile(tickets.get(i).detailsString(),filePath);
            }
        }
    }

    // Validates that an email is valid. Not complete
    public String emailValidator() {
        boolean run = true;
        String email = scanner.nextLine();
        if (email.equals("e")) {
            return "e";
        }
        while (run) {
            String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                for (int i=0; i<users.size(); i++) {
                    if (users.get(i).getEmail().equals(email)) {
                        System.out.println("Email already registered, please enter a different email");
                        email = scanner.nextLine();
                    } else {
                        run = false;
                        if (email.equals("e")) {
                            return "e";
                        }
                    }
                }
            } else {
                System.out.println("Please enter a valid email");
                email = scanner.nextLine();
                if (email.equals("e")) {
                    return "e";
                }
            }
        }
        return email;
    }

    // Check that a name is not null
    public String nameValidator() {
        boolean run = true;
        String name = scanner.nextLine();
        if (name.equals("e")) {
            return "e";
        }
        while (run) {
            if (!name.isEmpty()) {
                System.out.println("Valid name");
                run = false;
            } else {
                System.out.println("Please enter a name");
                name = scanner.nextLine();
                if (name.equals("e")) {
                    return "e";
                }
            }
        }
        return name;
    }

    // Validates that a password meets strong password criteria
    public String passwordValidator() {
        boolean run = true;
        String password = scanner.nextLine();
        if (password.equals("e")) {
            return "e";
        }
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
                    System.out.println("Include at least 20 characters");
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
                if (password.equals("e")) {
                    return "e";
                }
            }
        }
        return password;
    }

    // Validate entered phone number is valid format.
    public String validatePhone() {
        boolean run = true;
        String phone = scanner.nextLine();
        if (phone.equals("e")) {
            return "e";
        }
        while (run) {
            String regex = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(phone);
            if (matcher.matches()) {
                System.out.println("Valid phone");
                run = false;
            } else {
                System.out.println("Please enter a valid phone");
                phone = scanner.nextLine();
                if (phone.equals("e")) {
                    return "e";
                }
            }
        }
        return phone;
    }

    public String textValidator() {
        scanner.nextLine();
        boolean run = true;
        String name = scanner.nextLine();
        if (name.equals("e")) {
            return "e";
        }
        while (run) {
            if (!name.isEmpty()) {
                run = false;
            } else {
                System.out.println("Please enter a description");
                name = scanner.nextLine();
                if (name.equals("e")) {
                    return "e";
                }
            }
        }
        return name;
    }

    public void createTicket() {
        System.out.println("Create ticket");
        int ticketNumber = tickets.size() + 100;
        boolean ticketMatch = false;
        while (true) {
            for (int i=0; i<tickets.size(); i++) {
                if (ticketNumber == tickets.get(i).getTicketNumber()) {
                    ticketMatch = true;
                    break;
                }
            }
            if (!ticketMatch) {
                break;
            }
            ticketNumber += 1;
        }
        String staff = users.get(loggedIn).getEmail();
        System.out.println("Please enter a description of the issue");
        String description = textValidator();
        System.out.println("Please enter the severity of this issue\n" +
                "1) Low\n" +
                "2) Medium\n" +
                "3) High\n" +
                "4) Exit");
        int severity = validator(4);
        int technician;
        if (severity == 4) {
            return;
        } else {
            technician = assignTechnician(severity);
        }
        String status = "Open";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime currentTime = LocalDateTime.now();
        String openDateTime = dtf.format(currentTime);

        tickets.add(new Ticket(ticketNumber,openDateTime,"Ongoing",staff,description,severity,status,users.get(technician).getEmail()));
        writeToFile(ticketNumber + "," + openDateTime + "," + "Ongoing" + "," + staff + "," + description + "," + severity + "," + status + "," + users.get(technician).getEmail(),"src/tickets.csv");

    }

    public int assignTechnician(int severity) {
        int [] ticketCount = new int[users.size()];
        for (int i=0; i<users.size(); i++) {
            for (int y=0; y<tickets.size(); y++) {
                if (users.get(i).getEmail().equals(tickets.get(y).getAssignedTechnician())) {
                    ticketCount[i] += 1;
                }
            }
        }
        int selectedTechnician = 0; // Needs to be updated to ensure staff is not selected.
        if (severity <= 2) {
            int lowestTickets = Integer.MAX_VALUE;
            for (int i=0; i<users.size(); i++) {
                if (ticketCount[i] < lowestTickets && users.get(i).getTechnicianLevel() == 1) {
                    lowestTickets = ticketCount[i];
                    selectedTechnician = i;
                }
            }
        }
        if (severity == 3) {
            int lowestTickets = Integer.MAX_VALUE;
            for (int i=0; i<users.size(); i++) {
                if (ticketCount[i] < lowestTickets && users.get(i).getTechnicianLevel() == 2) {
                    lowestTickets = ticketCount[i];
                    selectedTechnician = i;
                }
            }
        }
        return selectedTechnician;
    }

    public void systemOwner() {
        while (true) {
            scanner.nextLine();
            System.out.println("Please enter the system owner password");
            String userEntry = scanner.nextLine();
            String from = "";
            String too = "";
            if (userEntry.equals("System&7Owner")) {
                System.out.println("Please enter the dates you wish to run a report from and too\n" +
                        "Enter the from date in the format YYYY-MM-DD");
                from = dateCheck();
                if (from.equals("exit")) {
                    return;
                }
                System.out.println("Enter the date too in the format YYYY-MM-DD");
                too = dateCheck();
                if (too.equals("exit")) {
                    return;
                }

                for (int i=0; i<tickets.size(); i++) {
                    try {
                        if (within2Date(from,too,tickets.get(i).getOpenDateTime())) {
                            System.out.println(tickets.get(i).detailsString());
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }

            } else {
                System.out.println("Incorrect password");
                return;
            }

            System.out.println("Would you like to run another report?\n" +
                    "1) Yes run another report\n" +
                    "2) No quit back to main menu");
            int selection = validator(2);
            if (selection == 2) {
                return;
            }
        }
    }

    public String dateCheck() {
        boolean isValid = false;
        String date = "";
        while (!isValid) {
            String input = scanner.nextLine();
            if (input.equals("e")) {
                return "exit";
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(input.trim());
                isValid = true;
                date = input;
            } catch (ParseException e) {
                System.out.println("Please enter a valid date in the format yyyy-MM-dd");
            }
        }
        return date;
    }

    // Testing methods not to be included in finished product

    public void debugMenu() {
        boolean run = true;
        while (run) {
            System.out.println("1) Print all accounts\n" +
                    "2) Print all tickets\n" +
                    "3) Current logged in account\n" +
                    "4) Print non archived tickets\n" +
                    "5) Exit");
            int selection = validator(5);
            switch (selection) {
                case 1:
                    printAccounts();
                    break;
                case 2:
                    printTickets();
                    break;
                case 3:
                    currentAccount();
                    break;
                case 4:
                    printNonArchiveTickets();
                    break;
                case 5:
                    run = false;
                    break;
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

    public void printNonArchiveTickets() {
        for (int i=0; i<tickets.size(); i++) {
            if (within24Hours(tickets.get(i).getCloseDateTime())) {
                System.out.println(tickets.get(i).detailsString());
            }
        }
    }

    public void within24HoursBackup() {
        String pattern = "yyyy/MM/dd HH:mm:ss";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime currentTime = LocalDateTime.now();
        String openDateTime = dtf.format(currentTime);

        boolean under24Hours;
        Date dateTime;
        Date dateTime2;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            dateTime = sdf.parse(openDateTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        try {
            dateTime2 = sdf.parse(openDateTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    static boolean within2Date(String from, String too, String selected) throws ParseException {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        Date fromDate = sdf.parse(from);
        Date tooDate = sdf.parse(too);
        Date selectedDate = sdf.parse(selected);

        return selectedDate.after(fromDate) && selectedDate.before(tooDate);
    }

    static boolean within24Hours(String closeDate)    {

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);


        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime currentTime = LocalDateTime.now();
        String currentTimeDate = dtf.format(currentTime);

        long hourDifference = 100;
        try {
            Date d1 = sdf.parse(closeDate);
            Date d2 = sdf.parse(currentTimeDate);

            long timeDifference = d2.getTime() - d1.getTime();
            long dayDifference = (timeDifference / (1000 * 60 * 60 * 24)) % 365;
            long yearDifference = (timeDifference / (1000L * 60 * 60 * 24 * 365));
            if (dayDifference > 0 || yearDifference > 0) {
                return false;
            }

            hourDifference = (timeDifference / (1000 * 60 * 60)) % 24;

        }
        // Catch the Exception
        catch (ParseException e) {
            e.printStackTrace();
        }
        return hourDifference < 24;
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

    public int getTechnicianLevel() {
        return 0;
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

    private String openDateTime;
    private String closeDateTime;
    private String staff;
    private String description;
    private int severity;
    private String status;
    private String assignedTechnician;

    public Ticket(int ticketNumber, String openDateTime, String closeDateTime, String staff, String description, int severity, String status, String assignedTechnician) {
        this.ticketNumber = ticketNumber;
        this.openDateTime = openDateTime;
        this.closeDateTime = closeDateTime;
        this.staff = staff;
        this.description = description;
        this.severity = severity;
        this.status = status;
        this.assignedTechnician = assignedTechnician;
    }

    public String detailsString() {
        return (ticketNumber + "," + openDateTime  + "," + closeDateTime +"," + staff + "," + description + "," + severity + "," + status + "," + assignedTechnician);
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public String getStaff() {
        return staff;
    }

    public String getDescription() {
        return description;
    }

    public int getSeverity() {
        return severity;
    }

    public String getStatus() {
        return status;
    }

    public String getOpenDateTime() {
        return openDateTime;
    }

    public String getCloseDateTime() {
        return closeDateTime;
    }

    public String getAssignedTechnician() {
        return assignedTechnician;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCloseDateTime(String closeDateTime) {
        this.closeDateTime = closeDateTime;
    }

    public void setAssignedTechnician(String assignedTechnician) {
        this.assignedTechnician = assignedTechnician;
    }
}
