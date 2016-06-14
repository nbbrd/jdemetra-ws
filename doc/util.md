# Utilities

## Chart

### /chart

Creates a chart image from a given XmlTsData. Height, width, color scheme, title and the visibility of the legend can be changed.

-   Consumes : `application/xml`, `application/json`
-   Produces : `image/png`, `image/jpeg`, `image/svg+xml`
-   Return type : `BufferedImage`

| Parameter | Type      | Description                                         | Required | Default Value |
|-----------|-----------|-----------------------------------------------------|:--------:|:-------------:|
| tsdata    | XmlTsData | Input times series                                  |   true   |               |
| w         | Integer   | Width of the chart image                            |          |      400      |
| h         | Integer   | Height of the chart image                           |          |      300      |
| scheme    | String    | Color scheme name (see ColorScheme implementations) |          |    "Smart"    |
| title     | String    | Title displayed above the chart                     |          |               |
| legend    | Boolean   | If true, a legend will be displayed below the chart |          |      true     |

### /chart/random

Creates a chart image from a randomly generated series. Height, width, color scheme, title and the visibility of the legend can be changed.

-   Produces : `image/png`, `image/jpeg`, `image/svg+xml`
-   Return type : `BufferedImage`

| Parameter | Type    | Description                                         | Required | Default Value |
|-----------|---------|-----------------------------------------------------|:--------:|:-------------:|
| w         | Integer | Width of the chart image                            |          |      400      |
| h         | Integer | Height of the chart image                           |          |      300      |
| scheme    | String  | Color scheme name (see ColorScheme implementations) |          |    "Smart"    |
| title     | String  | Title displayed above the chart                     |          |               |
| legend    | Boolean | If true, a legend will be displayed below the chart |          |      true     |

## TsData

### /tsdata

Returns a random XmlTsData to ease the testing of other services.

-   Produces : `application/xml`, `application/json`
-   Return type : `XmlTsData`

| Parameter | Type    | Description       | Required | Default Value |
|-----------|---------|-------------------|:--------:|:-------------:|
| frequency | Integer | Desired frequency |          |       12      |

### /tsdata/collection

Returns a random JsonTsCollection to ease the testing of other services.

-   Produces : `application/xml`, `application/json`
-   Return type : `JsonTsCollection`

| Parameter | Type    | Description                        | Required | Default Value |
|-----------|---------|------------------------------------|:--------:|:-------------:|
| frequency | Integer | Desired frequency                  |          |       12      |
| nb        | Integer | Number of series in the collection |          |       10      |
