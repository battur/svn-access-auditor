package svn;

import java.util.TreeSet;

/**
 * Abstraction to group of users.
 * @author bsanchin
 */
public class Group
    extends User {

  // A group will have at least one user
  TreeSet<User> users = new TreeSet<User>();

  public Group(String argId) {
    super(argId);
  }

  /**
   * Adds user.
   * @param argUser user to be added to this group
   */
  public void addUser(User argUser) {
    users.add(argUser);
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object other) {
    if (other instanceof Group) {
      return id.equals(((Group) other).id);
    }
    return false;
  }

  /**
   * Returns users of this group.
   * @return users of this group
   */
  public TreeSet<User> getAllUsers() {
    TreeSet<User> tmp = new TreeSet<User>();
    for (User u : users) {
      // This must a sub group within this group. We need its users.
      if (u instanceof Group) {
        tmp.addAll(((Group) u).getAllUsers());
      }
      else {
        tmp.add(u);
      }
    }
    return tmp;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "@" + id;
  }

}
