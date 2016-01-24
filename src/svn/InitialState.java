package svn;

/**
 * Initial state of the processor that would read the access configuration file first.
 * @author bsanchin
 */
public class InitialState
    implements IState {

  protected SvnAccessAuditor proc;

  InitialState(SvnAccessAuditor argProc) {
    this.proc = argProc;
  }

  @Override
  public void process(String line) {
    // Ignore blank lines.
    if (line == null || line.matches("^\\s*$")) {
      return;
    }
    // Create groups
    else if (line.matches("^\\[groups\\]\\s*$")) {
      CreateNewGroupState state = new CreateNewGroupState(proc);
      proc.currentState = state;
    }
    // The config file should be a valid file, otherwise, just don't process it.
    else {
      throw new RuntimeException("Unexpected string: " + line);
    }
  }
}
