package info.spotcomms.jhostsblock;

/**
 * Created using IntelliJ IDEA
 * User: Tad
 * Date: 5/13/15
 * Time; 11:25 AM
 */
public class Console {

    public Console(boolean isAdmin, String[] args) {
        System.out
            .println("========================= JHostsBlock =========================");//Banner
        if (!isAdmin) {//Inform the user and exit the program if not launched with administrative/root privileges
            System.out.println(
                "This program requires to be run with administrative/root privileges");//Inform
            System.exit(0);//Exit
        } else {
            HostsManager hostsManager = new HostsManager();//Instantiate a new HostsManager class
            switch (args[0]) {//Perform an action based off the command
                case "-h":
                case "--help":
                    printHelp();//Displays the help message
                    break;
                case "-u":
                case "--update":
                    hostsManager.update();//Updates the HOSTS file with the latest lists
                    break;
                case "-r":
                case "--rollback":
                    hostsManager.rollback(false);//Rolls back the HOSTS file to the previous one
                    break;
                case "-d":
                case "--disable":
                    hostsManager.rollback(true);//Rolls back to the HOSTS file to the original one
                    break;
                default:
                    System.out
                        .println("Unknown Command");//Inform the user that their command isn't valid
                    printHelp();//Displays the help message
                    break;
            }
            System.exit(0);//Exit
        }
    }

    public void printHelp() {//Prints out a help message
        System.out.println("-h or --help\n\tDisplays this message");
        System.out.println("-u or --update\n\tUpdates the HOSTS file with the latest lists");
        System.out.println("-r or --rollback\n\tRolls back the HOSTS file to the previous one");
        System.out.println("-d or --disable\n\tRolls back to the HOSTS file to the original one");
    }

}
