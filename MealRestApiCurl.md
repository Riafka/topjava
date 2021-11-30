### Получить всю еду

curl -i http://localhost:8080/topjava/rest/meals

### Получить еду по ID

curl -i http://localhost:8080/topjava/rest/meals/100002

### Получить еду с фильтром по полуоткрытому интервалу(дата/время начала, дата/время конца)

curl
-i 'http://localhost:8080/topjava/rest/meals/filter/?startDate=2020-01-30&startTime=10:00:00&endDate=2020-01-30&endTime=13:00:01'

### Создать еду

curl -i -X POST -H "Content-Type: application/json" -d '{"dateTime":"2021-11-29T10:00:00","description":"Created Meal","
calories":"300"}' http://localhost:8080/topjava/rest/meals/

### Обновить еду

curl -X PUT -H 'Content-Type: application/json' -d '{"id":"100002","dateTime":"2020-01-30T10:15:00","description":"
updated breakfast","calories":"300"}' http://localhost:8080/topjava/rest/meals/100002

### Удалить еду

curl -i -X DELETE http://localhost:8080/topjava/rest/meals/100003