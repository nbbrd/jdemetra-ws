# Automatic REGARIMA Modelling

## Overview

The software TRAMO and X13 provide automatic model identification (AMI) routines for identifying REGARIMA models (regression model with ARIMA noises)
that fit given time series.

The routines are able to choose the transformation of the series (log/levels), to identify outliers (additive outliers (AO), level shifts (LS), 
transitory changes (TC)...), to model calendar (trading days and Easter) effects and to select a suitable ARIMA model.

When a series has been correctly modelled, forecasts, backcasts or missing values can be derived. Outliers or unexpected values are also 
easily measured.

JDemetra-ws provides routines for estimating forecasts/backcasts, for identifying unexpected results at the end of the series or for identifying 
all the outliers in a series.

The pre-specified AMI routines that are available through the WEB services are shortly described below  

| TRAMO Id. | RegArima (X13) Id.| Log/level detection | Outliers detection | Calendar effects | ARIMA        |
|---------- |-------------------|---------------------|--------------------|------------------|--------------|
| TR0       |  RG0              | -                   | -                  | -                |Airline(+mean)|
| TR1       |  RG1              | automatic           | AO/LS/TC           | -                |Airline(+mean)|
| TR2       |  RG2              | automatic           | AO/LS/TC           | 2 td vars+Easter |Airline(+mean)|
| TR3       |  RG3              | automatic           | AO/LS/TC           | -                |automatic     |
| TR4       |  RG4              | automatic           | AO/LS/TC           | 2 td vars+Easter |automatic     |
| TR5       |  RG5              | automatic           | AO/LS/TC           | 7 td vars+Easter |automatic     |
| TRfull    |  -                | automatic           | AO/LS/TC           | automatic        |automatic     |

Remarks:
-   The treatment of the leap year is different in Tramo-Seats and in X13 in case of log-transformed series (pre-adjustment in X13). 
-   The parameters of the model are estimated by the method of the maximum likelihood.

## /forecast

Returns a given series with the requested forecasts and/or backcasts.

#### WEB API 

-   Consumes : `application/xml`, `application/json`
-   Produces : `application/xml`, `application/json`
-   Return type : `XmlTsData`
   
_Input:_

| Parameter | Type      | Description                        | Required | Default Value |
|-----------|-----------|------------------------------------|:--------:|:-------------:|
| ts        | XmlTsData | Input times series                 |   true   |               |
| start     | Integer   | Nb of backcasts (>= 0)             |          |               |
| end       | Integer   | Nb of forecasts (>= 0)             |          |               |
| algorithm | String    | Algorithm used (tramoseats or x13) |          |  "tramoseats" |
| spec      | String    | Specification used                 |          |   "TRfull"    |

## Check Last

### Description

`Terror` is a popular tool developed originally around TRAMO for detecting anomalies in the last observation(s). 
The tool simply compares the out-of-sample forecasts of the series (shortened by the corresponding number of observations) 
with the actual figures. The differences are expressed in function of the standard error of the forecasts. 
`checkLast` is a new implementation of that tool.

Observations will be considered as abnormal when the relative forecast errors (in absolute term) will exceed a given threshold. 
A typical threshold will lie between 4 and 5 (higher values mean lower sensitivity). CheckLast returns the relative forecast error 
as well as the forecasts.

The following methods perform `Check last` on a single series or on a group of series. The output contains the name of each series, 
the last value(s), possibly transformed (log transformation, leap year correction), the untransformed (=similar to the initial values) 
forecasts and the scores, which correspond to the ratios between the forecasts errors and their standard deviations (relative forecast errors).

### /checklast

Performs a Check Last on a single series.

#### WEB API

-   Consumes : `application/xml`, `application/json`
-   Produces : `application/xml`, `application/json`
-   Return type : `TerrorResult`

_Input:_

| Parameter | Type      | Description                        | Required | Default Value |
|-----------|-----------|------------------------------------|:--------:|:-------------:|
| ts        | XmlTsData | Input times series                 |   true   |               |
| nbLast    | Integer   | Number of Check Last results       |          |       1       |
| algorithm | String    | Algorithm used (tramoseats or x13) |          |  "tramoseats" |
| spec      | String    | Specification used                 |          |    "TRfull"   |

_Output (TerrorResult):_

| Element     | Type         | Description                               |
|-------------|--------------|-------------------------------------------|
| Name | String | Name of the series|
| Value| Double[\] | [log-transformed] value(s) of the series (last obs)|
| Forecast| Double[\] | forecast(s) of the series|
| Score| Double[\] | score(s) of the forecast(s)|


### /checklast/collection

Performs a Check Last on an array of series.

#### WEB API

-   Consumes : `application/xml`, `application/json`
-   Produces : `application/xml`, `application/json`
-   Return type : `List<TerrorResult>`

_Input:_

| Parameter | Type          | Description                        | Required | Default Value |
|-----------|---------------|------------------------------------|:--------:|:-------------:|
| ts        | XmlTsData\[\] | Array of input times series        |   true   |               |
| nbLast    | Integer       | Number of Check Last results       |          |       1       |
| algorithm | String        | Algorithm used (tramoseats or x13) |          |  "tramoseats" |
| spec      | String        | Specification used                 |          |    "TRfull"   |


## /outlier

Performs an outlier detection on a given XmlTsData, using TRAMO (currently, outliers detection by means of X13 is not supported).

#### WEB API

-   Consumes : `application/xml`, `application/json`
-   Produces : `application/xml`, `application/json`
-   Return type : `List<ShadowOutlier>`

_Input:_

| Parameter      | Type          | Description                               | Required | Default Value |
|----------------|---------------|-------------------------------------------|:--------:|:-------------:|
| ts             | XmlTsData\[\] | Array of input times series               |   true   |               |
| transformation | String        | Transformation function used              |          |     "None"    |
| critical       | Double        | Critical value used for outlier detection. Should be either 0 or >= 2 |          |      0        |
| spec           | String        | Specification used                        |          |    "TRfull"   |

The critical value is the threshold (corresponding to the absolute value of the T-Stat of the outlier coefficient in the regression model) used for selecting outliers. When the critical value is set to 0, the software selects automatically a suitable value, which depends on the length of the series. Otherwise, the critical value should be greater than 2, higher values corresponding to less sensitive outliers detection (typical values should be in the range [3, 5]).

_Output (ShadowOutlier):_

| Element     | Type         | Description                               |
|-------------|--------------|-------------------------------------------|
| outlierType | OutlierType  | AO, LS or TC |
| period      | XmlTsPeriod  | Position of the outlier |
| value       | Double | Coefficient of the outlier in the regression model| 
| stdev       | Double | stdev of the coefficient of the outlier in the regression model| 

Remark:
The T-Stat is the ratio between value and stdev

