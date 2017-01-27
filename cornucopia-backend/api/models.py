from __future__ import unicode_literals

from django.db import models

class Ingredient(models.Model):
    MEASURE_TYPES = (
            ('W', 'WEIGHT'),
            ('V', 'VOLUME'),
            # consider adding 'UNITS' (e.g.: 3 apples)
            # consider not adding it (bc it also creates the 'handful of cilantro' problem)
        )

    name = models.CharField(max_length=64)
    estimated_shelf_life = models.DurationField()
    preferred_measure_type = models.CharField(choices=MEASURE_TYPES)
    density = models.FloatField() # kg/m^3
