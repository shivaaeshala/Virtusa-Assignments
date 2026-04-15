import re
import datetime
import os

pattern = r"(CRITICAL|ERROR|FAILED LOGIN)"

with open("server.log", "r") as f:
    logs = f.readlines()

errors_count = {'CRITICAL': 0, 'ERROR': 0, 'FAILED LOGIN': 0}
filename = 'security_alert_' + str(datetime.date.today()) + '.txt'

with open(filename, 'w') as f:
    for log in logs:
        group = re.search(pattern, log)
        if group:
            errors_count[group.group()] += 1

            if group.group() == 'CRITICAL':
                    f.write(log)

print("Errors: ",errors_count)
print("Created file size:", os.path.getsize(filename))