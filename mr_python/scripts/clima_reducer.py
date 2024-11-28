import sys
import csv

current_category = None
count = 0

writer = csv.writer(sys.stdout)
for line in sys.stdin:
    category, value = line.strip().split("\t")
    value = int(value)

    if current_category == category:
        count += value
    else:
        if current_category:
            # Emitir el resultado anterior
            print(f"{current_category}\t{count}")
        current_category = category
        count = value

# Emitir el último resultado
if current_category:
    print(f"{current_category}\t{count}")
