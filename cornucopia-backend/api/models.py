from __future__ import unicode_literals

from django.db import models

class IngredientType(models.Model):
    MEASURE_TYPES = (
            ('W', 'WEIGHT'),
            ('V', 'VOLUME'),
            # consider adding 'UNITS' (e.g.: 3 apples)
            # consider not adding it (bc it also creates the 'handful of cilantro' problem)
        )

    name = models.CharField(max_length=64, db_index=True)
    estimated_shelf_life = models.IntegerField()
    preferred_measure_type = models.CharField(max_length=16,
            choices=MEASURE_TYPES)
    density = models.FloatField() # g/ml

class Ingredient(models.Model):
    ingredient_type = models.ForeignKey('IngredientType')
    quantity = models.FloatField() # units change based on measure
    measure = models.CharField(max_length=64)
    expiration_date = models.DateField()
    is_expiration_estimated = models.BooleanField()

