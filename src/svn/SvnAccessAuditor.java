package svn;

import java.io.*;
import java.util.TreeMap;

/**
 * SVN access auditor and report generator.
 * <p>
 * When you have 1000+ users accessing 250+ svn repositories/projects, you would like to know how many users
 * have what type of access rights to those repositories. Some repositories may host sensitive source code
 * that you want to conceal from certain groups. This small tool processes svn access configuration file and
 * generates an HTML report that makes sense. The report has 4 parts:
 * <p>
 * <ol>
 * <li>Summary: Number of users and repositories. Quick links to other parts of the report.
 * <li>List of all repositories: For each of repositories, it lists groups of readers, groups of writers, and
 * numbers for each. Repositories with the same readers and writers are grouped together, so that the report
 * would look compact and clear.
 * <li>Mapping of users to groups.
 * <li>List of all users. For each of users, it lists readable and writable repositories. Users with the same
 * readable and writable repositories are grouped together.
 * </ol>
 * <p>
 * TODO: Here is an example of a svn access confiruation file. And here is a report generated from the
 * configuration file. Cross linking between repositories, groups, and users makes auditing access rights so
 * much easier. As you see, this is an interesting example of graph problems.
 * <p>
 * 
 * @author Battur Sanchin (battursanchin@gmail.com)
 * <p>
 * License: Apache 2.0
 */
public class SvnAccessAuditor {

  public static void main(String[] args)
      throws Exception {
    SvnAccessAuditor driver = new SvnAccessAuditor();
    driver.drive(args);
    // driver.drive(new String[] {"svnaccess.conf"});
  }

  protected IState currentState;
  protected IReportGenerator reportGenerator;
  protected TreeMap<String, User> users = new TreeMap<String, User>();
  protected TreeMap<String, Group> groups = new TreeMap<String, Group>();
  protected TreeMap<String, Repo> repos = new TreeMap<String, Repo>();

  protected final Group EVERYONE = new Group("EVERYONE");

  /**
   * Drives the application.
   * @param args first element would be the svn access configuration file
   * @throws Exception if it fails to generate a report
   */
  public void drive(String[] args)
      throws Exception {

    // We need an access config file in order to generate a report.
    if (args == null || args.length < 1) {
      System.out.println("=== SVN Access Auditor v1.0 ===");
      System.out.println("Outputs mapping information (HTML) between SVN repos and users. ");
      System.out.println("Usage:");
      System.out.println("  java -jar svnaccessauditor.jar <svnaccess.conf>");
      System.out.println();
      System.out.println("Output: repos.html");
      return;
    }

    File accessConfigFile = new File(args[0]);
    if (!accessConfigFile.exists() || accessConfigFile.isDirectory()) {
      System.out.println("File not exists at: " + args[0]);
      return;
    }

    System.out.println("Processing the records...");
    BufferedReader reader = new BufferedReader(new FileReader(accessConfigFile));
    String line;

    // Let the states handle the records.
    currentState = new InitialState(this);
    while ((line = reader.readLine()) != null) {
      currentState.process(line);
    }
    reader.close();

    // Add all users to EVERYONE group.
    for (User u : users.values()) {
      EVERYONE.addUser(u);
    }
    groups.put("EVERYONE", EVERYONE);

    reportGenerator = new HtmlReportGenerator(users, groups, repos);
    reportGenerator.generateReport();
  }

}
