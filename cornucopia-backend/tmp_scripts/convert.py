import csv
import datetime as dt
import django.core.serializers.json.DjangoJSONEncoder

meass = { '0': 'V', '1': 'W' }

with open('ausnut/FoodMeasuresFile.csv', 'r') as f:
    data = []
    for e in csv.reader(f):
        if e[6] != 'density': continue

        print(e[2])

        try:
            shelf = int(input('shelf life (days) > '))
        except:
            shelf = int(input('TRY AGAIN > '))

        try:
            meas = meass(input('measure (0=V, 1=W) > '))
        except:
            meas = meass(input('TRY AGAIN > '))

        data.append({
            'fields': {
                'name': e[2],
                'density': float(e[10]),
                'estimated_shelf_life': dt.timedelta(days=shelf),
                'preferred_measure_type': meas,
            }
        })

    with open('data.json', 'w') as f:
        f.write(DjangoJSONEncoder(data))
