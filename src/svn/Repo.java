package svn;

import java.util.TreeSet;

/**
 * Abstraction to a svn repository.
 * @author bsanchin
 */
public class Repo
    implements Comparable<Repo> {

  // Readers of this repo
  TreeSet<Group> readers = new TreeSet<Group>();
  // Writers of this repo
  TreeSet<Group> writers = new TreeSet<Group>();

  String id;

  Repo(String argId) {
    this.id = argId;
  }

  @Override
  public int compareTo(Repo other) {
    return id.compareTo(other.id);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof Repo) {
      return id.equals(((Repo) other).id);
    }
    return false;
  }

  /**
   * Returns users who have read and write access to this repo.
   * @return users who have read and write access
   */
  public TreeSet<User> getReadersAndWritersAsUserSet() {
    TreeSet<User> users = new TreeSet<User>();
    users.addAll(getReadersAsUsersSet());
    users.addAll(getWritersAsUsersSet());
    return users;
  }

  /**
   * Returns users who have read access to this repo.
   * @return users who have read access
   */
  public TreeSet<User> getReadersAsUsersSet() {
    TreeSet<User> users = new TreeSet<User>();
    for (Group g : readers) {
      users.addAll(g.getAllUsers());
    }
    return users;
  }

  /**
   * Returns users who have write access to this repo.
   * @return users who have write access
   */
  public TreeSet<User> getWritersAsUsersSet() {
    TreeSet<User> users = new TreeSet<User>();
    for (Group g : writers) {
      users.addAll(g.getAllUsers());
    }
    return users;
  }

  @Override
  public int hashCode() {
    return 17 * id.hashCode();
  }

  @Override
  public String toString() {
    return id;
  }

}
