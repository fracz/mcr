<?php
require 'Counter.php';
$counter = new Counter();
$counter->incrementReviews();

$rawPost = file_get_contents('php://input');
$output = 'reviews/' . date("Y-m-d_G-i-s_") . rand(0, 1000) . '.zip';
file_put_contents($output, $rawPost);
mail("fraczwojciech@gmail.com", "MCR - Nowe Review", "http://mgr.fracz.pl/" . $output);