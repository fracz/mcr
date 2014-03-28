<?php
    ob_start();
	//$db = new PDO('mysql:host=sql1.47.pl;dbname=vojtek_mcr', 'vojtek_mcr', 'Xv8oSaBJ');
	$db = new PDO('mysql:host=localhost;dbname=mcr', 'php', '');
?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="pl" xml:lang="pl">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Mobile Code Review</title>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
		<script type="text/javascript" src="https://www.google.com/jsapi"></script>
        <script type="text/javascript">
            google.load('visualization', '1.0', {'packages':['corechart']});
            google.setOnLoadCallback(drawCharts);
            function drawCharts(){
                var WIDTH = parseInt($(window).width() * 0.9);
                var HEIGHT = 350;

                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Typ przeglądu');
                data.addColumn('number', 'Ilość przeglądów');
                data.addRows([
                    ['PC', <?=current($db->query('SELECT COUNT(*) FROM review WHERE type="pc"')->fetch())?>],
                    ['Aplikacja mobilna', <?=current($db->query('SELECT COUNT(*) FROM review WHERE type="mobile"')->fetch())?>]
                ]);
                // Set chart options
                var options = {'title':'Ilość wykonanych przeglądów',
                    'width': WIDTH,
                    'height': HEIGHT};

                // Instantiate and draw our chart, passing in some options.
                var chart = new google.visualization.PieChart(document.getElementById('quantity'));
                chart.draw(data, options);


                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Typ przeglądu');
                data.addColumn('number', 'Średnia ilość komentarzy');
                data.addRows([
                    ['PC', <?=current($db->query('SELECT AVG(c) FROM (SELECT COUNT(*) c FROM comment INNER JOIN review ON review.id=comment.review_id WHERE review.type="pc" GROUP BY review_id) AS t')->fetch())?>],
                    ['Aplikacja mobilna', <?=current($db->query('SELECT AVG(c) FROM (SELECT COUNT(*) c FROM comment INNER JOIN review ON review.id=comment.review_id WHERE review.type="mobile" GROUP BY review_id) AS t')->fetch())?>]
                ]);
                // Set chart options
                var options = {'title':'Średnia ilość komentarzy z jednego przeglądu',
                    'width': WIDTH,
                    'height': HEIGHT};

                // Instantiate and draw our chart, passing in some options.
                var chart = new google.visualization.BarChart(document.getElementById('avgcomm'));
                chart.draw(data, options);

                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Typ przeglądu');
                data.addColumn('number', 'Średnia ilość komentarzy');
                data.addRows([
                    ['PC', <?=current($db->query('SELECT AVG(c) FROM (SELECT COUNT(*) c FROM detected INNER JOIN review ON review.id=detected.review_id WHERE review.type="pc" GROUP BY review_id) AS t')->fetch())?>],
                    ['Aplikacja mobilna', <?=current($db->query('SELECT AVG(c) FROM (SELECT COUNT(*) c FROM detected INNER JOIN review ON review.id=detected.review_id WHERE review.type="mobile" GROUP BY review_id) AS t')->fetch())?>]
                ]);
                // Set chart options
                var options = {'title':'Średnia ilość "sensownych" komentarzy z jednego przeglądu (zrozumiałych, wskazujących zapach, bez duplikatów)',
                    'width': WIDTH,
                    'height': HEIGHT};

                // Instantiate and draw our chart, passing in some options.
                var chart = new google.visualization.BarChart(document.getElementById('avggoodcomm'));
                chart.draw(data, options);

                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Typ komentarza');
                data.addColumn('number', 'Ilość komentarzy');
                <? $detected = current($db->query('SELECT COUNT(*) FROM detected INNER JOIN review ON detected.review_id=review.id WHERE review.type="pc"')->fetch()); ?>
                data.addRows([
                    ['Wartościowe', <?=$detected?>],
                    ['Bezwartościowe / błędne', <?=current($db->query('SELECT COUNT(*) FROM comment INNER JOIN review ON comment.review_id=review.id WHERE review.type="pc"')->fetch()) - $detected?>]
                ]);
                // Set chart options
                var options = {'title':'Odsetek wartościowych komentarzy (PC)',
                    'width': WIDTH / 2,
                    'height': HEIGHT};
                var chart = new google.visualization.PieChart(document.getElementById('badpc'));
                chart.draw(data, options);

                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Typ komentarza');
                data.addColumn('number', 'Ilość komentarzy');
                <? $detected = current($db->query('SELECT COUNT(*) FROM detected INNER JOIN review ON detected.review_id=review.id WHERE review.type="mobile"')->fetch()); ?>
                data.addRows([
                    ['Wartościowe', <?=$detected?>],
                    ['Bezwartościowe / błędne', <?=current($db->query('SELECT COUNT(*) FROM comment INNER JOIN review ON comment.review_id=review.id WHERE review.type="mobile"')->fetch()) - $detected?>]
                ]);
                var options = {'title':'Odsetek wartościowych komentarzy - aplikacja mobilna',
                    'width': WIDTH / 2,
                    'height': HEIGHT};
                var chart = new google.visualization.PieChart(document.getElementById('badmobile'));
                chart.draw(data, options);

                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Typ komentarza');
                data.addColumn('number', 'Ilość komentarzy');
                <? $detected = current($db->query('SELECT COUNT(*) FROM detected INNER JOIN review ON detected.review_id=review.id WHERE review.type="mobile"')->fetch()); ?>
                data.addRows([
                    ['Przypisane do prawidłowej linii', <?=current($db->query('SELECT COUNT(*) FROM `all` WHERE review_type="pc" AND smell IS NOT NULL AND line_meant=line')->fetch())?>],
                    ['Przypisane do błędnej linii', <?=current($db->query('SELECT COUNT(*) FROM `all` WHERE review_type="pc" AND smell IS NOT NULL AND line_meant!=line')->fetch())?>]
                ]);
                var options = {'title':'Ilość komentarzy dodanych do błędnych linii (ale nadal pozwalających na rozpoznanie problemu) - PC',
                    'width': WIDTH / 2,
                    'height': HEIGHT};
                var chart = new google.visualization.PieChart(document.getElementById('missedpc'));
                chart.draw(data, options);

                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Typ komentarza');
                data.addColumn('number', 'Ilość komentarzy');
                data.addRows([
                    ['Przypisane do prawidłowej linii', <?=current($db->query('SELECT COUNT(*) FROM `all` WHERE review_type="mobile" AND smell IS NOT NULL AND line_meant=line')->fetch())?>],
                    ['Przypisane do błędnej linii', <?=current($db->query('SELECT COUNT(*) FROM `all` WHERE review_type="mobile" AND smell IS NOT NULL AND line_meant!=line')->fetch())?>]
                ]);
                var options = {'title':'Ilość komentarzy dodanych do błędnych linii (ale nadal pozwalających na rozpoznanie problemu) - aplikacja mobilna',
                    'width': WIDTH / 2,
                    'height': HEIGHT};
                var chart = new google.visualization.PieChart(document.getElementById('missedmobile'));
                chart.draw(data, options);

                var data = google.visualization.arrayToDataTable([
                    ['Język komentarza', 'Ilość komentarzy'],
                    ['polski', <?=current($db->query('SELECT COUNT(*) FROM `all` WHERE language="pl"')->fetch())?>],
                    ['angielski', <?=current($db->query('SELECT COUNT(*) FROM `all` WHERE language="en"')->fetch())?>]
                ]);
                var options = {'title':'Język komentarzy', 'width': WIDTH, 'height': HEIGHT};
                new google.visualization.PieChart(document.getElementById('lang')).draw(data, options);

                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Język komentarza');
                data.addColumn('number', 'Ilość komentarzy');
                data.addRows([
                    ['polski', <?=current($db->query('SELECT COUNT(*) FROM `all` WHERE review_type="pc" AND language="pl"')->fetch())?>],
                    ['angielski', <?=current($db->query('SELECT COUNT(*) FROM `all` WHERE review_type="pc" AND language="en"')->fetch())?>]
                ]);
                var options = {'title':'Język komentarzy - PC', 'width': WIDTH / 2, 'height': HEIGHT};
                new google.visualization.PieChart(document.getElementById('langpc')).draw(data, options);

                var data = google.visualization.arrayToDataTable([
                    ['Język komentarza', 'Ilość komentarza'],
                    ['polski', <?=current($db->query('SELECT COUNT(*) FROM `all` WHERE review_type="mobile" AND language="pl"')->fetch())?>],
                    ['angielski', <?=current($db->query('SELECT COUNT(*) FROM `all` WHERE review_type="mobile" AND language="en"')->fetch())?>]
                ]);
                var options = {'title':'Język komentarzy - aplikacja mobilna', 'width': WIDTH / 2, 'height': HEIGHT};
                new google.visualization.PieChart(document.getElementById('langmobile')).draw(data, options);

                var data = google.visualization.arrayToDataTable([
                    ['Język komentarza', 'Ilość komentarzy'],
                    ['polski', <?=current($db->query('SELECT COUNT(*) FROM `all` WHERE language="pl" AND type!="predefined"')->fetch())?>],
                    ['angielski', <?=current($db->query('SELECT COUNT(*) FROM `all` WHERE language="en" AND type!="predefined"')->fetch())?>]
                ]);
                var options = {'title':'Język komentarzy (z wyłączeniem komentarzy predefiniowanych)', 'width': WIDTH, 'height': HEIGHT};
                new google.visualization.PieChart(document.getElementById('lang_not_predefined')).draw(data, options);

                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Orientacja ekranu');
                data.addColumn('number', 'Ilość przeglądów');
                data.addRows([
                    ['pozioma', <?=current($db->query('SELECT COUNT(*) FROM `review` WHERE type="mobile" AND screen_width >= screen_height')->fetch())?>],
                    ['pionowa', <?=current($db->query('SELECT COUNT(*) FROM `review` WHERE type="mobile" AND screen_width < screen_height')->fetch())?>]
                ]);
                var options = {'title':'Orientacja ekranu przy przeglądzie mobilnym', 'width': WIDTH, 'height': HEIGHT};
                new google.visualization.PieChart(document.getElementById('orientation')).draw(data, options);

                <?
                $q = $db->query('SELECT screen_width, AVG(comments) FROM (SELECT review.screen_width, review.screen_height, COUNT(comment.id) comments
FROM `comment` comment INNER JOIN review ON comment.review_id = review.id
WHERE review.type="mobile" GROUP BY review.id) AS t GROUP BY screen_width ORDER BY screen_width;');
                $q2 = $db->query('SELECT screen_width, AVG(comments) FROM (SELECT review.screen_width, review.screen_height, COUNT(comment.id) comments
FROM `detected` comment INNER JOIN review ON comment.review_id = review.id
WHERE review.type="mobile" GROUP BY review.id) AS t GROUP BY screen_width ORDER BY screen_width;');
                ?>
                var data = google.visualization.arrayToDataTable([
                    ['Szerokość ekranu [px]', 'Średnia ilość komentarzy', 'Średnia ilość wartościowych komentarzy'],
                    <?
                        foreach($q as $row){
                            $row2 = $q2->fetch();
                            $values[] = "['$row[0]', $row[1], $row2[1]]";
                        }
                        echo implode(',', $values);
                    ?>

                ]);
                var options = {'title':'Szerokość ekranu a średnia ilość komentarzy - aplikacja mobilna', 'width': WIDTH, 'height': HEIGHT,
                    hAxis: { title: 'Szerokość ekranu [px]' }, vAxis: { title: 'Średnia ilość komentarzy' }, pointSize: 8};
                new google.visualization.LineChart(document.getElementById('avgqty_width')).draw(data, options);

                <?
                $q = $db->query('SELECT screen_height, AVG(comments) FROM (SELECT review.screen_width, review.screen_height, COUNT(comment.id) comments
FROM `comment` comment INNER JOIN review ON comment.review_id = review.id
WHERE review.type="mobile" GROUP BY review.id) AS t GROUP BY screen_height ORDER BY screen_height;');
                $q2 = $db->query('SELECT screen_height, AVG(comments) FROM (SELECT review.screen_width, review.screen_height, COUNT(comment.id) comments
FROM `detected` comment INNER JOIN review ON comment.review_id = review.id
WHERE review.type="mobile" GROUP BY review.id) AS t GROUP BY screen_height ORDER BY screen_height;');
                ?>
                var data = google.visualization.arrayToDataTable([
                    ['Wysokość ekranu [px]', 'Średnia ilość komentarzy', 'Średnia ilość wartościowych komentarzy'],
                    <?
                        $values = [];
                        foreach($q as $row){
                            $row2 = $q2->fetch();
                            $values[] = "['$row[0]', $row[1], $row2[1]]";
                        }
                        echo implode(',', $values);
                    ?>

                ]);
                var options = {'title':'Wysokość ekranu a średnia ilość komentarzy - aplikacja mobilna', 'width': WIDTH, 'height': HEIGHT,
                    hAxis: { title: 'Wysokość ekranu [px]' }, vAxis: { title: 'Średnia ilość komentarzy' }, pointSize: 8};
                new google.visualization.LineChart(document.getElementById('avgqty_height')).draw(data, options);

                var data = google.visualization.arrayToDataTable([
                    ['Typ komentarza', 'Ilość komentarza'],
                    ['predefiniowany', <?=current($db->query('SELECT COUNT(*) FROM `all` WHERE review_type="mobile" AND type="predefined"')->fetch())?>],
                    ['tekstowy', <?=current($db->query('SELECT COUNT(*) FROM `all` WHERE review_type="mobile" AND type="text"')->fetch())?>],
                    ['głosowy', <?=current($db->query('SELECT COUNT(*) FROM `all` WHERE review_type="mobile" AND type="voice"')->fetch())?>]
                ]);
                var options = {'title':'Typ dodawanych komentarzy - aplikacja mobilna', 'width': WIDTH, 'height': HEIGHT};
                new google.visualization.PieChart(document.getElementById('types')).draw(data, options);

                var data = google.visualization.arrayToDataTable([
                    ['Nr referencyjny zapachu', 'Częstość wykrycia'],
                    <?
                        $values = [];
                        foreach($db->query('SELECT smell, COUNT(*) c FROM detected GROUP BY smell ORDER BY c DESC') as $row){
                            $values[] = "['$row[0]', $row[1]]";
                        }
                        echo implode(',', $values);
                     ?>
                ]);
                var options = {'title':'Najczęściej znajdywane zapachy kodu', 'width': WIDTH, 'height': HEIGHT,
                    hAxis: { title: 'Nr referencyjny zapachu' }, vAxis: { title: 'Częstość wykrycia' }};
                new google.visualization.ColumnChart(document.getElementById('frequent')).draw(data, options);

                var data = google.visualization.arrayToDataTable([
                    ['Nr referencyjny zapachu', 'Częstość wykrycia'],
                    <?
                        $values = [];
                        foreach($db->query('SELECT smell, COUNT(*) c FROM detected WHERE review_type="pc" GROUP BY smell ORDER BY c DESC') as $row){
                            $values[] = "['$row[0]', $row[1]]";
                        }
                        echo implode(',', $values);
                     ?>
                ]);
                var options = {'title':'Najczęściej znajdywane zapachy kodu - PC', 'width': WIDTH, 'height': HEIGHT,
                    hAxis: { title: 'Nr referencyjny zapachu' }, vAxis: { title: 'Częstość wykrycia' }};
                new google.visualization.ColumnChart(document.getElementById('frequentpc')).draw(data, options);

                var data = google.visualization.arrayToDataTable([
                    ['Nr referencyjny zapachu', 'Częstość wykrycia'],
                    <?
                        $values = [];
                        foreach($db->query('SELECT smell, COUNT(*) c FROM detected WHERE review_type="mobile" GROUP BY smell ORDER BY c DESC') as $row){
                            $values[] = "['$row[0]', $row[1]]";
                        }
                        echo implode(',', $values);
                     ?>
                ]);
                var options = {'title':'Najczęściej znajdywane zapachy kodu - aplikacja mobilna', 'width': WIDTH, 'height': HEIGHT,
                    hAxis: { title: 'Nr referencyjny zapachu' }, vAxis: { title: 'Częstość wykrycia' }};
                new google.visualization.ColumnChart(document.getElementById('frequentmobile')).draw(data, options);


                var data = google.visualization.arrayToDataTable([
                    ['Nr referencyjny zapachu', 'Częstość wykrycia jako pierwszy błąd'],
                    <?
                        $values = [];
                        foreach($db->query('SELECT smell, COUNT(smell) c FROM detected WHERE timestamp IN(SELECT MIN(timestamp) FROM detected GROUP BY review_id) GROUP BY smell ORDER BY c DESC;') as $row){
                            $values[] = "['$row[0]', $row[1]]";
                        }
                        echo implode(',', $values);
                     ?>
                ]);
                var options = {'title':'Pierwszy znajdywany błąd', 'width': WIDTH, 'height': HEIGHT,
                    hAxis: { title: 'Nr referencyjny zapachu' }, vAxis: { title: 'Częstość wykrycia jako pierwszy błąd' }};
                new google.visualization.ColumnChart(document.getElementById('firstcom')).draw(data, options);

                var data = google.visualization.arrayToDataTable([
                    ['Nr referencyjny zapachu', 'Częstość wykrycia jako pierwszy błąd'],
                    <?
                        $values = [];
                        foreach($db->query('SELECT smell, COUNT(smell) c FROM detected WHERE timestamp IN(SELECT MIN(timestamp) FROM detected WHERE review_type="pc" GROUP BY review_id) GROUP BY smell ORDER BY c DESC;') as $row){
                            $values[] = "['$row[0]', $row[1]]";
                        }
                        echo implode(',', $values);
                     ?>
                ]);
                var options = {'title':'Pierwszy znajdywany błąd - PC', 'width': WIDTH/2, 'height': HEIGHT,
                    hAxis: { title: 'Nr referencyjny zapachu' }};
                new google.visualization.ColumnChart(document.getElementById('firstcompc')).draw(data, options);

                var data = google.visualization.arrayToDataTable([
                    ['Nr referencyjny zapachu', 'Częstość wykrycia jako pierwszy błąd'],
                    <?
                        $values = [];
                        foreach($db->query('SELECT smell, COUNT(smell) c FROM detected WHERE timestamp IN(SELECT MIN(timestamp) FROM detected WHERE review_type="mobile" GROUP BY review_id) GROUP BY smell ORDER BY c DESC;') as $row){
                            $values[] = "['$row[0]', $row[1]]";
                        }
                        echo implode(',', $values);
                     ?>
                ]);
                var options = {'title':'Pierwszy znajdywany błąd - aplikacja mobilna', 'width': WIDTH/2, 'height': HEIGHT,
                    hAxis: { title: 'Nr referencyjny zapachu' }};
                new google.visualization.ColumnChart(document.getElementById('firstcommobile')).draw(data, options);
            }
        </script>
		<style type="text/css">
			body{ margin: 0; padding: 10px; font-size: 28px; }
			.img {
				width: 95%;
				margin: 0 auto;
				text-align: center;
			}
			p{ width: 80%; margin: 0 auto; text-align: center; padding: 20px; }
			ol{ }
			img { max-width: 100%; }
			a{ color: #008eba; text-decoration: none; font-weight: bold; }
            div.chart{
                margin: 0 auto;
                width: 90%;
                clear: both;
                border-bottom: 1px solid #ccc;
                margin-bottom: 20px;
            }
            div.chart2{
                float: left;
                width: 50%;
                border-bottom: 1px solid #ccc;
                margin-bottom: 20px;
            }
            #smells{
                font-size: 12px;
                width: 80%;
                margin: 0 auto;
            }
            .code{ font-family: monospace; }
		</style>
	</head>
	<body>
		<div class="img"><img src="mcr.png" alt="MCR" /></div>
		<p>Poniższa strona prezentuje wyniki analizy przeglądów kodu wykonanych w ramach badania.</p>
		<p>Stan aktualny na <?=current($db->query('SELECT MAX(timestamp) FROM comment')->fetch())?>.</p>
        <div class="chart" id="quantity"></div>
        <div class="chart" id="avgcomm"></div>
        <div class="chart" id="avggoodcomm"></div>
        <div class="chart2" id="badpc"></div>
        <div class="chart2" id="badmobile"></div>
        <div class="chart2" id="missedpc"></div>
        <div class="chart2" id="missedmobile"></div>
        <div class="chart" id="lang"></div>
        <div class="chart2" id="langpc"></div>
        <div class="chart2" id="langmobile"></div>
        <div class="chart" id="lang_not_predefined"></div>
        <div class="chart" id="orientation"></div>
        <div class="chart" id="avgqty_width"></div>
        <div class="chart" id="avgqty_height"></div>
        <div class="chart" id="types"></div>
        <p>Kolejne statystyki skupiają się na analizie znalezionych błędów w kodzie.</p>
        <p>Poniżej podano tabelę opisującą zapachy kodu źródłowego, które podlegały analizie.</p>

<table id="smells" border="1">
    <thead>
        <tr>
            <th>Nr referencyjny zapachu</th>
            <th>Linie</th>
            <th>Kod zapachu (<a href="http://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882">Clean Code</a>)</th>
            <th>Opis / oczekiwany komentarz</th>
        </tr>
    </thead>
    <tbody>
    <tr><td>1. </td><td> 3-7 </td><td> C1 </td><td> Zbędny komentarz, zbędne informacje które powinny być przechowywane w systemie kontroli wersji</td></tr><tr><td>
        2. </td><td> 21-24 </td><td> J1 </td><td> Użycie <span class="code">wildcard</span> do zaimportowania pakietu <span class="code">pl.fracz.mcr.comment</span> znacznie skróciłoby listę importów </td></tr><tr><td>
        3. </td><td> 29 </td><td> G4 </td><td> Należy dodać konstruktor zamiast ukrywać ostrzeżenia</td></tr><tr><td>
        4. </td><td> 31 </td><td> G4 </td><td> Niepotrzebna definicja <span class="code">serialVersionUID</span> (wymaga jej aktualizacji z każdą zmianą)</td></tr><tr><td>
            5. </td><td> 32, 118 </td><td> G25 </td><td> Wykorzystana stała <span class="code">TWO</span> nie ma sensu</td></tr><tr><td>
        6. </td><td> 34, 36 </td><td> N6 </td><td> Niepotrzebne prefixy pól klasy</td></tr><tr><td>
        7. </td><td> 38 </td><td> C2 </td><td> Nieaktualny i nieprawdziwy komentarz</td></tr><tr><td>
        8. </td><td> 47-48 </td><td> F1 </td><td> Zbyt duża liczba argumentów</td></tr><tr><td>
        9. </td><td> 47-64 </td><td> G30 </td><td> Zbyt długi konstruktor, wykonuje kilka operacji</td></tr><tr><td>
        10. </td><td> 48 </td><td> F3, G15 </td><td> Argument flagowy</td></tr><tr><td>
        11. </td><td> 56 </td><td> - </td><td> Błąd kompilacji – nadmiarowy znak ,,<span class="code">;</span>’’</td></tr><tr><td>
        12. </td><td> 58 </td><td> G25 </td><td> ,,Magiczna’’ wartość - liczba</td></tr><tr><td>
        13. </td><td> 61 </td><td> - </td><td> Błąd semantyczny kodu, wartość zapisywana do zmiennej lokalnej zamiast do pola klasy; skutkuje wyrzuceniem <span class="code">NullPointerException</span> przy tworzeniu instancji klasy</td></tr><tr><td>
        14. </td><td> 62 </td><td> G10 </td><td> Wywołanie metody zależy od wykonania innej, ale nie jest to zapewnione na poziomie deklaracji</td></tr><tr><td>
        15. </td><td> 62 </td><td> G31 </td><td> Metoda zdefiniowana daleko od miejsca jej wywołania</td></tr><tr><td>
        16. </td><td> 64, 78, 92 </td><td> G14 </td><td> ,,Train wrecks’’, zazdrość o funkcjonalność</td></tr><tr><td>
        17. </td><td> 64, 79-82, 93-96 </td><td> G5 </td><td> Duplikacja kodu</td></tr><tr><td>
        18. </td><td> 67 </td><td> N1, N4 </td><td> Niewiele mówiąca nazwa metody, <span class="code">get()</span> jest zbyt wieloznaczna</td></tr><tr><td>
        19. </td><td> 71-76, 85-90 </td><td> C3 </td><td> Niepotrzebny komentarz</td></tr><tr><td>
        20. </td><td> 77, 91 </td><td> G11 </td><td> Niespójność konwencji nazw metod wykonujących podobne operacje</td></tr><tr><td>
        21. </td><td> 81, 95 </td><td> G25 </td><td> ,,Magiczna wartość'' - string</td></tr><tr><td>
        22. </td><td> 91 </td><td> - </td><td> Literówka w nazwie argumentu - <span class="code">recodedFile</span> zamiast <span class="code">recordedFile</span></td></tr><tr><td>
        23. </td><td> 99 - 101 </td><td> C5 </td><td> Zakomentowany kod</td></tr><tr><td>
        24. </td><td> 104 </td><td> G28, G29 </td><td> Skomplikowane i negatywne wyrażenie logiczne w instrukcji <span class="code">if</span></td></tr><tr><td>
        25. </td><td> 105, 107 </td><td> G5, G19 </td><td> Duplikacja kodu, wynik operacji <span class="code">Html.fromHtml(lineOfCode)</span> mógłby być zapisany do zmiennej wyjaśniającej</td></tr><tr><td>
        26. </td><td> 117 </td><td> G20, G30, N7 </td><td> Metoda wykonuje więcej operacji niż wynika to z jej nazwy (efekty uboczne) </td></tr>
    </tbody>
</table>

        <p>Zapachy, które nigdy nie zostały znalezione:
        <?
            $smells = 26;
            $seq = 'SELECT 1 s';
            for($i = 2; $i <= $smells; $i++)
                $seq .= ' UNION SELECT ' . $i;
            $seq = "($seq) seq";
            $missing = "SELECT s FROM $seq LEFT JOIN detected ON s=smell WHERE smell IS NULL ORDER BY s";
            foreach($db->query($missing) as $c)
                $smellsGlobalMissing[] = $c[0];
            echo implode(', ', $smellsGlobalMissing);
        ?>
        </p>

        <p>Zapachy, które zostały znalezione w aplikacji mobilnej ale nie na PC:
            <?
            $missing = "SELECT s FROM $seq LEFT JOIN detected ON s=smell AND review_type='pc' WHERE smell IS NULL ORDER BY s";
            $smellsMissing = [];
            foreach($db->query($missing) as $c)
                $smellsMissing[] = $c[0];
            echo implode(', ', array_diff($smellsMissing, $smellsGlobalMissing));
            ?>
        </p>

        <p>Zapachy, zostały zostały znalezione na PC ale nie w aplikacji mobilnej:
            <?
            $missing = "SELECT s FROM $seq LEFT JOIN detected ON s=smell AND review_type='mobile' WHERE smell IS NULL ORDER BY s";
            $smellsMissing = [];
            foreach($db->query($missing) as $c)
                $smellsMissing[] = $c[0];
            echo implode(', ', array_diff($smellsMissing, $smellsGlobalMissing));
            ?>
        </p>

        <div class="chart" id="frequent"></div>
        <div class="chart" id="frequentpc"></div>
        <div class="chart" id="frequentmobile"></div>
        <div class="chart" id="firstcom"></div>
        <div class="chart2" id="firstcompc"></div>
        <div class="chart2" id="firstcommobile"></div>



    </body>
</html>
<?
    $content = ob_get_contents();
    file_put_contents(__DIR__ . '/results.html', trim($content));
?>