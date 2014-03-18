<?php
	require_once 'Counter.php';
	
	$code = file_get_contents('Review1.java');
	
	$c = curl_init();
	curl_setopt($c, CURLOPT_URL, 'http://www.coderemarks.com/');
	curl_setopt($c, CURLOPT_POST, 1);
	curl_setopt($c, CURLOPT_POSTFIELDS, 'code=' . urlencode($code));
	curl_setopt($c, CURLOPT_FOLLOWLOCATION, 1);
	curl_setopt($c, CURLOPT_USERAGENT, 'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705; .NET CLR 1.1.4322)');
	curl_setopt($c, CURLOPT_HEADER, 1);
	curl_setopt($c, CURLOPT_RETURNTRANSFER, 1);
	curl_exec($c);
	$reviewUrl = curl_getinfo($c, CURLINFO_EFFECTIVE_URL);
	curl_close($c);
	
	if(preg_match('#review/#', $reviewUrl)){
		file_put_contents('pc_reviews_done.txt', $reviewUrl . PHP_EOL, FILE_APPEND | LOCK_EX);

		$counter = new Counter();
		$counter->incrementPcReviews();
		
		@mail('fraczwojciech@gmail.com', 'MCR - nowe PC review', $reviewUrl);
		
		header('Location: ' . $reviewUrl);
		exit;
	}
	
?><!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="pl" xml:lang="pl">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="refresh" content="5; url=/">
		<title>Mobile Code Review</title>
		<style type="text/css">
			body{ margin: 0; padding: 10px; font-size: 28px; }
			.img {
				width: 95%;
				margin: 0 auto;
				text-align: center;
			}
			p{ with: 80%; margin: 0 auto; text-align: center; padding: 20px; }
			ol{ }
			img { max-width: 100%; }
			a{ color: #008eba; text-decoration: none; font-weight: bold; }
		</style>
	</head>
	<body>
	<p>Problem przy tworzeniu PC review. Spróbuj ponownie za chwilę.</p>
	</body>
</html>
