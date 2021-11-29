#getAll
curl -i http://localhost:8080/topjava/rest/meals
#getSingle
curl -i http://localhost:8080/topjava/rest/meals/100002
#getWithFilter
curl -i http://localhost:8080/topjava/rest/meals/filter/?endDate=2020-01-30
#create
curl -i -X POST -H "Content-Type: application/json" -d '{"dateTime":"2021-11-29T10:00:00","description":"Created Meal","calories":"300"}' http://localhost:8080/topjava/rest/meals/
#update
curl -X PUT -H 'Content-Type: application/json' -d '{"id":"100002","dateTime":"2020-01-30T10:15:00","description":"updated breakfast","calories":"300"}' http://localhost:8080/topjava/rest/meals/100002
#delete
curl -i -X DELETE http://localhost:8080/topjava/rest/meals/100003