Plugin @PLUGIN@
===============

This plugin allows to associate Jira issues to Git commits thanks to
the Gerrit listener interface.

Comment links
----------------

Git commits are associated to Jira issues reusing the existing Gerrit
commitLink configuration to extract the issue ID from commit comments.

Example:
```
  [commentLink "Jira"]\n
      match = (\\[[a-zA-Z0-9]*-[0-9]*\\])\n
      html = "<a href=\"http://myjira.com/browse/$1\">$1</a>"
```

Once a Git commit with a comment link is detected, the Jira issue ID
is extracted and a new comment added to the issue, pointing back to
the original Git commit.

Jira connectivity
----------------

In order for Gerrit to connect to Jira, XML-RPC url and credentials
are required in your gerrit.config / secure.config under the [jira] section.

Example:
```
  [jira]\n
      username=jirauser\n
      passsword=jirapass\n
      rpcUrl=http://myjira.com/rpc/xmlrpc\n
```

GitWeb integration
----------------

When Gerrit gitweb is configured, an additional direct link from Jira to GitWeb
will be created, pointing exactly to the Git commit ID containing the Jira issue ID.

[1]: ../../../Documentation/config-gerrit.html#gerrit.canonicalWebUrl
