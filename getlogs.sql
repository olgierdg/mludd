-- set output mode to csv, represent NULLs as NA
.separator ,
.nullvalue NA

--SELECT timestamp, data FROM logs;
SELECT data FROM logs;
