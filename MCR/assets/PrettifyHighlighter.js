function initializeSource(){
	prettyPrint();
	var source = document.getElementById('source').innerHTML;
	PrettifyHighlighter.setSpannedSource(source.toString());
}