package svn;

import java.util.*;

/**
 * An abstract base for processor states. Grouping of users with the same access privileges and grouping of
 * repositories with the same access users are calculated in here.
 * @author bsanchin
 */
abstract class AbstractReportGenerator
    implements IReportGenerator {

  protected TreeMap<String, User> users = new TreeMap<String, User>();
  protected TreeMap<String, Group> groups = new TreeMap<String, Group>();
  protected TreeMap<String, Repo> repos = new TreeMap<String, Repo>();

  // Repositories grouped by the same users.
  protected TreeMap<Integer, TreeSet<Repo>> reposGroupedByUsers = new TreeMap<Integer, TreeSet<Repo>>();

  // Users grouped by the same access repos.
  protected TreeMap<Integer, TreeSet<User>> usersGroupedByRepos = new TreeMap<Integer, TreeSet<User>>();

  AbstractReportGenerator(TreeMap<String, User> argUsers, TreeMap<String, Group> argGroups,
      TreeMap<String, Repo> argRepos) {
    this.users = argUsers;
    this.groups = argGroups;
    this.repos = argRepos;
    groupRepos();
    groupUsers();
  }

  /**
   * Groups repositories based on their users.
   */
  protected final void groupRepos() {
    int counter = 0;

    // Add all repos into a single list.
    List<Repo> reposListUnderReview = new ArrayList<Repo>();
    for (Repo r : repos.values()) {
      reposListUnderReview.add(r);
    }

    while (counter < reposListUnderReview.size() - 1) {

      // Collect repos with the same users as current repo has.
      List<Integer> tmpReposWithSameUsers = new ArrayList<Integer>();
      Repo currentRepo = reposListUnderReview.get(counter);
      for (int i = counter + 1; i < reposListUnderReview.size(); i++ ) {
        Repo otherRepo = reposListUnderReview.get(i);
        if (currentRepo.readers.equals(otherRepo.readers) && currentRepo.writers.equals(otherRepo.writers)) {
          tmpReposWithSameUsers.add(i);
        }
      }

      // Well, we need a sorted list of repos that have the same users.
      TreeSet<Repo> sameRepos = new TreeSet<Repo>();
      sameRepos.add(reposListUnderReview.get(counter));

      // We need to remove those repos from the list of repos under review.
      for (int i = tmpReposWithSameUsers.size() - 1; i >= 0; i-- ) {
        sameRepos.add(reposListUnderReview.get(tmpReposWithSameUsers.get(i)));
        reposListUnderReview.remove(reposListUnderReview.get(tmpReposWithSameUsers.get(i)));
      }
      reposGroupedByUsers.put(counter, sameRepos);
      counter++ ;
    }
  }

  /**
   * Groups users based on their accessible repositories.
   */
  protected final void groupUsers() {
    int counter = 0;

    // Add all users into a single list.
    List<User> usersListUnderReview = new ArrayList<User>();
    for (User u : users.values()) {
      usersListUnderReview.add(u);
    }

    // We need some cache (dynamic programming) to speed up the processing.
    Hashtable<String, TreeSet<Repo>> readables = new Hashtable<String, TreeSet<Repo>>();
    Hashtable<String, TreeSet<Repo>> writables = new Hashtable<String, TreeSet<Repo>>();

    while (counter < usersListUnderReview.size() - 1) {

      // Collect users with the same access privileges as the current user has.
      List<Integer> tmpUsersWithSameRepos = new ArrayList<Integer>();
      User currentUser = usersListUnderReview.get(counter);
      for (int i = counter + 1; i < usersListUnderReview.size(); i++ ) {
        User otherUser = usersListUnderReview.get(i);
        if (getReadableRepos(currentUser, readables).equals(getReadableRepos(otherUser, readables))
            && getWritableRepos(currentUser, writables).equals(getWritableRepos(otherUser, writables))) {
          tmpUsersWithSameRepos.add(i);
        }
      }

      // Well, we need a sorted list of users that have the same access privileges.
      TreeSet<User> sameUsers = new TreeSet<User>();
      sameUsers.add(usersListUnderReview.get(counter));

      // We need to remove those users from the list of users under review.
      for (int i = tmpUsersWithSameRepos.size() - 1; i >= 0; i-- ) {
        sameUsers.add(usersListUnderReview.get(tmpUsersWithSameRepos.get(i)));
        usersListUnderReview.remove(usersListUnderReview.get(tmpUsersWithSameRepos.get(i)));
      }
      usersGroupedByRepos.put(counter, sameUsers);
      counter++ ;
    }
  }

  /**
   * Returns readable repos for this user. If the cache has the repos for this user, then it returns the
   * result from the cache. If it does not have, then it loads the repos and stores them in the cache.
   * @param argUser the user
   * @param argRepos a cache that holds user to repo mapping
   * @return readable repos for the user
   */
  private TreeSet<Repo> getReadableRepos(User argUser, Hashtable<String, TreeSet<Repo>> argRepos) {
    TreeSet<Repo> result = argRepos.get(argUser.id);
    if (result != null) {
      return result;
    }
    else {
      result = argUser.getReadableRepos(repos.values());
      argRepos.put(argUser.id, result);
      return result;
    }
  }

  /**
   * Returns writable repos for this user. If the cache has the repos for this user, then it returns the
   * result from the cache. If it does not have, then it loads the repos and stores them in the cache.
   * @param argUser the user
   * @param argRepos a cache that holds user to repo mapping
   * @return writable repos for the user
   */
  private TreeSet<Repo> getWritableRepos(User argUser, Hashtable<String, TreeSet<Repo>> argRepos) {
    TreeSet<Repo> result = argRepos.get(argUser.id);
    if (result != null) {
      return result;
    }
    else {
      result = argUser.getWritableRepos(repos.values());
      argRepos.put(argUser.id, result);
      return result;
    }
  }

}
