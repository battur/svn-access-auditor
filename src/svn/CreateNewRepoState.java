package svn;

/**
 * A processor state that is responsible for creating new repositories.
 * @author bsanchin
 */
public class CreateNewRepoState
    implements IState {

  protected SvnAccessAuditor proc;
  protected Repo currentRepo;

  CreateNewRepoState(SvnAccessAuditor argProc) {
    this.proc = argProc;
  }

  /** {@inheritDoc} */
  @Override
  public void process(String line) {

    // Skip empty lines.
    if (line.matches("^\\s*$")) {
      return;
    }
    // Looks like current line has a new repository definition.
    else if (line.matches("^\\s*\\[.+")) {
      Repo repo = new Repo(line.trim());
      currentRepo = repo;
      proc.repos.put(repo.id, repo);
    }
    // This line must have groups that have access to this group.
    else {
      AddGroupsToRepoState state = new AddGroupsToRepoState(proc);
      state.currentRepo = this.currentRepo;
      proc.currentState = state;
      state.process(line);
    }
  }

}
