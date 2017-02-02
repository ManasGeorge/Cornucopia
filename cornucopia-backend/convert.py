import csv
import datetime as dt
import json

meass = ['V', 'W']

with open('sr28asc/temporary.csv', 'r') as f:
    data = []

    i = 0
    for e in csv.reader(f):
        if i == 0:
            i += 1
            continue

        if e[0].upper() == 'DELETE' or e[0] == '': continue

        print(e)

        data.append({
            'model': 'api.IngredientType',
            'pk': i,
            'fields': {
                'name': e[0],
                'density': float(e[2]),
                'estimated_shelf_life': int(e[3]),
                'preferred_measure_type': 'V' if e[4] == 'volume' else 'W',
            }
        })
        i += 1

    with open('data.json', 'w') as f:
        f.write(json.dumps(data))
