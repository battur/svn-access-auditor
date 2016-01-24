package svn;

import java.util.Collection;
import java.util.TreeSet;

/**
 * Abstraction to a svn user.
 * @author bsanchin
 */
public class User
    implements Comparable<User> {

  protected String id;

  public User(String argId) {
    this.id = argId;
  }

  @Override
  public int compareTo(User other) {
    return id.compareTo(other.id);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof User) {
      return id.equals(((User) other).id);
    }
    return false;
  }

  /**
   * Based on given list of repos, returns reabable and writable repos for this user.
   * @param argRepos list of repos
   * @return readable and writable repos
   */
  public TreeSet<Repo> getReadableAndWritableRepos(Collection<Repo> argRepos) {
    TreeSet<Repo> repos = new TreeSet<Repo>();
    repos.addAll(getReadableRepos(argRepos));
    repos.addAll(getWritableRepos(argRepos));
    return repos;
  }

  /**
   * Based on given list of repos, returns the readable repos for this user.
   * @param argRepos list of repos
   * @return readable repos
   */
  public TreeSet<Repo> getReadableRepos(Collection<Repo> argRepos) {
    TreeSet<Repo> repos = new TreeSet<Repo>();
    for (Repo r : argRepos) {
      if (r.getReadersAsUsersSet().contains(this)) {
        repos.add(r);
      }
    }
    return repos;
  }

  /**
   * Based on given list of repos, returns the writable repos for this user.
   * @param argRepos list of repos
   * @return writable repos
   */
  public TreeSet<Repo> getWritableRepos(Collection<Repo> argRepos) {
    TreeSet<Repo> repos = new TreeSet<Repo>();
    for (Repo r : argRepos) {
      if (r.getWritersAsUsersSet().contains(this)) {
        repos.add(r);
      }
    }
    return repos;
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
