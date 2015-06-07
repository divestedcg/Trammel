JHostsBlock
==========

Requirements
------------
- Java 8.0
- Linux, Mac OS X, Windows
- 256MB

Supported Lists
---------------
- File Types: .txt, .hosts, .gz, .zip, [none] will be treated as .txt
- The main file in the .zip must start with or equal "hosts"
- Acceptable entries are either "[hostname]", "127.0.0.1[space/tab][hostname]", or "0.0.0.0[space/tab][hostname]"
- Lines starting with "#" will be ignored

Recommended Lists
-----------------
- http://hosts-file.net/ad_servers.asp
- http://hosts-file.net/download/hosts.zip
- http://hosts-file.net/hphosts-partial.asp
- http://hostsfile.mine.nu/Hosts.zip
- http://mirror1.malwaredomains.com/files/blockeddomain.hosts
- http://pgl.yoyo.org/as/serverlist.php?hostformat=hosts&mimetype=plaintext
- http://someonewhocares.org/hosts/hosts
- http://sysctl.org/cameleon/hosts
- http://winhelp2002.mvps.org/hosts.zip
- http://www.malwaredomainlist.com/hostslist/hosts.txt
- https://s3.amazonaws.com/lists.disconnect.me/simple_malvertising.txt
- https://s3.amazonaws.com/lists.disconnect.me/simple_malware.txt
- https://s3.amazonaws.com/lists.disconnect.me/simple_tracking.txt

Known Issues
------------
- None yet

Planned Updates
---------------
- .7z file support
- .tar.gz file support
- Better parsing
- Caching of downloaded files
- GUI

Reasons for Creation || Issues with Similar Programs
----------------------------------------------------
- Others had issues handling certain URLs

Credits
-------
- http://stackoverflow.com/a/14656534
- http://stackoverflow.com/a/18974782
- http://stackoverflow.com/a/23538961
- http://stackoverflow.com/a/4895572
- http://stackoverflow.com/a/5741080