# -*- coding: utf-8 -*-
# Generated by Django 1.9.7 on 2017-02-02 04:14
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('api', '0001_initial'),
    ]

    operations = [
        migrations.AlterField(
            model_name='ingredienttype',
            name='estimated_shelf_life',
            field=models.IntegerField(),
        ),
    ]
