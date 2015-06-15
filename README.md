Trammel
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
The following curated lists only block ads, trackers, malware sites, phishing sites, hijacked sites, etc.
- http://hosts-file.net/ad_servers.txt
- http://hosts-file.net/emd.txt
- http://hosts-file.net/exp.txt
- http://hosts-file.net/fsa.txt
- http://hosts-file.net/grm.txt
- http://hosts-file.net/hfs.txt
- http://hosts-file.net/hjk.txt
- http://hosts-file.net/mmt.txt
- http://hosts-file.net/pha.txt
- http://hosts-file.net/psh.txt
- http://malwaredomains.lehigh.edu/files/justdomains
- http://pgl.yoyo.org/as/serverlist.php?hostformat=html&mimetype=plaintext
- http://someonewhocares.org/hosts/hosts
- http://sysctl.org/cameleon/hosts
- http://www.malwaredomainlist.com/hostslist/hosts.txt
- http://www.winhelp2002.mvps.org/hosts.txt
- https://raw.githubusercontent.com/chiehmin/MinMinGuard/master/assets/host/output_file
- https://raw.githubusercontent.com/StevenBlack/hosts/master/data/StevenBlack/hosts
- https://s3.amazonaws.com/lists.disconnect.me/simple_malvertising.txt
- https://s3.amazonaws.com/lists.disconnect.me/simple_malware.txt
- https://s3.amazonaws.com/lists.disconnect.me/simple_tracking.txt


Known Issues
------------
- Sometimes erroneous text will be at the end of the file

Planned Updates
---------------
- .7z file support
- .tar.gz file support
- Command line support

Reasons for Creation || Issues with Similar Programs
----------------------------------------------------
- Multiplatform support
- Handling of URLs

Credits
-------
- http://fahdshariff.blogspot.ru/2011/08/java-7-deleting-directory-by-walking.html
- http://stackoverflow.com/a/1418724
- http://stackoverflow.com/a/14656534
- http://stackoverflow.com/a/18974782
- http://stackoverflow.com/a/203992
- http://stackoverflow.com/a/23538961
- http://stackoverflow.com/a/4895572
- http://stackoverflow.com/a/5667402
