package svn;

/**
 * A processor state that is responsible for creating new groups.
 * @author bsanchin
 */
public class CreateNewGroupState
    implements IState {

  protected SvnAccessAuditor proc;
  protected Group currentGroup;

  CreateNewGroupState(SvnAccessAuditor argProc) {
    this.proc = argProc;
  }

  /** {@inheritDoc} */
  @Override
  public void process(String line) {

    // Skip empty lines.
    if (line.matches("^\\s*$")) {
      return;
    }
    // Looks like current line has a new group definition.
    else if (line.matches(".+=.+")) {
      String[] parts = line.split(IState.EQUAL_SIGN);
      currentGroup = new Group(parts[0].trim());
      proc.groups.put(currentGroup.id, currentGroup);
      for (String st : parts[1].trim().split(IState.REGEX_SPLIT)) {
        if (st.trim().startsWith("@")) {
          // Well, this must be a group in a group because it starts with @.
          Group subGroup = proc.groups.get(st.trim().replace("@", ""));
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
    // Looks like this line has list of users who belong to this group.
    else {
      // This must be a malformed access configuration file.
      if (currentGroup == null) {
        throw new RuntimeException("Expected a new group. But it was not found in: " + line);
      }

      AddUsersToGroupState state = new AddUsersToGroupState(proc);
      state.currentGroup = this.currentGroup;
      proc.currentState = state;
      state.process(line);
    }
  }

}
