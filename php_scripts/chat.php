<?php
$mysql_host = "localhost"; // sql сервер
$mysql_user = "kaslad"; // пользователь
$mysql_password = "CdRoom301"; // пароль
$mysql_database = "e-chat"; // имя базы данных chat
//chat.php?action=get&time=303030030
if (isset($_GET["action"])) { 
    $action = $_GET['action'];
}


$link = mysql_connect($mysql_host, $mysql_user, $mysql_password); // коннект к серверу SQL
if ($link == false) {
    print("sql doesnot connected");
}
$link = mysql_select_db($mysql_database); // коннект к БД на сервере
if ($link == false) {
    print("e-chat doesnot connected");
}
mysql_set_charset('utf8'); // кодировка



if($action == get){
	if (isset($_GET["time"])){
		$time = $_GET['time'];
	}
	$q=mysql_query("SELECT * FROM Messages WHERE time > $time");
	while($e=mysql_fetch_assoc($q))
        $output[]=$e;
	print(json_encode($output));
}
if($action == send){
    $current_time = round(microtime(1) * 1000);
    
        $text = $_GET['message'];
    
    //проблема возможно здесь не нужно устаначливать id он сам устанавливается если таблица правильно создана
    
mysql_query("INSERT INTO `Messages`(`content`,`time`) VALUES ('$text','$current_time')");
    // mysql_query("INSERT INTO 'Messages'('time','content') VALUES ('$current_time','$text')");
    //&content = mysql_query("SELECT * FROM Messages")
    print("success");
}
/*if($_GET["action"]=="hello")
	print("Hello user");
if($_GET["action"]=="time"){
	$current_time = round(microtime(1) * 1000);
	print($current_time);
}
*/
mysql_close();

?>