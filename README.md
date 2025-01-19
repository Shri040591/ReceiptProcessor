Receipt Processing API


Description
This project implements a receipt processing system with two endpoints:

/receipts/process (POST): Accepts a JSON object representing a receipt and returns a generated ID for that receipt.
/receipts/{id}/points (GET): Accepts a receipt ID and returns the number of points awarded based on predefined rules.

Receipt Processing
The /receipts/process endpoint processes the receipt and generates an ID. This ID will be used in the subsequent request to retrieve the points awarded to the receipt.

Example Response for Processing a Receipt:
json
Copy
{
  "id": "7fb1377b-b223-49d9-a31a-5a02701dd310"
}
Example Response for Retrieving Points:
json
Copy
{
  "points": 15
}
