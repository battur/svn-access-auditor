package svn;

import java.io.*;
import java.util.*;

/**
 * Generates an HTML report based on repositories, groups, users, and access permission information. Generated
 * report would have a name repos.html. There will a lot of cross linking between entities but all these makes
 * navigation easier.
 * @author bsanchin
 */
public class HtmlReportGenerator
    extends AbstractReportGenerator {

  static final String REPORT_FILE = "repos.html";
  static final String COMMA = ", ";
  static final String BR = "</br>";
  static final String NL = System.getProperty("line.separator");

  HtmlReportGenerator(TreeMap<String, User> argUsers, TreeMap<String, Group> argGroups,
      TreeMap<String, Repo> argRepos) {
    super(argUsers, argGroups, argRepos);
  }

  /** {@inheritDoc} */
  @Override
  public void generateReport()
      throws Exception {

    SimpleStringBuilder sb = new SimpleStringBuilder();
    sb.appendln(makeHeader());
    sb.appendln(makeSummary());
    sb.appendln(makeRepos());
    sb.appendln(makeGroups());
    sb.appendln(makeUsers());
    sb.appendln("</body></html>");

    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(REPORT_FILE)));
    writer.write(sb.toString());
    writer.close();
    System.out.println("Report generation completed! See repos.html for the result.");
  }

  /**
   * Generates HTML portion of the report with repositories information.
   * @return HTML portion of the report with repositories information
   */
  protected String makeRepos() {
    SimpleStringBuilder sb = new SimpleStringBuilder();
    sb.appendln("<h1 id='repositories'>Repositories</h1>");
    sb.appendln("<table border='1'>");
    sb.appendln("  <tr>");
    sb.appendln("    <th>Repos</th>");
    sb.appendln("    <th>Readers and Writers Combined</th>");
    sb.appendln("    <th>Readers Only</th>");
    sb.appendln("    <th>Readers Only Count</th>");
    sb.appendln("    <th>Writers</th>");
    sb.appendln("    <th>Writers Count</th>");
    sb.appendln("  </tr>");

    for (Integer num : reposGroupedByUsers.keySet()) {
      Repo oneOfTheRepos = reposGroupedByUsers.get(num).first();
      sb.appendln("  <tr>");
      sb.append("    <td>");
      sb.append(bookmark(reposGroupedByUsers.get(num), "repo", "</br>"));
      sb.appendln("    </td>");
      sb.append("    <td align='center'>");
      sb.append(oneOfTheRepos.getReadersAndWritersAsUserSet().size());
      sb.appendln("    </td>");
      sb.append("    <td class='read'>");
      sb.append(getGroupDetail(oneOfTheRepos.readers));
      sb.appendln("    </td>");
      sb.append("    <td align='center' class='read'>");
      sb.append(oneOfTheRepos.getReadersAsUsersSet().size());
      sb.appendln("    </td>");
      sb.append("    <td class='write'>");
      sb.append(getGroupDetail(oneOfTheRepos.writers));
      sb.appendln("    </td>");
      sb.append("    <td align='center' class='write'>");
      sb.append(oneOfTheRepos.getWritersAsUsersSet().size());
      sb.appendln("    </td>");
      sb.appendln("  </tr>");
    }
    sb.append("</table><br><br>");
    return sb.toString();
  }

  /**
   * Creates anchor for each of the list elements.
   * @param list elements to be anchored/bookmarked.
   * @param anchor anchor pattern
   * @param connector a connector that will be put between the elements
   * @return anchored elements
   */
  private String bookmark(Collection<? extends Object> list, String anchor, String connector) {
    SimpleStringBuilder sb = new SimpleStringBuilder();
    int cursor = 0;
    for (Object obj : list) {
      sb.append("<span id='" + anchor + ":" + win(obj) + "'></span>");
      sb.append(obj);
      if (cursor < list.size() - 1) {
        sb.appendln(connector);
      }
      cursor++ ;
    }
    return sb.toString();
  }

  /**
   * Generates HTML portion of the report with group and user mapping information.
   * @param argGroups list of groups
   * @return HTML portion of the report with group and user mapping information
   */
  private String getGroupDetail(Collection<Group> argGroups) {
    SimpleStringBuilder sb = new SimpleStringBuilder();
    for (User user : argGroups) {
      if (user instanceof Group) {
        sb.append("<a href='#group:" + win(user.id) + "'>");
        sb.append("@" + user.id);
        sb.appendln("</a>");
        sb.appendln(BR);
      }
      else {
        sb.append(user);
      }
    }
    return sb.toString().replaceAll(COMMA + "$", "");
  }

  /**
   * Creates links using give list of objects.
   * @param list list of objects to be linked
   * @param anchor anchor pattern
   * @param connector a connector that will be put between elements
   * @return linked elements
   */
  private String link(Collection<? extends Object> list, String anchor, String connector) {
    SimpleStringBuilder sb = new SimpleStringBuilder();
    int cursor = 0;
    for (Object obj : list) {
      sb.append("<a href='#" + anchor + ":" + win(obj) + "'>");
      sb.append(obj);
      sb.append("</a>");
      if (cursor < list.size() - 1) {
        sb.appendln(connector);
      }
      cursor++ ;
    }
    return sb.toString();
  }

  /**
   * Generates HTML portion of the report with groups information.
   * @return HTML portion of the report with groups information
   */
  private String makeGroups() {
    SimpleStringBuilder sb = new SimpleStringBuilder();
    sb.appendln("<h1 id='groups'>Groups</h1>");
    for (Group g : groups.values()) {
      sb.append("<h3 id='group:" + g.id + "'>");
      sb.append(g.id);
      sb.append(" (" + g.getAllUsers().size() + ")");
      sb.appendln("</h3>");
      SimpleStringBuilder tmp = new SimpleStringBuilder();
      for (User u : g.users) {
        boolean isGroup = (u instanceof Group);
        tmp.append("<a href='#" + (isGroup ? "group" : "user") + ":" + win(u.id) + "'>");
        tmp.append(u);
        tmp.append("</a>");
        tmp.appendln(COMMA);
      }
      sb.append(tmp.toString().replaceAll(", $", ""));
      sb.append("</br>");
    }
    sb.append("<br><br>");

    return sb.toString();
  }

  /**
   * Generates HTML portion of the report with header and some styling.
   * @return HTML portion of the report with header and some styling
   */
  private String makeHeader() {
    SimpleStringBuilder sb = new SimpleStringBuilder();
    sb.appendln("<html><head><style>");
    sb.appendln("  table { border-collapse: collapse;}");
    sb.appendln("  tr:nth-child(even) {background-color: #f2f2f2}");
    sb.appendln("  th {padding: 15px; background-color: #4CAF50; color: white; font-family: arial;}");
    sb.appendln("  td{vertical-align:top;}");
    sb.appendln("  h3{margin-bottom: 2px;}");
    sb.appendln("  .read{background-color: #E0F8F1}");
    sb.appendln("  .write{background-color: #F5F6CE}");
    sb.appendln("</style></head><body>");
    return sb.toString();
  }

  /**
   * Generates HTML portion of the report with summary of the report.
   * @return HTML portion of the report with summary of the report
   */
  private String makeSummary() {
    SimpleStringBuilder sb = new SimpleStringBuilder();
    TreeSet<User> tmp = new TreeSet<User>();
    for (Group g : groups.values()) {
      tmp.addAll(g.getAllUsers());
    }
    sb.appendln("<h1>Summary</h1>");
    sb.appendln("Repositories (including sub repositories): " + repos.size());
    sb.appendln("<br>");
    sb.appendln("SVN Users: " + tmp.size());
    sb.appendln("<br><br>");
    sb.appendln(
        "Quick Links: <a href='#repositories'>Repositories</a>, <a href='#groups'>Groups</a>, <a href='#users'>SVN Users</a>");
    sb.appendln("<br><br><br>");
    return sb.toString();
  }

  /**
   * Generates HTML portion of the report with users information.
   * @return HTML portion of the report with users information
   */
  private String makeUsers() {
    SimpleStringBuilder sb = new SimpleStringBuilder();
    sb.appendln("<h1 id='users'>SVN Users</h1>");
    sb.appendln("<table border='1'>");
    sb.appendln("  <tr>");
    sb.appendln("    <th>Username</th>");
    sb.appendln("    <th>Reabable and Writable Repos Combined</th>");
    sb.appendln("    <th>Readable Only Repos</th>");
    sb.appendln("    <th>Readable Only Repos Count</th>");
    sb.appendln("    <th>Writable Repos</th>");
    sb.appendln("    <th>Writable Repos Count</th>");
    sb.appendln("  </tr>");

    for (Integer num : usersGroupedByRepos.keySet()) {
      sb.appendln("  <tr>");
      sb.append("    <td>");
      sb.append(bookmark(usersGroupedByRepos.get(num), "user", "</br>"));
      sb.appendln("    </td>");
      sb.append("    <td align='center'>");
      User oneOfTheUsers = usersGroupedByRepos.get(num).first();
      sb.append(oneOfTheUsers.getReadableAndWritableRepos(repos.values()).size());
      sb.appendln("    </td>");
      sb.append("    <td class='read'>");
      sb.append(link(oneOfTheUsers.getReadableRepos(repos.values()), "repo", "</br>"));
      sb.appendln("    </td>");
      sb.append("    <td align='center' class='read'>");
      sb.append(oneOfTheUsers.getReadableRepos(repos.values()).size());
      sb.appendln("    </td>");
      sb.append("    <td class='write'>");
      sb.append(link(oneOfTheUsers.getWritableRepos(repos.values()), "repo", "</br>"));
      sb.appendln("    </td>");
      sb.append("    <td align='center' class='write'>");
      sb.append(oneOfTheUsers.getWritableRepos(repos.values()).size());
      sb.appendln("    </td>");
      sb.appendln("  </tr>");
    }
    sb.appendln("</table>");
    return sb.toString();
  }

  /**
   * Looks like we need to replace backslash with forward slashes if we are to use the report on Windows OS.
   * @param argNonWindowsAnchor link or anchor string
   * @return massaged link or anchor string
   */
  private String win(Object argNonWindowsAnchor) {
    return argNonWindowsAnchor.toString().replace("\\", "/");
  }

  /**
   * Java's StringBuilder lacks appendln method. But we could wrap this guy to create what we wanted.
   * @author bsanchin
   */
  static class SimpleStringBuilder {
    private StringBuilder sb = new StringBuilder();

    @Override
    public String toString() {
      return sb.toString();
    }

    void append(Object argObject) {
      sb.append(argObject.toString());
    }

    void appendln(Object argObject) {
      append(argObject);
      append(NL);
    }
  }

}
