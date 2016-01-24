package svn;

/**
 * A common interface to a report generator.
 * @author bsanchin
 */
public interface IReportGenerator {

  /**
   * Generates a report from a svn configuration file. A report could be an HTML document or a PDF file.
   * @throws Exception if it fails to generate a report.
   */
  public void generateReport()
      throws Exception;
}
