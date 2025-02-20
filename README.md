# Trade data API

## Run instructions

- Create .env file and specify REDIS_HOST, REDIS_PORT and REDIS_PASSWORD there
- Navigate to the project directory:
    ```bash
  cd trade-data-api
- Install the required dependencies:
    ```bash
  mvn install

## Usage

- Run the server using:
    ```bash
  mvn spring-boot:run

After starting the server, you can interact with the Trade Data API using the following endpoints:

## Design

- There are two files inside `src/main/resources/data`. `largeSizeProduct.csv` stores product names which is used to
  fill Redis DB with data. `testProductNames.csv` is used for testing
- Clean design with standard MVC structure
- src/main/kotlin/org/example/tradedataapi/infrastructure/shutdown contains a code when the server is being shut down (
  disconnect from database in our case)


## Future ideas

- Host the project on the remote server, so that it can be available anywhere
- Add more tests that cover more specific cases

### Enrich JSON Data

- **Endpoint**: `/api/v1/enrich_json`
- **Method**: `GET`
- **Parameters**:
    - `file`: The CSV file containing the trade data (multipart file).
- **Description**: This endpoint enriches the provided CSV file and returns the enriched data in JSON format.

### Enrich SCV file

- **Endpoint**: `/api/v1/enrich`
- **Method**: `GET`
- **Parameters**:
    - `file`: The CSV file containing the trade data (multipart file).
- **Description**: This endpoint enriches the provided CSV file and returns the enriched data in CSV format.

#### Example Request

You can use a tool like `curl` or Postman to test this endpoint. Here are examples using `curl`:

- Run the server using:
    ```bash
  curl -X GET "http://localhost:8080/api/v1/enrich_json" -F "file=@path/to/your/file.csv"

or

```bash
  curl -X GET "http://localhost:8080/api/v1/enrich" -F "file=@path/to/your/file.csv"