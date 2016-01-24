# svn-access-auditor
SVN access auditor and report generator.

When you have 1000+ users accessing 250+ svn repositories/projects, you would like to know who has what type of access rights to those repositories. Some repositories may host sensitive source code that you want to conceal from certain groups. This small tool processes svn access configuration file and generates an HTML report that makes sense. The report has 4 parts:

1. Summary: Number of users and repositories. Quick links to other parts of the report.
2. List of all repositories: For each of repositories, it lists groups of readers, groups of writers, and numbers for each. Repositories with the same readers and writers are grouped together, so that the report would look compact and clear.
3. Mapping of users to groups.
4. List of all users: For each of users, it lists readable and writable repositories. Users with the same readable and writable repositories are grouped together. 

TODO: Here is an example of a svn access confiruation file. And here is a report generated from the configuration file. Cross linking between repositories, groups, and users makes auditing access rights so much easier. As you see, this is an interesting example of graph problems.

Author: Battur Sanchin (battursanchin@gmail.com)

License: Apache 2.0
