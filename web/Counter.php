<?php

if(!file_exists(Counter::FILE))
	file_put_contents(Counter::FILE, '0,0,0,0');

class Counter{
	
	const FILE = './__counts__.txt';
	
	private $file;
	
	private $viewCount, $downloadCount, $reviewsCount, $pcReviewsCount;
	
	public function __construct(){
		$counts = file_get_contents(self::FILE);
		$this->file = realpath(self::FILE);
		list($this->viewCount, $this->downloadCount, $this->reviewsCount, $this->pcReviewsCount) = explode(',', $counts);
	}
	
	private function update(){
		$content = implode(',', array($this->viewCount, $this->downloadCount, $this->reviewsCount));
		file_put_contents(self::FILE, implode(',', array($this->viewCount, $this->downloadCount, $this->reviewsCount, $this->pcReviewsCount)));
	}
	
	public function incrementViews(){
		++ $this->viewCount;
		$this->update();
	}
	
	public function incrementReviews(){
		++ $this->reviewsCount;
		$this->update();
	}
	
	public function incrementPcReviews(){
		++ $this->pcReviewsCount;
		$this->update();
	}
	
	public function incrementDownloads(){
		++ $this->downloadCount;
		$this->update();
	}
	
	public function getViews(){
		return $this->viewCount;
	}
	
	public function getDownloads(){
		return $this->downloadCount;
	}
	
	public function getReviews(){
		return $this->reviewsCount;
	}
	
	public function getPcReviews(){
		return $this->pcReviewsCount;
	}
}