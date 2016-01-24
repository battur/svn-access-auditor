package svn;

/**
 * A processor state that is responsible for adding users to current group.
 * @author bsanchin
 */
public class AddUsersToGroupState
    implements IState {

  protected Group currentGroup;
  protected SvnAccessAuditor proc;

  AddUsersToGroupState(SvnAccessAuditor argProc) {
    this.proc = argProc;
  }

  /** {@inheritDoc} */
  @Override
  public void process(String line) {

    // Skip empty lines.
    if (line.matches("^\\s*$")) {
      return;
    }
    // Looks like this line has a new group definition.
    else if (line.matches(".+=.+")) {
      proc.currentState = new CreateNewGroupState(proc);
      proc.currentState.process(line);
    }
    // Looks like repositories start from this line on.
    else if (line.matches(".*\\[.*")) {
      proc.currentState = new CreateNewRepoState(proc);
      proc.currentState.process(line);
    }
    // Looks like this line consists of list of users.
    else {
      for (String st : line.split("\\s*,\\s*")) {
        if (st.trim().startsWith("@")) {
          Group subGroup = proc.groups.get(st.trim().replace("@", ""));
          // If this is a group, this must be defined before.
          if (subGroup != null) {
            currentGroup.addUser(subGroup);
          }
          else {
            System.err.println("Unable to identify group: " + st);
          }
        }
        else {
          User user = proc.users.get(st.trim());
          if (user == null) {
            user = new User(st.trim());
            proc.users.put(user.id, user);
          }
          currentGroup.addUser(user);
        }
      }
    }

  }

}
