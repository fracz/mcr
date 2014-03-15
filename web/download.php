<?php

require_once 'Counter.php';
$counter  = new Counter();
$counter->incrementDownloads();

$mcr = 'application.apk';
$handle = @fopen($mcr, "rb");

@header("Cache-Control: no-cache, must-revalidate"); 
@header("Pragma: no-cache"); //keeps ie happy
@header("Content-Disposition: attachment; filename=mcr.apk");
@header("Content-type: application/octet-stream");
@header("Content-Length: " . filesize($mcr));
@header('Content-Transfer-Encoding: binary');

fpassthru($handle);