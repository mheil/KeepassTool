# KeepassTool

The KeepassTool allows reading of Keepass database files on systems where none of the
standard keepass tools are available, but java is present. Therefore, this tool is not
meant (at least at the time of writing) as complete replacement for the tools
mentioned on the official Keepass Website (https://keepass.info).

Why another tool for doing this?

At work, we have to use a specific brand of Linux which:
* does not provide a preinstalled Keepass-alternative
* does not provide the prerequisites to compile/run any of them (no QT4, no mono)
* available (rpm-based) software repositories do only contain hand-selected packages
* adding repositories is not permitted

So, as Java-Developer I decided to write a small tool to let me access my previously
created keepass-Database again and to make (my) life more easy and more safe again.
I chose to use Swing, cause Swing is available on most Systems where Java-Development
happens, although you might think that Swing itself is a little anachronistic these days.
