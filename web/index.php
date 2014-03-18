<?php
	require_once 'Counter.php';
	$counter = new Counter();
	$counter->incrementViews();
?><!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="pl" xml:lang="pl">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
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
		<div class="img"><a href="mcr.apk"><img src="mcr.png" alt="MCR" /></a></div>
		<p><strong>Pobierz aplikację i sprawdź swoje umiejętności w Code Review!</strong></p>		 
		<p>W ramach pracy magisterskiej stworzyłem aplikację wspierającą praktykę Code Review na urządzeniach mobilnych.</p>
		<p>W celu weryfikacji, czy takie rozwiązanie ma sens, postanowiłem porównać ilość błędów znalezionych w kodzie źródłowym za pomocą urządzenia mobilnego oraz za pomocą "dużego" monitora.</p>
		<p>Klikając w logo aplikacji na górze strony, możesz ją pobrać i zainstalować na swoim urządzeniu mobilnym z systemem Android.</p>
		<p><strong>Nie masz Androida? <a href="#no-android">Zobacz niżej!</a></strong></p>
		<p>Doświadczenie nie potrwa dłużej niż 10 minut.</p>
		<p>Z góry bardzo dziękuję za wzięcie udziału w badaniu!</p>
		<hr />
		<p>Jeżeli nie uczestniczyłeś w żadnym wprowazdeniu do aplikacji - zapoznaj się proszę <u>przed pobraniem</u> z jej funkcjonalnościami i użytkowaniem poniżej.<p>
		<div class="img"><img src="instructions.png" alt="MCR" /></div>
		<p>Przyciski 1, 2 i 3 służą do wprowadzania predefiniowanych komentarzy do kodu źródłowego. Można je dodać za pomocą jednego "tapnięcia". Ich lista znajduje sie ponżej.</p>
		<ol>
			<li>
				Predefiniowany komentarz, kategoria "styl i struktura kodu".
				<ul>
					<li>"Magiczna" liczba</li>
					<li>Niepoprawna nazwa</li>
					<li>Stwórz stałą</li>
					<li>Wydziel do osobnej metody</li>
					<li>Zapisz wynik do zmiennej</li>
					<li>Zbędne białe znaki</li>
				</ul>
			</li>
			<li>
				Predefiniowany komentarz, kategoria "błąd w kodzie".
				<ul>
					<li>Błąd kompilacji</li>
					<li>Duplikacja kodu</li>
					<li>Nieobsługiwany wyjątek</li>
					<li>Nieużywany argument / zmienna</li>
					<li>Niewykorzystywany kod</li>
				</ul>
			</li>
			<li>
				Predefiniowany komentarz, kategoria "pozostałe".
				<ul>
					<li>Literówka</li>
					<li>Niezrozumiały kod</li>
					<li>Użyj istniejącego kodu</li>
					<li>Zbędny komentarz</li>
				</ul>
			</li>
			<li>Wprowadzanie dowolnego komentarza w polu tekstowym.</li>
			<li>Wprowadzenie komentarza w formie notatki głosowej (nagranie).</li>
			<li>Numery linii zawierających komentarz oznaczane są zielonym kolorem.</li>
			<li>Obecnie zaznaczona linia jest podświetlana na szaro - wprowadzone komentarze będą do niej dodawane.</li>
		</ol>
		<p>W celu podgląnięcia komentarzy dodanych do wybranej linii należy ją przytrzymać palcem. Lista komentarzy pojawi się w dolnej części ekranu.</p>
		<p>Na wykonanie zadania masz określoną ilość czasu. Jeśli zakończysz review przed jego upłynięciem - możesz wcześniej zakończyć zadanie wybierając Menu -> Zakończ.</p>
		<hr />
		<p>Po zakończonym doświadczeniu aplikacja prześle do mnie wprowadzone komentarze do kodu źródłowego. Ich analiza pomoże mi w ocenie zaproponowanego przeze mnie rozwiązania przeglądu kodu źródłowego.</p>
		<p>Oprócz komentarzy, otrzymam także informacje o rozmiarze ekranu Twojego urządzenia oraz o orientacji ekranu, w jakiej było wykonywane review (portrait / landscape). 
		Przesłany będzie również czas, w którym przegląd został wykonany (zakładając, że został zakończony opcją z menu).</p>
		<p>Z góry bardzo dziękuję za wzięcie udziału w badaniu!</p>
		<hr />
		<div class="img" id="no-android"><img src="no-android.png" alt="No-android" /></div>
		<p>Jeżeli nie masz urządzenia z systemem Android, nadal możesz mi pomóc, wykonując przegląd tego samego kodu na ekranie komputera. Zebrane w ten sposób wyniki posłużą mi do porównania code review na urządzeniu mobilnym oraz na komputerze.</p>
		<p>Musisz jednak, pamiętać, że:</p>
		<ul>
			<li>Powinieneś wykonać review tylko raz, albo na urządzeniu mobilnym albo na komputerze</li>
			<li>Wykonując review na komputerze, sam musisz zadbać o wyegzekwowanie limitu czasu 7 minut, ponieważ nie jestem twórcą systemu code review, którego używam do przeprowadzenia doświadczenia</li>
			<li>System ten nie wymaga rejestracji, musisz podać jedynie swój nick; wpisz tam cokolwiek - nie potrzebuję informacji kto wykonywał przegląd</li>
		</ul>
		<p>Wszystko jasne?</p>
		<p><a href="/pc-review">Wykonaj code review na dużym ekranie</a></p>
		<p>Z góry bardzo dziękuję za wzięcie udziału w badaniu!</p>
		<hr />
		<p>
		Wejść na stronę: <?=$counter->getViews()?><br>
		Pobrań aplikacji: <?=$counter->getDownloads()?><br>
		Wykonanych review "mobilnych": <?=$counter->getReviews()?><br>
		Wykonanych review "PC": <?=$counter->getPcReviews()?></p>
		<p>dyplomant: Wojciech Frącz</p>
	</body>
</html>