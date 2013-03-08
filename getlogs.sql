-- set output mode to csv, represent NULLs as NA
.separator ,
.header OFF
.nullvalue NA

--SELECT timestamp, data FROM logs;
SELECT latitude, longitude FROM locationlog;

