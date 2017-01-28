import csv
import datetime as dt
from django.core import serializers

meass = ['V', 'W']

with open('ausnut/FoodMeasuresFile.csv', 'r') as f:
    data = []
    shelf = None
    meas = None
    for e in csv.reader(f):
        if e[6] != 'density': continue

        print(e[2])
        skip = False

        try:
            inp = input('shelf life (days) \'.\' to use last > ')
            if inp != '.':
                shelf = int()
            else:
                skip = True
        except:
            shelf = int(input('TRY AGAIN > '))

        try:
            if not skip:
                meas = meass[int(input('measure (0=V, 1=W) > '))]
        except:
            meas = meass[int(input('TRY AGAIN > '))]

        data.append({
            'fields': {
                'name': e[2],
                'density': float(e[10]),
                'estimated_shelf_life': dt.timedelta(days=shelf),
                'preferred_measure_type': meas,
            }
        })

    with open('data.json', 'w') as f:
        f.write(serializers.serialize('json', data))
