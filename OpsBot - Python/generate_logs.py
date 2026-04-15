import random
from datetime import datetime, timedelta

log_levels = ["GET", "POST", "DELETE", "ERROR", "CRITICAL", "FAILED LOGIN"]
endpoints = ["/home", "/login", "/dashboard", "/products", "/cart", "/checkout", "/api/data", "/profile", "/search", "/orders"]
messages = {
    "INFO": ["200 OK"],
    "ERROR": ["500 Internal Server Error", "Database timeout", "API failure"],
    "CRITICAL": ["Server overload", "Disk full", "Unauthorized access"],
    "FAILED LOGIN": ["401 Unauthorized"]
}

start_time = datetime(2026, 4, 14, 10, 0, 0)

logs = []

for i in range(1500):
    time = start_time + timedelta(seconds=i*3)
    level = random.choices(
        log_levels,
        weights=[35, 25, 15, 10, 5, 10],  # mostly normal logs
        k=1
    )[0]

    ip = f"192.168.1.{random.randint(1,255)}"
    endpoint = random.choice(endpoints)

    if level == "GET":
        log = f"{time} INFO GET {endpoint} 200 User:{ip}"
    elif level == "POST":
        log = f"{time} INFO POST {endpoint} 200 User:{ip}"
    elif level == "DELETE":
        log = f"{time} INFO DELETE {endpoint} 200 User:{ip}"
    elif level == "ERROR":
        log = f"{time} ERROR {random.choice(messages['ERROR'])} User:{ip}"
    elif level == "CRITICAL":
        log = f"{time} CRITICAL {random.choice(messages['CRITICAL'])}"
    else:
        log = f"{time} FAILED LOGIN /login 401 User:{ip}"

    logs.append(log)

# Save to file
with open("server.log", "w") as f:
    for log in logs:
        f.write(log + "\n")

print("Generated 1500 logs in server.log")