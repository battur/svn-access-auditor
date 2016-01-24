package svn;

/**
 * A processor state that is responsible for adding groups to current repository.
 * @author bsanchin
 */
public class AddGroupsToRepoState
    implements IState {

  final static String WRITE = "rw";
  final static String READ = "r";

  protected SvnAccessAuditor proc;
  protected Repo currentRepo;

  public AddGroupsToRepoState(SvnAccessAuditor argProc) {
    this.proc = argProc;
  }

  /** {@inheritDoc} */
  @Override
  public void process(String line) {

    // Skip empty lines.
    if (line.matches("^\\s*$")) {
      return;
    }
    // Looks like this line has a new repository definition.
    else if (line.matches("^\\s*\\[.+")) {
      proc.currentState = new CreateNewRepoState(proc);
      proc.currentState.process(line);
    }
    // Looks like this line has access privilege information about a group.
    else if (line.matches(".+=.+")) {
      String[] parts = line.split("\\s*=\\s*");
      Group group = null;
      if (parts[0].trim().equals("*")) {
        group = proc.EVERYONE;
      }
      else {
        group = proc.groups.get(parts[0].trim().replace("@", ""));
      }

      if (group == null) {
        throw new RuntimeException("Failed to identify group from: " + line);
      }

      if (parts[1].trim().equalsIgnoreCase(READ)) {
        currentRepo.readers.add(group);
      }
      else if (parts[1].trim().equalsIgnoreCase(WRITE)) {
        currentRepo.writers.add(group);
      }
      else {
        throw new RuntimeException("Failed to identify permission from: " + line);
      }
    }
    else {
      throw new RuntimeException("Unable to parse this line:" + line);
    }

  }

}
