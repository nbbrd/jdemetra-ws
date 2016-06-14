#Seasonal adjustment

## Overview

JDemetra-ws provides new implementations of the two leading algorithms for seasonal adjustment: TRAMO-SEATS+ and X12-ARIMA (X13-ARIMA-SEATS).

 The program TRAMO-SEATS+ is developed by Gianluca Caporello and Agustin Maravall -with programming support from Domingo Perez and Roberto Lopez- 
 at the Bank of Spain. It is based on the program TRAMO-SEATS, previously developed by Victor Gomez and Agustin Maravall.
 The original software can be downloaded from the following address:
  [http://www.bde.es](http://www.bde.es "Bank of Spain")

 The program X12-ARIMA (or X13-ARIMA-SEATS) is produced by the US-Census Bureau. It can be downloaded from the following address: [http://www.census.gov/srd/www/x12a](http://www.census.gov/srd/www/x12a "US-Census Bureau (X12a)")
 It should be noted that JDemetra-ws always uses the acronym X13 for this software.
 
 The WEB service is designed to produce in the easiest way the most important results of a seasonal adjustment, namely the decomposition of a series in its different components:
 trend, seasonal and irregular. The seasonally adjusted series and the forecasts of the series are also provided.

## /tramoseats

#### Description

The specification used for the seasonal adjustment can be chosen in the following pre-specified options

| Identifier      | Log/level detection | Outliers detection | Calendar effects | ARIMA        |
|-----------------|---------------------|--------------------|------------------|--------------|
| RSA0            | -                   | -                  | -                |Airline(+mean)|
| RSA1            | automatic           | AO/LS/TC           | -                |Airline(+mean)|
| RSA2            | automatic           | AO/LS/TC           | 2 td vars+Easter |Airline(+mean)|
| RSA3            | automatic           | AO/LS/TC           | -                |automatic     |
| RSA4            | automatic           | AO/LS/TC           | 2 td vars+Easter |automatic     |
| RSA5            | automatic           | AO/LS/TC           | 7 td vars+Easter |automatic     |
| RSAfull         | automatic           | AO/LS/TC           | automatic        |automatic     |

#### WEB API

-   Consumes : `application/xml`, `application/json`
-   Produces : `application/json`
-   Return type : `Map<String, XmlTsData>`

_Input:_

| Parameter | Type      | Description        | Required | Default Value |
|-----------|-----------|--------------------|:--------:|:-------------:|
| tsData    | XmlTsData | Input times series |   true   |               |
| spec      | String    | Specification used |          |     "RSAfull"    |

_Output:_

The returned map contains the following items

| Key | Description |
|-----|-------------|
|"sa" | Seasonally adjusted series|
|"t"  | trend|
|"s"  | Seasonal component|
|"i"  | Irregular component|
|"y_f"| Forecasts of the series (1 year)|

## /x13

#### Description

The specification used for the seasonal adjustment can be chosen in the following pre-specified options

| X13 spec        | Log/level detection | Outliers detection | Calendar effects | ARIMA        |
|-----------------|---------------------|--------------------|------------------|--------------|
| X11             | -                   | -                  | -                |-             |
| RSA0            | -                   | -                  | -                |Airline(+mean)|
| RSA1            | automatic           | AO/LS/TC           | -                |Airline(+mean)|
| RSA2c           | automatic           | AO/LS/TC           | 2 td vars+Easter |Airline(+mean)|
| RSA3            | automatic           | AO/LS/TC           | -                |automatic     |
| RSA4c           | automatic           | AO/LS/TC           | 2 td vars+Easter |automatic     |
| RSA5c           | automatic           | AO/LS/TC           | 7 td vars+Easter |automatic     |

#WEB API

-   Consumes : `application/xml`, `application/json`
-   Produces : `application/json`
-   Return type : `Map<String, XmlTsData>`

_Input:_

| Parameter | Type      | Description        | Required | Default Value |
|-----------|-----------|--------------------|:--------:|:-------------:|
| tsData    | XmlTsData | Input times series |   true   |               |
| spec      | String    | Specification used |          |    "RSA4c"    |

_Output:_

The returned map contains the following items

| Key | Description |
|-----|-------------|
|"sa" | Seasonally adjusted series|
|"t"  | trend|
|"s"  | Seasonal component|
|"i"  | Irregular component|
|"y_f"| Forecasts of the series (1 year)|



