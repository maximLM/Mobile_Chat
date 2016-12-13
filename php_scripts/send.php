<?php
$mysql_host = "localhost"; // sql сервер
$mysql_user = "kaslad"; // пользователь
$mysql_password = "CdRoom301"; // пароль
$mysql_database = "e-chat"; // имя базы данных chat

$link = mysql_connect($mysql_host, $mysql_user, $mysql_password); // коннект к серверу SQL
if ($link == false) {
    print("sql doesnot connected");
}
$link = mysql_select_db($mysql_database); // коннект к БД на сервере
if ($link == false) {
    print("e-chat doesnot connected");
}
mysql_set_charset('utf8'); // кодировка

if (isset($_GET["username"])) { 
    $username = $_GET['username'];
	if(isset($_GET["conversationID"])){
		$conversationID = $_GET['conversationID'];
		if(isset($_GET["content"])){
			$content = $_GET['content'];
			$time = round(microtime(1) * 1000);
			mysql_query("INSERT INTO `MessagesMain` (`conversationID`, `username`, `content`, `time`) VALUES('$conversationID','$username', '$content', '$time')");
			print(success);
		}
	}
}


?>