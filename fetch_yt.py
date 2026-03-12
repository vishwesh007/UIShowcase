import urllib.request
import re

html = urllib.request.urlopen('https://www.youtube.com/watch?v=KXxXc4SNd3I').read().decode('utf-8', errors='ignore')
desc = re.search(r'"shortDescription":"(.*?)"', html)
title = re.search(r'<title>(.*?)</title>', html)

print("Title:", title.group(1) if title else 'No title')
print("---")
print("Desc:", desc.group(1).replace('\\n', '\n') if desc else 'No description')