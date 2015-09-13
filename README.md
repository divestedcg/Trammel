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

Blocklists | Blocked
---------- | -------
http://adblock.gjtech.net/?format=unix-hosts | Ads
http://hosts.eladkarako.com/hosts.txt | Ads
http://hostsfile.mine.nu/Hosts.zip | Ads - Outdated
http://hosts-file.net/ad_servers.txt | Ads
http://hosts-file.net/emd.txt | Malware
http://hosts-file.net/exp.txt | Malware
http://hosts-file.net/fsa.txt | Illegal
http://hosts-file.net/grm.txt | Spam
http://hosts-file.net/hfs.txt | Spam
http://hosts-file.net/hjk.txt | Malware
http://hosts-file.net/mmt.txt | Spam
http://hosts-file.net/pha.txt | Illegal
http://hosts-file.net/psh.txt | Phishing
http://hosts-file.net/wrz.txt | Illegal
http://malwaredomains.lehigh.edu/files/immortal_domains.txt | Malware
http://malwaredomains.lehigh.edu/files/justdomains | Malware
http://moaab.tk/hosts.txt  | Ads/Analytics/Malware/Spam/Tracking - Too many false positives
http://pgl.yoyo.org/as/serverlist.php?hostformat=html&mimetype=plaintext | Ads
https://adaway.org/hosts.txt | Ads/Analytics/Tracking
http://someonewhocares.org/hosts/hosts | Ads/Analytics/Malware/Misc/Spam/Tracking
https://raw.githubusercontent.com/chiehmin/MinMinGuard/master/assets/host/output_file | Ads/Analytics/Tracking
https://raw.githubusercontent.com/StevenBlack/hosts/master/data/StevenBlack/hosts | Misc
https://s3.amazonaws.com/lists.disconnect.me/simple_ad.txt | Ads
https://s3.amazonaws.com/lists.disconnect.me/simple_malvertising.txt | Malware/Spam
https://s3.amazonaws.com/lists.disconnect.me/simple_malware.txt | Malware
https://s3.amazonaws.com/lists.disconnect.me/simple_tracking.txt | Analytics/Tracking
http://sysctl.org/cameleon/hosts | Ads
http://www.malwaredomainlist.com/hostslist/hosts.txt | Malware
http://www.winhelp2002.mvps.org/hosts.txt | Ads/Analytics/Malware/Misc/Spam/Tracking
https://zeustracker.abuse.ch/blocklist.php?download=baddomains | Malware

Known Issues
------------
- Sometimes erroneous text will be at either end of the file, its usually okay

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
- Zip4j: http://www.lingala.net/zip4j/
- Icon: ic8.link/10484
- http://fahdshariff.blogspot.ru/2011/08/java-7-deleting-directory-by-walking.html
- http://stackoverflow.com/a/1418724
- http://stackoverflow.com/a/14656534
- http://stackoverflow.com/a/18974782
- http://stackoverflow.com/a/203992
- http://stackoverflow.com/a/23538961
- http://stackoverflow.com/a/4895572
- http://stackoverflow.com/a/5667402
