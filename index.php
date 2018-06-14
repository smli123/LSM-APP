<?php

$dir="./bin/";
$filter=".apk";
$app_files=scandir_filter($dir, $filter);
//$app_files=scandir($dir);
//print_r($app_files);

function scandir_filter($path, $ext){
	$files = scandir($path);
	$files_filter = array();
	$i = 0;
	foreach ($files as $key => $value) {
				if ($value == '.' or $value == '..') 
					continue;
				$ext_file = substr($value,-4,4);
				if ($ext_file == $ext){
					$files_filter[$i++] = $value;
				}
	}
	
	return $files_filter;
}
	
?>

<!DOCTYPE HTML> 
<html lang="en"> 
<head>
<meta charset="utf-8"/>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width; initial-scale=1.0;  minimum-scale=1.0; maximum-scale=2.0"/>
<meta name="MobileOptimized" content="240"/>

<title>APPStores下载</title> 
</head>

<body bgcolor="#FFFFFF"> 
<font color="#000000">
	<h1></h1> 
	<p></p>

		<div class="banner-buttons" aligin="left" style="text-align:center">
		
		<?php
			foreach ($app_files as $key => $value) {
				if ($value == '.' or $value == '..') 
					continue;
				
				echo '<br>';
				echo	 '<div class="banner-button green-button">';
				echo		'<a href="http://192.168.0.109/bin/' . $value . '"><img src="" alt="' . $value . '" /></a>';
				echo	'</div>';
				echo '<br>';
			}
		?>
		</div>
</font>
</body>
</html>