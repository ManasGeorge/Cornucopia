heroku pg:reset DATABASE_URL
heroku run python manage.py makemigrations
heroku run python manage.py migrate