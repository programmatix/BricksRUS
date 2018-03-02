# Bricks-R-US
A simple Spring service exposing a REST interface for ordering bricks for a fictional brick company, Bricks-R-US. Written for a tech test.


## Testing
Create an order for 1000 bricks:
```
curl -X POST -H "Content-Type: application/json" -d '{"numBricks":1000}' http://localhost:8080/api/order/
{"id":1}
```

Get that order back:
```
curl http://localhost:8080/api/order/1
{"id":1,"numBricks":1000}
```