package svn;

/**
 * The access configuration is processed in 5 steps/states:
 * <p>
 * <ul>
 * <li>{@link InitialState}
 * <li>{@link CreateNewGroupState}
 * <li>{@link AddUsersToGroupState}
 * <li>{@link CreateNewRepoState}
 * <li>{@link AddGroupsToRepoState}
 * </ul>
 * <p>
 * State transition happens based on the configuration file line that is currently being read.
 * @author bsanchin
 */
public interface IState {

  public static final String EQUAL_SIGN = "=";
  public static final String REGEX_SPLIT = "\\s*,\\s*";

  /**
   * Processes current line and makes transition based on the implementation.
   * @param line line to be processed
   */
  void process(String line);

}
