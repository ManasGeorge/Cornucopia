from __future__ import unicode_literals

from django.db import models

class Ingredient(models.Model):
    name = models.CharField(max_length=64)
    estimated_shelf_life = models.DurationField()
